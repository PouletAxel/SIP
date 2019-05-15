package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import gui.GuiAnalysis;
import utils.HiCExperimentAnalysis;
import utils.HicFileProcessing;

/**
 * test of the GUI
 * 
 * @author Axel Poulet
 *
 */
public class TestGui {
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException{
		GuiAnalysis gui = new GuiAnalysis();
		while( gui.isShowing()){
			try {Thread.sleep(1);}
			catch (InterruptedException e) {e.printStackTrace();}
	    }	
		if (gui.isStart()){
			System.out.println("test");
			String chrSizeFile = gui.getChrSizeFile();
			String output = gui.getOutputDir();
			String input = gui.getRawDataDir();
			int matrixSize = gui.getMatrixSize();
			int diagSize = gui.getDiagSize();
			int resolution = gui.getResolution();
			
			double gauss = gui.getGaussian();
			double max = gui.getMax();
			double min = gui.getMin();
			double saturatedPixel = gui.getEnhanceSignal();
			int thresholdMax = gui.getNoiseTolerance();
			int factorChoice = gui.getFactorChoice();
			boolean isHic  = gui.isHic();
			boolean isProcessed = gui.isProcessed();
			String juiceBoxTools = gui.getJuiceBox();
			String juiceBoXNormalisation = "KR";
			if(gui.isNONE()) juiceBoXNormalisation = "NONE";
			else if (gui.isVC()) juiceBoXNormalisation = "VC";
			else if (gui.isVC_SQRT()) juiceBoXNormalisation = "VC_SQRT";	
			int nbZero = gui.getNbZero();
			 
			System.out.println("hic mode:\ninput: "+input
					+ "\noutput: "+output
					+ "\nChr size file: "+chrSizeFile
					+ "\njuiceBox: "+juiceBoxTools
					+"\nnorm: "+ juiceBoXNormalisation
					+"\ngauss: "+gauss
					+ "\nmin: "+min
					+"\nmax: "+max
					+"\nmatrix size: "+matrixSize
					+"\ndiag size: "+diagSize
					+"\nresolution: "+resolution
					+"\nsaturated pixel: "+saturatedPixel
					+"\nthreshold: "+thresholdMax
					+"\nisHic: "+isHic
					+"\nisProcessed: "+isProcessed
					+"\n number of zero:"+nbZero
					+"\n factor "+ factorChoice+"\n");
			ArrayList<Integer> factor = new ArrayList<Integer>();
			factor.add(1);
			factor.add(2);
			HashMap<String,Integer> chrSize = readChrSizeFile(gui.getChrSizeFile());
			
			HiCExperimentAnalysis hicExp = new HiCExperimentAnalysis(output, chrSize,
					gauss, min, max, resolution,
					saturatedPixel, thresholdMax, diagSize, matrixSize, nbZero
					,factor,0.01);
			hicExp.setIsHichip(gui.isHiChIP());
			if(isHic){
				System.out.println("hic mode:\ninput: "+input+"\noutput: "+output+"\njuiceBox: "+juiceBoxTools+"\nnorm: "+ juiceBoXNormalisation+"\ngauss: "+gauss+"\n"
						+ "min: "+min+"\nmax: "+max+"\nmatrix size: "+matrixSize+"\ndiag size: "+diagSize+"\nresolution: "+resolution+"\nsaturated pixel: "+saturatedPixel
						+"\nthreshold: "+thresholdMax+"\n number of zero:"+nbZero+"\n ");
				HicFileProcessing hfp =  new HicFileProcessing(input, hicExp, chrSize, juiceBoxTools, juiceBoXNormalisation);
				hfp.run(true);
			}else if (isProcessed){
				System.out.println("processed mode:\ninput: "+input+"\noutput: "+output+"\njuiceBox: "+juiceBoxTools+"\nnorm: "+ juiceBoXNormalisation+"\ngauss: "+gauss
						+"\nmin: "+min+"\nmax: "+max+"\nmatrix size: "+matrixSize+"\ndiag size: "+diagSize+"\nresolution: "+resolution+"\nsaturated pixel: "+saturatedPixel
						+"\nthreshold: "+thresholdMax+"\nisHic: "+isHic+"\nisProcessed: "+isProcessed+"\n number of zero:"
						+nbZero+"\n");
				hicExp.runGUI(input);
			}
		}
		else {
			System.out.println("program NO Name closed: if you want the help: -h");
			System.exit(0);
		}
	}
	
	/**
	 * Run the input file and stock the info of name chr and their size in hashmap
	 * @param chrSizeFile path chr size file
	 * @throws IOException if file does't exist
	 */
	private static HashMap<String,Integer> readChrSizeFile( String chrSizeFile) throws IOException{
		HashMap<String,Integer> m_chrSize =  new HashMap<String,Integer>();
		BufferedReader br = new BufferedReader(new FileReader(chrSizeFile));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();
		while (line != null){
			sb.append(line);
			String[] parts = line.split("\\t");				
			String chr = parts[0]; 
			int size = Integer.parseInt(parts[1]);
			m_chrSize.put(chr, size);
			sb.append(System.lineSeparator());
			line = br.readLine();
		}
		br.close();
		return m_chrSize;
	}
}