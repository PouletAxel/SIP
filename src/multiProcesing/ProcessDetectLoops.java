package multiProcesing;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import gui.Progress;
import process.CallLoops;
import utils.SIPObject;

/**
 * multi thread class
 * Construct all the RunnableDetectLoops Object and run them sequencily with the available processors
 * 
 * @author axel poulet
 *
 */
public class ProcessDetectLoops{
	
	/**int: number of processus*/
	static int _nbLance = 0;
	/** boolean: if true continue the process else take a break*/
	static boolean _continuer;
	/** */
	static int _indice = 0;
	/** progress bar if gui is true*/
	private Progress _p;

	/**	 */
	public ProcessDetectLoops(){ }

	/**
	 * Run the process of loops detcetion in different CPU for each chr.
	 * Make a RunnableDetectLoops for each chr
	 * The SiIPObject contains all the information and parameters for the loops detection and post filtering
	 * @param sip SiIPObject
	 * @param nbCPU int number of CPU
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
			_p._bar.setValue(nb);
		}
		_nbLance = 0;
		ArrayList<Thread> threadCallLoops = new ArrayList<Thread>();
		int j = 0; 
		Iterator<String> chrName = sip.getChrSizeHashMap().keySet().iterator();
		while(chrName.hasNext()){
			String chr = chrName.next();
			CallLoops cl = new CallLoops(sip);
			String normFile = sip.getOutputDir()+File.separator+"normVector"+File.separator+chr+".norm";
			if (sip.isProcessed()){
				normFile = sip.getinputDir()+File.separator+"normVector"+File.separator+chr+".norm";
			}
			threadCallLoops.add( new RunnableDetectLoops(chr,  cl,	resuFile, sip, sip.testNormaVectorValue(normFile)));
			threadCallLoops.get(j).start();
			
			while (_continuer == false) 	Thread.sleep(10);
			while (_nbLance > nbCPU)		Thread.sleep(10);
			++j;
			if(sip.isGui()) 	_p._bar.setValue(nb);
			nb++;
		}
		for (int i = 0; i < threadCallLoops.size(); ++i)
			while(threadCallLoops.get(i).isAlive())
				Thread.sleep(10);
		if(sip.isGui())	_p.dispose();
	}
}