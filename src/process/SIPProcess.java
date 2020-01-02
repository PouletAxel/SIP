package process;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import gui.Progress;
import utils.Loop;
import utils.SIPObject;

public class SIPProcess {
	/** SIP object containing all the parameter for the loops detection*/
	private SIPObject _sip;
	/** String name of the chr*/
	private boolean _delImages = true;
	private Progress _p;
	
	public SIPProcess ( SIPObject sip, boolean delFile){
		this._sip = sip;
		this._delImages = delFile;
	}
	
	
	public void run() throws InterruptedException { 
		String resuFile = _sip.getOutputDir()+File.separator+"loops.txt";
		File fileResu = new File(resuFile);
		if(fileResu.exists()) fileResu.delete();
		File file = new File(_sip.getOutputDir());
		if (file.exists()==false) file.mkdir();
		Iterator<String> chrName = _sip.getChrSizeHashMap().keySet().iterator();
		int nb = 0;
		if(_sip.isGui()){
			_p = new Progress("Loop Detection step",_sip.getChrSizeHashMap().size()+1);
			_p._bar.setValue(nb);
		}
		while(chrName.hasNext()){
			String chr = chrName.next();
			CallLoops cl = new CallLoops(_sip);
			String normFile = _sip.getOutputDir()+File.separator+"normVector"+File.separator+chr+".norm";
			if (_sip.isProcessed()){
				normFile = _sip.getinputDir()+File.separator+"normVector"+File.separator+chr+".norm";
			}
			String outputDir = this._sip.getOutputDir();
			String inputDir = this._sip.getinputDir();
			String dir = outputDir+File.separator+chr+File.separator;
			if (this._sip.isProcessed()) dir = inputDir+File.separator+chr+File.separator;
			try {
				File[] listOfFiles = _sip.fillList(dir);
				System.out.println("normVector end loading file: "+chr+".norm");
				if (listOfFiles.length == 0) System.out.println("!!!!!!!!!! dumped directory of chromosome"+chr+" empty");
				else{
					HashMap<String, Loop> data = cl.detectLoops(listOfFiles,chr,_sip.testNormaVectorValue(normFile));
					if (fileResu.length() == 0)	_sip.saveFile(resuFile,data,false);
					else this._sip.saveFile(resuFile,data, true);
					listOfFiles = _sip.fillList(dir);
					if(_delImages){
						System.out.println("Deleting image file for "+chr);
						for(int i = 0; i < listOfFiles.length;++i) {
							String name = listOfFiles[i].toString();
							if(name.contains(".tif"))  listOfFiles[i].delete();
						}
					}
				}
			} catch (IOException e1) { e1.printStackTrace();}
			System.gc();
			nb++;
			if(_sip.isGui()) _p._bar.setValue(nb);
		}
		if(_sip.isGui())	_p.dispose();
	}
}
