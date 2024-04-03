package plop.multiProcesing;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import plop.gui.Progress;
import plop.process.DumpData;
import plop.utils.SIPObject;


/**
 * multi thread class dumping the data via cooler 
 *  and make file step by step
 * bed file: start1 start2 obs-expected distanceNormalizedValue
 * 
 * @author axel poulet
 *
 */
public class ProcessHicDumpData{
	/** progress bar if plop.gui is true*/
	private Progress _p;

	/**
	 * 
	 */
	public ProcessHicDumpData(){ }
	
	/**
	 *  * run the processing on different cpu, if all cpu are running, take break else run a new one.
	 * 
	 * @param hicFile path to the hic file
	 * @param sip SIP object storing the parameters
	 * @param chrSize path to the chromosome file
	 * @param juiceBoxTools path to juicer tool
	 * @param normJuiceBox Normalization method use for the dumped data
	 * @param nbCPU number of cpu use for the analysis
	 * @throws InterruptedException if input file doesn't exist throw an exception
	 */
	public void go(String hicFile, SIPObject sip, HashMap<String,Integer> chrSize,
				   String juiceBoxTools, String normJuiceBox,int nbCPU) throws InterruptedException {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nbCPU);
		Iterator<String> chrName = chrSize.keySet().iterator();
		File outDir = new File(sip.getOutputDir());
		if (!outDir.exists())
			outDir.mkdir();
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
}