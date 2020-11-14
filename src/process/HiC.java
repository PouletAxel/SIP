package process;


import cli.CLIOptionHiC;
import gui.GuiAnalysis;
import multiProcesing.ProcessDetectLoops;
import multiProcesing.ProcessDumpData;
import org.apache.commons.cli.CommandLine;
import sip.SIPInter;
import sip.SIPIntra;
import utils.MultiResProcess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class HiC {

    private String _log;
    /** */
    private SIPIntra _sipIntra;
    /** */
    private SIPInter _sipInter;
    /** */
    private CommandLine _cmd;

    /** */
    private String _input;
    /** */
    private String _output;
    /** */
    private String _juicerTool;
    /** */
    private String _interOrIntra;
    /** */
    private String _chrSizeFile;
    /** */
    private String _juicerNorm = "KR";
    /** */
    private int _nbZero = 6;
    /** */
    private boolean _delImages = true;

    private int _cpu =1;

    private boolean _gui;

    private int _factor = 1;

    private GuiAnalysis _guiAnalysis;
    /**
     *
     * @param args
     * @throws Exception
     */
    public HiC(String args []) throws Exception {
        _gui = false;
        String [] argsSubset = Arrays.copyOfRange(args, 1, args.length);
        CLIOptionHiC cli = new CLIOptionHiC(argsSubset);
        _cmd = cli.getCommandLine();
        OptionalParamaterCheck opt = new OptionalParamaterCheck(_cmd);
        opt.testOptionalParametersValueCommons();
        opt.testOptionalParametersValueIntra();
    }

    public HiC(GuiAnalysis guiAnalysis ) throws Exception {
        _gui = true;
        _guiAnalysis = guiAnalysis;
    }
    /**
     *
     * Normalisation method to dump the the data with hic method (KR,NONE.VC,VC_SQRT)
     *
     */
    public void run() throws IOException, InterruptedException {

        if(_gui) {
        }else {/* common required parameters*/
            _input = _cmd.getOptionValue("input");
            _output = _cmd.getOptionValue("output");
            _juicerTool = _cmd.getOptionValue("juicerTool");
            _interOrIntra = _cmd.getOptionValue("lt");
            _chrSizeFile = _cmd.getOptionValue("chrSize");
            /* common optional parameters */
            if (_cmd.hasOption("norm"))
                _juicerNorm = _cmd.getOptionValue("norm");
            if (_cmd.hasOption("nbZero"))
                _nbZero = Integer.parseInt(_cmd.getOptionValue("nbZero"));
            if (_cmd.hasOption("delete"))
                _delImages = Boolean.parseBoolean(_cmd.getOptionValue("delImages"));

            if (_cmd.hasOption("cpu"))
                _cpu = Integer.parseInt(_cmd.getOptionValue("cpu"));
        }

        _log = _output+File.separator+"log.txt";

        ProcessDumpData processDumpData = new ProcessDumpData();

        if(_interOrIntra.equals("intra")){
            /* Param spe intra chromosomal loop*/
            this.setSipIntra();
            _sipIntra.setIsGui(_gui);


            String allParam = "SIPHiC hic: \n" +
                    "input: "+_input+"\n" +
                    "output: "+_output+"\n"+
                    "juiceBox: "+_juicerTool+"\n"+
                    "norm: "+_juicerNorm+"\n" +
                    "inter or intra chromosomal: "+_interOrIntra+"\n" +
                    "gauss: "+this._sipIntra.getGauss()+"\n"+
                    "min: "+this._sipIntra.getMin()+"\n"+
                    "max: "+this._sipIntra.getMax()+"\n"+
                    "matrix size: "+this._sipIntra.getMatrixSize()+"\n"+
                    "diagonal size: "+this._sipIntra.getDiagonalSize()+"\n"+
                    "resolution: "+this._sipIntra.getResolution()+"\n"+
                    "saturated pixel: "+this._sipIntra.getSaturatedPixel()+"\n"+
                    "threshold: "+this._sipIntra.getThresholdMaxima()+"\n"+
                    "number of zero: "+this._nbZero+"\n"+
                    "factor: "+ _factor+"\n"+
                    "fdr: "+this._sipIntra.getFdr()+"\n"+
                    "delete images: "+_delImages+"\n"+
                    "cpu: "+ _cpu+"\n" +
                    "isDroso: "+this._sipIntra.isDroso()+"\n";

            System.out.println("########### Starting dump Step inter chromosomal interactions");

            processDumpData.go(_input, _sipIntra, _juicerTool, _juicerNorm, _cpu);
            System.out.println("########### End of the dump step\n");

            System.out.println("########### Start loop detection\n");
            MultiResProcess multi = new MultiResProcess(_sipIntra, _cpu, _delImages, _chrSizeFile);
            multi.run();
            System.out.println("###########End loop detection step\n");
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(_log)));
            writer.write(allParam);


        }else{
            setSipInter();
            _sipInter.setIsGui(_gui);

            String allParam = "SIPHiC hic: \n" +
                    "input: "+_input+"\n" +
                    "output: "+_output+"\n"+
                    "juiceBox: "+_juicerTool+"\n"+
                    "norm: "+_juicerNorm+"\n" +
                    "inter or intra chromosomal: "+_interOrIntra+"\n" +
                    "gauss: "+this._sipInter.getGauss()+"\n"+
                    "matrix size: "+this._sipInter.getMatrixSize()+"\n"+
                    "resolution: "+this._sipInter.getResolution()+"\n"+
                    "threshold: "+this._sipInter.getThresholdMaxima()+"\n"+
                    "number of zero :"+_nbZero+"\n"+
                    "fdr "+this._sipInter.getFdr()+"\n"+
                    "delete images "+_delImages+"\n"+
                    "cpu "+ _cpu+"\n";
            processDumpData.go(_input,_sipInter,_juicerTool,_juicerNorm,_cpu);

            ProcessDetectLoops detectLoops = new ProcessDetectLoops();
            detectLoops.go(_sipInter, _cpu, _delImages, "/home/plop/Desktop/testInter/loops.txt");
        }


    }


    /**
     *
     *
     */
    private void setSipIntra(){
        double min = 2.0;
        double max = 2.0;
        double gauss = 1.5;
        int matrixSize = 2000;
        double thresholdMax = 2800;
        double fdr = 0.01;
        int resolution = 5000;
        int diagSize = 6;
        double saturatedPixel = 0.01;
        boolean isDroso = false;
        ArrayList<Integer> factor = new ArrayList<Integer>();
        factor.add(1);

        if(_gui){

        }else{

            if(_cmd.hasOption("min")) min = Double.parseDouble(_cmd.getOptionValue("min"));
            if(_cmd.hasOption("max")) max = Double.parseDouble(_cmd.getOptionValue("max"));
            if(_cmd.hasOption("diagonal")) diagSize = Integer.parseInt(_cmd.getOptionValue("diagonal"));
            if(_cmd.hasOption("saturated")) saturatedPixel = Double.parseDouble(_cmd.getOptionValue("saturated"));
            if(_cmd.hasOption("gaussian")) saturatedPixel = Double.parseDouble(_cmd.getOptionValue("gaussian"));

            if(_cmd.hasOption("factor")) {
                _factor= Integer.parseInt(_cmd.getOptionValue("factor"));
                if(_factor == 2){	factor.add(2);}
                else if(_factor == 4){
                    factor.add(2);
                    factor.add(5);
                }else{ factor.add(5);}
            }

            _sipIntra = new SIPIntra(_output, _chrSizeFile, gauss, min, max, resolution, saturatedPixel,
                    thresholdMax, diagSize, matrixSize, _nbZero, factor, fdr, false, isDroso);
        }

    }

    /**
     *
     *
     */
    private void setSipInter(){
        double gauss = 1;
        int matrixSize = 500;
        double thresholdMax = 0.01;
        double fdr = 0.025;
        int resolution = 100000;
        _sipInter = new SIPInter(_input, _output, _chrSizeFile, gauss, resolution,  thresholdMax, matrixSize, _nbZero, _delImages, fdr);
    }



}
