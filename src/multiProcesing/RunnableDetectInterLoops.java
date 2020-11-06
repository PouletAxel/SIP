package multiProcesing;

import process.CallLoopsInter;
import utils.Loop;
import utils.SIPInter;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;


public class RunnableDetectInterLoops extends Thread implements Runnable {

    /**
     * SIP object containing all the parameter for the loops detection
     */
    private SIPInter _sipInter;
    /**
     * CallLoops object
     */
    private CallLoopsInter _callLoopsInter;
    /**
     * String results file
     */
    private String _resuFile;
    /**
     * String name of the chr
     */
    private String _chr1;
   /**
    *
    */
    private String _chr2;
    /**
     * String name of the chr
     */
    private boolean _delImages = true;
    /**
     * norn vector table for the chr of interest
     */

    /**
     * Construtor, initialize all the variables  of interest
     */
    public RunnableDetectInterLoops(String chr1, String chr2, String resuFile, SIPInter sip,  boolean delFile) {
        this._sipInter = sip;
        this._callLoopsInter = new CallLoopsInter(_sipInter);
        this._chr1 = chr1;
        this._chr2 = chr2;
        this._resuFile = resuFile;
        this._delImages = delFile;
    }




    /**
     * Run all the process for loops detection by chr using the objet CallLoops and then save loops in
     * txt file with SIPObject via he method saveFile
     */
    public void run() {
        String resName = String.valueOf(this._sipInter.getResolution());
        resName = resName.replace("000", "")+"kb";
        String dir =  this._sipInter.getOutputDir()+resName+File.separator+_chr1+"_"+_chr2+File.separator;
        HashMap<String, Loop> data;
        if (this._sipInter.isProcessed()) dir = this._sipInter.getInputDir()+resName+File.separator+_chr1+"_"+_chr2+File.separator;
        try {
            File folder = new File(dir);
            File[] listOfFiles = folder.listFiles();
            System.out.println(dir);
            if (listOfFiles.length == 0) System.out.println("!!!!!!!!!! dumped directory of chromosome"+this._chr1+"_"+_chr2+"empty");
            else{
                File file = new File(this._resuFile);
               /* if(_sipInter.isCooler() == false) {
                    System.out.println(_normvectorFile+"normVector end loading file: "+_chr+".norm "+resName);
                }*/
                data = this._callLoopsInter.detectLoops(listOfFiles,this._chr1, this._chr2);
                System.out.println(data.size()+"!!!!!!!!!!!!!!!! "+this._chr1+" "+this._chr2);
                synchronized(this) {
                    if (file.length() == 0)	_sipInter.writeResu(this._resuFile,data,false);
                    else this._sipInter.writeResu(this._resuFile,data,true);
                }
                folder = new File(dir);
               listOfFiles = folder.listFiles();
                if(_delImages){
                    System.out.println("Deleting image file for "+_chr1+"_"+_chr2);
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
