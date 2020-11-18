package test;

import java.io.IOException;
import java.util.ArrayList;

import gui.GuiAnalysis;
import multiProcesing.ProcessDumpData;
import sip.SIPIntra;


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
		SIPIntra sip ;
		GuiAnalysis gui = new GuiAnalysis();
		while( gui.isShowing()){
			try {Thread.sleep(1);}
			catch (InterruptedException e) {e.printStackTrace();}
	    }	
		if (gui.isStart()){
			System.out.println("test");
			String output = gui.getOutputDir();
			String input = gui.getInput();
			int matrixSize = gui.getMatrixSize();
			int diagSize = gui.getDiagSize();
			int resolution = gui.getResolution();
			double gauss = gui.getGaussian();
			double max = gui.getMax();
			double min = gui.getMin();
			double saturatedPixel = gui.getSaturatedPixel();
			int thresholdMax = gui.getThresholdMaxima();
			boolean isHic  = gui.isHic();
			boolean isProcessed = gui.isProcessed();
			String juiceBoxTools = gui.getJuicerTool();
			String juiceBoXNormalisation = "KR";
			if(gui.isNONE()) juiceBoXNormalisation = "NONE";
			else if (gui.isVC()) juiceBoXNormalisation = "VC";
			else if (gui.isVC_SQRT()) juiceBoXNormalisation = "VC_SQRT";	
			int nbZero = gui.getNbZero();
			 
			ArrayList<Integer> factor = new ArrayList<Integer>();
			factor.add(1);
			//factor.add(2);
			String chrSize = gui.getChrSizeFile();
			
			if(gui.isProcessed()==false){
				System.out.println("hic mode:\ninput: "+input+"\noutput: "+output+"\njuiceBox: "+juiceBoxTools+"\nnorm: "+ juiceBoXNormalisation+"\ngauss: "+gauss+"\n"
						+ "min: "+min+"\nmax: "+max+"\nmatrix size: "+matrixSize+"\ndiag size: "+diagSize+"\nresolution: "+resolution+"\nsaturated pixel: "+saturatedPixel
						+"\nthreshold: "+thresholdMax+"\n number of zero:"+nbZero+"\n ");
				sip = new SIPIntra(output, chrSize, gauss, min, max, resolution, saturatedPixel, thresholdMax, diagSize, matrixSize, nbZero,factor,0.01,false,true,2);
				sip.setIsGui(true);
				ProcessDumpData processDumpData = new ProcessDumpData();
				processDumpData.go(input, sip, juiceBoxTools, juiceBoXNormalisation);
			}else{
				System.out.println("processed mode:\ninput: "+input+"\noutput: "+output+"\njuiceBox: "+juiceBoxTools+"\nnorm: "+ juiceBoXNormalisation+"\ngauss: "+gauss
						+"\nmin: "+min+"\nmax: "+max+"\nmatrix size: "+matrixSize+"\ndiag size: "+diagSize+"\nresolution: "+resolution+"\nsaturated pixel: "+saturatedPixel
						+"\nthreshold: "+thresholdMax+"\nisHic: "+isHic+"\nisProcessed: "+isProcessed+"\n number of zero:"
						+nbZero+"\n");
				sip = new SIPIntra(input,output, chrSize, gauss, min, max, resolution, saturatedPixel, thresholdMax,
						diagSize, matrixSize, nbZero,factor,0.01,false, true ,2);

				sip.setIsProcessed(gui.isProcessed());
				sip.setIsGui(true);
			}
			
			//ProcessDetectLoops processDetectloops = new ProcessDetectLoops();
			//processDetectloops.go(sip, gui.getNbCpu(),gui.isDeletTif());
			
		}
	}
}