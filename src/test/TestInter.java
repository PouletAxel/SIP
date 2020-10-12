package test;

import multiProcesing.ProcessDumpData;
import utils.SIPInter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
        SIPInter sipInter = new SIPInter(output, chrsize, gauss, min, max, resolution, saturatedPixel, thresholdMax, matrixSize, nbZero,keepTif);
        ProcessDumpData process = new ProcessDumpData();
        process.go(input,sipInter,chrsize,juicerTools,norm,2);



      /*

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
        }*/
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
