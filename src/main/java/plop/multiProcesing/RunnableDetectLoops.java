package plop.multiProcesing;
import plop.process.CallLoops;
import plop.utils.SIPObject;
import plop.utils.Loop;
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
	/** String name of the chr*/
	private boolean _delImages = true;
	/** norn vector table for the chr of interest*/
	private HashMap<Integer, String> _normVector = new HashMap<Integer, String> ();
	/** */
	private String _normvectorFile;
	
	

	/**
	 * Construtor, initialize all the variables  of interest
	 *
	 * @param chr
	 * @param resuFile
	 * @param sip
	 * @param normVectorFile
	 * @param delFile
	 */
	public RunnableDetectLoops (String chr, String resuFile, SIPObject sip, String normVectorFile, boolean delFile){
		this._sip = sip;
		this._callLoops = new CallLoops(sip);
		this._chr= chr;
		this._resuFile = resuFile;
		this._normVector = sip.getNormValueFilter(normVectorFile);
		this._delImages = delFile;
		this._normvectorFile = normVectorFile; 
	}

	/**
	 *
	 * @param chr
	 * @param resuFile
	 * @param sip
	 * @param delFile
	 */
	public RunnableDetectLoops (String chr, String resuFile, SIPObject sip, boolean delFile){
		this._sip = sip;
		this._callLoops = new CallLoops(sip);
		this._chr= chr;
		this._resuFile = resuFile;
		this._delImages = delFile;
	}
	
	/**
	 * Run all the plop.process for loops detection by chr using the objet CallLoops and then save loops in
	 * txt file with SIPObject via he method saveFile
	 * 
	 */
	public void run(){
		String resName = String.valueOf(this._sip.getResolution());
		resName = resName.replace("000", "")+"kb";
		String dir =  this._sip.getOutputDir()+resName+File.separator+_chr+File.separator;
		HashMap<String, Loop> data = new HashMap<String, Loop> ();
		if (this._sip.isProcessed()) dir = this._sip.getInputDir()+resName+File.separator+this._chr+File.separator;
		try {
			File[] listOfFiles = _sip.fillList(dir);
			System.out.println(dir);
			if (listOfFiles.length == 0) System.out.println("!!!!!!!!!! dumped directory of chromosome"+this._chr+" empty");
			else{
				File file = new File(this._resuFile);
				if(_sip.isCooler() == false) {
					System.out.println(_normvectorFile+"normVector end loading file: "+_chr+".norm "+resName);
				}
				data = this._callLoops.detectLoops(listOfFiles,this._chr,this._normVector);
				synchronized(this) {
					if (file.length() == 0)	_sip.saveFile(this._resuFile,data,false);
					else this._sip.saveFile(this._resuFile,data, true);
				}
				listOfFiles = _sip.fillList(dir);
				if(_delImages){
					System.out.println("Deleting image file for "+_chr);
					for(int i = 0; i < listOfFiles.length;++i) {
						String name = listOfFiles[i].toString();
						if(name.contains(".tif"))  listOfFiles[i].delete();
					}
				}
			}
		} catch (IOException e1) { e1.printStackTrace();}
		System.gc();
	}
}