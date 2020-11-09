package multiProcesing;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import gui.Progress;
import dumpProcessing.DumpData;
import dumpProcessing.DumpInterChromosomal;
import sip.SIPInter;
import sip.SIPIntra;


/**
 * multi thread class dumping the data via cooler 
 *  and make file step by step
 * bed file: start1 start2 obs-expected distanceNormalizedValue
 * 
 * @author axel poulet
 *
 */
public class ProcessDumpData {
	/** progress bar if gui is true*/
	private Progress _p;

	/**
	 * 
	 */
	public ProcessDumpData(){ }
	
	/**
	 *  run SiP for intra chr loops
	 *
	 * @param hicFile Sting input file path
	 * @param sip SIPIntra with all the paramters needed
	 * @param juiceBoxTools juicerTools.jar path
	 * @param normJuiceBox String normalization method
	 * @param nbCPU int nb cpu
	 * @throws InterruptedException exception
	 */
	public void go(String hicFile, SIPIntra sip, String juiceBoxTools, String normJuiceBox, int nbCPU) throws InterruptedException {
		HashMap<String,Integer> chrSize = sip.getChrSizeHashMap();
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nbCPU);
		Iterator<String> chrName = chrSize.keySet().iterator();
		File outDir = new File(sip.getOutputDir());
		if (!outDir.exists()) outDir.mkdir();
		while(chrName.hasNext()){
			String chr = chrName.next();
			DumpData dumpData = new DumpData(juiceBoxTools, hicFile, normJuiceBox);
			RunnableDumpDataHiC task =  new RunnableDumpDataHiC(sip.getOutputDir(), chr, chrSize.get(chr), dumpData, sip.getResolution(), sip.getMatrixSize(), sip.getStep(), sip.getListFactor());
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
	 *	run SiP for inter chr loops
	 *
	 * @param hicFile Sting input file path
	 * @param sipInter SIPInter object with all the parameters needed
	 * @param juiceBoxTools juicerTools.jar path
	 * @param normJuiceBox String normalization method
	 * @param nbCPU int nb cpu
	 * @throws InterruptedException exception
	 */
	public void go(String hicFile, SIPInter sipInter, String juiceBoxTools, String normJuiceBox, int nbCPU) throws InterruptedException {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nbCPU);
		HashMap<String,Integer> chrSize = sipInter.getChrSizeHashMap();
		Object [] chrName = chrSize.keySet().toArray();

		System.out.println(sipInter.getOutputDir());
		File outDir = new File(sipInter.getOutputDir());
		if (!outDir.exists()) outDir.mkdir();
		for(int i = 0; i < chrName.length;++i){
			String chr1 = chrName[i].toString();
			for(int j = i+1; j < chrName.length;++j){
				String chr2 = chrName[j].toString();
				int size1 = chrSize.get(chr1);
				int size2 = chrSize.get(chr2);
				System.out.println(chr1+"\t"+size1+"\t"+chr2+"\t"+size2);
				DumpInterChromosomal DumpInterChromosomal = new DumpInterChromosomal(juiceBoxTools, hicFile, normJuiceBox);
				RunnableDumpInterChromoData task =  new RunnableDumpInterChromoData(sipInter.getOutputDir(), chr1, chrSize.get(chr1),
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