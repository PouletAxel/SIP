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

public class DumpData {

	private String m_logError = "";
	private String m_log = "";
	private String m_normalisation= "";
	private String m_hicFile = "";
	private int m_resolution;
	private static String m_juiceBoxTools = "";
	private ArrayList<Double> m_lExpected =  new ArrayList<Double>();
	
	public DumpData(String juiceboxTools,String hicFile, String norm, int resolution){
		m_juiceBoxTools = juiceboxTools;
		m_normalisation = norm;
		m_resolution = resolution;
		m_hicFile = hicFile;
		
		System.out.println(m_hicFile+"\n"
				+ m_juiceBoxTools+"\n"
				+m_resolution+"\n"
				+m_normalisation+"\n");
	}
	
	public DumpData() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * dump observed KR https://hicfiles.s3.amazonaws.com/hiseq/gm12878/in-situ/combined.hic 1:20480000:40960000 1:20480000:40960000 BP 10000 combined_10Kb.txt

	 * @param chr
	 * @param output
	 * @return
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
	 * 
	 * @param chr
	 * @param output
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
	 * 
	 * @param obs
	 * @param expected
	 * @param chr
	 * @throws IOException
	 */
	private void observedMExpected(String obs, String chr) throws IOException{
		//System.out.println("je passe par la: "+obs+"\t"+chr);
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
	 * 
	 * @return
	 */
	public String getLogError(){return this.m_logError;}
	/**
	 * 
	 * @return
	 */
	public String getLog(){return this.m_log;}
	
	
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
	 * 
	 * @author plop
	 *
	 */
	public class ReturnFlux extends Thread {  

		/**  Le flux à rediriger  */
		private InputStream flux;

		/**
		 * <b>Constructeur de RecuperationSorties</b>
		 * @param flux
		 *  Le flux à rediriger
		 */
		public ReturnFlux(InputStream flux){
			this.flux = flux;
		}
		
		@Override
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
