package multiProcesing;
import process.CallLoops;
import utils.SIPObject;
import utils.Loop;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Runnable class
 * 
 * @author axel poulet
 *
 */
public class RunnableDetectLoops extends Thread implements Runnable{

	private SIPObject _sip;
	private CallLoops _callLoops;
	private String _resuFile;
	private String _chr;
	private HashMap<Integer, String> _normVector = new HashMap<Integer, String> ();
	
	/**
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
		_normVector = normVector;
	}
	
	/**
	 * 
	 */
	public void run(){
		ProcessDetectLoops._nbLance++;
		ProcessDetectLoops._continuer = true;
		String outputDir = this._sip.getOutputDir();
		String inputDir = this._sip.getinputDir();
		String dir = outputDir+File.separator+_chr+File.separator;
		if (this._sip.isProcessed())
			dir = inputDir+File.separator+_chr+File.separator;
		try {
			File[] listOfFiles = _sip.fillList(dir);
			System.out.println("normVector end loading file: "+_chr+".norm");
			if (listOfFiles.length == 0)
				System.out.println("!!!!!!!!!! dumped directory of chromosome"+_chr+" empty");
			else{
				File file = new File(this._resuFile);
				HashMap<String, Loop> data = this._callLoops.detectLoops(listOfFiles,_chr,this._normVector);
				if (file.length() == 0){
					_sip.saveFile(_resuFile,data,false);
				}else 
					_sip.saveFile(_resuFile,data, true);
			}
		} catch (IOException e1) {
				e1.printStackTrace();
		}
		System.gc();
		ProcessDetectLoops._nbLance--;
	}
}