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

        /* Param spe intra chromosomal loop*/
        double min = 2.0;
        double max = 2.0;
        int diagSize = 6;
        double saturatedPixel = 0.01;
        /* end spe intra */

        /* common */
        String input = _cmd.getOptionValue("input");
        String output = _cmd.getOptionValue("output");
        String juicerTool = _cmd.getOptionValue("juicerTool");
        String interOrIntra = _cmd.getOptionValue("lt");
        String chrSizeFile = _cmd.getOptionValue("chrSize");

        String juicerNorm = "KR";
        if (_cmd.hasOption("norm")) {
            juicerNorm = _cmd.getOptionValue("norm");
            if (!juicerNorm.equals("KR") && !juicerNorm.equals("NONE") && !juicerNorm.equals("VC") && !juicerNorm.equals("VC_SQRT")) {
                System.out.println("-norm = "+juicerNorm+", not defined for SIP, available norm: KR,NONE.VC,VC_SQRT\n Check the presence of this norm method in your hic file\n");
                //helper hic
                System.exit(0);
            }
        }

        int nbZero = 6;
        if (_cmd.hasOption("nbZero")) {
            nbZero = Integer.parseInt(_cmd.getOptionValue("nbZero"));
            if (nbZero > 24|| nbZero < 0) {
                System.out.println("\n-nbZero"+ nbZero+" value invalid: choose an integer value between 0 and 24\n");
                //erreur mettre l'aide et stopper le prog.

                System.exit(0);
            }
        }

        boolean delImages = true;
        if (_cmd.hasOption("delete"))  delImages = Boolean.parseBoolean(_cmd.getOptionValue("delImages"));

        int cpu = 1;
        if (_cmd.hasOption("cpu")){
            cpu = Integer.parseInt(_cmd.getOptionValue("cpu"));
            if(cpu > Runtime.getRuntime().availableProcessors() || cpu <= 0){
                System.out.println("\nThe number of CPU "+ cpu+" is superior of the server/computer' cpu "+Runtime.getRuntime().availableProcessors()+"\n");

                System.exit(0);
            }
        }
        ;
        /* end common */


        /* common with modif*/
        double gauss = 1.5;
        int matrixSize = 2000;
        int thresholdMax = 2800;
        double fdr = 0.01;
        int resolution = 5000;
        /* end common with modif*/


        if(interOrIntra.equals("intra")){
            if(_cmd.hasOption("min")) {
                min = Double.parseDouble(_cmd.getOptionValue("min"));
                if(min  < 0){
                    System.out.println("\n-min"+ min+": this parameter need to be >= 0\n");
                    System.exit(0);
                }
            }

            if(_cmd.hasOption("max")) {
                max = Double.parseDouble(_cmd.getOptionValue("max"));
                if(max  < 0){
                    System.out.println("\n-max"+ max+": this parameter need to be >= 0\n");

                    System.exit(0);
                }
            }

            if(_cmd.hasOption("diagonal")) {
                diagSize = Integer.parseInt(_cmd.getOptionValue("diagonal"));
                if(diagSize  < 0){
                    System.out.println("\n-d"+ diagSize+": this parameter need to be >= 0\n");

                    System.exit(0);
                }
            }



            saturatedPixel = Double.parseDouble(_cmd.getOptionValue("saturated"));

        }



        boolean isDroso = false;
        ArrayList<Integer> factor = new ArrayList<Integer>();
        String factOption = "1";
        boolean isProcessed = false;



        boolean _gui = false;

        File f = new File(juicerTool);
        if(!f.exists()){
            System.out.println(juicerTool+" doesn't existed !!! \n\n");
            // Mettre help ici
            return;
        }

        System.out.println("hic mode: \n"+ "input: "+input+"\n"+ "output: "+output+"\n"+ "juiceBox: "+juicerTool+"\n"+ "norm: "+ juicerNorm+"\n"
                + "gauss: "+gauss+"\n"+ "min: "+min+"\n"+ "max: "+max+"\n"+ "matrix size: "+matrixSize+"\n"+ "diag size: "+diagSize+"\n"+ "resolution: "+resolution+"\n"
                + "saturated pixel: "+saturatedPixel+"\n"+ "threshold: "+thresholdMax+"\n"+ "number of zero :"+nbZero+"\n"+ "factor "+ factOption+"\n"+ "fdr "+fdr+"\n"
                + "del "+delImages+"\n"+ "cpu "+ cpu+"\n-isDroso "+isDroso+"\n");

        SIPIntra sip = new SIPIntra(output, chrSizeFile, gauss, min, max, resolution, saturatedPixel,
               thresholdMax,diagSize, matrixSize, nbZero, factor,fdr, isProcessed,isDroso);
        sip.setIsGui(_gui);
        ProcessDumpData processDumpData = new ProcessDumpData();
        processDumpData.go(input, sip, juicerTool, juicerNorm, cpu);
        System.out.println("########### End of the dump Step");

        MultiResProcess multi = new MultiResProcess(sip, cpu, delImages, chrSizeFile);
        multi.run();
        System.out.println("###########End loop detction step");
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(output+File.separator+"parameters.txt")));
    }



}
