package process;


import cli.CLIOptionHiC;
import multiProcesing.ProcessDumpData;
import org.apache.commons.cli.CommandLine;
import sip.SIPIntra;
import utils.MultiResProcess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class HiC {


      private CommandLine _cmd;

    public HiC(String args []) throws Exception {

        String [] argsSubset = Arrays.copyOfRange(args, 1, args.length);
        CLIOptionHiC cli = new CLIOptionHiC(argsSubset);
        _cmd = cli.getCommandLine();


    }

    /**
     *
     * Normalisation method to dump the the data with hic method (KR,NONE.VC,VC_SQRT)
     *
     */
    public void run() throws IOException, InterruptedException {

        String input = _cmd.getOptionValue("input");
        String output = _cmd.getOptionValue("output");
        String juiceBoxTools = "";
        String juiceBoXNormalisation = "KR";
        double gauss = 1.5;
        int matrixSize = 2000;
        double min = 2.0;
        double max = 2.0;
        int thresholdMax = 2800;
        int nbZero = 6;
        String chrSizeFile = _cmd.getOptionValue("output");;
        boolean delImages = true;
        int cpu = 1;
        double fdr = 0.01;


        int diagSize = 6;
        int resolution = 5000;
        double saturatedPixel = 0.01;
        boolean isHic = true;
        boolean isCool = false;
        boolean isDroso = false;
        ArrayList<Integer> factor = new ArrayList<Integer>();
        String factOption = "1";
        boolean isProcessed = false;



        boolean _gui = false;

        File f = new File(juiceBoxTools);
        if(!f.exists()){
            System.out.println(juiceBoxTools+" doesn't existed !!! \n\n");
            // Mettre help ici
            return;
        }

        System.out.println("hic mode: \n"+ "input: "+input+"\n"+ "output: "+output+"\n"+ "juiceBox: "+juiceBoxTools+"\n"+ "norm: "+ juiceBoXNormalisation+"\n"
                + "gauss: "+gauss+"\n"+ "min: "+min+"\n"+ "max: "+max+"\n"+ "matrix size: "+matrixSize+"\n"+ "diag size: "+diagSize+"\n"+ "resolution: "+resolution+"\n"
                + "saturated pixel: "+saturatedPixel+"\n"+ "threshold: "+thresholdMax+"\n"+ "number of zero :"+nbZero+"\n"+ "factor "+ factOption+"\n"+ "fdr "+fdr+"\n"
                + "del "+delImages+"\n"+ "cpu "+ cpu+"\n-isDroso "+isDroso+"\n");

        SIPIntra sip = new SIPIntra(output, chrSizeFile, gauss, min, max, resolution, saturatedPixel,
               thresholdMax,diagSize, matrixSize, nbZero, factor,fdr, isProcessed,isDroso);
        sip.setIsGui(_gui);
        ProcessDumpData processDumpData = new ProcessDumpData();
        processDumpData.go(input, sip, juiceBoxTools, juiceBoXNormalisation, cpu);
        System.out.println("########### End of the dump Step");

        MultiResProcess multi = new MultiResProcess(sip, cpu, delImages, chrSizeFile);
        multi.run();
        System.out.println("###########End loop detction step");
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(output+File.separator+"parameters.txt")));
    }



}
