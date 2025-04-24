package plop.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;


import plop.multiProcesing.ProcessDetectLoops;
import plop.utils.Loop;
import plop.utils.SIPObject;

/**
 * 
 * @author axel poulet
 *
 */
public class MultiResProcess {

	private SIPObject _sip;
	/**
	 * 
	 */
	private int _nbCpu;
	/**
	 * 
	 */
	private boolean _delImage;
	private String _date;
	
	private String _doc = ("#SIP Version 1 run with java 8\n"
				+ "\nUsage:\n"
				+ "\thic <hicFile> <chrSizeFile> <Output> <juicerToolsPath> [options]\n"
				+ "\tcool <mcoolFile> <chrSizeFile> <Output> <cooltoolsPath> <coolerPath> [options]\n"
				+ "\tprocessed <Directory with processed data> <chrSizeFile> <Output> [options]\n"
				+ "\nParameters:\n"
				+ "\t chrSizeFile: path to the chr size file, with the same name of the chr as in the hic file " +
			"(i.e. chr1 does not match Chr1 or 1)\n"
				+ "\t-res: resolution in bp (default 5000 bp)\n"
				+ "\t-mat: matrix size to use for each chunk of the chromosome (default 2000 bins)\n"
				+ "\t-d: diagonal size in bins, remove the maxima found at this size (eg: a size of 2 at 5000 " +
			"bp resolution removes all maxima"
				+ " detected at a distance inferior or equal to 10kb) (default 6 bins).\n"
				+ "\t-g: Gaussian filter: smoothing factor to reduce noise during primary maxima detection (default 1.5)\n"
				+ "\t-cpu: Number of CPU used for SIP processing (default 1)\n"
				+ "\t-factor: Multiple resolutions can be specified using:\n"
				+ "\t\t-factor 1: run only for the input res (default)\n"
				+ "\t\t-factor 2: res and res*2\n"
				+ "\t\t-factor 3: res and res*5\n"
				+ "\t\t-factor 4: res, res*2 and res*5\n"
				+ "\t-max: Maximum filter: increases the region of high intensity (default 2)\n"
				+ "\t-min: Minimum filter: removes the isolated high value (default 2)\n"
				+ "\t-sat: % of saturated pixel: enhances the contrast in the image (default 0.01)\n"
				+ "\t-t Threshold for loops detection (default 2800)\n"
				+ "\t-nbZero: number of zeros: number of pixels equal to zero that are allowed in the 24 pixels " +
			"surrounding the detected maxima (default 6)\n"
				+ "\t-norm: <NONE/VC/VC_SQRT/KR> (default KR)\n"
				+ "\t-del: true or false, whether not to delete tif files used for loop detection (default true)\n"
				+ "\t-fdr: Empirical FDR value for filtering based on random sites (default 0.01)\n"
				+ "\t-isDroso: default false, if true apply extra filter to help detect loops similar to those found" +
			" in D. mel cells\n"
				+ "\t-h, --help print help\n"
				+ "\nCommand line eg:\n"
				+ "\tjava -jar SIP_HiC.jar processed inputDirectory pathToChromosome.size OutputDir .... parameters\n"
				+ "\tjava -jar SIP_HiC.jar hic inputDirectory pathToChromosome.size OutputDir juicer_tools.jar\n"
				+ "\nAuthors:\n"
				+ "Axel Poulet\n"
				+ "\tDepartment of Molecular, Cellular  and Developmental Biology Yale University 165 Prospect St\n"
				+ "\tNew Haven, CT 06511, USA\n"
				+ "M. Jordan Rowley\n"
				+ "\tDepartment of Genetics, Cell Biology and Anatomy, University of Nebraska Medical Center Omaha," +
			"NE 68198-5805\n"
				+ "\nContact: pouletaxel@gmail.com OR jordan.rowley@unmc.edu");
	
	private String _chrFile;
	/**
	 * 
	 * @param sip SIPObject with the parameters of the analysis
	 * @param cpu number of cpu used for teh analysis
	 * @param delImage delete the image at the end if True
	 * @param chrSizeFile Chromosome size file
	 */
	public MultiResProcess(SIPObject sip, int cpu, boolean delImage, String chrSizeFile) {
		this._nbCpu = cpu;
		this._sip = sip;
		this._delImage = delImage;
		this._chrFile = chrSizeFile;
		LocalDateTime myObj = LocalDateTime.now();
		this._date = myObj.toString().replaceAll(":","");
		this._date = this._date.replaceAll("\\.","_");

	}
	
	/**
	 * @throws InterruptedException 
	 * @throws IOException 
	 * 
	 */
	public void run() throws InterruptedException, IOException{

		ArrayList<Integer> listFactor = this._sip.getListFactor();
		ArrayList<String> listOfFile = new ArrayList<String>();
		File outDir = new File(_sip.getOutputDir());
		if (!outDir.exists())
			outDir.mkdir();
		if (_sip.isProcessed()){
			if(!this.testDir()){
				if(_sip.isGui()) {
					JOptionPane.showMessageDialog(null,"Resolution problem",
							"Enable to find all the directories needed for SIP (-factor option)",
							JOptionPane.ERROR_MESSAGE);
				}
				System.out.println("!!!! It is missing one or several directories for factor parameter\n");
				System.out.println(_doc);
				System.exit(0);
			}
		}
		for(int indexFact = 0; indexFact < listFactor.size(); ++indexFact) {
			int res = this._sip.getResolution()*listFactor.get(indexFact);
			String resName = String.valueOf(res);
			resName = resName.replace("000", "")+"kb";
			if(res < 1000){
				resName = String.valueOf(res);
				resName = resName+"b";
			}
			String resuFile = this._sip.getOutputDir()+File.separator+resName+"Loops_"+this._date+".txt";
			listOfFile.add(resuFile);
			File file = new File(resuFile);
			if(file.exists()) 
				file.delete();
			SIPObject sipTmp = new SIPObject ();
			if(indexFact == 0) {
				ProcessDetectLoops processDetectloops = new ProcessDetectLoops();
				processDetectloops.go(this._sip, this._nbCpu,this._delImage,resuFile,resName);
			}else {
				sipTmp.setInputDir(_sip.getOutputDir());
				sipTmp.setOutputDir(_sip.getOutputDir());
				if(this._sip.isProcessed()) {
					sipTmp.setInputDir(_sip.getInputDir());
					sipTmp.setOutputDir(_sip.getOutputDir());
				}
				sipTmp.setChrSizeHashMap(this._sip.getChrSizeHashMap());
				sipTmp.setIsGui(_sip.isGui());
				sipTmp.setDiagSize(this._sip.getDiagSize());
				sipTmp.setGauss(this._sip.getGauss()/listFactor.get(indexFact));
				sipTmp.setMatrixSize((int)(this._sip.getMatrixSize()/listFactor.get(indexFact)));
				sipTmp.setResolution(res);
				sipTmp.setStep(this._sip.getStep()/listFactor.get(indexFact));
				sipTmp.setMax(_sip.getMax());
				sipTmp.setMin(_sip.getMin());
				sipTmp.setSaturatedPixel(_sip.getSaturatedPixel());
				sipTmp.setThresholdMaxima(_sip.getThresholdMaxima());
				sipTmp.setNbZero(_sip.getNbZero());
				sipTmp.setIsProcessed(_sip.isProcessed());
				sipTmp.setFdr(_sip.getFdr());
				sipTmp.setIsDroso(_sip.isDroso());
				sipTmp.setIsCooler(_sip.isCooler());
				ProcessDetectLoops processDetectloops = new ProcessDetectLoops();
				processDetectloops.go(sipTmp, this._nbCpu,this._delImage,resuFile,resName);
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
	 * @return boolean if the folder tested exist
	 */
	private boolean testDir(){
		String input = _sip.getInputDir();
		ArrayList<Integer> listFactor = this._sip.getListFactor();
		for (Integer integer : listFactor) {
			int res = this._sip.getResolution() * integer;
			String resName = String.valueOf(res);
			resName = resName.replace("000", "") + "kb";
			if(res < 1000){
				resName = String.valueOf(res);
				resName = resName+"b";
			}
			File inputDir = new File(input + resName);
			if (!inputDir.exists()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @param listOfFile
	 * @throws IOException
	 */
	private void mergedFile(ArrayList<String> listOfFile) throws IOException{
		HashMap<String,Integer> chrSize = readChrIndex(this._chrFile);
		Set<String> key = chrSize.keySet();
		ArrayList <HashMap<String,Loop>> data = new ArrayList <HashMap<String,Loop>>();
		for (int i = 0; i < key.size();++i) {
			HashMap<String,Loop> plop = new HashMap<String,Loop>();
			data.add(plop);
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
			HashMap<String,Loop> plop = data.get(index);
			plop.put(line, loop);
			data.set(index, plop);
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
				HashMap<String,Loop> plop = data.get(index);
				if(compareLoop(plop,loop)){
					plop.put(line,loop);
					data.set(index, plop);
				}
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			br.close();
		}	
		
		BufferedWriter writer;
		writer = new BufferedWriter(new FileWriter(new File(_sip.getOutputDir()+"finalLoops"+this._date+".txt")));
		writer.write(title+"\n");
		for (int i = 0; i < data.size();++i){
			HashMap<String,Loop> plop = data.get(i);
			Set<String> keyBis = plop.keySet();
			List<String> output = keyBis.stream().collect(Collectors.toList());
			for (int j = 0; j < output.size();++j){
				writer.write(output.get(j)+"\n");
			}
		}
		writer.close();
	}
		
		
	/**
	 * 
	 * @param loopHashMap loop collection
	 * @param a loop tested
	 * @return boolean if the loop is present in the hash map
	 */
	private  boolean compareLoop(HashMap<String,Loop> loopHashMap, Loop a) {
		Set<String> key = loopHashMap.keySet();
		Iterator<String> it = key.iterator();
		while (it.hasNext()){
			String loopName = it.next();
			Loop loop = loopHashMap.get(loopName);
			int xEnd = loop.getCoordinates().get(1);
			int yEnd = loop.getCoordinates().get(3);
			int xtest = a.getCoordinates().get(0);
			int xtestEnd = a.getCoordinates().get(1);
			int res = xtestEnd-xtest;
			int ytest = a.getCoordinates().get(2);
			int ytestEnd = a.getCoordinates().get(3);
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
	 * 
	 * @param chrSizeFile file with the chromosome size
	 * @return hash map chr name chr size
	 * @throws IOException throw exception if file doesn't exist
	 */
	private HashMap<String, Integer> readChrIndex(String chrSizeFile) throws IOException{
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
