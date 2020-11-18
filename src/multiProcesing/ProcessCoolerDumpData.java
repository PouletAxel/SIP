package multiProcesing;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import gui.Progress;
import dumpProcessing.CoolerDumpData;
import dumpProcessing.CoolerExpected;
import sip.SIPIntra;

/**
 * multi thread class dumping the data via juicertoolsbox.jar 
 *  and make file step by step
 * bed file: start1 start2 obs-expected distanceNormalizedValue
 * 
 * @author axel poulet
 */
public class ProcessCoolerDumpData {
	
	/** progress bar if gui is true*/
	private Progress _p;
	
	/**
	 * Constructor
	 */
	public ProcessCoolerDumpData(){ }
		
	/**
	 * run the processing on different cpu, if all cpu are running, take break else run a new one.
	 *for each chromosome the normalized data and expected data will be dump via cooler and cooltool.
	 * the SIP are produce in this step allowing later, the creation of the images and the loops calling step.
	 *
	 * if gui is true a progress bar will pop up.
	 *
	 *
	 * @param coolTools String coolTools path
	 * @param cooler String cooler path
	 * @param sip SIPIntra
	 * @param coolFile path mcool file
	 * @throws InterruptedException exception
	 */
	public void go(String coolTools, String cooler, SIPIntra sip, String coolFile) throws InterruptedException {
		HashMap<String,Integer> chrSize= sip.getChrSizeHashMap();
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(sip.getCpu());
		Iterator<String> chrName = chrSize.keySet().iterator();
		File outDir = new File(sip.getOutputDir());
		if (!outDir.exists()) outDir.mkdir();
		
		ArrayList<Integer> listFactor = sip.getListFactor();
		for(int indexFact = 0; indexFact < listFactor.size(); ++indexFact) {
			
			int res = sip.getResolution()*listFactor.get(indexFact);
			int matrixSize = sip.getMatrixSize()/listFactor.get(indexFact);
			CoolerExpected expected = new CoolerExpected(coolTools,coolFile,  res, matrixSize,sip.getCpu());
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
