package process;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import sip.SIPIntra;
import sip.SIPObject;

import java.io.File;

import static cli.CLISipOption.getHelperInfos;

public class ParametersCheck {

    private SIPIntra _sipIntra;

    private SIPIntra _sipInter;

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
            System.out.println(getHelperInfos());
            System.exit(1);
        }

        file = new File(output);

        if(!file.exists()){
            System.out.println("-i "+output+" => this file doesn't existed !!! \n\n");
            System.out.println(getHelperInfos());
            System.exit(1);
        }

        file = new File(chrSizeFile);
        if(!file.exists()){
            System.out.println("-i "+chrSizeFile+" => this file doesn't existed !!! \n\n");
            System.out.println(getHelperInfos());
            System.exit(1);
        }

        if(!interOrIntra.equals("inter") && !interOrIntra.equals("intra")){
            System.out.println("-tl "+interOrIntra+", wrong value, choose inter or intra !!! \n\n");
            System.out.println(getHelperInfos());
            System.exit(1);
        }
    }

    /**
     *
     */
    public void testCommonParametersValidity(SIPObject sip){


       /*if(_cmd.hasOption("resolution")){
            int res = Integer.parseInt(_cmd.getOptionValue("resolution"));
            if(res <= 0 ){
                System.out.println("-r "+res+", resolution need to be a >= 0 !!! \n\n");
                System.out.println(getHelperInfos());
                System.exit(1);

            }
        }

        int nbZero = sip.getNbZero();
            if (nbZero > 24|| nbZero < 0) {
                System.out.println("\n-nbZero"+ nbZero+" value invalid: choose an integer value between 0 and 24\n");
                //erreur mettre l'aide et stopper le prog.

                System.exit(0);
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
        */
    }

    /**
     *
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
     */
    public void testCoolOption() {

    }

}
