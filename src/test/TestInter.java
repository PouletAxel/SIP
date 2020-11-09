package test;

import multiProcesing.ProcessDetectLoops;
import sip.SIPInter;

import java.io.IOException;
import java.util.ArrayList;

public class TestInter {

    static ArrayList <String> _chr= new ArrayList<String>();

    public static void main(String[] args) throws IOException, InterruptedException {
        //String chr = "chr21";
        String input = "/home/plop/Desktop/testInter/";
        String output = "/home/plop/Desktop/testInter/";
        String fileChr = "/home/plop/Desktop/SIP/testSize.tab";
        String juicerTools = "/home/plop/Tools/juicer_tools_1.19.02.jar";
        int resolution = 100000;
        int matrixSize = 500;
        double gauss = 1;
        double min = 2;
        double max = 2;
        int nbZero = 6;
        double thresholdMax = 0.9;
        double saturatedPixel = 0.01;

        boolean keepTif = true;
        int cpu = 2;
        SIPInter sipInter = new SIPInter(input, output, fileChr, gauss, resolution,  thresholdMax, matrixSize, nbZero,keepTif,0.025);
       //ProcessDumpData process = new ProcessDumpData();
       //process.go(input,sipInter,chrsize,juicerTools,norm,2);

        ProcessDetectLoops detectLoops = new ProcessDetectLoops();
        detectLoops.go(sipInter, 1, false, "/home/plop/Desktop/testInter/loops.txt");


        System.out.println("end");

    }


}
