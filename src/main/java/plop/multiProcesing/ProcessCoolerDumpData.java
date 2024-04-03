package plop.multiProcesing;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import plop.gui.Progress;
import plop.process.CoolerDumpData;
import plop.utils.CoolerExpected;
import plop.utils.SIPObject;

/**
 * multi thread class dumping the data via juicertoolsbox.jar 
 *  and make file step by step
 * bed file: start1 start2 obs-expected distanceNormalizedValue
 * 
 * @author axel poulet
 */
public class ProcessCoolerDumpData {
	
	/** progress bar if plop.gui is true*/
	private Progress _p;
	
	/**
	 * 
	 */
	public ProcessCoolerDumpData(){ }
		
	/**
	 * Run the process of cool file on different cpu, if all cpu are running,
	 * take break else run a new one.
	 *
	 * @param coolTools path to coolTools
	 * @param cooler  path to coolTools
	 * @param sip  SIP object
	 * @param coolFile input cool file
	 * @param chrSize chromosome size file
	 * @param nbCPU number of cpu use for the analysis
	 * @throws InterruptedException test if the chr file exist or the cool file exist
	 */
	public void go(String coolTools, String cooler,
				   SIPObject sip, String coolFile,
				   HashMap<String,Integer> chrSize,int nbCPU)
			throws InterruptedException {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nbCPU);
		Iterator<String> chrName = chrSize.keySet().iterator();
		File outDir = new File(sip.getOutputDir());
		if (!outDir.exists())
			outDir.mkdir();
		
		ArrayList<Integer> listFactor = sip.getListFactor();
		for(int indexFact = 0; indexFact < listFactor.size(); ++indexFact) {
			
			int res = sip.getResolution()*listFactor.get(indexFact);
			int matrixSize = sip.getMatrixSize()/listFactor.get(indexFact);
			CoolerExpected expected = new CoolerExpected(coolTools,coolFile,  res, matrixSize, nbCPU);
			String nameRes = String.valueOf(res);
			nameRes = nameRes.replace("000", "");
			nameRes = nameRes+"kb"; 
			String expectedFile = sip.getOutputDir()+nameRes+".expected";
			System.out.println("start "+expectedFile);
			expected.dumpExpected(expectedFile);
			System.out.println("!!!!!!! End "+expectedFile);
		}
		
		while(chrName.hasNext()){
			String chr = chrName.next();
			CoolerDumpData dumpData = new CoolerDumpData( cooler, coolFile);
			RunnableDumpDataCooler task =  new RunnableDumpDataCooler(sip.getOutputDir(), chr, chrSize.get(chr), dumpData, sip.getResolution(), sip.getMatrixSize(), sip.getStep(), sip.getListFactor());
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
		File folder = new File(sip.getOutputDir());
		File[] listOfFiles = folder.listFiles();
		for(int i = 0; i < listOfFiles.length;++i) {
				String name = listOfFiles[i].toString();
				if(name.contains(".expected"))  listOfFiles[i].delete();
		}
		if(sip.isGui())	_p.dispose();
		
	}
}
