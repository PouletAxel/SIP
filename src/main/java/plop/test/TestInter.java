package plop.test;


import plop.multiProcessing.ProcessDetectLoops;
import plop.sip.SIPInter;

import java.io.IOException;
import java.util.ArrayList;

public class TestInter {

    static ArrayList <String> _chr= new ArrayList<String>();

    public static void main(String[] args) throws IOException, InterruptedException {
        //String chr = "chr21";
        // hic -i SIP/GSE104333_Rao-2017-treated_6hr_combined_30.hic -c SIP/testSize.tab -o /home/plop/Desktop/interTreatedP -j /home/plop/Tools/juicer_tools_1.19.02.jar -lt inter -cpu 2 -r 50000 -ms 500
        //processed -i /home/plop/Desktop/interTreatedP/ -c /home/plop/Desktop/SIP/testSize.tab -o /home/plop/Desktop/re -lt inter -cpu 2 -r 50000 -ms 500 -t 0.001 --nbZero 6 -k -fdr 1 -g 1
        //String input = "/home/plop/Desktop/testInter/";
        String input = "/home/plop/Desktop/SIP/hicData/4DNFILIM6FDL.hic";
        String output = "/home/plop/Desktop/testInter/";
        String fileChr = "/home/plop/Desktop/SIP/hg38_small.size";
        String juicerTools = "/home/plop/Tools/juicer_tools_1.19.02.jar";
        int resolution = 100000;
        int matrixSize = 500;
        double gauss = 1;

        int nbZero = 3;
        double thresholdMax = 0.6;
        double fdr =0.025;

        boolean delTif = false;
        int cpu = 2;

        SIPInter sipInter = new SIPInter(output, fileChr, gauss, resolution,  thresholdMax, matrixSize, nbZero,fdr,delTif,cpu);
        //ProcessDumpHic java.plop.process = new ProcessDumpHic();
        //java.plop.process.go(input,sipInter,juicerTools,"KR");
        System.out.println("dans Test out : "+sipInter.getOutputDir());
        System.out.println("dans Test in : "+sipInter.getInputDir());
        ProcessDetectLoops detectLoops = new ProcessDetectLoops();
        detectLoops.go(sipInter, "/home/plop/Desktop/testInter/loopsBis.txt");


        System.out.println("end");

    }


}
