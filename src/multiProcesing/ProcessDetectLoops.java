package multiProcesing;
import java.io.File;
import java.util.Iterator;

import gui.Progress;
import utils.SIPInter;
import utils.SIPObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * multi thread class
 * Construct all the RunnableDetectLoops Object and run them sequencily with the available processors
 * 
 * @author axel poulet
 *
 */
public class ProcessDetectLoops{
	
	/** progress bar if gui is true*/
	private Progress _p;

	/**	 */
	public ProcessDetectLoops(){ }

	
	public void go(SIPObject sip,int nbCPU, boolean delImage, String resuFile,String resName) throws InterruptedException { 
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nbCPU);
		Iterator<String> chrName = sip.getChrSizeHashMap().keySet().iterator();
		if(sip.isProcessed()) {
			boolean isCool = isProcessedMcool(sip.getOutputDir()+resName+File.separator+"normVector");
			//System.out.println(isCool);
			sip.setIsCooler(isCool);
		}
		while(chrName.hasNext()){
			String chr = chrName.next();
			if(sip.isCooler()){
				RunnableDetectLoops task =  new RunnableDetectLoops(chr, resuFile, sip, delImage);
				executor.execute(task);	
				
			}else {
				String normFile = sip.getOutputDir()+resName+File.separator+"normVector"+File.separator+chr+".norm";
				if (sip.isProcessed()){
					normFile = sip.getInputDir()+resName+File.separator+"normVector"+File.separator+chr+".norm";
				}
				RunnableDetectLoops task =  new RunnableDetectLoops(chr, resuFile, sip,normFile, delImage);
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
	 * @param sipInter
	 * @param nbCPU
	 * @param delImage
	 * @param resuFile
	 * @param resName
	 * @throws InterruptedException
	 */
	public void go(SIPInter sipInter, int nbCPU, boolean delImage, String resuFile, String resName) throws InterruptedException {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nbCPU);
		Iterator<String> chrName = sipInter.getChrSizeHashMap().keySet().iterator();
		if(sipInter.isProcessed()) {
			boolean isCool = isProcessedMcool(sipInter.getOutputDir()+resName+File.separator+"normVector");
			sipInter.setIsCooler(isCool);
		}
		while(chrName.hasNext()){
			String chr = chrName.next();
			RunnableDetectInterLoops task =  new RunnableDetectInterLoops(resuFile, chr, sipInter, delImage);
			executor.execute(task);
		}
		executor.shutdown();
		int nb = 0;
		if(sipInter.isGui()){
			_p = new Progress(resName+" Loops Detection step",sipInter.getChrSizeHashMap().size()+1);
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
	/**
	 * 
	 * @param dirToTest
	 * @return
	 */
	 private boolean isProcessedMcool(String dirToTest) {
		 File test = new File (dirToTest);
		 boolean b = !test.exists();
		 return b;
	 }
}