package process;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.filter.GaussianBlur;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import utils.CoordinatesCorrection;
import utils.FindMaxima;
import utils.Loop;
import utils.SIPInter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import static process.TupleFileToImage.readTupleFileInter;

public class CallLoopsInter {

    private int _matrixSize;
    private int _resolution;
    /**
     *
     * @param sipInter
     */
    void CallLoopsInter(SIPInter sipInter){
        _matrixSize = sipInter.getMatrixSize();
        _resolution = sipInter.getResolution();
    }

    /**
     * Detect loops methods
     * detect the loops at two different resolution, initial resolution + 2 fold bigger
     * call the loops first in the smaller resolution
     * then making image with bigger resolution and fill no Zero list
     * faire un gros for deguelasse por passer les faceteur de grossissement seulement si listDefacteur > 1.
     * make and save image at two differents resolution (m_resolution and m_resolution*2)
     * if there is a lot pixel at zero in the images adapt the threshold for the maxima detection
     * @param fileList
     * @param chr
     * @param normVector
     * @return
     * @throws IOException
     */
    public HashMap<String, Loop> detectLoops(File[] fileList, String chrName1, String chrName2, HashMap<Integer,String> normVector) throws IOException{
        File folder = new File(outdir);
        File[] listOfFiles = folder.listFiles();
        CoordinatesCorrection coord = new CoordinatesCorrection();
        //System.out.println(outdir+" !!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + listOfFiles.length);
        for (int i = 0; i<listOfFiles.length; ++i) {
            String fileName = listOfFiles[i].toString();
            if(fileName.contains("txt")) {
                TupleFileToImage tuple = new  TupleFileToImage(fileName, _matrixSize, _resolution);
                ImagePlus img = tuple.readTupleFileInter();
                String imgPath = fileName;
                String [] name = fileName.split(File.separator);
                imgPath = imgPath.replace("txt", "tif");
                //System.out.println(imgPath);
                saveFile(img, imgPath);
                ImagePlus imageDiff = imgDiff(img,imgPath);
                FindMaxima findMaxima = new FindMaxima( imageDiff, chrName1,chrName2, 20, _resolution);
                HashMap<String, Loop> hloop =  coord.imageToGenomeCoordinate(findMaxima.findloopInter(5,img,3) , name[name.length-1]);
                Set<String> key = hloop.keySet();
                Iterator<String> it = key.iterator();
                while (it.hasNext()) {
                    Loop loop = hloop.get(it.next());
                    ArrayList<Integer> coord = loop.getCoordinates();
                    System.out.println(loop.getChr()+"\t"+coord.get(0)+"\t"+coord.get(1)+"\t"+loop.getChr2()+"\t"+coord.get(2)+"\t"+coord.get(3)+"\t0,0,0"
                            +"\t"+loop.getPaScoreAvg()+"\t"+loop.getRegionalPaScoreAvg()+"\t"+loop.getNeigbhoord1()+"\t"+loop.getNeigbhoord2()+"\t"+loop.getAvg()+"\t"
                            +loop.getStd()+"\t"+loop.getValue()+"\t"+loop.getNbOfZero());
                }
            }
        }
    }

        /**
         * Save the image file
         *
         * @param imagePlusInput image to save
         * @param pathFile path to save the image
         */
    public ImagePlus imgDiff(ImagePlus imagePlusInput, String pathFile){
        GaussianBlur gb = new GaussianBlur();
        ImageProcessor ip = imagePlusInput.getProcessor();
        gb.blurGaussian(ip, 2);
        FloatProcessor pRaw = new FloatProcessor(ip.getWidth(), ip.getHeight());
        //faire un gaussiane

        for(int i = 3; i < ip.getWidth()-3; ++i){
            for(int j = 3; j < ip.getWidth()-3; ++j){
                float sum = 0;
                for(int ii = i-3; ii < i+3; ++ii) {
                    for (int jj = j-3; jj < j+3; ++jj) {
                        sum = sum+(ip.getf(i,j)- ip.getf(ii,jj));
                    }
                }
                //if(sum < 0 ) sum = 0;
                pRaw.setf(i,j,sum);
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
    public void saveFile ( ImagePlus imagePlusInput, String pathFile){
        FileSaver fileSaver = new FileSaver(imagePlusInput);
        fileSaver.saveAsTiff(pathFile);
    }


}
