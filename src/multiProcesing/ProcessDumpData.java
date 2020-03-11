package multiProcesing;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import gui.Progress;
import process.DumpData;
import utils.SIPObject;


/**
 * multi thread class dumping the data via juicertoolsbox.jar 
 *  and make file step by step
 * bed file: start1 start2 obs-expected distanceNormalizedValue
 * 
 * @author axel poulet
 *
 */
public class ProcessDumpData{
	/** progress bar if gui is true*/
	private Progress _p;

	/**
	 * 
	 */
	public ProcessDumpData(){ }
	
	/**
	 *  * run the processing on different cpu, if all cpu are running, take break else run a new one.
	 * 
	 * @param hicFile
	 * @param sip
	 * @param chrSize
	 * @param juiceBoxTools
	 * @param normJuiceBox
	 * @param nbCPU
	 * @throws InterruptedException
	 */
	public void go(String hicFile, SIPObject sip, HashMap<String,Integer> chrSize, String juiceBoxTools, String normJuiceBox,int nbCPU) throws InterruptedException {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nbCPU);
		Iterator<String> chrName = chrSize.keySet().iterator();
		File outDir = new File(sip.getOutputDir());
		if (outDir.exists()==false) outDir.mkdir();
		while(chrName.hasNext()){
			String chr = chrName.next();
			DumpData dumpData = new DumpData(juiceBoxTools, hicFile, normJuiceBox, sip.getResolution());
			RunnableDumpData task =  new RunnableDumpData(sip.getOutputDir(), chr, chrSize.get(chr), dumpData, sip.getResolution(), sip.getMatrixSize(),	sip.getStep());
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
				System.out.println("plop:"+executor.getCompletedTaskCount());
			}
		}
		if(sip.isGui())	_p.dispose();
	}
}