package process;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;

public class OptionalParamaterCheck {

    private CommandLine _cmd;

    /**
     *
     * @param commandLine
     */
    public OptionalParamaterCheck(CommandLine commandLine){
       _cmd = commandLine;
    }


    public void testOptionalParametersValueCommons(){

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

    }
    public void testOptionalParametersValueIntra(){

        if(_cmd.hasOption("min")) {
          double  min = Double.parseDouble(_cmd.getOptionValue("min"));
            if(min  < 0){
                System.out.println("\n-min"+ min+": this parameter need to be >= 0\n");
                //trouver commande sortir l'aide propre
                System.exit(0);
            }
        }

        if(_cmd.hasOption("max")) {
           double max = Double.parseDouble(_cmd.getOptionValue("max"));
            if(max  < 0){
                System.out.println("\n-max"+ max+": this parameter need to be >= 0\n");

                System.exit(0);
            }
        }

        if(_cmd.hasOption("diagonal")) {
            int diagSize = Integer.parseInt(_cmd.getOptionValue("diagonal"));
            if (diagSize < 0) {
                System.out.println("\n-d" + diagSize + ": this parameter need to be >= 0\n");

                System.exit(0);
            }
        }
    }

}
