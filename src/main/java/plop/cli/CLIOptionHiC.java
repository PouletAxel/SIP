package plop.cli;

import org.apache.commons.cli.*;

public class CLIOptionHiC extends CLIOptionProcessed {



    public CLIOptionHiC(String [] args) {
        this._options.addOption(_inputFolder);
        this._options.addOption(_outputFolder);
        this._options.addOption(_chrSize);
        this._options.addOption(_interOrIntra);

        /*optional parameters*/
        this._options.addOption(_resolution);
        this._options.addOption(_deleteImage);
        this._options.addOption(_fdr);
        this._options.addOption(_nbZero);
        this._options.addOption(_sizeImage);
        this._options.addOption(_cpu);
        this._options.addOption(_gaussianStrength);
        this._options.addOption(_threshold);
        this._options.addOption(_diagonal);
        this._options.addOption(_factor);
        this._options.addOption(_max);
        this._options.addOption(_min);
        this._options.addOption(_saturated);
        this._options.addOption(_isDroso);
        this._options.addOption(Option.builder("j").longOpt("juicerTool").required()
                .type(String.class).desc("Path to juicerTool.jar\n").numberOfArgs(1).build());

        this._options.addOption(Option.builder("n").longOpt("norm")
                .type(String.class).desc("\n<NONE/VC/VC_SQRT/KR> (default KR)\n").numberOfArgs(1).build());

       try {
           this._cmd = this._parser.parse(this._options, args, false);
        }
        catch (ParseException exp){
            System.out.println("\n"+exp.getMessage()+"\n");
            CLIHelper.CmdHelpHiC();
        }catch (IllegalArgumentException exp){
           System.out.println( exp.getMessage());
           CLIHelper.CmdHelpHiC();
       }


    }


    /**
     *
     * @return
     */
    public CommandLine getCommandLine() {
        return _cmd;
    }

    /**
     *
     * @return
     */
    public Options getOptions() {
        return _options;
    }
}
