package multiProcesing;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import gui.Progress;
import process.CallLoops;
import utils.SIPObject;

/**
 * multi thread class for the nucleus segmentation
 * 
 * @author axel poulet
 *
 */
public class ProcessDetectLoops{
	static int _nbLance = 0;
	static boolean _continuer;
	static int _indice = 0;
	/** */
	private Progress _p;

	/**
	 * 
	 */
	public ProcessDetectLoops(){ }

	/**
	 * 
	 * @param sip
	 * @param nbCPU
	 * @throws InterruptedException
	 */
	public void go(SIPObject sip,int nbCPU) throws InterruptedException{
		String resuFile = sip.getOutputDir()+File.separator+"loops.txt";
		File file = new File(resuFile);
		if(file.exists()) file.delete();
		file = new File(sip.getOutputDir());
		if (file.exists()==false) file.mkdir();
		int nb = 0;
		if(sip.isGui()){
			_p = new Progress("Loop Detection step",sip.getChrSizeHashMap().size());
			_p.bar.setValue(nb);
		}
		_nbLance = 0;
		ArrayList<Thread> arrayListImageThread = new ArrayList<Thread>();
		int j = 0; 
		Iterator<String> chrName = sip.getChrSizeHashMap().keySet().iterator();
		while(chrName.hasNext()){
			String chr = chrName.next();
			CallLoops cl = new CallLoops(sip);
			String normFile = sip.getOutputDir()+File.separator+"normVector"+File.separator+chr+".norm";
			if (sip.isProcessed()){
				normFile = sip.getinputDir()+File.separator+"normVector"+File.separator+chr+".norm";
			}
			arrayListImageThread.add( new RunnableDetectLoops(
					chr,  cl,	resuFile,
					sip, sip.testNormaVectorValue(normFile)));
			arrayListImageThread.get(j).start();
			
			while (_continuer == false)
				Thread.sleep(10);
			while (_nbLance > nbCPU)
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