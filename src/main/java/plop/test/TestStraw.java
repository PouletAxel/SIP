package plop.test;

import plop.sip.SIPIntra;
import plop.straw.StrawRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TestStraw {


    public static void main(String[] args) throws IOException, InterruptedException {
        String hicFileName = "/home/plop/Desktop/4DNFI1UEG1HD.hic";
        String output= "/home/plop/Desktop/testStraw";

        int matrixSize = 2000;
        int resolution = 5000;
        int diagSize = 5;
        double gauss = 1.5;
        double min = 2;
        double max = 2;
        int nbZero = 6;
        int thresholdMax = 2800;
        String juiceBoXNormalisation = "KR";
        double saturatedPixel = 0.01;


        int factor = 1;
        //factor.add(2);
        //factor.add(5);
        boolean keepTif = false;
        int cpu = 1;

        System.out.println("output "+output+"\n"
                + "norm "+ juiceBoXNormalisation+"\n"
                + "gauss "+gauss+"\n"
                + "min "+min+"\n"
                + "max "+max+"\n"
                + "matrix size "+matrixSize+"\n"
                + "diag size "+diagSize+"\n"
                + "resolution "+resolution+"\n"
                + "saturated pixel "+saturatedPixel+"\n"
                + "threshold "+thresholdMax+"\n");

        File file = new File(output);
        if (!file.exists()){file.mkdir();}

        SIPIntra sip = new SIPIntra(output, gauss,
                min, max, resolution, saturatedPixel,
                thresholdMax, diagSize, matrixSize,
                nbZero,factor,0.01,false, keepTif,cpu );
        sip.setIsGui(false);

        StrawRunner strawRunner = new StrawRunner (sip, hicFileName, "KR", true,  "/home/plop/Desktop/testStraw/test.loop");
        strawRunner.run();
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaa");
        System.out.println("End");
    }

}
