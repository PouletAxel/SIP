package test;

import java.io.IOException;
import gui.GuiAnalysis;

/**
 * 
 * @author plop
 *
 */
public class TestGui {
	/**
	 * 
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
			System.out.println("Prout");
			String m_chrSizeFile = gui.getChrSizeFile();
			String m_output = gui.getOutputDir();
			String m_input = gui.getRawDataDir();
			int m_matrixSize = gui.getMatrixSize();
			int m_diagSize = gui.getDiagSize();
			int m_resolution = gui.getResolution();
			
			double m_gauss = gui.getGaussian();
			double m_max = gui.getMax();
			double m_min = gui.getMin();
			//m_nbZero = gui.getNbZero();
			double m_saturatedPixel = gui.getEnhanceSignal();
			int m_thresholdMax = gui.getNoiseTolerance();
			int m_factor = gui.getFactorChoice();
			boolean m_isObserved = gui.isObserved();
			boolean m_isHic  = gui.isHic();
			boolean m_isProcessed = gui.isProcessed();
			String m_juiceBoxTools = gui.getJuiceBox();
			String m_juiceBoXNormalisation = "KR";
			if(gui.isNONE()) m_juiceBoXNormalisation = "NONE";
			else if (gui.isVC()) m_juiceBoXNormalisation = "VC";
			else if (gui.isVC_SQRT()) m_juiceBoXNormalisation = "VC_SQRT";	
			int m_nbZero = gui.getNbZero();
			
			System.out.println("hic mode:\ninput: "+m_input
					+ "\noutput: "+m_output
					+ "\nChr size file: "+m_chrSizeFile
					+ "\njuiceBox: "+m_juiceBoxTools
					+"\nnorm: "+ m_juiceBoXNormalisation
					+"\ngauss: "+m_gauss
					+ "\nmin: "+m_min
					+"\nmax: "+m_max
					+"\nmatrix size: "+m_matrixSize
					+"\ndiag size: "+m_diagSize
					+"\nresolution: "+m_resolution
					+"\nsaturated pixel: "+m_saturatedPixel
					+"\nthreshold: "+m_thresholdMax
					+"\nisObserved: "+m_isObserved
					+"\nisHic: "+m_isHic
					+"\nisProcessed: "+m_isProcessed
					+"\n number of zero:"+m_nbZero
					+"\n factor "+ m_factor+"\n");


		}
		else {
			System.out.println("program NO Name closed: if you want the help: -h");
			System.exit(0);
		}
	}			
}