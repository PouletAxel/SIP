package dumpProcessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * dump data contained in mcool file via cooler tools
 *
 * https://github.com/open2c/cooler
 * Abdennur, N., and Mirny, L. (2019). Cooler: scalable storage for Hi-C data and other genomically labeled arrays. Bioinformatics. doi: 10.1093/bioinformatics/btz540.
 */
public class CoolerDumpInter {


    /** String to stock the error if need of juicerbox_tools*/
    private String _logError = "";
    /** String for the log*/
    private String _log = "";
    /** path to the hic file or url link*/
    private String _coolFile;
    /** List of doucle to stock the expected vector*/
    private ArrayList<Double> _lExpected =  new ArrayList<Double>();
    /** path to cooler tools*/
    private String _cooler ;


    /**
     * Constructor of this class to iniatilise the different variables
     *
     * @param cooler path to cooler bin
     * @param coolFile path of mcool file
     */
    public CoolerDumpInter(String cooler, String coolFile) {
        this._coolFile = coolFile;
        this._cooler = cooler;
    }
    /**
     * Method to dump the oMe matrix
     *
     * @param chr: String for the name of teh chromosome
     * @param output: String path of the output
     * @return boolean
     * @throws IOException exception
     */
    public boolean dumpObserved(String chr, String chr2,  String output, int resolution) throws IOException{
        int exitValue=1;
        Runtime runtime = Runtime.getRuntime();
        String obs = output.replaceAll(".txt", "_obs.txt");
        try {
            String cool = this._coolFile+"::/resolutions/"+resolution;
            String line = this._cooler+" dump "+cool+" --balanced -r "+chr+" -r2 "+ chr2+" -o "+obs +" --join --na-rep NaN";
            System.out.println(line);
            this._log = this._log+"\n"+obs+"\t"+line;
            Process process = runtime.exec(line);

            new ReturnFlux(process.getInputStream()).start();
            new ReturnFlux(process.getErrorStream()).start();
            exitValue=process.waitFor();
        }
        catch (IOException | InterruptedException e) {	e.printStackTrace();}
        if(_logError!=""){
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
                    if(!line.contains("WARN")) _logError = _logError+line+"\n";
                }
            }
            catch (IOException ioe){
                ioe.printStackTrace();
            }
        }
    }
}
