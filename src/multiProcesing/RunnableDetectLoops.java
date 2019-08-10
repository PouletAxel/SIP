package multiProcesing;
import process.CallLoops;
import utils.SIPObject;
import utils.Loop;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Runnable class for loops detcetion
 * 
 * @author axel poulet
 *
 */
public class RunnableDetectLoops extends Thread implements Runnable{
	/** SIP object containing all the parameter for the loops detection*/
	private SIPObject _sip;
	/** CallLoops object */
	private CallLoops _callLoops;
	/** String results file*/
	private String _resuFile;
	/** String name of the chr*/
	private String _chr;
	/** norn vector table for the chr of interest*/
	private HashMap<Integer, String> _normVector = new HashMap<Integer, String> ();
	
	/**
	 * Construtor, initialize all the variables  of interest
	 * 
	 * @param chr
	 * @param callLoops
	 * @param resuFile
	 * @param sip
	 * @param normVector
	 */
	public RunnableDetectLoops (String chr, CallLoops callLoops, String resuFile, SIPObject sip, HashMap<Integer, String> normVector){
		this._sip = sip;
		this._callLoops =callLoops;
		this._chr= chr;
		this._resuFile = resuFile;
		this._normVector = normVector;
	}
	
	/**
	 * Run all the process for loops detection by chr using the objet CallLoops and then save loops in 
	 * txt file with SIPObject via he method saveFile
	 * 
	 */
	public void run(){
		ProcessDetectLoops._nbLance++;
		ProcessDetectLoops._continuer = true;
		String outputDir = this._sip.getOutputDir();
		String inputDir = this._sip.getinputDir();
		String dir = outputDir+File.separator+_chr+File.separator;
		if (this._sip.isProcessed()) dir = inputDir+File.separator+this._chr+File.separator;
		try {
			File[] listOfFiles = _sip.fillList(dir);
			System.out.println("normVector end loading file: "+_chr+".norm");
			if (listOfFiles.length == 0) System.out.println("!!!!!!!!!! dumped directory of chromosome"+this._chr+" empty");
			else{
				File file = new File(this._resuFile);
				HashMap<String, Loop> data = this._callLoops.detectLoops(listOfFiles,this._chr,this._normVector);
				if (file.length() == 0)	_sip.saveFile(this._resuFile,data,false);
				else this._sip.saveFile(this._resuFile,data, true);
			}
		} catch (IOException e1) { e1.printStackTrace();}
		System.gc();
		ProcessDetectLoops._nbLance--;
	}
}