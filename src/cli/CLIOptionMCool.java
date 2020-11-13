package cli;

import org.apache.commons.cli.*;

/**
 *
 */
public class CLIOptionMCool extends CLISipOption{
    /** */
    private CommandLine _commandLine;
    /**
     *
     * @param args
     * @throws Exception
     */
    public CLIOptionMCool (String [] args)throws Exception{
        super(args);
        HelpFormatter formatter ;
        CommandLineParser parser= new DefaultParser();
        CommandLine cmd = this._cmd;
        String typeOfLoop = cmd.getOptionValue("tl");
        this._options.addOption(Option.builder("cooltools").longOpt("cooltools").required()
                .type(String.class).desc("Path to cooltools bin\n").numberOfArgs(1).build());

        this._options.addOption(Option.builder("cooler").longOpt("cooler").required()
                .type(String.class).desc("Path to cooler bin\n").numberOfArgs(1).build());


        if(typeOfLoop.equals("inter")){
            this.addInterChange();
        }else{
            this.addIntraParam();
        }
        try {
            _commandLine = parser.parse(this._options, args);
        }
        catch (ParseException  exp){
            System.out.println(exp.getMessage()+"\n");
            System.out.println(getHelperInfos());
            System.exit(1);
        }

    }

    /**
     *
     * @return
     */
    public CommandLine getCommandLine() {
        return _commandLine;
    }

}
