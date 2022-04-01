package plop.test;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.FloatProcessor;
import ij.process.ShortProcessor;
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
import plop.straw.StrawRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TestStraw {
    public static void main(String[] args) throws IOException, InterruptedException {
        ArrayList<String> listOfFile = new ArrayList<String> ();
        String hicFileName = "/home/plop/Desktop/4DNFI1UEG1HD.hic";
        StrawRunner sr = new StrawRunner();
        makeImage(hicFileName,"KR",5000, 2000, "/home/plop/Desktop/testStraw");

    }

    /**
     *
     * @param hicFileName
     * @param userSpecifiedNorm
     * @param resolution
     * @param matrixWidth
     */
    static private void makeImage(String hicFileName, String userSpecifiedNorm, int resolution, int matrixWidth,String outputDir) {
        boolean useCache = true;
        Dataset ds = HiCFileTools.extractDatasetForCLT(hicFileName, false, useCache);
        NormalizationType norm = ds.getNormalizationHandler().getNormTypeFromString(userSpecifiedNorm);
        //NormalizationType norm = NormalizationPicker.getFirstValidNormInThisOrder(ds, new String[]{"KR", "SCALE", "VC", "VC_SQRT", "NONE"});
        //System.out.println("Norm being used: " + norm.getLabel());
        ExpectedValueFunction expFunction = ds.getExpectedValues(new HiCZoom(resolution), norm, true);
        Chromosome[] chromosomes = ds.getChromosomeHandler().getChromosomeArrayWithoutAllByAll();
        Chromosome chromosome = chromosomes[20];
        Matrix matrix = ds.getMatrix(chromosome,chromosome);

        MatrixZoomData zd = matrix.getZoomData(new HiCZoom(resolution));
        int chromosomeBinSize = (int) (chromosome.getLength() / resolution) + 1;
        int step = matrixWidth / 2;
        FilterLoops filterLoops = new FilterLoops(resolution);
        double[] expected = expFunction.getExpectedValuesWithNormalization(chromosome.getIndex()).getValues().get(0);
        for (int binStart = 0; binStart < chromosomeBinSize; binStart += step) {
            int binEnd = binStart + matrixWidth;
            System.out.println("chr: " + chromosome.toString()+" start "+binStart+" end "+binEnd);
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

                imgRaw.setProcessor(pRaw);
                imgNorm.setProcessor(pNorm);
                String pathFile = outputDir+ File.separator+"21_"+binStart+"_raw.tiff";
                FileSaver fileSaver = new FileSaver(imgRaw);
                fileSaver.saveAsTiff(pathFile);
                pathFile = outputDir+ File.separator+"21_"+binStart+"_Norm.tiff";
                fileSaver = new FileSaver(imgNorm);
                fileSaver.saveAsTiff(pathFile);


                //try {
                //detectLoops(imgRaw, imgNorm, matrixWidth, chromosome, resolution, filterLoops);
                //int genomeStart = binStart * resolution;


                //} catch (IOException e) {
                //   e.printStackTrace();
                //}
            }
            if (useCache) {
                zd.clearCache();
            }
        }
    //}
}
