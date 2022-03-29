package plop.loops;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.filter.GaussianBlur;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import plop.utils.CoordinatesCorrection;
import plop.utils.FindMaxima;
import plop.utils.PeakAnalysisScore;
import plop.utils.TupleFileToImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


public class CallLoopsInter {

    private int _matrixSize;
    private int _resolution;
    private double _noiseTolerance;
    private double _gaussian;
    private int _nbZero;
    private boolean _cooler;



    /**
     * Detect java.plop.loops methods
     *
     * @param fileList list with tuple file
     * @param chrName1 name of chr 1
     * @param chrName2 name of chr 2
     * @return HashMap name loop => Loop Object
     * @throws IOException exception
     */
    public HashMap<String, Loop> detectLoops(File[] fileList, String chrName1, String chrName2){
        CoordinatesCorrection coord = new CoordinatesCorrection();
        //System.out.println(outdir+" !!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + listOfFiles.length);
        HashMap<String, Loop> hLoop = new  HashMap<> ();
        for (int i = 0; i<fileList.length; ++i) {
            String fileName = fileList[i].toString();
            if(fileName.contains("txt")) {
                TupleFileToImage tuple = new  TupleFileToImage(fileName, _matrixSize, _resolution);
                ImagePlus img = new ImagePlus();
                if(_cooler){
                    img = tuple.readTupleFileInterCool();
                }else
                    img = tuple.readTupleFileInter();


                String imgPath = fileName;
                String [] name = fileName.split(File.separator);
                imgPath = imgPath.replace("txt", "tif");
                //System.out.println(imgPath);
                saveFile(img, imgPath);
                GaussianBlur gb = new GaussianBlur();
                gb.blurGaussian(img.getProcessor(), _gaussian);

                ArrayList<Double> col = computeSumCol(img);
                ArrayList<Double> row = computeSumRow(img);

                ImagePlus imageDiff = imgDiff(img,imgPath);
                FindMaxima findMaxima = new FindMaxima( imageDiff, chrName1,chrName2, _noiseTolerance, _resolution,_gaussian,_nbZero);
                HashMap<String, Loop> temp = findMaxima.findLoopInter(imgPath);
                filterLoop(col,row,temp);
                PeakAnalysisScore pas = new PeakAnalysisScore(img,temp);
                pas.computeScore();
                coord.setData(hLoop);
                coord.imageToGenomeCoordinate(temp,name[name.length-1]);
                hLoop = coord.getData();
                //System.out.println(hLoop.size()+" temp "+ temp.size());
            }
        }
        return hLoop;
    }

    /**
     * Create and save the diff image.
     * for each pixel compute the new value computing the average subtraction between the pixel of interest and all
     * pixel inside the neighbor 3
     *
     * @param imagePlusInput imagePlus raw
     * @param pathFile path to the imagePlus raw
     * @return  the diff ImagePlus
     */
    private ImagePlus imgDiff(ImagePlus imagePlusInput, String pathFile){

        ImageProcessor ip = imagePlusInput.getProcessor();



        FloatProcessor ipDiff = new FloatProcessor(ip.getWidth(), ip.getHeight());

        int nbPixel = 48;
        int x = 3;

        for(int i = 0; i < ip.getWidth(); ++i){
            for(int j = 0; j < ip.getHeight(); ++j){
                float sum = 0;
                float valueA = ip.getf(i, j);
                if (Double.isNaN(ip.getf(i, j))) valueA = 0;
                for (int ii = i - x; ii < i + x; ++ii) {
                    for (int jj = j - x; jj < j + x; ++jj) {
                        if ((i != ii || j != jj) && ii >= 0 && jj >= 0 && ii < ip.getWidth() && jj < ip.getHeight()) {
                            float valueB = ip.getf(ii, jj);
                            if (Double.isNaN(ip.getf(ii, jj))) valueB = 0;
                            sum = sum + (valueA - valueB);
                        }
                    }
                }ipDiff.setf(i,j,sum/nbPixel);
            }
        }

        ImagePlus img = new ImagePlus();
        img.setProcessor(ipDiff);

        pathFile = pathFile.replace(".tif","_diff.tif");
        saveFile(img, pathFile);
        return img;

    }

    private void filterLoop(ArrayList<Double> col , ArrayList<Double> row, HashMap<String, Loop> temp) {

        double threshLowCol = this.computeAvg(col) - this.computeStdDev(col, this.computeAvg(col)) * 1.5;
        double threshHighCol = this.computeAvg(col) + this.computeStdDev(col, this.computeAvg(col));
        double threshLowRow = this.computeAvg(row) - this.computeStdDev(row, this.computeAvg(row)) * 1.5;
        double threshHighRow = this.computeAvg(row) + this.computeStdDev(row, this.computeAvg(row));
        System.out.println(  threshLowCol + "\t" + threshHighCol + "\t" + threshLowRow + "\t" + threshHighRow);
        Set<String> arrayKey = temp.keySet();
        Iterator<String> it = arrayKey .iterator();

        while (it.hasNext()) {
            String key = it.next();
            Loop loop = temp.get(key);
            int i = loop.getX();
            int j = loop.getY();
            if (col.get(i) > threshLowCol && col.get(i) < threshHighCol && row.get(j) > threshLowRow && row.get(j) < threshHighRow) {
            }
        }
    }

    /**
     *
     * @param plop
     * @return
     */
    private double computeAvg(ArrayList<Double> plop){
        double sum = 0;
        for(int i = 0; i < plop.size(); ++i){
            sum = sum + plop.get(i);
        }
        return sum/plop.size();
    }


    /**
     *
     * @param plop
     * @param avg
     * @return
     */
    private double computeStdDev(ArrayList<Double> plop, double avg){
        double sum = 0;
        for(int i = 0; i < plop.size(); ++i){
            sum = sum + (plop.get(i)-avg)*(plop.get(i)-avg);
        }
        return Math.sqrt(sum/plop.size());
    }


    /**
     *
     * @param
     * @return
     */
    ArrayList<Double> computeSumRow(ImagePlus img){
        ImageProcessor ip = img.getProcessor();
        ArrayList<Double> plop = new ArrayList<>();
        for(int i = 0; i < ip.getHeight(); ++i) {
            double sum = 0;
            for (int j = 0; j < ip.getWidth(); ++j) {
                double valueA = ip.getf(j, i);
                if (Double.isNaN(ip.getf(j, i))) valueA = 0;
                sum = sum + valueA ;
            }
            plop.add(sum);
        }
        return plop;
    }

    /**
     *
     * @param
     * @return
     */
    ArrayList<Double> computeSumCol(ImagePlus img){
        ImageProcessor ip = img.getProcessor();
        ArrayList<Double> plop = new ArrayList<>();
        for(int i = 0; i < ip.getWidth(); ++i) {
            double sum = 0;
            for (int j = 0; j < ip.getHeight(); ++j) {
                double valueA = ip.getf(i, j);
                if (Double.isNaN(ip.getf(i, j))) valueA = 0;
                sum = sum + valueA ;
            }
            plop.add(sum);
        }
        return plop;
    }

    /**
     * Save the image file
     *
     * @param imagePlusInput image to save
     * @param pathFile path to save the image
     */
    private void saveFile ( ImagePlus imagePlusInput, String pathFile){
        FileSaver fileSaver = new FileSaver(imagePlusInput);
        fileSaver.saveAsTiff(pathFile);
    }


}
