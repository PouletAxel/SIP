package plop.multiProcessing;

import java.io.File;
import java.io.IOException;

import plop.dumpProcessing.CoolerDumpInter;

/**
 *
 * @author axel poulet
 *
 */
public class RunnableDumpCoolerInter extends Thread implements Runnable{
    /** path outdir */
    private String _outDir;
    /** chr1 name */
    private String _chr1;
    /** chr2 name*/
    private String _chr2;
    /** size chr1*/
    private int _sizeChr1;
    /** size chr2 */
    private int _sizeChr2;
    /** Object HicDumpInter*/
    private CoolerDumpInter _dumpInter;
    /** bin size*/
    private int _resolution;
    /** image size*/
    private int _matrixSize;


    /**
     * Constructor, initialize the variables of interest
     */
    public RunnableDumpCoolerInter(String outputDir, String chr1, int sizeChr1, String chr2, int sizeChr2, CoolerDumpInter dumpData, int res, int matrixSize){
        this._outDir = outputDir;
        this._sizeChr1 = sizeChr1;
        this._sizeChr2 = sizeChr2;
        this._chr1 = chr1;
        this._chr2 = chr2;
        this._resolution = res;
        this._matrixSize = matrixSize;
        this._dumpInter = dumpData;
    }

    /**
     * Dump the data by chr for mcool data
     * using cooler and cooltools
     */
    public void run(){
        int step = _matrixSize;
        String nameRes = String.valueOf(_resolution);
        nameRes = nameRes.replace("000", "");
        nameRes = nameRes+"kb";
        String outdir = _outDir+nameRes+ File.separator+_chr1+"_"+_chr2+File.separator;
        File file = new File(outdir);
        if (!file.exists())
            file.mkdirs();
        step = step*_resolution;
        System.out.println("start dump "+_chr1+" size "+_sizeChr1+" "+_chr2+" size "+_sizeChr2+" res "+ nameRes);
        int endChr1 = _matrixSize*_resolution;
        if(endChr1 > _sizeChr1) endChr1 = _sizeChr1;
        try {
            for(int startChr1 = 0 ; endChr1-1 <= _sizeChr1; startChr1+=step,endChr1+=step){
                int endChr2 = _matrixSize*_resolution;
                if(endChr2 > _sizeChr2) endChr2 = _sizeChr2;
                int end1 =endChr1-1;

                //hgdhdhgd
                String dump1 = _chr1+":"+startChr1+"-"+end1;
                for(int startChr2 = 0 ; endChr2-1 <= _sizeChr2; startChr2+=step,endChr2+=step) {
                    int end2 =endChr2-1;
                    //
                    String dump2 = _chr2+":"+startChr2+"-"+end2;
                    String name = outdir + _chr1 +"_" + startChr1 + "_" + end1 +"_" +_chr2 +"_" + startChr2 + "_" + end2 + ".txt";
                    _dumpInter.dumpObserved(dump1, dump2, name, _resolution);
                    if (endChr2 + step > _sizeChr2 && endChr2 < _sizeChr2) {
                        endChr2 = _sizeChr2-1;
                        startChr2 += step;
                        dump2 = _chr2+":"+startChr2+"-"+endChr2;
                        name = outdir + _chr1 +"_" + startChr1 + "_" + end1 +"_" +_chr2 +"_" + startChr2 + "_" + endChr2 + ".txt";
                        _dumpInter.dumpObserved(dump1, dump2, name, _resolution);
                    }
                }
                if (endChr1 + step > _sizeChr1 && endChr1 < _sizeChr1) {
                    endChr1 = _sizeChr1-1;
                    startChr1 += step;
                    dump1 = _chr1+":"+startChr1+"-"+endChr1;
                    endChr2 = _matrixSize*_resolution;
                    for(int startChr2 = 0 ; endChr2-1 <= _sizeChr2; startChr2+=step,endChr2+=step) {
                        int end2 =endChr2-1;
                        String dump2 = _chr2+":"+startChr2+"-"+end2;
                        String name = outdir + _chr1 +"_" + startChr1 + "_" + endChr1 +"_" +_chr2 +"_" + startChr2 + "_" + end2 + ".txt";
                        _dumpInter.dumpObserved(dump1, dump2, name, _resolution);
                        if (endChr2 + step > _sizeChr2 && endChr2 < _sizeChr2) {
                            endChr2 = _sizeChr2-1;
                            startChr2 += step;
                            dump2 = _chr2+":"+startChr2+"-"+endChr2;
                            name = outdir + _chr1 +"_" + startChr1 + "_" + endChr1 +"_" +_chr2 +"_" + startChr2 + "_" + endChr2 + ".txt";
                            _dumpInter.dumpObserved(dump1, dump2, name, _resolution);
                        }
                    }
                }
            }
            System.gc();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
