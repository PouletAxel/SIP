package plop.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


import plop.cli.CLIHelper;
import plop.multiProcessing.ProcessDetectLoops;
import plop.loops.Loop;
import plop.sip.SIPIntra;

/**
 * Class allowing the multi resolution loop calling
 *
 * @author axel poulet
 *
 */
public class MultiResProcess {

	/** SIP object*/
	private SIPIntra _sip;


	/** path to chr size file*/
	private String _chrFile;

	/**
	 * Constructor
	 *
	 * @param sip SIPIntra
	 * @param chrSizeFile path of chrSize file
	 */
	public MultiResProcess(SIPIntra sip, String chrSizeFile) {
		this._sip = sip;
		this._chrFile = chrSizeFile;
	}
	
	/**
	 *
	 * for each resolution used run the same java.plop.process to otain the loop at the resolution of interest
	 * at each resolution the input parameters will be adapted to the resolution used
	 * @throws InterruptedException exception
	 * @throws IOException exception
	 * 
	 */
	public void run() throws InterruptedException, IOException{
		ArrayList<Integer> listFactor = this._sip.getListFactor();
		ArrayList<String> listOfFile = new ArrayList<String>();
		if (_sip.isProcessed()){
			if(!this.testDir()){
				BufferedWriter writer = new BufferedWriter(new FileWriter(new File(_sip.getOutputDir()+"log.txt"), true));
				writer.write("!!!! It is missing one or several directories for factor parameter\nEnable to find all the directories needed for SIP (-factor option)\n\n");
				System.out.println("!!!! It is missing one or several directories for factor parameter\n");
				CLIHelper.CmdHelpProcessed();
			}
		}

		for(int indexFact = 0; indexFact < listFactor.size(); ++indexFact) {
			int res = this._sip.getResolution()*listFactor.get(indexFact);
			String resName = String.valueOf(res);
			resName = resName.replace("000", "")+"kb";
			String resuFile = this._sip.getOutputDir()+File.separator+resName+"Loops.txt";
			listOfFile.add(resuFile);
			File file = new File(resuFile);
			if(file.exists()) 
				file.delete();
			if(indexFact == 0) {
				//System.out.println("plopi");
				ProcessDetectLoops processDetectloops = new ProcessDetectLoops();
				processDetectloops.go(this._sip, resuFile,resName);
			}else {
				SIPIntra sipTmp = setSIPIntraTmp(listFactor.get(indexFact));
				ProcessDetectLoops processDetectloops = new ProcessDetectLoops();
				processDetectloops.go(sipTmp,resuFile,resName);
			}
		}
		if(listOfFile.size() > 1) {
			System.out.println("Merging File ... ");
			this.mergedFile(listOfFile);
			System.out.println("Merging File End !!!! ");
		}
	}


	/**
	 *
	 * @param factor
	 * @return
	 */
	private SIPIntra setSIPIntraTmp(int factor){
		int res = this._sip.getResolution()*factor;
		int matrixSizeTpm =  this._sip.getMatrixSize()/factor;
		double gaussTpm = this._sip.getGauss()/factor;
		SIPIntra sipTmp = new SIPIntra(_sip.getOutputDir(), _sip.getChrSizeFile(), gaussTpm,
				_sip.getMin(), _sip.getMax(), res, _sip.getSaturatedPixel(),_sip.getThresholdMaxima(),
				_sip.getDiagonalSize(), matrixSizeTpm, _sip.getNbZero(), _sip.getFactor(),
				_sip.getFdr(), _sip.isDroso(), _sip.isDelImage(), _sip.getCpu());

		if(this._sip.isProcessed()) {
			sipTmp = new SIPIntra(_sip.getInputDir(),_sip.getOutputDir(),   _sip.getChrSizeFile(), gaussTpm,
					_sip.getMin(), _sip.getMax(), res, _sip.getSaturatedPixel(),_sip.getThresholdMaxima(),
					_sip.getDiagonalSize(), matrixSizeTpm, _sip.getNbZero(), _sip.getFactor(),
					_sip.getFdr(), _sip.isDroso(), _sip.isDelImage(), _sip.getCpu());
		}
		sipTmp.setIsGui(_sip.isGui());
		sipTmp.setStep(this._sip.getStep()/factor);
		sipTmp.setIsProcessed(_sip.isProcessed());
		sipTmp.setIsCooler(_sip.isCooler());
		return sipTmp;

	}


	/**
	 * java.plop.test the data organization
	 * @return boolean if yes the dir of interest is existing
	 */
	private boolean testDir(){
		String input = _sip.getInputDir();
		ArrayList<Integer> listFactor = this._sip.getListFactor();
		for(int indexFact = 0; indexFact < listFactor.size(); ++indexFact){
			int res = this._sip.getResolution()*listFactor.get(indexFact);
			String resName = String.valueOf(res);
			resName = resName.replace("000", "")+"kb";
			File inputDir = new File(input+resName);
			if(!inputDir.exists()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * merge java.plop.loops from different resolution
	 * java.plop.test if java.plop.loops are present at different resolution if yes keep this one at  smaller resolution
	 *
	 * @param listOfFile list of of llops file
	 * @throws IOException exception
	 */
	private void mergedFile(ArrayList<String> listOfFile) throws IOException{
		HashMap<String,Integer> chrSize = readChrIndex(this._chrFile);
		Set<String> key = chrSize.keySet();
		ArrayList <HashMap<String,Loop>> arrayListLoop = new ArrayList <HashMap<String,Loop>>();
		for (int i = 0; i < key.size();++i) {
			HashMap<String,Loop> loopHashMap = new HashMap<String,Loop>();
			arrayListLoop.add(loopHashMap);
		}
			
		BufferedReader br = new BufferedReader(new FileReader(listOfFile.get(0)));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();
		String title = line;
		sb.append(System.lineSeparator());
		line = br.readLine();
		while (line != null){
			sb.append(line);
			Loop loop = new Loop(line);
			String chr = loop.getChr();
			int index = chrSize.get(chr);
			HashMap<String,Loop> loopHashMap = arrayListLoop.get(index);
			loopHashMap.put(line, loop);
			arrayListLoop.set(index, loopHashMap);
			sb.append(System.lineSeparator());
			line = br.readLine();
		}
		br.close();
			
		for (int i = 1; i < listOfFile.size();++i) {
			br = new BufferedReader(new FileReader((listOfFile.get(i))));
			sb = new StringBuilder();
			line = br.readLine();
			sb.append(System.lineSeparator());
			line = br.readLine();
			while (line != null){
				Loop loop = new Loop(line);
				String chr = loop.getChr();
				int index = chrSize.get(chr);
				HashMap<String,Loop> loopHashMap = arrayListLoop.get(index);
				if(compareLoop(loopHashMap,loop)){
					loopHashMap.put(line,loop);
					arrayListLoop.set(index, loopHashMap);
				}
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			br.close();
		}	
		
		BufferedWriter writer;
		writer = new BufferedWriter(new FileWriter(new File(_sip.getOutputDir()+"finalLoops.txt")));
		writer.write(title+"\n");
		for (int i = 0; i < arrayListLoop.size();++i){
			HashMap<String,Loop> loopHashMap = arrayListLoop.get(i);
			Set<String> keyBis = loopHashMap.keySet();
			List<String> output = keyBis.stream().collect(Collectors.toList());
			for (int j = 0; j < output.size();++j){
				writer.write(output.get(j)+"\n");
			}	
		}
		writer.close();
	}
		
		
	/**
	 * java.plop.test if the loop is existing at other resolution, if yes keep it if it is the smaller reolution
	 *
	 * @param loopHashMap hashmap loop name => Loop object
	 * @param loopOfInterest Loop to java.plop.test
	 * @return boolean true or false
	 */
	private  boolean compareLoop(HashMap<String,Loop> loopHashMap, Loop loopOfInterest) {
		Set<String> key = loopHashMap.keySet();
		Iterator<String> it = key.iterator();
		while (it.hasNext()){
			String loopName = it.next();
			Loop loop = loopHashMap.get(loopName);
			int xEnd = loop.getCoordinates().get(1);
			int yEnd = loop.getCoordinates().get(3);
			int xtest = loopOfInterest.getCoordinates().get(0);
			int xtestEnd = loopOfInterest.getCoordinates().get(1);
			int res = xtestEnd-xtest;
			int ytest = loopOfInterest.getCoordinates().get(2);
			int ytestEnd = loopOfInterest.getCoordinates().get(3);
			xtest = xtest-res*2;
			xtestEnd = xtestEnd+res*2;
			ytest = ytest-res*2;
			ytestEnd = ytestEnd+res*2;
			//System.out.println(x+"\t"+xEnd+"\t"+ xtestEnd);
			if((xtest <= xEnd && xtestEnd >= xEnd) && (ytest <= yEnd && ytestEnd >= yEnd))
				return false;
		}		
		return true;
	}
	

	/**
	 * Read chr index file and return HashMap of chr name and size
	 * @param chrSizeFile path to the chr size file
	 * @return hashMap chrName => chrSize
	 * @throws IOException exception
	 */
	private HashMap<String, Integer> readChrIndex( String chrSizeFile) throws IOException{
		HashMap<String,Integer> chrSize =  new HashMap<String,Integer>();
		BufferedReader br = new BufferedReader(new FileReader(chrSizeFile));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();
		int i = 0;
		while (line != null){
			sb.append(line);
			String[] parts = line.split("\\t");				
			String chr = parts[0];
			chrSize.put(chr, i);
			sb.append(System.lineSeparator());
			line = br.readLine();
			++i;
		}
		br.close();
		return  chrSize;
	} 
	
}
