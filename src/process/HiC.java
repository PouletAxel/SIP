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

/**
 *
 */
public class HiC {
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
    private String _chrSizeFile;

    /** */
    private int _nbZero = 6;
    /** */
    private boolean _delImages = true;
    /** */
    private int _cpu =1;
    /** */
    private boolean _isGui;
    /** */
    private int _factorParam = 1;
    /** */
    private GuiAnalysis _guiAnalysis;


    String _log;

    /**
     *
     * @param args
     * @throws Exception
     */
    public HiC(String args []) throws Exception {
        _isGui = false;
        String [] argsSubset = Arrays.copyOfRange(args, 1, args.length);
        CLIOptionHiC cli = new CLIOptionHiC(argsSubset);
        _cmd = cli.getCommandLine();
        _input = _cmd.getOptionValue("input");
        _output = _cmd.getOptionValue("output");
        _log = _output+File.separator+"log.txt";
    }

    /**
     *
     * @param guiAnalysis
     */
    public HiC(GuiAnalysis guiAnalysis ){
        _isGui = true;
        _guiAnalysis = guiAnalysis;
        _input =  this._guiAnalysis.getInput();
        _output = this._guiAnalysis.getOutputDir();
        _log = _output+File.separator+"log.txt";
    }
    /**
     *
     * Normalisation method to dump the the data with hic method (KR,NONE.VC,VC_SQRT)
     *
     */
    public void run() throws IOException, InterruptedException {
        String juicerTool;
        String interOrIntra;
        String allParam;
        String juicerNorm = "KR";
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(_log)));
        ProcessDumpData processDumpData = new ProcessDumpData();

        if(_isGui) {
            juicerTool = this._guiAnalysis.getJuicerTool();
            if(this._guiAnalysis.isInter())  interOrIntra = "inter";
            else  interOrIntra = "intra";
            _chrSizeFile = this._guiAnalysis.getChrSizeFile();
            if(this._guiAnalysis.isNONE()) juicerNorm = "NONE";
            else if (this._guiAnalysis.isVC()) juicerNorm = "VC";
            else if (this._guiAnalysis.isVC_SQRT()) juicerNorm = "VC_SQRT";
            _nbZero = this._guiAnalysis.getNbZero();
            _delImages = this._guiAnalysis.isDeletTif();
            _cpu = this._guiAnalysis.getNbCpu();
        }else {
            /* common required parameters*/
            juicerTool = _cmd.getOptionValue("juicerTool");
            interOrIntra = _cmd.getOptionValue("lt");
            _chrSizeFile = _cmd.getOptionValue("chrSize");
            /* common optional parameters */
            if (_cmd.hasOption("norm")) juicerNorm = _cmd.getOptionValue("norm");
            if (_cmd.hasOption("nbZero")) _nbZero = Integer.parseInt(_cmd.getOptionValue("nbZero"));
            if (_cmd.hasOption("delete"))_delImages = Boolean.parseBoolean(_cmd.getOptionValue("delImages"));
            if (_cmd.hasOption("cpu")) _cpu = Integer.parseInt(_cmd.getOptionValue("cpu"));
        }
        ParametersCheck paramaterCheck = new ParametersCheck(_input, _output, _chrSizeFile, interOrIntra);
        paramaterCheck.testHiCOption(juicerTool, juicerNorm);

        if(interOrIntra.equals("intra")){
            /* Param spe intra chromosomal loop*/
            this.setSipIntra();
            _sipIntra.setIsGui(_isGui);

            allParam = "SIPHiC hic: \n" +
                    "input: "+_input+"\n" +
                    "output: "+_output+"\n"+
                    "juiceBox: "+juicerTool+"\n"+
                    "norm: "+juicerNorm+"\n" +
                    "inter or intra chromosomal: "+interOrIntra+"\n" +
                    "gauss: "+this._sipIntra.getGauss()+"\n"+
                    "min: "+this._sipIntra.getMin()+"\n"+
                    "max: "+this._sipIntra.getMax()+"\n"+
                    "matrix size: "+this._sipIntra.getMatrixSize()+"\n"+
                    "diagonal size: "+this._sipIntra.getDiagonalSize()+"\n"+
                    "resolution: "+this._sipIntra.getResolution()+"\n"+
                    "saturated pixel: "+this._sipIntra.getSaturatedPixel()+"\n"+
                    "threshold: "+this._sipIntra.getThresholdMaxima()+"\n"+
                    "number of zero: "+this._nbZero+"\n"+
                    "factor: "+ _factorParam +"\n"+
                    "fdr: "+this._sipIntra.getFdr()+"\n"+
                    "delete images: "+_delImages+"\n"+
                    "cpu: "+ _cpu+"\n" +
                    "isDroso: "+this._sipIntra.isDroso()+"\n";

            System.out.println("########### Starting dump Step inter chromosomal interactions");

            paramaterCheck.testCommonParametersValidity(_sipIntra);
            processDumpData.go(_input, _sipIntra, juicerTool, juicerNorm, _cpu);
            System.out.println("########### End of the dump step\n");

            System.out.println("########### Start loop detection\n");
            MultiResProcess multi = new MultiResProcess(_sipIntra, _cpu, _delImages, _chrSizeFile);
            multi.run();
            System.out.println("###########End loop detection step\n");

        }else{
            this.setSipInter();
            _sipInter.setIsGui(_isGui);

            allParam = "SIPHiC hic: \n" +
                "input: "+_input+"\n" +
                   "output: "+_output+"\n"+
                    "juiceBox: "+juicerTool+"\n"+
                    "norm: "+juicerNorm+"\n" +
                    "inter or intra chromosomal: "+interOrIntra+"\n" +
                    "gauss: "+this._sipInter.getGauss()+"\n"+
                    "matrix size: "+this._sipInter.getMatrixSize()+"\n"+
                    "resolution: "+this._sipInter.getResolution()+"\n"+
                    "threshold: "+this._sipInter.getThresholdMaxima()+"\n"+
                    "number of zero :"+_nbZero+"\n"+
                    "fdr "+this._sipInter.getFdr()+"\n"+
                    "delete images "+_delImages+"\n"+
                    "cpu "+ _cpu+"\n";
            processDumpData.go(_input,_sipInter,juicerTool,juicerNorm,_cpu);

            String loopFileRes = _sipInter.getOutputDir()+"finalLoops.txt";

            ProcessDetectLoops detectLoops = new ProcessDetectLoops();
            detectLoops.go(_sipInter, _cpu, _delImages, loopFileRes);

        }
        writer.write(allParam);


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

        if(_isGui){
            min = this._guiAnalysis.getMin();
            max = this._guiAnalysis.getMax();
            gauss = this._guiAnalysis.getGaussian();
            matrixSize = this._guiAnalysis.getMatrixSize();
            thresholdMax = this._guiAnalysis.getThresholdMaxima();
            fdr = this._guiAnalysis.getFDR();
            resolution = this._guiAnalysis.getResolution();
            diagSize = this._guiAnalysis.getDiagSize();
            saturatedPixel = this._guiAnalysis.getSaturatedPixell();
            isDroso= this._guiAnalysis.isDroso();

            if(this._guiAnalysis.getFactorChoice() == 2) factor.add(2);
            else if(this._guiAnalysis.getFactorChoice() == 4){
                factor.add(2);
                factor.add(5);
            }else if(this._guiAnalysis.getFactorChoice() == 3)  factor.add(5);
        }else {
            if (_cmd.hasOption("min")) min = Double.parseDouble(_cmd.getOptionValue("min"));
            if (_cmd.hasOption("max")) max = Double.parseDouble(_cmd.getOptionValue("max"));
            if (_cmd.hasOption("gaussian")) gauss = Double.parseDouble(_cmd.getOptionValue("gaussian"));
            if (_cmd.hasOption("matrixSize")) matrixSize = Integer.parseInt(_cmd.getOptionValue("matrixSize"));
            if (_cmd.hasOption("threshold")) thresholdMax = Double.parseDouble(_cmd.getOptionValue("threshold"));
            if (_cmd.hasOption("fdr")) fdr = Double.parseDouble(_cmd.getOptionValue("fdr"));
            if (_cmd.hasOption("resolution")) resolution = Integer.parseInt(_cmd.getOptionValue("resolution"));
            if (_cmd.hasOption("diagonal")) diagSize = Integer.parseInt(_cmd.getOptionValue("diagonal"));
            if (_cmd.hasOption("saturated")) saturatedPixel = Double.parseDouble(_cmd.getOptionValue("saturated"));
            if (_cmd.hasOption("isDroso")) isDroso = Boolean.parseBoolean(_cmd.getOptionValue("isDroso"));
            if (_cmd.hasOption("factor")) {
                _factorParam = Integer.parseInt(_cmd.getOptionValue("factor"));
                if (_factorParam == 2)  factor.add(2);
                else if (_factorParam == 4) {
                    factor.add(2);
                    factor.add(5);
                } else factor.add(5);

            }
        }
            _sipIntra = new SIPIntra(_output, _chrSizeFile, gauss, min, max, resolution, saturatedPixel,
                    thresholdMax, diagSize, matrixSize, _nbZero, factor, fdr, false, isDroso);

    }

    /**
     *
     *
     */
    private void setSipInter() throws IOException {
        double gauss = 1;
        int matrixSize = 500;
        double thresholdMax = 0.01;
        double fdr = 0.025;
        int resolution = 100000;
        if(_isGui){
            gauss = this._guiAnalysis.getGaussian();
            matrixSize = this._guiAnalysis.getMatrixSize();
            thresholdMax = this._guiAnalysis.getThresholdMaxima();
            fdr = this._guiAnalysis.getFDR();
            resolution = this._guiAnalysis.getResolution();

        }else{
            if (_cmd.hasOption("gaussian")) gauss = Double.parseDouble(_cmd.getOptionValue("gaussian"));
            if (_cmd.hasOption("matrixSize")) matrixSize = Integer.parseInt(_cmd.getOptionValue("matrixSize"));
            if (_cmd.hasOption("threshold")) thresholdMax = Double.parseDouble(_cmd.getOptionValue("threshold"));
            if (_cmd.hasOption("fdr")) fdr = Double.parseDouble(_cmd.getOptionValue("fdr"));
            if (_cmd.hasOption("resolution")) resolution = Integer.parseInt(_cmd.getOptionValue("resolution"));
        }

        _sipInter = new SIPInter(_output, _chrSizeFile, gauss, resolution,  thresholdMax, matrixSize, _nbZero, _delImages, fdr);

    }
}
