package multiProcesing;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import gui.Progress;
import process.DumpData;
import utils.SIPObject;


/**
 * multi thread class
 * 
 * @author axel poulet
 *
 */
public class ProcessDumpData{
	static int _nbLance = 0;
	static boolean _continuer;
	static int _indice = 0;
	/** */
	private Progress _p;

	/**
	 * 
	 */
	public ProcessDumpData(){ }

	/**
	 * 
	 * @param hicFile
	 * @param sip
	 * @param chrSize
	 * @param juiceBoxTools
	 * @param normJuiceBox
	 * @param nbCPU
	 * @throws InterruptedException
	 */
	public void go(String hicFile, SIPObject sip, HashMap<String,Integer> chrSize, String juiceBoxTools, String normJuiceBox,int nbCPU) throws InterruptedException{
		int nb = 0;
		File file = new File(sip.getOutputDir());
		if (file.exists()==false) file.mkdir();
		if(sip.isGui()){
			_p = new Progress("Dump data step",chrSize.size());
			_p.bar.setValue(nb);
		}
		_nbLance = 0;
		ArrayList<Thread> arrayListImageThread = new ArrayList<Thread>() ;
		int j = 0; 
		Iterator<String> chrName = chrSize.keySet().iterator();
		while(chrName.hasNext()){
			String chr = chrName.next();
			DumpData dumpData = new DumpData(juiceBoxTools, hicFile, normJuiceBox, sip.getResolution());
			arrayListImageThread.add(
				new RunnableDumpData(sip.getOutputDir(), 
						chr, chrSize.get(chr),
						dumpData, sip.getResolution(),
						sip.getMatrixSize(),	sip.getStep()						 
				)
			);
			arrayListImageThread.get(j).start();
			
			while (_continuer == false)
				Thread.sleep(10);
			while (_nbLance >=nbCPU)
				Thread.sleep(10);
			++j;
			if(sip.isGui())
				_p.bar.setValue(nb);
			nb++;
		}
		for (int i = 0; i < arrayListImageThread.size(); ++i)
			while(arrayListImageThread.get(i).isAlive())
				Thread.sleep(10);
		if(sip.isGui())
			_p.dispose();
	}
}