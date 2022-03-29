package plop.multiProcessing;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import plop.gui.Progress;
import plop.sip.SIPIntra;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * multi thread class for the SIP loop calling step
 *
 * 
 * @author axel poulet
 *
 */
public class ProcessDetectLoops{
	
	/** progress bar if java.plop.gui is true*/
	private Progress _p;

	/**	 */
	public ProcessDetectLoops(){ }


	/**
	 * multiprocessing method for SIP intra chromosomal interaction
	 *
	 * @param sip SIPIntra
	 * @param resuFile path to results file
	 * @param resName name of the resolution used
	 * @throws InterruptedException exception
	 */
	public void go(SIPIntra sip,  String resuFile, String resName) throws InterruptedException {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(sip.getCpu());
		Iterator<String> chrName = sip.getChrSizeHashMap().keySet().iterator();
		if(sip.isProcessed()) {
			boolean isCool = isProcessedMcool(sip.getInputDir()+resName+File.separator+"normVector");
			//System.out.println(isCool);
			sip.setIsCooler(isCool);
		}
		while(chrName.hasNext()){
			String chr = chrName.next();
			if(sip.isCooler()){
				RunnableDetectIntraLoops task =  new RunnableDetectIntraLoops(chr, resuFile, sip);
				executor.execute(task);	
				
			}else {
				String normFile = sip.getInputDir()+resName+File.separator+"normVector"+File.separator+chr+".norm";
				/*if (java.plop.sip.isProcessed()){
					normFile = java.plop.sip.getInputDir()+resName+File.separator+"normVector"+File.separator+chr+".norm";
				}*/
				RunnableDetectIntraLoops task =  new RunnableDetectIntraLoops(chr, resuFile, sip,normFile);
				executor.execute(task);	
			}
		}
		executor.shutdown();
		int nb = 0;
		if(sip.isGui()){
			_p = new Progress(resName+" Loops Detection step",sip.getChrSizeHashMap().size()+1);
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
	 * 
	 * @param dirToTest path to java.plop.test if it is existing
	 * @return boolean
	 */
	 private boolean isProcessedMcool(String dirToTest) {
		 File test = new File (dirToTest);
		 boolean b = !test.exists();
		 return b;
	 }
}