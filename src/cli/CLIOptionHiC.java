package cli;

import org.apache.commons.cli.*;

import java.io.File;

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

        this._options.addOption(Option.builder("n").longOpt("norm")
                .type(String.class).desc(" <NONE/VC/VC_SQRT/KR> (default KR)\n").numberOfArgs(1).build());



        String typeOfLoop = cmd.getOptionValue("tl");
        if(typeOfLoop.equals("inter")){
            this.addInterChange();
        }else{
            this.addIntraParam();
        }
        try {
            _commandLine = parser.parse(this._options, args);
            testParam();
        }
        catch (ParseException exp){
            System.out.println(exp.getMessage()+"\n");
            System.out.println(getHelperInfos());
            System.exit(1);
        }

    }

    private void testParam(){



        if (_cmd.hasOption("norm")) {
           String juicerNorm = _cmd.getOptionValue("norm");
            if (!juicerNorm.equals("KR") && !juicerNorm.equals("NONE") && !juicerNorm.equals("VC") && !juicerNorm.equals("VC_SQRT")) {
                System.out.println("-norm = "+juicerNorm+", not defined for SIP, available norm: KR,NONE.VC,VC_SQRT\n Check the presence of this norm method in your hic file\n");
                //helper hic
                System.exit(0);
            }
        }

        String juicerTools = _cmd.getOptionValue("juicerTool");
        File file = new File(juicerTools);
        if(!file.exists()){
            System.out.println("-j "+juicerTools+" => this file doesn't existed !!! \n\n");
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
