package plop.multiProcessing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import plop.dumpProcessing.HicDumpIntra;
import plop.dumpProcessing.HicDumpInter;
import plop.gui.Progress;
import plop.sip.SIPInter;
import plop.sip.SIPIntra;


/**
 * multi thread class dumping the data via cooler 
 *  and make file step by step
 * bed file: start1 start2 obs-expected distanceNormalizedValue
 * 
 * @author axel poulet
 *
 */
public class ProcessDumpHic {
	/** progress bar if java.plop.gui is true*/
	private Progress _p;

	/**
	 * 
	 */
	public ProcessDumpHic(){ }
	
	/**
	 *  run SiP for intra chr java.plop.loops
	 *
	 * @param hicFile Sting input file path
	 * @param sip SIPIntra with all the paramters needed
	 * @param juiceBoxTools juicerTools.jar path
	 * @param normJuiceBox String normalization method
	 * @throws InterruptedException exception
	 */
	public void go(String hicFile, SIPIntra sip, String juiceBoxTools, String normJuiceBox) throws InterruptedException {
		HashMap<String,Integer> chrSize = sip.getChrSizeHashMap();
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(sip.getCpu());
		Iterator<String> chrName = chrSize.keySet().iterator();
		File outDir = new File(sip.getInputDir());
		if (!outDir.exists()) outDir.mkdir();
		while(chrName.hasNext()){
			String chr = chrName.next();
			HicDumpIntra dumpData = new HicDumpIntra(juiceBoxTools, hicFile, normJuiceBox);
			RunnableDumpHicIntra task =  new RunnableDumpHicIntra(sip.getInputDir(), chr, chrSize.get(chr), dumpData, sip.getResolution(), sip.getMatrixSize(), sip.getStep(), sip.getListFactor());
			executor.execute(task);
		}
		executor.shutdown();
		int nb = 0;
		
		if(sip.isGui()){
			_p = new Progress("Loop Detection step",sip.getChrSizeHashMap().size()+1);
			_p._bar.setValue(nb);
		}
		while (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
			if (nb != executor.getCompletedTaskCount()) {
				nb = (int) executor.getCompletedTaskCount();
				if(sip.isGui()) _p._bar.setValue(nb);
			}
		}
		if(sip.isGui())	_p.dispose();
	}

	/**
	 *	run SiP for inter chr java.plop.loops
	 *
	 * @param hicFile Sting input file path
	 * @param sipInter SIPInter object with all the parameters needed
	 * @param juiceBoxTools juicerTools.jar path
	 * @param normJuiceBox String normalization method
	 * @throws InterruptedException exception
	 */
	public void go(String hicFile, SIPInter sipInter, String juiceBoxTools, String normJuiceBox) throws InterruptedException, IOException {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(sipInter.getCpu());
		HashMap<String,Integer> chrSize = sipInter.getChrSizeHashMap();
		ArrayList<String> chrName = sipInter.getChrList();
		if (chrName.size() < 2){
			System.out.println("Error: !!! only one chromosome in"+ sipInter.getChrSizeFile() +", you  need at least 2 chromosome in your file.\n");
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(sipInter.getOutputDir()+File.separator+"log.txt")));
			writer.write("Error: !!! only one chromosome in"+ sipInter.getChrSizeFile() +", you  need at least 2 chromosome in your file.\n");
			writer.close();
			System.exit(1);
		}
		for(int i = 0; i < chrName.size();++i){
			String chr1 = chrName.get(i);
			for(int j = i+1; j < chrName.size();++j){
				String chr2 = chrName.get(j);
				int size1 = chrSize.get(chr1);
				int size2 = chrSize.get(chr2);
				System.out.println(chr1+"\t"+size1+"\t"+chr2+"\t"+size2);
				HicDumpInter DumpInterChromosomal = new HicDumpInter(juiceBoxTools, hicFile, normJuiceBox);
				RunnableDumpHicInter task =  new RunnableDumpHicInter(sipInter.getInputDir(), chr1, chrSize.get(chr1),
						chr2, chrSize.get(chr2),DumpInterChromosomal, sipInter.getResolution(), sipInter.getMatrixSize());
				executor.execute(task);
			}
		}

		executor.shutdown();
		int nb = 0;

		if(sipInter.isGui()){
			_p = new Progress("Loop Detection step",sipInter.getChrSizeHashMap().size()+1);
			_p._bar.setValue(nb);
		}
		while (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
			if (nb != executor.getCompletedTaskCount()) {
				nb = (int) executor.getCompletedTaskCount();
				if(sipInter.isGui()) _p._bar.setValue(nb);
			}
		}
		if(sipInter.isGui())	_p.dispose();
	}

}