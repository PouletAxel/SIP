package plop.straw;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import inra.ijpb.morphology.Morphology;
import inra.ijpb.morphology.Strel;
import javastraw.reader.Dataset;
import javastraw.reader.Matrix;
import javastraw.reader.basics.Chromosome;
import javastraw.reader.block.Block;
import javastraw.reader.block.ContactRecord;
import javastraw.reader.datastructures.ListOfDoubleArrays;
import javastraw.reader.expected.ExpectedValueFunction;
import javastraw.reader.mzd.MatrixZoomData;
import javastraw.reader.norm.NormalizationVector;
import javastraw.reader.type.HiCZoom;
import javastraw.reader.type.NormalizationType;
import javastraw.tools.HiCFileTools;
import plop.loops.CallLoops;
import plop.loops.FilterLoops;
import plop.loops.Loop;
import plop.sip.SIPIntra;
import plop.utils.CoordinatesCorrection;
import plop.utils.FindMaxima;
import plop.utils.ImageProcessingMethod;
import plop.utils.PeakAnalysisScore;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class StrawRunner {


    /**	 image background value*/
    private float _backgroudValue = (float) 0.25;

    Dataset _ds;
    String _hicFileName;
    String _norm;
    SIPIntra _sip;
    int _resolution;
    int _matrixSize;
    boolean _useCache = true;
    String _resuFile;

    /**
     *
     * @param sip
     * @param hicFileName
     * @param norm
     */
    public StrawRunner (SIPIntra sip, String hicFileName, String norm){
        this._hicFileName = hicFileName;
        this._norm = norm;
        this._sip = sip;
        this._matrixSize = sip.getMatrixSize();
        this._resolution = sip.getResolution();
        String resName = String.valueOf(this._resolution);
        resName = resName.replace("000", "")+"kb";
        this._resuFile = this._sip.getOutputDir()+File.separator+resName+"Loops.txt";
    }

    /**
     *
     * @param sip
     * @param hicFileName
     * @param norm
     * @param useCache
     */
    public StrawRunner (SIPIntra sip, String hicFileName, String norm, boolean useCache, String resuFile){
        this._hicFileName = hicFileName;
        this._norm = norm;
        this._sip = sip;
        this._matrixSize = sip.getMatrixSize();
        this._resolution = sip.getResolution();
        this._useCache = useCache;
        String resName = String.valueOf(this._resolution);
        resName = resName.replace("000", "")+"kb";
        this._resuFile = this._sip.getOutputDir()+File.separator+resName+"Loops.txt";
    }

    /**
     *
     */
    public StrawRunner (){ }


    /**
     *
     */
    public void run() throws IOException {
        this._ds = HiCFileTools.extractDatasetForCLT(this._hicFileName, false, this._useCache);
        NormalizationType norm = this._ds.getNormalizationHandler().getNormTypeFromString(this._norm);
        ExpectedValueFunction expFunction = this._ds.getExpectedValues(new HiCZoom(this._resolution), norm, true);
        Chromosome[] chromosomes = this._ds.getChromosomeHandler().getChromosomeArrayWithoutAllByAll();
        readTupleFile(chromosomes,expFunction,norm);

    }

    /**
     *
     * @param chromosomes
     * @param expFunction
     * @param norm
     * @throws IOException
     */
    public void readTupleFile(Chromosome[] chromosomes,ExpectedValueFunction expFunction, NormalizationType norm) throws IOException {
        HashMap<String, Loop> loopHashMap = new HashMap<String, Loop>();
        CallLoops callLoops = new CallLoops(this._sip);
        CoordinatesCorrection coord = new CoordinatesCorrection();
        HashMap<String,Loop> hLoop= new HashMap<String,Loop>();
      //  for (Chromosome chromosome : chromosomes) {
        Chromosome chromosome = chromosomes[19];
            Matrix matrix = this._ds.getMatrix(chromosome, chromosome);
            //NormalizationVector nv = this._ds.getNormalizationVector(chromosome.getIndex(),
            //                                                       new HiCZoom(this._resolution),
            //                                                     this._ds.getNormalizationHandler().getNormTypeFromString(this._norm));
            // ListOfDoubleArrays nvArray = nv.getData();

        //if (matrix == null) continue;
            MatrixZoomData zd = matrix.getZoomData(new HiCZoom(this._resolution));
            //if (zd == null) continue;

            int chromosomeBinSize = (int)(chromosome.getLength()/this._resolution) + 1;
            int step = this._matrixSize/2;
            FilterLoops filterLoops = new FilterLoops(this._resolution);

            double[] expected = expFunction.getExpectedValuesWithNormalization(chromosome.getIndex()).getValues().get(0);
            for(int binStart = 0, numImage = 0; binStart < chromosomeBinSize; binStart+=step,++numImage){
                int binEnd = binStart + this._matrixSize;

                ImagePlus imgRaw = new ImagePlus();
                ImagePlus imgNorm = new ImagePlus();

                ShortProcessor pRaw = new ShortProcessor(this._matrixSize, this._matrixSize);
                FloatProcessor pNorm = new FloatProcessor(this._matrixSize, this._matrixSize);

                List<Block> blocks = zd.getNormalizedBlocksOverlapping(binStart, binStart, binEnd, binEnd, norm, false);
                makeImages(blocks,pRaw,pNorm,binStart,expected);
                imgRaw.setProcessor(pRaw);
                imgNorm.setProcessor(pNorm);
                int a = binStart*_resolution;
                int b = binEnd*_resolution;
                String name = chromosome.toString()+"_"+a+"_"+b;
                HashMap<String,Loop> temp = callLoops.detectLoops(imgRaw, imgNorm, name, chromosome.toString(), numImage);
                coord.setData(hLoop);
                coord.imageToGenomeCoordinate(temp, numImage,step);
                hLoop = coord.getData();

            }
            if(this._useCache){
                zd.clearCache();
            }
       // }
        System.out.println("####### Endloops detection for chr "+ chromosome.toString() +"\t"+hLoop.size()+" loops before the FDR filter");
        this._sip.saveFile(this._resuFile,hLoop, true);
    }


    /**
     *
     * @param blocks
     * @param pRaw
     * @param pNorm
     * @param binStart
     * @param expected
     */
    private void makeImages(List<Block> blocks, ShortProcessor pRaw, FloatProcessor pNorm, int binStart, double[] expected) {
        for (Block b : blocks) {
            if (b != null) {
                for (ContactRecord rec : b.getContactRecords()) {
                    if (rec.getCounts() > 0) {
                        // only called for small regions - should not exceed int
                        int relativeX = (rec.getBinX() - binStart);
                        int relativeY = (rec.getBinY() - binStart);
                        if (relativeX >= 0 && relativeX < this._matrixSize) {
                            if (relativeY >= 0 && relativeY < this._matrixSize) {
                                int dist = Math.abs(rec.getBinX() - rec.getBinY());
                                float oe = (float) ((rec.getCounts() + 1) / (expected[dist] + 1));
                                int raw = (int) (rec.getCounts() - expected[dist]);
                                if (raw < 0) raw = 0;
                                pRaw.set(relativeX, relativeY, raw);
                                pRaw.set(relativeY, relativeX, raw);
                                pNorm.setf(relativeX, relativeY, oe);
                                pNorm.setf(relativeY, relativeX, oe);
                            }
                        }
                    }
                }
            }
        }
    }

}
