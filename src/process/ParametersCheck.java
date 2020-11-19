package process;


import sip.SIPIntra;
import sip.SIPObject;

import java.io.File;


public class ParametersCheck {


    /**
     *
     * @param sip
     */
    public void optionalParametersValidity(SIPObject sip){

        if(sip.getResolution() <= 0 ){
            System.out.println("-r "+sip.getResolution()+", resolution need to be a >= 0 !!! \n\n");
            //System.out.println(getHelperInfos());
            System.exit(1);
        }


        if (sip.getNbZero() > 24|| sip.getNbZero() < 0) {
            System.out.println("\n-nbZero"+ sip.getNbZero() +" value invalid: choose an integer value between 0 and 24\n");
            //erreur mettre l'aide et stopper le prog.
            System.exit(0);
        }

        if(sip.getCpu() > Runtime.getRuntime().availableProcessors() || sip.getCpu() <= 0){
            System.out.println("\n-cpu "+ sip.getCpu() +" is superior to server/computer' cpu "+Runtime.getRuntime().availableProcessors()+"\n");
            System.exit(0);
        }

        if(sip.getFdr() < 0 ){
            System.out.println("-fdr "+sip.getFdr()+", fdr need to be a >= 0 !!! \n\n");
            //System.out.println(getHelperInfos());
            System.exit(1);
        }

        if(sip.getMatrixSize() < 0 ){
            System.out.println("-ms "+sip.getMatrixSize()+", matrix size need to be a > 0 !!! \n\n");
            //System.out.println(getHelperInfos());
            System.exit(1);
        }

        if(sip.getGauss() < 0 ){
            System.out.println("-g "+sip.getGauss()+", gaussian strength filter need to be a > 0 !!! \n\n");
            //System.out.println(getHelperInfos());
            System.exit(1);
        }

        if(sip.getThresholdMaxima() < 0 ){
            System.out.println("-t "+sip.getThresholdMaxima()+", threshold for loops detection need to be a > 0 !!! \n\n");
            //System.out.println(getHelperInfos());
            System.exit(1);
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
            System.out.println("-i "+input+" => this file doesn't existed !!! \n\n");
            //System.out.println(getHelperInfos());
            System.exit(1);
        }

        file = new File(output);

        if(!file.exists()){
            System.out.println("-i "+output+" => this file doesn't existed !!! \n\n");
            //System.out.println(getHelperInfos());
            System.exit(1);
        }

        file = new File(chrSizeFile);
        if(!file.exists()){
            System.out.println("-i "+chrSizeFile+" => this file doesn't existed !!! \n\n");
            //System.out.println(getHelperInfos());
            System.exit(1);
        }

        if(!interOrIntra.equals("inter") && !interOrIntra.equals("intra")){
            System.out.println("-tl "+interOrIntra+", wrong value, choose inter or intra !!! \n\n");
            //System.out.println(getHelperInfos());
            System.exit(1);
        }
    }

    /**
     *
     * @param juicerTool
     * @param juicerNorm
     */
    public void testHiCOption(String juicerTool, String juicerNorm){

       if (!juicerNorm.equals("KR") && !juicerNorm.equals("NONE") && !juicerNorm.equals("VC") && !juicerNorm.equals("VC_SQRT")) {
           System.out.println("-norm = "+juicerNorm+", not defined for SIP, available norm: KR,NONE.VC,VC_SQRT\n Check the presence of this norm method in your hic file\n");
           //helper hic
           System.exit(0);
        }


        File file = new File(juicerTool);
        if(!file.exists()){
            System.out.println("-j "+juicerTool+" => this file doesn't existed !!! \n\n");
            //helper hic
            System.exit(0);
        }
    }

    /**
     *
     * @param factor
     */
    public void checkFactor(int factor){

        if(factor < 1 || factor  > 4 ){
            System.out.println("-f "+factor+", value for factor are 1, 2, 3 or 4 !!! \n\n");
          //  System.out.println(getHelperInfos());
            System.exit(1);
        }
    }

    /**
     *
     * @param sipIntra
     */
    public void speOption(SIPIntra sipIntra){

        if(sipIntra.getDiagonalSize() < 0 ){
            System.out.println("-d "+sipIntra.getDiagonalSize()+", diagonal size need to be a > 0 !!! \n\n");
            //System.out.println(getHelperInfos());
            System.exit(1);
        }

        if(sipIntra.getMin() < 0 ){
            System.out.println("-min "+sipIntra.getMin()+", min strength filter need to be a > 0 !!! \n\n");
            //System.out.println(getHelperInfos());
            System.exit(1);
        }

        if(sipIntra.getMax() < 0 ){
            System.out.println("-max "+sipIntra.getMax()+", max strength filter need to be a > 0 !!! \n\n");
            //System.out.println(getHelperInfos());
            System.exit(1);
        }

        if(sipIntra.getSaturatedPixel() < 0 ){
            System.out.println("-sat "+sipIntra.getSaturatedPixel()+", max strength filter need to be a > 0 !!! \n\n");
            //System.out.println(getHelperInfos());
            System.exit(1);
        }



    }



    /**
     *
     */
    public void testCoolOption() {

    }

}
