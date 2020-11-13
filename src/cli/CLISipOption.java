package cli;
import org.apache.commons.cli.*;

import java.io.File;

public class CLISipOption {

    Options _options= new Options();
    CommandLine  _cmd;

    private Option _inputFolder= Option.builder("i").longOpt("input").required()
            .type(String.class).desc("Path to input .hic, .mcool or SIP folder containing processed data\n")
            .numberOfArgs(1).build();
    private Option _outputFolder= Option.builder("o").longOpt("output").required()
            .type(String.class).desc("Path to output folder for SIP's files \n")
            .numberOfArgs(1).build();
    private Option _chrSize = Option.builder("c").longOpt("chrSize").required()
            .type(String.class).desc("Path to the chr size file, with the same name of the chr as in the hic file (i.e. chr1 does not match Chr1 or 1).\n")
            .numberOfArgs(1).build();
    private Option _resolution = Option.builder("r").longOpt("resolution")
            .type(Number.class).desc("Resolution in bp (default 5000 bp)\n")
            .numberOfArgs(1).build();
    private Option _sizeImage = Option.builder("m").longOpt("matrix")
            .type(Number.class).desc("Matrix size to use for each chunk of the chromosome (default 2000 bins)\n")
            .numberOfArgs(1).build();
    private Option _cpu = Option.builder("cpu")
            .type(Number.class).desc("Number of CPU used for SIP processing (default 1)\n")
            .numberOfArgs(1).build();
    private Option _gaussianStrength = Option.builder("g").longOpt("gaussian")
            .type(Number.class).desc("Gaussian filter: smoothing factor to reduce noise during primary maxima detection (default 1.5)\n")
            .numberOfArgs(1).build();
    private Option _threshold = Option.builder("t").longOpt("threshold")
            .type(Number.class).desc("Threshold for loops detection (default 2800)\n")
            .numberOfArgs(1).build();
    private Option _nbZero = Option.builder("nbZero")
            .type(Number.class).desc("Number of zeros: number of pixels equal to zero that are allowed in the 24 pixels surrounding the detected maxima (default 6)\n")
            .numberOfArgs(1).build();
    private Option _fdr = Option.builder("fdr")
            .type(Number.class).desc("Empirical FDR value for filtering based on random sites (default 0.01)\n")
            .numberOfArgs(1).build();
    private Option _deleteImage = Option.builder("d").longOpt("delete")
            .type(boolean.class).desc("Delete tif files used for loop detection (default true)\n")
            .build();
    private Option _interOrIntra = Option.builder("lt").longOpt("loopsType").required()
            .type(String.class).desc("(inter or intra), call loops inter chromosomal or intra chromosomal loops\n")
            .build();
    private Option _isDroso = Option.builder("isDroso")
            .type(boolean.class).desc("Default false, if true apply extra filter to help detect loops similar to those found in D. mel cells\n")
            .build();
    private Option _diagonal = Option.builder("d").longOpt("diagonal")
            .type(Number.class).desc("diagonal size in bins, remove the maxima found at this size (eg: a size of 2 at 5000 bp resolution removes all maxima\n" +
                    " detected at a distance inferior or equal to 10kb) (default 6 bins).\n")
            .numberOfArgs(1).build();
    private Option _factor = Option.builder("f").longOpt("factor")
            .type(Number.class).desc(" Multiple resolutions can be specified using:\n" +
                    "\t\t-factor 1: run only for the input res (default)\n" +
                    "\t\t-factor 2: res and res*2\n" +
                    "\t\t-factor 3: res and res*5\n" +
                    "\t\t-factor 4: res, res*2 and res*5\n")
            .numberOfArgs(1).build();

    private Option _min = Option.builder("min").longOpt("minimum")
            .type(Number.class).desc("Minimum filter strength  (default 2)\n")
            .numberOfArgs(1).build();
    private Option _max = Option.builder("max").longOpt("maximum")
            .type(Number.class).desc("Maximum filter strength (default 2)\n")
            .numberOfArgs(1).build();
    private Option _saturated = Option.builder("sat").longOpt("saturated")
            .type(Number.class).desc("% of saturated pixel: enhances the contrast in the image (default 0.01)\n")
            .numberOfArgs(1).build();

    private static String _Jversion ="1.6.1";
    /**
     *
     * @param args
     * @throws Exception
     */
    public CLISipOption(String[] args)throws Exception  {
        CommandLineParser parser= new DefaultParser();

        this._options.addOption(_inputFolder);
        this._options.addOption(_outputFolder);
        this._options.addOption(_chrSize);
        this._options.addOption(_resolution);
        this._options.addOption(_deleteImage);
        this._options.addOption(_fdr);
        this._options.addOption(_nbZero);
        this._options.addOption(_sizeImage);
        this._options.addOption(_cpu);
        this._options.addOption(_gaussianStrength);
        this._options.addOption(_threshold);
        this._options.addOption(_interOrIntra);
        try {
            _cmd = parser.parse(this._options, args);

        }
        catch (ParseException  exp){
            System.out.println(exp.getMessage()+"\n");
            System.out.println(getHelperInfos());
            System.exit(1);
         }
        this.testParam();
    }

    private void testParam(){
        String input = _cmd.getOptionValue("input");
        String output = _cmd.getOptionValue("output");
        String chrSizeFile = _cmd.getOptionValue("chrSize");
        File file = new File(input);

        if(!file.exists() && !input.startsWith("https")){
            System.out.println("-i "+input+"doesn't existed !!! \n\n");
            System.out.println(getHelperInfos());
            System.exit(1);
        }

        file = new File(output);
        if(!file.exists()){
            System.out.println("-o "+output+"doesn't existed !!! \n\n");
            System.out.println(getHelperInfos());
            System.exit(1);
        }

        file = new File(chrSizeFile);
        if(!file.exists()){
            System.out.println("-c "+chrSizeFile+"doesn't existed !!! \n\n");
            System.out.println(getHelperInfos());
            System.exit(1);
        }

        String inter ;
    }

    /**
     *
     * @return
     */
    public static String  getHelperInfos() {
            return "More details :\n" +
                    "java -jar SIPHiC"+_Jversion+".jar hic --help \n" +
                    "or \n"+
                    "java -jar SIPHiC-"+_Jversion+".jar cool -h or --help\n"+
                    "or \n" +
                    "java -jar SIPHiC-"+_Jversion+".jar processed -h or --help \n" +
                    "\n\nCommand line eg:\n" +
                    "\tjava -jar SIP_HiC.jar hic -i hicFile -c chrSizeFile -o Output -j juicerTool [options]\n" +
                    "\tjava -jar SIP_HiC.jar cool -i mcoolFile -c chrSizeFile -o Output -cooltools cooltoolsPath -cooler coolerPath [options]\n" +
                    "\tjava -jar SIP_HiC.jar processed  -i PathDirectoryWithProcessedData -c chrSizeFile -o Output [options]\n" +
                    "\nAuthors:\n" +
                    "Axel Poulet\n" +
                    "\tDepartment of Molecular, Cellular  and Developmental Biology Yale University 165 Prospect St\n" +
                    "\tNew Haven, CT 06511, USA\n" +
                    "\nM. Jordan Rowley\n" +
                    "\tDepartment of Genetics, Cell Biology and Anatomy, University of Nebraska Medical Center Omaha,NE 68198-5805\n" +
                    "\nContact: pouletaxel@gmail.com OR jordan.rowley@unmc.edu\n";
    }



    /**
     *
     */
    public void addInterChange(){
        this._gaussianStrength.setDescription("Gaussian filter: smoothing factor to reduce noise during primary maxima detection (default 1)\n");
        this._fdr.setDescription("Empirical FDR value for filtering based on random sites (default 0.025)\n");
        this._resolution.setDescription("Resolution in bp (default 100000 bp)\n");
        this._sizeImage.setDescription("Matrix size to use for each chunk of the chromosome (default 500 bins)\n");
        this._threshold.setDescription("Threshold for loops detection (default 0.9)\n");
    }


    /**
     *
     */
    public void addIntraParam(){
        this._options.addOption(_diagonal);
        this._options.addOption(_factor);
        this._options.addOption(_max);
        this._options.addOption(_min);
        this._options.addOption(_saturated);
        this._options.addOption(_isDroso);
    }

}
