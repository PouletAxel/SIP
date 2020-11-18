package multiProcesing;
import loops.CallLoops;
import sip.SIPIntra;
import loops.Loop;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Runnable class for loops detection
 * 
 * @author axel poulet
 *
 */
public class RunnableDetectLoops extends Thread implements Runnable{
	/** SIP object containing all the parameter for the loops detection*/
	private SIPIntra _sip;
	/** CallLoops object */
	private CallLoops _callLoops;
	/** String results file*/
	private String _resuFile;
	/** String name of the chr*/
	private String _chr;
	/** norn vector table for the chr of interest*/
	private HashMap<Integer, String> _normVector = new HashMap<Integer, String> ();
	/** path of Normalized vector from juicer tools dump for each chr by SIP*/
	private String _normVectorFile;
	
	

	/**
	 * Constructor, initialize all the variables needed for runnableDetectLoop, for hic file
	 * 
	 *
	 * @param chr String chromosome name
	 * @param resuFile path to the result file
	 * @param sip SIPIntra
	 * @param normVectorFile path to normalized vector
	 *
	 */
	public RunnableDetectLoops (String chr, String resuFile, SIPIntra sip, String normVectorFile){
		this._sip = sip;
		this._callLoops = new CallLoops(sip);
		this._chr= chr;
		this._resuFile = resuFile;
		this._normVector = sip.getNormValueFilter(normVectorFile);
		this._normVectorFile = normVectorFile;
	}

	/**
	 * Constructor, initialize all the variables needed for runnableDetectLoop, for mcool file
	 *
	 * @param chr String chromosome name
	 * @param resuFile path to the result file
	 * @param sip SIPIntra
	 *
	 */
	public RunnableDetectLoops (String chr, String resuFile, SIPIntra sip){
		this._sip = sip;
		this._callLoops = new CallLoops(sip);
		this._chr= chr;
		this._resuFile = resuFile;
	}
	
	/**
	 * Run all the process for loops detection by chr using the objet CallLoops and then save loops in 
	 * txt file with SIPIntra via he method saveFile
	 * 
	 */
	public void run(){
		String resName = String.valueOf(this._sip.getResolution());
		resName = resName.replace("000", "")+"kb";
		String dir =  this._sip.getOutputDir()+resName+File.separator+_chr+File.separator;
		HashMap<String, Loop> data = new HashMap<String, Loop> ();
		if (this._sip.isProcessed()) dir = this._sip.getInputDir()+resName+File.separator+this._chr+File.separator;
		try {
			File folder = new File(dir);
			File[] listOfFiles = folder.listFiles();

			System.out.println(dir);
			if (listOfFiles.length == 0) System.out.println("!!!!!!!!!! dumped directory of chromosome"+this._chr+" empty");
			else{
				File file = new File(this._resuFile);
				if(!_sip.isCooler()) {
					System.out.println(_normVectorFile +"normVector end loading file: "+_chr+".norm "+resName);
				}
				data = this._callLoops.detectLoops(listOfFiles,this._chr,this._normVector);
				synchronized(this) {
					if (file.length() == 0)	_sip.saveFile(this._resuFile,data,false);
					else this._sip.saveFile(this._resuFile,data, true);
				}
				folder = new File(dir);
				listOfFiles = folder.listFiles();
				if(_sip.isDelImage()){
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