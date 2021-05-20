package plop.multiProcessing;

import plop.loops.CallLoopsInter;
import plop.loops.Loop;
import plop.sip.SIPInter;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 *  runnable class for inter java.plop.loops detection
 *
 */
public class RunnableDetectInterLoops extends Thread implements Runnable {

    /*** SIP object containing all the parameter for the java.plop.loops detection */
    private SIPInter _sipInter;
    /** CallLoops object */
    private CallLoopsInter _callLoopsInter;
    /** String results file */
    private String _resuFile;
    /** String name of the chr1 */
    private String _chr1;
   /** String chr name 2  */
    private String _chr2;


    /**
     *  Construtor, initialize all the variables  of interest
     *
     * @param chr1 chr1 name
     * @param chr2 chr2 name
     * @param resuFile path output file
     * @param sip SIPInter object
     */
    public RunnableDetectInterLoops(String chr1, String chr2, String resuFile, SIPInter sip) {
        this._sipInter = sip;
        this._callLoopsInter = new CallLoopsInter(_sipInter);
        this._chr1 = chr1;
        this._chr2 = chr2;
        this._resuFile = resuFile;
    }




    /**
     * Run all the java.plop.process for java.plop.loops detection by chr using the objet CallLoops and then save java.plop.loops in
     * txt file with SIPIntra via he method saveFile
     */
    public void run() {
        String resName = String.valueOf(this._sipInter.getResolution());
        resName = resName.replace("000", "")+"kb";
        String dir =  this._sipInter.getInputDir()+resName+File.separator+_chr1+"_"+_chr2+File.separator;
        HashMap<String, Loop> data;
        if (this._sipInter.isProcessed()) dir = this._sipInter.getInputDir()+resName+File.separator+_chr1+"_"+_chr2+File.separator;
        try {
            File folder = new File(dir);

            File[] listOfFiles = folder.listFiles();
            if(folder.exists()){
                System.out.println("yupyup !!!"+dir);
            }else{
                System.out.println("yupyup !!!"+folder.toString());
            }
            System.out.println(dir+" "+listOfFiles.length);
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
                if(_sipInter.isDelImage()){
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
