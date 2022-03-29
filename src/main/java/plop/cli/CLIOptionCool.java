package plop.cli;

import org.apache.commons.cli.*;

/**
 *
 */
public class CLIOptionCool extends CLIOptionProcessed {

    /**
     *
     * @param args
     * @throws Exception
     */
    public CLIOptionCool(String [] args){
        this._options.addOption(_inputFolder);
        this._options.addOption(_outputFolder);
        this._options.addOption(_chrSize);

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

        this._options.addOption(Option.builder("cooltools").longOpt("cooltools").required()
                .type(String.class).desc("Path to cooltools bin\n").numberOfArgs(1).build());
        this._options.addOption(Option.builder("cooler").longOpt("cooler").required()
                .type(String.class).desc("Path to cooler bin\n").numberOfArgs(1).build());


        try {
            _cmd = _parser.parse(this._options, args,false);
        }
        catch (ParseException  exp){
            System.out.println("\n"+exp.getMessage()+"\n");
            CLIHelper.CmdHelpCool();
        }catch (IllegalArgumentException exp){
            System.out.println( exp.getMessage());
            CLIHelper.CmdHelpCool();
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
