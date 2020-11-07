package multiProcesing;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import gui.Progress;
import utils.SIPInter;
import utils.SIPObject;

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
	
	/** progress bar if gui is true*/
	private Progress _p;

	/**	 */
	public ProcessDetectLoops(){ }


	/**
	 * multiprocessing method for SIP intra chromosomal interaction
	 *
	 * @param sip SIPObject
	 * @param nbCPU nb of cpu
	 * @param delImage boolean if true del all the tif file
	 * @param resuFile path to results file
	 * @param resName name of the resolution used
	 * @throws InterruptedException exception
	 */
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
	 * multiprocessing  for sip on inter-chromosomal interaction
	 * for each couple of chromosome, RunnableDetectInterLoop is call.
	 * @param sipInter SIPInter object
	 * @param nbCPU number of cpu
	 * @param delImage boolean if true del all the tif file
	 * @param resuFile path to results file
	 * @throws InterruptedException exception
	 */
	public void go(SIPInter sipInter, int nbCPU, boolean delImage, String resuFile) throws InterruptedException {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nbCPU);
		HashMap<String,Integer> chrSize = sipInter.getChrSize();
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
				RunnableDetectInterLoops task =  new RunnableDetectInterLoops(chr1, chr2, resuFile, sipInter, delImage);
				executor.execute(task);
			}
		}

		executor.shutdown();
		int nb = 0;

		if(sipInter.isGui()){
			_p = new Progress("Loop Detection step",sipInter.getChrSize().size()+1);
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
	 * @param dirToTest path to test if it is existing
	 * @return boolean
	 */
	 private boolean isProcessedMcool(String dirToTest) {
		 File test = new File (dirToTest);
		 boolean b = !test.exists();
		 return b;
	 }
}