package process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Class aims to dump the data of the hic file. this class need juicer_toolbox.jar to obtain the raw data and does the 2D images which will represent the HiC matrix.
 * If some error is detected a file log is created for that.
 * At the end this class a file by step is created (coordinate1	coordinate2 hic_value). The file created if the oMe option is chose the subtraction Observed-Expected
 * is done to obtain the value of the interaction between two bins.
 * If observed is used the value will be the observed.
 *
 * You can also chosse the option of the normalisation for the hic matrix, the different normalisations are these one available in juicertoolnox.jar
 * (https://github.com/theaidenlab/juicer/wiki).
 *
 * eg of commad line use : dump observed KR https://hicfiles.s3.amazonaws.com/hiseq/gm12878/in-situ/combined.hic 1:20480000:40960000 1:20480000:40960000 BP 10000 combined_10Kb.txt
 *
 * Neva C. Durand, Muhammad S. Shamim, Ido Machol, Suhas S. P. Rao, Miriam H. Huntley, Eric S. Lander, and Erez Lieberman Aiden. "Juicer provides a
 * one-click system for analyzing loop-resolution Hi-C experiments." Cell Systems 3(1), 2016.
 *
 * @author axel poulet
 *
 */
public class DumpInterChromosomal {

    /** path of juiceBox  */
    private String _juiceBoxTools;
    /** path of the hic file*/
    private String _hicFile;
    /** normalization method name*/
    private String _normalisation;
    /** log*/
    private String _log = "";
    /** logError*/
    private String _logError = "";

    /**
     *Constructor
     *
     * @param juiceboxTools path to juicer tools
     * @param hicFile path to the hic file
     * @param norm normalization methode used
     */
    public DumpInterChromosomal(String juiceboxTools,String hicFile, String norm) {
        this._juiceBoxTools = juiceboxTools;
        this._normalisation = norm;
        this._hicFile = hicFile;
    }


    /**
     *
     * @param chr1 string containing name and coordinate of interest eg: chr2+":"+startChr2+":"+end2
     * @param chr2 string containing name and coordinate of interest eg: chr2+":"+startChr2+":"+end2
     * @param obs string path output observed create
     * @param resolution  bin resolution
     * @return boolean
     * @throws IOException exception
     */
    public boolean dumpObserved(String chr1, String chr2, String obs, int resolution) throws IOException {
        int exitValue=1;
        Runtime runtime = Runtime.getRuntime();
        try {
            String line = "java"+" -jar "+this._juiceBoxTools+" dump observed "+this._normalisation+" "+this._hicFile+" "+chr1+" "+chr2+" BP "+resolution+" "+obs;
            System.out.println(line);
            this._log = this._log+"\n"+obs+"\t"+line;
            Process process = runtime.exec(line);

            new DumpInterChromosomal.ReturnFlux(process.getInputStream()).start();
            new DumpInterChromosomal.ReturnFlux(process.getErrorStream()).start();
            exitValue=process.waitFor();
        }
        catch (IOException | InterruptedException e) {	e.printStackTrace();}

        if(_logError.contains("Exception")) {
            System.out.println(_logError);
            System.exit(0);
        }
        return exitValue==0;
    }

    /**
     * Class to run command line in java
     * @author axel poulet
     *
     */
    public class ReturnFlux extends Thread {

        /**  Flux to redirect  */
        private InputStream _flux;

        /**
         * <b>Constructor of ReturnFlux</b>
         * @param flux
         *  flux to redirect
         */
        private ReturnFlux(InputStream flux){this._flux = flux; }

        /**
         *
         */
        public void run(){
            try {
                InputStreamReader reader = new InputStreamReader(this._flux);
                BufferedReader br = new BufferedReader(reader);
                String line=null;
                while ( (line = br.readLine()) != null) {
                    if(!line.contains("WARN") && !line.contains("INFO")) _logError = _logError+line+"\n";
                }
            }
            catch (IOException ioe){
                ioe.printStackTrace();
            }
        }
    }
}
