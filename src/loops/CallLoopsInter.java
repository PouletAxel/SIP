package loops;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.filter.GaussianBlur;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import sip.SIPInter;
import utils.CoordinatesCorrection;
import utils.FindMaxima;
import utils.PeakAnalysisScore;
import utils.TupleFileToImage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;



public class CallLoopsInter {

    private int _matrixSize;
    private int _resolution;
    private double _noiseTolerance;
    private double _gaussian;

    /**
     *
     * @param sipInter
     */
    public CallLoopsInter(SIPInter sipInter){
        _matrixSize = sipInter.getMatrixSize();
        _resolution = sipInter.getResolution();
        _noiseTolerance= sipInter.getThresholdMaxima();
        _gaussian = sipInter.getGauss();
    }

    /**
     * Detect loops methods
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
                ImagePlus img = tuple.readTupleFileInter();
                String imgPath = fileName;
                String [] name = fileName.split(File.separator);
                imgPath = imgPath.replace("txt", "tif");
                //System.out.println(imgPath);
                saveFile(img, imgPath);
                ImagePlus imageDiff = imgDiff(img,imgPath);
                FindMaxima findMaxima = new FindMaxima( imageDiff, chrName1,chrName2, _noiseTolerance, _resolution,_gaussian);
                HashMap<String, Loop> temp = findMaxima.findLoopInter(imgPath);
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
        GaussianBlur gb = new GaussianBlur();
        ImageProcessor ip = imagePlusInput.getProcessor();
        gb.blurGaussian(ip, _gaussian);
        FloatProcessor pRaw = new FloatProcessor(ip.getWidth(), ip.getHeight());
        for(int i = 3; i < ip.getWidth()-3; ++i){
            for(int j = 3; j < ip.getWidth()-3; ++j){
                float sum = 0;
                for(int ii = i-3; ii < i+3; ++ii) {
                    for (int jj = j-3; jj < j+3; ++jj){
                        if(i!=ii || j!= jj) {
                            float valueA = ip.getf(i, j);
                            float valueB = ip.getf(ii, jj);
                            if (Double.isNaN(ip.getf(i, j)))  valueA = 0;
                            if (Double.isNaN(ip.getf(ii, jj)))  valueB = 0;
                            sum = sum + (valueA - valueB);
                        }
                    }
                }
                pRaw.setf(i,j,sum/48);
            }
        }
        ImagePlus img = new ImagePlus();
        img.setProcessor(pRaw);

        pathFile = pathFile.replace(".tif","_diff.tif");
        saveFile(img, pathFile);
        return img;

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
