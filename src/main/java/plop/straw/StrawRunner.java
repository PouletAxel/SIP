package plop.straw;

import ij.ImagePlus;
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
import javastraw.reader.expected.ExpectedValueFunction;
import javastraw.reader.mzd.MatrixZoomData;
import javastraw.reader.type.HiCZoom;
import javastraw.reader.type.NormalizationType;
import javastraw.tools.HiCFileTools;
import plop.loops.FilterLoops;
import plop.loops.Loop;
import plop.utils.FindMaxima;
import plop.utils.ImageProcessingMethod;
import plop.utils.PeakAnalysisScore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class StrawRunner {


    /** Strength of the gaussian filter*/
    private double _gauss;
    /** Strength of the min filter*/
    private double _min;
    /** Strength of the max filter*/
    private double _max;
    /** % of staurated pixel after enhance contrast*/
    private double _saturatedPixel;
    /** Threshold for the maxima detection*/
    private int _thresholdMaxima;
    /** Diage size to removed maxima close to diagonal*/
    private int _diagSize;
    /** Number of pixel = 0 allowed around the loop*/
    private int _nbZero = -1;
    /**	 struturing element for the MM method used (MorpholibJ)*/
    private Strel _strel = Strel.Shape.SQUARE.fromRadius(40);
    /**	 image background value*/
    private float _backgroudValue = (float) 0.25;


    public StrawRunner (String hicFileName, String userSpecifiedNorm, int matrixWidth, int resolution){

    }

    public StrawRunner (){

    }

    /**
     *
     * @param hicFileName
     * @param userSpecifiedNorm
     * @param matrixWidth
     * @param resolution
     */
    public void readTupleFile(String hicFileName, String userSpecifiedNorm, int matrixWidth, int resolution){
        boolean useCache = true;
        Dataset ds = HiCFileTools.extractDatasetForCLT(hicFileName, false, useCache);
        NormalizationType norm = ds.getNormalizationHandler().getNormTypeFromString(userSpecifiedNorm);
        //NormalizationType norm = NormalizationPicker.getFirstValidNormInThisOrder(ds, new String[]{"KR", "SCALE", "VC", "VC_SQRT", "NONE"});
        //System.out.println("Norm being used: " + norm.getLabel());
        ExpectedValueFunction expFunction = ds.getExpectedValues(new HiCZoom(resolution), norm, true);
        Chromosome[] chromosomes = ds.getChromosomeHandler().getChromosomeArrayWithoutAllByAll();

        for (Chromosome chromosome : chromosomes) {
            Matrix matrix = ds.getMatrix(chromosome, chromosome);
            if (matrix == null) continue;
            MatrixZoomData zd = matrix.getZoomData(new HiCZoom(resolution));
            if (zd == null) continue;

            int chromosomeBinSize = (int)(chromosome.getLength()/resolution) + 1;
            int step = matrixWidth/2;
            FilterLoops filterLoops = new FilterLoops(resolution);

            double[] expected = expFunction.getExpectedValuesWithNormalization(chromosome.getIndex()).getValues().get(0);


            for(int binStart = 0; binStart < chromosomeBinSize; binStart+=step){
                int binEnd = binStart + matrixWidth;

                ImagePlus imgRaw = new ImagePlus();
                ImagePlus imgNorm = new ImagePlus();

                ShortProcessor pRaw = new ShortProcessor(matrixWidth, matrixWidth);
                FloatProcessor pNorm = new FloatProcessor(matrixWidth, matrixWidth);

                List<Block> blocks = zd.getNormalizedBlocksOverlapping(binStart, binStart, binEnd, binEnd, norm, false);
                for (Block b : blocks) {
                    if (b != null) {
                        for (ContactRecord rec : b.getContactRecords()) {

                            if (rec.getCounts() > 0) {
                                // only called for small regions - should not exceed int
                                int relativeX = (rec.getBinX() - binStart);
                                int relativeY = (rec.getBinY() - binStart);
                                if (relativeX >= 0 && relativeX < matrixWidth) {
                                    if (relativeY >= 0 && relativeY < matrixWidth) {

                                        int dist = Math.abs(rec.getBinX() - rec.getBinY());

                                        float oe = (float) ((rec.getCounts()+1)/(expected[dist]+1));
                                        int raw = (int) (rec.getCounts() - expected[dist]);

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

                imgRaw.setProcessor(pRaw);
                imgNorm.setProcessor(pNorm);


                //try {
                    //detectLoops(imgRaw, imgNorm, matrixWidth, chromosome, resolution, filterLoops);
                    //int genomeStart = binStart * resolution;


                //} catch (IOException e) {
                 //   e.printStackTrace();
                //}


            }
            if(useCache){
                zd.clearCache();
            }
        }
    }


    /*public HashMap<String, Loop> detectLoops(ImagePlus imgRaw, ImagePlus imgNorm,
                                             int matrixSize, Chromosome chromosome,
                                             int resolution, FilterLoops filterLoops) throws IOException{
        ImagePlus imgFilter = imgRaw.duplicate();
        QuickStats stats = correctImage(imgFilter);
        ImageProcessingMethod m = new ImageProcessingMethod(imgFilter,this._min,this._max,this._gauss);
        imageProcessing(imgFilter, m);
        int thresh = this._thresholdMaxima;
        double pixelPercent = 100*stats.numPosPixels/(matrixSize*matrixSize);
        if(pixelPercent < 7)
            thresh =  _thresholdMaxima/5;
        FindMaxima findLoop = new FindMaxima(imgNorm, imgFilter, chromosome.getName(),
                thresh, this._diagSize, resolution);
        HashMap<String,Loop> temp = findLoop.findloop(numImage, this._nbZero,imgRaw, this._backgroudValue);
        PeakAnalysisScore pas = new PeakAnalysisScore(imgNorm,temp);
        pas.computeScore();

        temp = filterLoops.removedBadLoops(temp);
        coord.setData(hLoop);
        coord.imageToGenomeCoordinate(temp, numImage,_step);
        hLoop = coord.getData();
        hLoop = filterLoops.removedLoopCloseToWhiteStrip(hLoop,normVector);
        System.out.println("####### End loops detection for chr "+ chromosome.getName() +"\t"+hLoop.size()+" loops before the FDR filter");
        return hLoop;
    }*/



    private void imageProcessing(ImagePlus imgFilter, ImageProcessingMethod pm){
        pm.enhanceContrast(this._saturatedPixel);
        pm.runGaussian();
        imgFilter.setProcessor(Morphology.whiteTopHat(imgFilter.getProcessor(), _strel));
        pm.setImg(imgFilter);
        pm.runGaussian();
        pm.runMin(this._min);
        pm.runMax(this._max);
        pm.runMax(this._max);
        pm.runMin(this._min);
    }


    /**
     * Method to correct the image, remove the high value close to the diagonal to allow
     * the dection of the structure of interest
     * @param img ImagePlus to correct
     */
    public QuickStats correctImage(ImagePlus img){
        ImageProcessor ip = img.getProcessor();
        int numPosPixels = 0;
        float sum = 0;
        for(int i = 0; i < ip.getWidth(); ++i){
            for(int j = 0; j < ip.getWidth(); ++j){
                if(ip.getf(i, j) > 0){
                    ++numPosPixels;
                    sum += ip.getf(i, j);
                }
            }
        }
        float avg = sum/numPosPixels;
        float std = std(avg,img);
        retainSpecificValues(img, ip, avg, std);
        return new QuickStats(numPosPixels, avg, std, sum);
    }

    public static void retainSpecificValues(ImagePlus img, ImageProcessor ip, float _avg, float _std) {
        for(int i = 0; i < ip.getWidth(); ++i){
            for(int j = 0; j < ip.getWidth(); ++j){
                float a = ip.getf(i, j);
                if (Math.abs(j-i) <= 2 && a >= _avg+_std*2)
                    ip.setf(i,j,_avg);
            }
        }
        img.setProcessor(ip);
    }

    /**
     * Compute the standard deviation of the pixel non zero values of m_img
     * @param mean average value in m_img
     * @param img ImagePlus
     * @return double satndard deivation
     */
    private float std(float mean,ImagePlus img){
        float semc = 0;
        ImageProcessor ip = img.getProcessor();
        int noZeroPixel = 0;
        for(int i = 0; i < ip.getWidth(); ++i){
            for(int j = 0; j < ip.getWidth(); ++j){
                if(ip.getPixel(i, j) > 0){
                    ++noZeroPixel;
                    semc += (ip.getf(i, j)-mean)*(ip.getf(i, j)-mean);
                }
            }
        }
        semc = (float) Math.sqrt(semc/noZeroPixel);
        return semc;
    }
}
