package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class CoolerExpected {
	
	/** String to stock the error if need of juicerbox_tools*/
	private String _logError = "";
	/** String for the log*/
	private String _log = "";
	/** path to the hic file or url link*/
	private String _coolFile = "";
	/** List of doucle to stock the expected vector*/
	private int _resolution = 0;
	private int _imgSize = 0;
	private String _expected;
	private String _coolTools = "";
	private HashMap<String, ArrayList<Double>> _hashExpected = new HashMap<String,ArrayList<Double>>();
	private int _cpu;
	
	public CoolerExpected(String cooltools, String coolFile, int resolution, int imageSize, int cpu){
		_coolTools = cooltools;
		this._resolution = resolution;
		this._imgSize = imageSize;
		this._coolFile = coolFile+"::/resolutions/"+this._resolution;
		_cpu = cpu;
	}
	
	public CoolerExpected(String expectedFile,  int imageSize){
		this._imgSize = imageSize;
		this._expected = expectedFile;
	}
	/**
	 * 
	 * @param output
	 * @param resolution
	 * @return
	 */
	public boolean dumpExpected(String expected){
		int exitValue=1;
		Runtime runtime = Runtime.getRuntime();
		String cmd = this._coolTools+" compute-expected "+_coolFile+" -p "+_cpu+" --ignore-diags 0 -o "+expected;
		System.out.println(cmd);
		this._log = this._log+"\n"+expected+"\t"+cmd;
		Process process;
		try {
			process = runtime.exec(cmd);
		
			new ReturnFlux(process.getInputStream()).start();
			new ReturnFlux(process.getErrorStream()).start();
			exitValue=process.waitFor();
		
			
			BufferedReader br = Files.newBufferedReader(Paths.get(expected), StandardCharsets.UTF_8);
			String line = br.readLine();
			for (line = null; (line = br.readLine()) != null;){
				String [] tline = line.split("\t");
				if(Integer.parseInt(tline[1]) < this._imgSize) {
					if (_hashExpected.containsKey(tline[0])){
						ArrayList<Double> lExpected =  _hashExpected.get(tline[0]);
						lExpected.add(Double.parseDouble(tline[6]));
						_hashExpected.put(tline[0], lExpected);
					}else {
						ArrayList<Double> lExpected =  new ArrayList<Double>();
						lExpected.add(Double.parseDouble(tline[6]));
						_hashExpected.put(tline[0], lExpected);
					}
					
				}
		}
		br.close();
		} catch (IOException | InterruptedException e) {
		e.printStackTrace();
		}
		if(_logError!=""){
			System.out.println(_logError);
			System.exit(0);
		}
		return exitValue==0;
	}
	
	
	
	public void parseExpectedFile() throws IOException {
		BufferedReader br = Files.newBufferedReader(Paths.get(_expected), StandardCharsets.UTF_8);
		String line = br.readLine();
		for (line = null; (line = br.readLine()) != null;){
			String [] tline = line.split("\t");
			if(Integer.parseInt(tline[1]) < this._imgSize) {
				if (_hashExpected.containsKey(tline[0])){
					ArrayList<Double> lExpected =  _hashExpected.get(tline[0]);
					lExpected.add(Double.parseDouble(tline[6]));
					_hashExpected.put(tline[0], lExpected);
				}else {
					ArrayList<Double> lExpected =  new ArrayList<Double>();
					lExpected.add(Double.parseDouble(tline[6]));
					_hashExpected.put(tline[0], lExpected);
				}
				
			}
	}
	br.close();
	}
	
	/**
	 * getter of the expected matrix. 
	 * 
	 * @param chr: String name of the chromosme
	 * @param output: path to the output
	 * @throws IOException 
	 */
	public ArrayList<Double> getExpected(String chr) throws IOException{
		return this._hashExpected.get(chr);
		
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
					if(line.contains("WARN")== false) _logError = _logError+line+"\n";
				}
			}
			catch (IOException ioe){
				ioe.printStackTrace();
			}
		}		
	}  
	

}
