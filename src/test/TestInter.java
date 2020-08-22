package test;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.filter.GaussianBlur;
import ij.plugin.filter.MaximumFinder;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import process.DumpInterChromosomal;
import utils.CoordinatesCorrection;
import utils.FindMaxima;
import utils.Loop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class TestInter {

    static ArrayList <String> _chr= new ArrayList<String>();

    public static void main(String[] args) throws IOException, InterruptedException {
        //String chr = "chr21";
        String input = "/home/plop/Desktop/SIP/GSE104333_Rao-2017-treated_6hr_combined_30.hic";
        String output = "/home/plop/Desktop/testInter/";
        String fileChr = "/home/plop/Desktop/SIP/w_hg19.sizes";
        String juicerTools = "/home/plop/Tools/juicer_tools_1.19.02.jar";
        HashMap<String,Integer> chrsize = readChrSizeFile(fileChr);
        int resolution = 100000;
        int matrixSize = 500;
        int diagSize = 5;
        double gauss = 1.5;
        double min = 2;
        double max = 2;
        int nbZero = 6;
        String norm = "KR";
        int thresholdMax = 2800;
        double saturatedPixel = 0.01;
        ArrayList<Integer> factor = new ArrayList<Integer>();
        factor.add(1);
        factor.add(2);
        factor.add(5);
        boolean keepTif = false;
        int cpu = 1;

        //for (int i = 0; i < 1; ++i){
            //for(int j = i+1; j < 2; ++j){
                String chrName1 = _chr.get(3);
                String chrName2 = _chr.get(5);
                int chrSize1 = chrsize.get(chrName1);
                int chrSize2 = chrsize.get(chrName2);
                int step = matrixSize;
                String nameRes = String.valueOf(resolution);
                nameRes = nameRes.replace("000", "");
                nameRes = nameRes+"kb";
                String outdir = output+nameRes+ File.separator+chrName1+"_"+chrName2+File.separator;
                File file = new File(outdir);
                if (file.exists()==false) file.mkdirs();
                step = step*resolution;
                DumpInterChromosomal dumpInter = new DumpInterChromosomal(juicerTools,input, norm);
                    System.out.println("start dump "+chrName1+" size "+chrSize1+" "+chrName2+" size "+chrSize2+" res "+ nameRes);
                    int endChr1 = matrixSize*resolution;
                    if(endChr1 > chrSize1) endChr1 = chrSize1;

                    for(int startChr1 = 0 ; endChr1-1 <= chrSize1; startChr1+=step,endChr1+=step){
                        int endChr2 = matrixSize*resolution;
                        if(endChr2 > chrSize2) endChr2 = chrSize2;
                        int end1 =endChr1-1;
                        String dump1 = chrName1+":"+startChr1+":"+end1;
                        for(int startChr2 = 0 ; endChr2-1 <= chrSize2; startChr2+=step,endChr2+=step) {
                            int end2 =endChr2-1;
                            String dump2 = chrName2+":"+startChr2+":"+end2;
                            String name = outdir + chrName1 +"_" + startChr1 + "_" + end1 +"_" +chrName2 +"_" + startChr2 + "_" + end2 + ".txt";
                            //System.out.println("\tstart dump " + chrName1 + " " + chrName2 + " dump " + dump1 +" "+ dump2 + " res " + nameRes);
                            dumpInter.dumpObserved(dump1, dump2, name, resolution);
                            if (endChr2 + step > chrSize2 && endChr2 < chrSize2) {
                                endChr2 = chrSize2-1;
                                startChr2 += step;
                                dump2 = chrName2+":"+startChr2+":"+endChr2;
                                name = outdir + chrName1 +"_" + startChr1 + "_" + end1 +"_" +chrName2 +"_" + startChr2 + "_" + endChr2 + ".txt";
                                //System.out.println("\tstart dump " + chrName1 + " " + chrName2 + " dump " + dump1 +" "+ dump2 + " res " + nameRes);
                                dumpInter.dumpObserved(dump1, dump2, name, resolution);
                            }
                        }
                        if (endChr1 + step > chrSize1 && endChr1 < chrSize1) {
                            endChr1 = chrSize1-1;
                            startChr1 += step;
                            dump1 = chrName1+":"+startChr1+":"+endChr1;
                            endChr2 = matrixSize*resolution;
                            for(int startChr2 = 0 ; endChr2-1 <= chrSize2; startChr2+=step,endChr2+=step) {
                                int end2 =endChr2-1;
                                String dump2 = chrName2+":"+startChr2+":"+end2;
                                String name = outdir + chrName1 +"_" + startChr1 + "_" + endChr1 +"_" +chrName2 +"_" + startChr2 + "_" + end2 + ".txt";
                                //System.out.println("\tstart dump " + chrName1 + " " + chrName2 + " dump " + dump1 +" "+ dump2 + " res " + nameRes);
                                dumpInter.dumpObserved(dump1, dump2, name, resolution);
                                if (endChr2 + step > chrSize2 && endChr2 < chrSize2) {
                                    endChr2 = chrSize2-1;
                                    startChr2 += step;
                                    dump2 = chrName2+":"+startChr2+":"+endChr2;
                                    name = outdir + chrName1 +"_" + startChr1 + "_" + endChr1 +"_" +chrName2 +"_" + startChr2 + "_" + endChr2 + ".txt";
                                   // System.out.println("\tstart dump " + chrName1 + " " + chrName2 + " dump " + dump1 +" "+ dump2 + " res " + nameRes);
                                    dumpInter.dumpObserved(dump1, dump2, name, resolution);
                                }
                            }
                        }
                    }
                    //System.out.println("##### End dump "+chrName1+" "+chrName2+" "+nameRes);
          //  }
        //}

        File folder = new File(outdir);
        File[] listOfFiles = folder.listFiles();
        //System.out.println(outdir+" !!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + listOfFiles.length);
        for (int i = 0; i<listOfFiles.length; ++i) {
            String fileName = listOfFiles[i].toString();
            if(fileName.contains("txt")) {
                ImagePlus img = readTupleFile(fileName, matrixSize, resolution);
                String imgPath = fileName;
                String [] name = fileName.split(File.separator);
                imgPath = imgPath.replace("txt", "tif");
                //System.out.println(imgPath);
                saveFile(img, imgPath);
                ImagePlus imageDiff = imgDiff(img,imgPath);
                FindMaxima findMaxima = new FindMaxima( imageDiff, chrName1,chrName2, 20, resolution);
                HashMap<String, Loop> hloop =   imageToGenomeCoordinate(findMaxima.findloopInter(5,img,3) , name[name.length-1]);
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
        System.out.println("end");

    }


    /**
     *
     * @param chrSizeFile
     * @return
     * @throws IOException
     */
    private static HashMap<String, Integer> readChrSizeFile( String chrSizeFile) throws IOException{
        HashMap<String,Integer> chrSize =  new HashMap<String,Integer>();

        BufferedReader br = new BufferedReader(new FileReader(chrSizeFile));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();
        while (line != null){
            sb.append(line);
            String[] parts = line.split("\\t");
            String chr = parts[0];
            int size = Integer.parseInt(parts[1]);
            chrSize.put(chr, size);
            _chr.add(chr);
            sb.append(System.lineSeparator());
            line = br.readLine();
        }
        br.close();
        return  chrSize;
    }







}
