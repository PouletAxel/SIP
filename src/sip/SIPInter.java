package sip;

import utils.FDR;
import loops.Loop;

import java.io.*;
import java.util.*;

/**
 *
 *
 * @author axel poulet
 *
 */

public class SIPInter  extends SIPObject {


    /**
     *
     *  constructor for hic file
     *
     * @param output output path
     * @param chrSize chr size path
     * @param gauss gaussian filter strength
     * @param resolution bins size
     * @param thresholdMax threshold value for loops detection
     * @param matrixSize image size
     * @param nbZero nb of zero allowed around a loop
     * @param fdr fdr value for the final loops filter
     * @throws IOException exception
     */
    public SIPInter(String output,String chrSize, double gauss,  int resolution, double thresholdMax, int matrixSize, int nbZero,double fdr,
      boolean delTif, int cpu) throws IOException {
        super(output, gauss, resolution, thresholdMax,matrixSize, nbZero,  fdr,chrSize, delTif, cpu) ;

    }

    /**
     *  constructor for processed data
     *
     * @param input input file with SIP file
     * @param output output path
     * @param chrSize chr size path
     * @param gauss gaussian filter strength
     * @param resolution bins size
     * @param thresholdMax threshold value for loops detection
     * @param matrixSize image size
     * @param nbZero nb of zero allowed around a loop
     * @param fdr fdr value for the final loops filter
     * @throws IOException exception
     */
    public SIPInter(String input,String output,String chrSize, double gauss, int resolution,
                 double thresholdMax, int matrixSize, int nbZero, double fdr,   boolean delTif, int cpu){

        super(input, output,  gauss, resolution, thresholdMax, matrixSize, nbZero,  fdr, chrSize, delTif, cpu);


    }

    /**
     * Write detected loops after filtering via the fdr value
     * @param pathFile path for the output file
     * @param hLoop hashmap loopsName => Loop object
     * @param first boolean if true it is the first results so need to write the header
     * @throws IOException exception
     */
    public void writeResu(String pathFile, HashMap<String, Loop> hLoop, boolean first) throws IOException {
        double fdr = this.getFdr();
        FDR fdrDetection = new FDR (fdr, hLoop);
        fdrDetection.run();
        double RFDRcutoff = fdrDetection.getRFDRCutoff();
        double FDRcutoff = fdrDetection.getFDRCutoff();
        System.out.println("Filtering value at "+fdr+" FDR is "+FDRcutoff+" APscore and "+RFDRcutoff+" RegionalAPscore\n");
        BufferedWriter writer;
        if(first) writer = new BufferedWriter(new FileWriter(new File(pathFile), true));
        else{
            writer = new BufferedWriter(new FileWriter(new File(pathFile)));
            writer.write("chromosome1\tx1\tx2\tchromosome2\ty1\ty2\tcolor\tAPScoreAvg\tAPRegScoreAvg\tAvg_diffMaxNeighbour_1\tAvg_diffMaxNeighbour_2\tavg\tstd\tvalue\tvalueDiff\tnbOfZero\tProbabilityofEnrichment\n");
        }

        if(hLoop.size()>0) {
            Set<String> key = hLoop.keySet();
            Iterator<String> it = key.iterator();
            while (it.hasNext()) {
                Loop loop = hLoop.get(it.next());
                ArrayList<Integer> coord = loop.getCoordinates();
                if (loop.getPaScoreAvg() > FDRcutoff && loop.getRegionalPaScoreAvg() > RFDRcutoff && loop.getValueDiff() > 1.3 && loop.getValue() >= 8) {
                    writer.write(loop.getChr() + "\t" + coord.get(0) + "\t" + coord.get(1) + "\t" + loop.getChr2() + "\t" + coord.get(2) + "\t" + coord.get(3) + "\t0,0,0"
                            + "\t" + loop.getPaScoreAvg() + "\t" + loop.getRegionalPaScoreAvg() + "\t" + loop.getNeigbhoord1() + "\t" + loop.getNeigbhoord2() + "\t" + loop.getAvg() + "\t"
                            + loop.getStd() + "\t" + loop.getValue() + "\t" +loop.getValueDiff()  + "\t" + loop.getNbOfZero() +"\t"+loop.getPaScoreAvgdev()+"\n");
                }
            }
        }
        writer.close();
    }
}


