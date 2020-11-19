package process;


import cli.CLIHelper;
import sip.SIPIntra;
import sip.SIPObject;

import javax.swing.*;
import java.io.*;


public class ParametersCheck {

    String _logError;

    /**
     *
     * @param sip
     */
    public void optionalParametersValidity(SIPObject sip){

        if(sip.getResolution() <= 0 ){
            System.out.println("Parameter value error !!!!\n-r "+sip.getResolution()+", resolution need to be a >= 0 !!! \n\n");
            CLIHelper.getHelperInfos();
        }


        if (sip.getNbZero() > 24|| sip.getNbZero() < 0) {
            System.out.println("Parameter value error !!!!\n-nbZero"+ sip.getNbZero() +" value invalid: choose an integer value between 0 and 24\n");
            CLIHelper.getHelperInfos();
        }

        if(sip.getCpu() > Runtime.getRuntime().availableProcessors() || sip.getCpu() <= 0){
            System.out.println("Parameter value error !!!!\n-cpu "+ sip.getCpu() +" is superior to server/computer' cpu "+Runtime.getRuntime().availableProcessors()+"\n");
            CLIHelper.getHelperInfos();
        }

        if(sip.getFdr() < 0 ){
            System.out.println("Parameter value error !!!!\n-fdr "+sip.getFdr()+", fdr need to be a >= 0 !!! \n\n");
            CLIHelper.getHelperInfos();
        }

        if(sip.getMatrixSize() < 0 ){
            System.out.println("Parameter value error !!!!\n-ms "+sip.getMatrixSize()+", matrix size need to be a > 0 !!! \n\n");
            CLIHelper.getHelperInfos();
        }

        if(sip.getGauss() < 0 ){
            System.out.println("Parameter value error !!!!\n-g "+sip.getGauss()+", gaussian strength filter need to be a > 0 !!! \n\n");
            CLIHelper.getHelperInfos();
        }

        if(sip.getThresholdMaxima() < 0 ){
            System.out.println("Parameter value error !!!!\n-t "+sip.getThresholdMaxima()+", threshold for loops detection need to be a > 0 !!! \n\n");
            CLIHelper.getHelperInfos();
        }
    }

    /**
     *
     * @param input
     * @param output
     * @param chrSizeFile
     * @param interOrIntra
     */
    public ParametersCheck(String input, String output, String chrSizeFile, String interOrIntra){
        File file = new File(input);

        if(!file.exists() && !input.startsWith("https")){
            System.out.println("File problem !!!!\n-i "+input+": this file doesn't existed !!! \n\n");
            CLIHelper.getHelperInfos();
        }

        file = new File(output);

        if(!file.exists()){
            System.out.println("File problem !!!!\n-o "+output+": this file doesn't existed !!! \n\n");
            CLIHelper.getHelperInfos();
        }

        file = new File(chrSizeFile);
        if(!file.exists()){
            System.out.println("File problem !!!!\n-c "+chrSizeFile+": this file doesn't existed !!! \n\n");
            CLIHelper.getHelperInfos();
        }

        if(!interOrIntra.equals("inter") && !interOrIntra.equals("intra")){
            System.out.println("Parameter value error !!!!\n-tl "+interOrIntra+", wrong value, choose inter or intra !!! \n\n");

        }
    }

    /**
     *
     * @param coolTool
     * @param cooler
     * @param isGui
     */
    public  void testCoolOption(String coolTool, String cooler,boolean isGui){

       File f = new File(coolTool);
        if(!f.exists()){
            System.out.println("File problem !!!!\n-coolTool "+coolTool+" doesn't existed or wrong path !!! \n\n");
            CLIHelper.CmdHelpCool();
        }

        f = new File(cooler);
        if(!f.exists()){
            System.out.println("File problem !!!!\n-cooler "+cooler+" doesn't existed or wrong path !!! \n\n");
            CLIHelper.CmdHelpCool();
        }

        if(!testTools(coolTool, 0, 3, 0) || !testTools(cooler, 0, 8, 6)) {
            System.out.println("Versioning problem !!!!\n"+coolTool +
                    " or" + cooler + " version is not the good one for SIP (it needs cooltool version >= 0.3.0 and " +
                    "cooler version >= 0.8.6) !!! \n\n");
            CLIHelper.CmdHelpCool();
            if (isGui) {
                JOptionPane.showMessageDialog(null, "Error SIP program", coolTool + " or" + cooler + "version is not the good one" +
                                " for SIP (it needs cooltools version >= 0.3.0 and cooler version >= 0.8.6) !!!"
                        , JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    /**
     *
     * @param juicerTool
     * @param juicerNorm
     */
    public void testHiCOption(String juicerTool, String juicerNorm){

       if (!juicerNorm.equals("KR") && !juicerNorm.equals("NONE") && !juicerNorm.equals("VC") && !juicerNorm.equals("VC_SQRT")) {
           System.out.println("Parameter value error !!!!\n-norm = "+juicerNorm+", not defined for SIP, available norm: KR,NONE.VC,VC_SQRT\n Check the presence of this norm method in your hic file\n");
           CLIHelper.CmdHelpHiC();
        }


        File file = new File(juicerTool);
        if(!file.exists()){
            System.out.println("File problem !!!!\n-j "+juicerTool+": this file doesn't existed !!! \n\n");
            CLIHelper.CmdHelpHiC();
        }
    }

    /**
     *
     * @param factor
     */
    public void checkFactor(int factor){

        if(factor < 1 || factor  > 4 ){
            System.out.println("Parameter value error !!!!\n-f "+factor+", value for factor are 1, 2, 3 or 4 !!! \n\n");
            CLIHelper.getHelperInfos();
        }
    }

    /**
     *
     * @param sipIntra
     */
    public void speOption(SIPIntra sipIntra){

        if(sipIntra.getDiagonalSize() < 0 ){
            System.out.println("Parameter value error !!!!\n-d "+sipIntra.getDiagonalSize()+", diagonal size need to be a > 0 !!! \n\n");
            CLIHelper.getHelperInfos();
        }

        if(sipIntra.getMin() < 0 ){
            System.out.println("Parameter value error !!!!\n-min "+sipIntra.getMin()+", min strength filter need to be a > 0 !!! \n\n");
            CLIHelper.getHelperInfos();
        }

        if(sipIntra.getMax() < 0 ){
            System.out.println("\"Parameter value error !!!!\n-max "+sipIntra.getMax()+", max strength filter need to be a > 0 !!! \n\n");
            CLIHelper.getHelperInfos();
        }

        if(sipIntra.getSaturatedPixel() < 0 ){
            System.out.println("Parameter value error !!!!\n-sat "+sipIntra.getSaturatedPixel()+", max strength filter need to be a > 0 !!! \n\n");
            CLIHelper.getHelperInfos();
        }

    }


    /**
     *
     * @param pathTools
     * @param first
     * @param second
     * @param third
     * @return
     */
    public boolean testTools(String pathTools, int first, int second, int third) {
        Runtime runtime = Runtime.getRuntime();
        String cmd = pathTools+" --version";
        Process process;
        try {
            process = runtime.exec(cmd);

            new ReturnFlux(process.getInputStream()).start();
            new ReturnFlux(process.getErrorStream()).start();
            process.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        String [] tline = _logError.split(" ");
        System.out.println(_logError);
        _logError = "";
        if(tline.length > 0){
            tline = tline[tline.length-1].split("\\.");
            tline[2] = tline[2].replace("\n", "");
            if(Integer.parseInt(tline[0]) >= first && Integer.parseInt(tline[1]) >= second) //&& Integer.parseInt(tline[2]) >= third)
                return true;
            else
                return false;
        }else
            return false;
    }

    /**
     *
     */
    public class ReturnFlux extends Thread {

        /**  Flux to redirect  */
        private InputStream _flux;

        /**
         * <b>Constructor of ReturnFlux</b>
         * @param flux
         *  flux to redirect
         */
        public ReturnFlux(InputStream flux){this._flux = flux; }

        /**
         *
         */
        public void run(){
            try {
                InputStreamReader reader = new InputStreamReader(this._flux);
                BufferedReader br = new BufferedReader(reader);
                String line=null;
                while ( (line = br.readLine()) != null) {
                    if(!line.contains("WARN")) _logError = _logError+line+"\n";
                }
            }
            catch (IOException ioe){
                ioe.printStackTrace();
            }
        }
    }

}
