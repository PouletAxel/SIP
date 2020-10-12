package process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 */
public class DumpInterChromosomal {

    private String _juiceBoxTools;
    private String _hicFile;
    private String _normalisation;
    private String _log = "";
    private String _logError = "";

    /**
     *
     * @param juiceboxTools
     * @param hicFile
     * @param norm
     */
    public DumpInterChromosomal(String juiceboxTools,String hicFile, String norm) {
        this._juiceBoxTools = juiceboxTools;
        this._normalisation = norm;
        this._hicFile = hicFile;
    }


    /**
     *
     * @param chr1
     * @param chr2
     * @param obs
     * @param resolution
     * @return
     * @throws IOException
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
        catch (IOException e) {	e.printStackTrace();}
        catch (InterruptedException e) {e.printStackTrace();}

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
        public ReturnFlux(InputStream flux){this._flux = flux; }

        /**
         *
         */
        public void run(){
            try {
                InputStreamReader reader = new InputStreamReader(this._flux);
                BufferedReader br = new BufferedReader(reader);
                String line=null;
                while ( (line = br.readLine()) != null) {
                    if(line.contains("WARN")== false && line.contains("INFO")== false) _logError = _logError+line+"\n";
                }
            }
            catch (IOException ioe){
                ioe.printStackTrace();
            }
        }
    }
}
