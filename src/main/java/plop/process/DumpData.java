package plop.process;

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
 * Class aims to dump the data of the hic file. this classe need juicer_toolbox.jar to obtain the raw data and does the 2D images which will represent the HiC matrix.
 * If some error is detected a file log is created for that.
 * At the end this class a file by step is created (coordinate1	coordinate2 hic_value). The file created if the oMe option is chosse the substarction Observed-Expected
 * is done to obatin the value of the interaction between two bins.
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
public class DumpData {
	/** String to stock the error if need of juicerbox_tools*/
	private String _logError = "";
	/** String for the log*/
	private String _log = "";
	/** String => normalisation to dump the data (NONE, KR, VC, VC_SQRT or NONE)*/
	private String _normalisation= "";
	/** path to the hic file or url link*/
	private String _hicFile = "";
	/** path to juicer_toolsbox.jars*/
	private String _juiceBoxTools = "";
	/** List of doucle to stock the expected vector*/
	private ArrayList<Double> _lExpected =  new ArrayList<Double>();

	
	
	/**
	 * Constructor of this class to iniatilise the different variables
	 * 
	 * @param juiceboxTools: String: path of juicertoolsBox
	 * @param hicFile: String: path to the HiC file
	 * @param norm: String: type of normalisation
	 */
	public DumpData(String juiceboxTools,String hicFile, String norm) {
		this._juiceBoxTools = juiceboxTools;
		this._normalisation = norm;
		this._hicFile = hicFile;
	}
	
	/**
	 * Method to dump the oMe matrix
	 * 
	 * @param chr: String for the name of teh chromosome
	 * @param output: String path of the output
	 * @return
	 * @throws IOException
	 */
	public boolean dumpObservedMExpected(String chr, String output, int resolution) throws IOException{
		int exitValue=1;
		Runtime runtime = Runtime.getRuntime();
		String obs = output.replaceAll(".txt", "_obs.txt");
		try {
			String line = "java"+" -jar "+this._juiceBoxTools+" dump observed "+this._normalisation+" "+this._hicFile+" "+chr+" "+chr+" BP "+resolution+" "+obs;
			System.out.println(line);
			this._log = this._log+"\n"+obs+"\t"+line;
			Process process = runtime.exec(line);

			new ReturnFlux(process.getInputStream()).start();
			new ReturnFlux(process.getErrorStream()).start();
			exitValue=process.waitFor();		
		}
		catch (IOException e) {	e.printStackTrace();}
		catch (InterruptedException e) {e.printStackTrace();}
		observedMExpected(obs,output,resolution);
		if(_logError.contains("Exception")) {
			System.out.println(_logError);
			System.exit(0);
		}
		return exitValue==0;
	}
	
	/**
	 * Compute the value observed-expected, the norm vector and also the distance normalized value
	 *  and write the output file the "tuple": x y oMe DistanceNormalized.
	 * this method are only for the intra chromosomal interaction.
	 * 
	 * @param obs: String path with the file of the observed value
	 * @param chr: name of the chr
	 * @throws IOException
	 */
	private void observedMExpected(String obs, String chr, int resolution) throws IOException{
		BufferedReader br = Files.newBufferedReader(Paths.get(obs), StandardCharsets.UTF_8);
		BufferedWriter 	writer = new BufferedWriter(new FileWriter(new File(chr)));
		for (String line = null; (line = br.readLine()) != null;){
			String [] tline = line.split("\t");
			int dist = Math.abs((Integer.parseInt(tline[0])-Integer.parseInt(tline[1]))/resolution);
			if(!tline[2].equals("NaN")){
				double normalizedValue = (Double.parseDouble(tline[2])+1)/(this._lExpected.get(dist)+1);
				double oMe = Double.parseDouble(tline[2])-this._lExpected.get(dist);
				writer.write(tline[0]+"\t"+tline[1]+"\t"+oMe+"\t"+normalizedValue+"\n");
			}
		}
		File file = new File(obs);
		file.delete();
		writer.close();
		br.close();
	}
	
	
	/**
	 * getter of the logerror file if necessary
	 * 
	 * @return return the String with the error
	 */
	public String getLogError(){ return this._logError;}
	
	/**
	 * getter of the log info if necessary 
	 * @return return a String with the log info
	 */
	public String getLog(){	return this._log;}
	
	/**
	 * getter of the expected matrix. 
	 * 
	 * @param chr: String name of the chromosme
	 * @param output: path to the output
	 * @return
	 */
	public boolean getExpected(String chr,String output,int resolution){
		int exitValue=1;
		Runtime runtime = Runtime.getRuntime();
		String expected = output.replaceAll(".txt", "_expected.txt");
		String cmd = "java"+" -jar "+this._juiceBoxTools+" dump expected "+this._normalisation+" "+this._hicFile+" "+chr+" BP "+resolution+" "+expected;
		System.out.println(cmd);
		this._log = this._log+"\n"+expected+"\t"+cmd;
		Process process;
		try {
			process = runtime.exec(cmd);
		
			new ReturnFlux(process.getInputStream()).start();
			new ReturnFlux(process.getErrorStream()).start();
			exitValue=process.waitFor();
		
			BufferedReader br = Files.newBufferedReader(Paths.get(expected), StandardCharsets.UTF_8);
			for (String line = null; (line = br.readLine()) != null;)
				this._lExpected.add(Double.parseDouble(line));
			br.close();
			File file =  new File(expected);
			file.delete();
			
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		if(_logError.contains("Exception")){
			System.out.println(_logError);
			System.exit(0);
		}
		return exitValue==0;
	}
	
	/**
	 * getter of the expected matrix. 
	 * 
	 * @param chr: String name of the chromosme
	 * @param output: path to the output
	 * @return 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public boolean getNormVector(String chr,String output,int resolution) throws IOException, InterruptedException{
		int exitValue=1;
		Runtime runtime = Runtime.getRuntime();
		String cmd = "java"+" -jar "+this._juiceBoxTools+" dump norm VC "+this._hicFile+" "+chr+" BP "+resolution+" "+output;
		this._log = this._log+"\n"+output+"\t"+cmd;
		Process process = runtime.exec(cmd);
		new ReturnFlux(process.getInputStream()).start();
		new ReturnFlux(process.getErrorStream()).start();
		process.getOutputStream();
		exitValue=process.waitFor();
		if(_logError!="" &&  _logError.contains("Exception") == false){
			System.out.println("VC vector not find, SIP is using "+this._normalisation+" for "+ chr+"\n"+_logError);
			runtime = Runtime.getRuntime();
			_logError ="";
			cmd =  "java"+" -jar "+this._juiceBoxTools+" dump norm "+this._normalisation+" "+this._hicFile+" "+chr+" BP "+resolution+" "+output;
			System.out.println(cmd);
			process = runtime.exec(cmd);

			new ReturnFlux(process.getInputStream()).start();
			new ReturnFlux(process.getErrorStream()).start();
			exitValue=process.waitFor();

			if(_logError.contains("Exception")){
				System.out.println(_logError);
				System.out.println("juicer tool error !!!! "+ chr+" "+cmd);
				System.exit(0);
			}
		}else if ( _logError.contains("Exception")){
			System.out.println(_logError);
			System.out.println("juicer tool error !!!! "+ chr+" "+cmd);
			System.exit(0);

		}
		//System.out.println(_logError);
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
