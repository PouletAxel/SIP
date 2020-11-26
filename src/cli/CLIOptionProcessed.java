package cli;
import org.apache.commons.cli.*;

/**
 *
 */
public class CLIOptionProcessed {

    /** */
    Options _options= new Options();
    /** */
    CommandLine  _cmd;
    /** */
    Option _inputFolder= Option.builder("i").longOpt("input").required()
            .type(String.class).desc("Path to input .hic, .mcool or SIP folder containing processed data\n")
            .numberOfArgs(1).build();
    /** */
    Option _interOrIntra = Option.builder("lt").longOpt("loopsType").required()
            .type(String.class).desc("value: inter or intra, call loops inter chromosomal or intra chromosomal interactions\n")
            .numberOfArgs(1).build();
    /** */
    Option _outputFolder= Option.builder("o").longOpt("output").required()
            .type(String.class).desc("Path to output folder for SIP's files \n")
            .numberOfArgs(1).build();
    /** */
    Option _chrSize = Option.builder("c").longOpt("chrSize").required()
            .type(String.class).desc("Path to the chr size file, with the same name of the chr as in the hic file (i.e. chr1 does not match Chr1 or 1).\n")
            .numberOfArgs(1).build();
    /** */
    Option _resolution = Option.builder("r").longOpt("resolution").numberOfArgs(1)
            .type(Number.class).desc("Resolution in bp (default inter 5000 bp, intra 100 000 bp)\n").build();
    /** */
    Option _sizeImage = Option.builder("ms").longOpt("matrixSize")
            .type(Number.class).desc("Matrix size to use for each chunk of the chromosome (default intra 2000 bins, inter 500)\n")
            .numberOfArgs(1).build();
    /** */
    Option _cpu = Option.builder("cpu").longOpt("cpu")
            .type(Number.class).desc("Number of CPU used for SIP processing (default 1)\n")
            .numberOfArgs(1).build();
    /** */
    Option _gaussianStrength = Option.builder("g").longOpt("gaussian")
            .type(Number.class).desc("Gaussian filter: smoothing factor to reduce noise during primary loop detection (default intra 1.5, inter 1)\n")
            .numberOfArgs(1).build();
    /** */
    Option _threshold = Option.builder("t").longOpt("threshold")
            .type(Number.class).desc("Threshold for loops detection (default intra 2800, inter 0.9)\n")
            .numberOfArgs(1).build();
    /** */
    Option _nbZero = Option.builder("nb").longOpt("nbZero")
            .type(Number.class).desc("Number of zeros: number of pixels equal to zero that are allowed in the 24 pixels surrounding the detected loop (default intra 6; inter 3)\n")
            .numberOfArgs(1).build();
    /** */
    Option _fdr = Option.builder("fdr").longOpt("fdr")
            .type(Number.class).desc("Empirical FDR value for filtering based on random sites (default intra value: 0.01, inter value: 0.025)\n")
            .numberOfArgs(1).build();
    /** */
    Option _deleteImage = Option.builder("k").longOpt("keepImage").hasArg(false)
            .type(boolean.class).desc("keep tif in output folder if used\n")
            .numberOfArgs(0).build();

    /** */
    final Option _isDroso = Option.builder("isDroso").longOpt("isDroso").hasArg(false)
            .type(boolean.class).desc("If option use, apply extra filter to help detect loops similar to those found in D. mel cells (used only for intra chromosomal)\n")
            .build();
    /** */
    Option _diagonal = Option.builder("d").longOpt("diagonal")
            .type(Number.class).desc("diagonal size in bins, remove loops found at this size (eg: a size of 2 at 5000 bp resolution removes all loops\n" +
                    " detected at a distance inferior or equal to 10kb) (default: 6 bins, used only for intra chromosomal).\n")
            .numberOfArgs(1).build();
    /** */
    Option _factor = Option.builder("f").longOpt("factor")
            .type(Number.class).desc(" Multiple resolutions can be specified using (used only for intra chromosomal):\n" +
                    "\t\t-factor 1: run only for the input res (default)\n" +
                    "\t\t-factor 2: res and res*2\n" +
                    "\t\t-factor 3: res and res*5\n" +
                    "\t\t-factor 4: res, res*2 and res*5\n")
            .numberOfArgs(1).build();

    /** */
    Option _min = Option.builder("min").longOpt("minimum")
            .type(Number.class).desc("Minimum filter strength  (default 2, used only for intra chromosomal)\n")
            .numberOfArgs(1).build();
    /** */
    Option _max = Option.builder("max").longOpt("maximum")
            .type(Number.class).desc("Maximum filter strength (default 2, used only for intra chromosomal)\n")
            .numberOfArgs(1).build();
    /** */
    Option _saturated = Option.builder("sat").longOpt("saturated")
            .type(Number.class).desc("% of saturated pixel: enhances the contrast in the image (default 0.01, used only for intra chromosomal)\n")
            .numberOfArgs(1).build();

    /**
     * Command line parser
     */
    CommandLineParser _parser= new DefaultParser();


    public CLIOptionProcessed()  {

    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public CLIOptionProcessed(String[] args)  {
         /*required parameters*/
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

        try {
            _cmd = _parser.parse(this._options, args,false);
        }
        catch (ParseException  exp ){
            System.out.println("\n"+exp.getMessage()+"\n");
            CLIHelper.CmdHelpProcessed();
         }catch (IllegalArgumentException exp){
            System.out.println( exp.getMessage());
            System.exit(1);
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
