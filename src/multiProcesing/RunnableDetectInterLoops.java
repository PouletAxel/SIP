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
    private SIPInter _sip;
    /**
     * CallLoops object
     */
    private CallLoopsInter _callLoops;
    /**
     * String results file
     */
    private String _resuFile;
    /**
     * String name of the chr
     */
    private String _chr;
    /**
     * String name of the chr
     */
    private boolean _delImages = true;
    /**
     * norn vector table for the chr of interest
     */
    private HashMap<Integer, String> _normVector = new HashMap<Integer, String>();


    /**
     * Construtor, initialize all the variables  of interest
     */
    public RunnableDetectInterLoops(String chr, String resuFile, SIPInter sip,  boolean delFile) {
        this._sip = sip;
        this._callLoops = new CallLoopsInter();
        this._chr = chr;
        this._resuFile = resuFile;
        this._delImages = delFile;
    }




    /**
     * Run all the process for loops detection by chr using the objet CallLoops and then save loops in
     * txt file with SIPObject via he method saveFile
     */
    public void run() {

    }
}
