package cli;

import org.apache.commons.cli.HelpFormatter;

import java.io.PrintWriter;

public class CLIHelper {

    /** */
    private static String _version ="1.6.1";

    /** */
    private static String _hic ="java -jar SIPHiC"+ _version +".jar hic ";
    /** */
    private static String _cool ="java -jar SIPHiC"+ _version +".jar cool ";
    /** */
    private static String processed ="java -jar SIPHiC"+ _version +".jar processed ";

    /* Constructor*/

    public CLIHelper(){  }


    public static void main(String[] args) throws Exception{

        CmdHelpCool();
    }



    /**
     * Method get help for command line
     * with example command line
     */
     public static void CmdHelpHiC() {
        String argument =  "-i path/to/hicFile.hic -c path/to/chrSizeFile.txt -o path/to/output/folder -j path/to/juicerTool.jar -lt intra ";
        String[] argv = argument.split(" ");
        CLIOptionHiC command = new CLIOptionHiC (argv);
        String usage = _cool+argument+" [options]";
        HelpFormatter formatter = new HelpFormatter();

        System.out.println("\nHelp for "+_cool+"!!!!!!! \n");
        formatter.printHelp(200, usage, "SIP_HiC option hic : ", command.getOptions(),getAuthors());
        System.exit(1);
     }


    /**
     * Method get help for command line
     * with example command line
     */
    public static void CmdHelpCool() {
        String argument =  "-i path/to/hicFile.hic -c path/to/chrSizeFile.txt -o path/to/output/folder -cooltools path/to/cooltools -cooler path/to/cooler -lt intra ";
        String[] argv = argument.split(" ");
        CLIOptionCool command = new CLIOptionCool (argv);
        String usage = _hic+argument+" [options]";
        HelpFormatter formatter = new HelpFormatter();

        System.out.println("\nHelp for "+_hic+"!!!!!!! \n");
        formatter.printHelp(200, usage, "SIP_HiC option hic : ", command.getOptions(),getAuthors());
        System.exit(1);
    }


    /**
     *
     * @return
     */
    public static String  getHelperInfos() {
        return "More details :\n" +
                "java -jar SIPHiC"+ _version +".jar hic --help \n" +
                "or \n"+
                "java -jar SIPHiC-"+ _version +".jar cool -h or --help\n"+
                "or \n" +
                "java -jar SIPHiC-"+ _version +".jar processed -h or --help \n" +
                "\n\nCommand line g:\n" +
                "\tjava -jar SIP_HiC.jar hic -i hicFile -c chrSizeFile -o Output -j juicerTool -tl inter [options]\n" +
                "\tjava -jar SIP_HiC.jar cool -i mcoolFile -c chrSizeFile -o Output -cooltools cooltoolsPath -cooler coolerPath -tl inter [options]\n" +
                "\tjava -jar SIP_HiC.jar processed  [options]\n";

    }

    /**
     *
     * @return
     */
    public static String getAuthors() {
        return  "Authors:\n" +
                "\nAxel Poulet\n" +
                "Department of Molecular, Cellular  and Developmental Biology Yale University \n" +
                "\nM. Jordan Rowley\n" +
                "Department of Genetics, Cell Biology and Anatomy, University of Nebraska Medical\n" +
                "\nContact: pouletaxel@gmail.com OR jordan.rowley@unmc.edu\n";
    }



    /**
     *
     * @return
     */
    private String getCool() {
        return "-i mcoolFile -c chrSizeFile -o Output -cooltools cooltoolsPath -cooler coolerPath -tl intra";
    }

    /**
     *
     * @return
     */
    private String getPocessed() {
        return  "-i PathDirectoryWithProcessedData -c chrSizeFile --o Output -tl intra ";
    }

}
