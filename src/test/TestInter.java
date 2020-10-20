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
