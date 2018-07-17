package utils;

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
	private String m_logError = "";
	/** String for the log*/
	private String m_log = "";
	/** String => normalisation to dump the data (NONE, KR, VC, VC_SQRT or NONE)*/
	private String m_normalisation= "";
	/** path to the hic file or url link*/
	private String m_hicFile = "";
	/** int in base for the bin resolution (5000,10000, etc)*/
	private int m_resolution;
	/** path to juicer_toolsbox.jars*/
	private static String m_juiceBoxTools = "";
	/** List of doucle to stock the expected vector*/
	private ArrayList<Double> m_lExpected =  new ArrayList<Double>();
	
	
	/**
	 * Constructor of this class to iniatilise the different variables
	 * 
	 * @param juiceboxTools: String: path of juicertoolsBox
	 * @param hicFile: String: path to the HiC file
	 * @param norm: String: type of normalisation
	 * @param resolution: int: resolution of the bins 
	 */
	public DumpData(String juiceboxTools,String hicFile, String norm, int resolution){
		m_juiceBoxTools = juiceboxTools;
		m_normalisation = norm;
		m_resolution = resolution;
		m_hicFile = hicFile;
	}
	
	/**
	 * 
	 * Dump the observed matrix.
	 * Only call the juicerToolsbox command line with all the paramaters.
	 * 
	 * @param chr: String for the chromosome to dump
	 * @param output: String for the output file with the dumped data
	 * @return: boolean
	 * @throws IOException
	 */
	public boolean dumpObserved(String chr, String output) throws IOException{
		int exitValue=1;
		Runtime runtime = Runtime.getRuntime();
		try {
			Process process = runtime.exec("java"+" -jar "+m_juiceBoxTools+" dump observed "+
											m_normalisation+" "+m_hicFile+" "+chr+" "+chr+" BP "+m_resolution+" "+output);
			new ReturnFlux(process.getInputStream()).start();
			new ReturnFlux(process.getErrorStream()).start();
			exitValue=process.waitFor();
		}
		catch (IOException e) {	e.printStackTrace();}
		catch (InterruptedException e) {e.printStackTrace();}
		return exitValue==0;
	}
	
	/**
	 * Method to dump the oMe matrix
	 * 
	 * @param chr: String for the name of teh chromosome
	 * @param output: String path of the output
	 * @return
	 * @throws IOException
	 */
	public boolean dumpObservedMExpected(String chr, String output) throws IOException{
		int exitValue=1;
		Runtime runtime = Runtime.getRuntime();
		String obs = output.replaceAll(".txt", "_obs.txt");
		
		try {
			String line = "java"+" -jar "+m_juiceBoxTools+" dump observed "+m_normalisation+" "+m_hicFile+" "+chr+" "+chr+" BP "+m_resolution+" "+obs;
			m_log = m_log+"\n"+obs+"\t"+line;
			Process process = runtime.exec(line);
			new ReturnFlux(process.getInputStream()).start();
			new ReturnFlux(process.getErrorStream()).start();
			exitValue=process.waitFor();
			
		}
		catch (IOException e) {	e.printStackTrace();}
		catch (InterruptedException e) {e.printStackTrace();}
		observedMExpected(obs,output);
		return exitValue==0;
	}
	
	
	
	
	/**
	 * Compute the value observed-expected, and write the output file the tuple: x y value.
	 * this methode are only for the intra chromosomal interaction.
	 * 
	 * @param obs: String path with the file of the observed value
	 * @param chr: name of the chr
	 * @throws IOException
	 */
	private void observedMExpected(String obs, String chr) throws IOException{
		BufferedReader br = Files.newBufferedReader(Paths.get(obs), StandardCharsets.UTF_8);
		System.out.println(chr);
		BufferedWriter 	writer = new BufferedWriter(new FileWriter(new File(chr)));
		for (String line = null; (line = br.readLine()) != null;){
			String [] tline = line.split("\t");
			int dist = Math.abs((Integer.parseInt(tline[0])-Integer.parseInt(tline[1]))/m_resolution);
			if(!tline[2].equals("NaN")){
				double oMe = Double.parseDouble(tline[2])-m_lExpected.get(dist);
				writer.write(tline[0]+"\t"+tline[1]+"\t"+oMe+"\n");
			}
		}
		
		File file = new File(obs);
		file.delete();
		writer.close();
	}
	
	
	/**
	 * getter of the logerror file if necessary
	 * 
	 * @return return the String with the error
	 */
	public String getLogError(){return this.m_logError;}
	
	/**
	 * getter of the lof info if necessary 
	 * @return return a String with the log info
	 */
	public String getLog(){return this.m_log;}
	
	/**
	 * getter of the expected matrix. 
	 * 
	 * @param chr: String name of the chromosme
	 * @param output: path to the output
	 * @return 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public boolean getExpected(String chr,String output) throws IOException, InterruptedException{
		int exitValue=1;
		Runtime runtime = Runtime.getRuntime();
		String expected = output.replaceAll(".txt", "_expected.txt");
		String cmd = "java"+" -jar "+m_juiceBoxTools+" dump expected "+m_normalisation+" "+m_hicFile+" "+chr+" BP "+m_resolution+" "+expected;
		m_log = m_log+"\n"+expected+"\t"+cmd;
		Process process = runtime.exec(cmd);
		
		new ReturnFlux(process.getInputStream()).start();
		new ReturnFlux(process.getErrorStream()).start();
		exitValue=process.waitFor();
		
		BufferedReader br = Files.newBufferedReader(Paths.get(expected), StandardCharsets.UTF_8);
		for (String line = null; (line = br.readLine()) != null;)
			m_lExpected.add(Double.parseDouble(line));
		br.close();
		
		File file =  new File(expected);
		file.delete();
		return exitValue==0;
	}
	
	
	/**
	 * Class to run command line in java
	 * @author axel poulet
	 *
	 */
	public class ReturnFlux extends Thread {  

		/**  Flux to redirect  */
		private InputStream flux;

		/**
		 * <b>Constructor of ReturnFlux</b>
		 * @param flux
		 *  flux to redirect
		 */
		public ReturnFlux(InputStream flux){
			this.flux = flux;
		}
		
		/**
		 * 
		 */
		public void run(){
			try {    
				InputStreamReader reader = new InputStreamReader(flux);
				BufferedReader br = new BufferedReader(reader);
				String line=null;
				while ( (line = br.readLine()) != null) m_logError = m_logError+line+"\n";
			}
			catch (IOException ioe){
				ioe.printStackTrace();
			}
		}
		
	} 



}
