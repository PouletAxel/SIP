package multiProcesing;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import gui.Progress;
import process.DumpInterChromosomal;
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
	 * @param dirToTest
	 * @return
	 */
	 private boolean isProcessedMcool(String dirToTest) {
		 File test = new File (dirToTest);
		 boolean b = !test.exists();
		 return b;
	 }
}