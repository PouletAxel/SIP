package plop.multiProcessing;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import plop.dumpProcessing.HicDumpIntra;
import plop.gui.Progress;
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


}