package cli;

import org.apache.commons.cli.*;

public class CLIOptionHiC extends CLISipOption{

    /** */
    private CommandLine _commandLine;

    public CLIOptionHiC(String [] args)throws Exception{
        super(args);
        HelpFormatter formatter ;
        CommandLineParser parser= new DefaultParser();
        CommandLine cmd = this._cmd;

        this._options.addOption(Option.builder("j").longOpt("juicerTool").required()
                .type(String.class).desc("Path to juicerTool.jar\n").numberOfArgs(1).build());

        this._options.addOption(Option.builder("n").longOpt("norm").required()
                .type(String.class).desc(" <NONE/VC/VC_SQRT/KR> (default KR)\n").numberOfArgs(1).build());



        String typeOfLoop = cmd.getOptionValue("tl");
        if(typeOfLoop.equals("inter")){
            this.addInterChange();
        }else{
            this.addIntraParam();
        }
        try {
            _commandLine = parser.parse(this._options, args);
        }
        catch (ParseException exp){
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
