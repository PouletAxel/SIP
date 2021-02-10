package sipMain;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import cli.CLIHelper;
import gui.GuiAnalysis;
import process.HiC;
import process.Cool;
import process.Processed;

/**
 * 
 * command line eg:
 * java -jar SIP_HiC.jar processed inputDirectory pathToChromosome.size OutputDir .... paramaters
 * java -jar SIP_HiC.jar hic inputDirectory pathToChromosome.size OutputDir juicer_tools.jar
 * 
 * @author axel poulet 
 *
 */
public class Hic_main {

			
	/**
	 * Main function to run all the process, can be run with gui or in command line.
	 * With command line with 1 or less than 5 parameter => run only the help
	 * With zero parameter only java -jar SIP.jar  => gui
	 * With more than 5 paramter => command line mode
	 * 
	 * @param args table with parameters for command line
	 * @throws IOException  exception
	 * @throws InterruptedException exception
	 */
	public static void main(String[] args) throws IOException, InterruptedException {


		//command line test:
		// hic -i /home/plop/Desktop/SIP/GSE104333_Rao-2017-treated_6hr_combined_30.hic -o /home/plop/Desktop/test -j /home/plop/Tools/juicer_tools_1.19.02.jar -c /home/plop/Desktop/SIP/testSize.tab -lt intra
		/*CLI */
		if(args.length == 1) {
			CLIHelper.getHelperAllInfos();
		}else if(args.length > 1) {
			String [] argsSubset = Arrays.copyOfRange(args, 1, args.length);
			if (args[0].equals("hic")) {
				if(args[1].equals("-h") || args[1].equals("--help"))
					CLIHelper.CmdHelpHiC();
				HiC hic = new HiC(argsSubset);
				hic.run();

			}else if (args[0].equals("processed")) {
				if(args[1].equals("-h") || args[1].equals("--help"))
					CLIHelper.CmdHelpProcessed();

				Processed processed = new Processed(argsSubset);
				processed.run();

			}else if (args[0].equals("cool")) {
				if(args[1].equals("-h") || args[1].equals("--help"))
					CLIHelper.CmdHelpCool();

				Cool cool = new Cool(argsSubset);
				cool.run();

			}else
				CLIHelper.getHelperAllInfos();

		}
		/*GUI */
		else{
			GuiAnalysis gui = new GuiAnalysis();
			while( gui.isShowing()){
				try {Thread.sleep(1);}
				catch (InterruptedException e) {e.printStackTrace();}
			}
			if (gui.isStart()){
				if(gui.isHic()) {
					HiC hic = new HiC(gui);
					hic.run();
				}else if(gui.isCool()){
					Cool cool = new Cool(args);
					cool.run();

				}else if(gui.isProcessed()){
					Processed processed = new Processed(gui);
					processed.run();
				}
			}else {
				System.out.println("\nSIP closed: ");
				CLIHelper.getHelperAllInfos();
			}
		}

/*
		
		SIPIntra sip;
		if(_isHic){
			sip = new SIPIntra(_output, _chrSizeFile, _gauss, _min, _max, _resolution, _saturatedPixel,
					_thresholdMax, _diagSize, _matrixSize, _nbZero, _factor,_fdr, _isProcessed,_isDroso);
			sip.setIsGui(_gui);
			ProcessDumpHic processDumpData = new ProcessDumpHic();
			processDumpData.go(_input, sip, _juiceBoxTools, _juiceBoXNormalisation, _cpu);
			System.out.println("########### End of the dump Step");
		}else if(_isCool){
			sip = new SIPIntra(_output, _chrSizeFile, _gauss, _min, _max, _resolution, _saturatedPixel, _thresholdMax, _diagSize, _matrixSize, _nbZero, _factor,_fdr, _isProcessed,_isDroso);
			sip.setIsCooler(_isCool);

			ProcessDumpCooler processDumpData = new ProcessDumpCooler();
			processDumpData.go(_cooltools, _cooler, sip, _input, _cpu);
			
			}else{

			sip = new SIPIntra(_input,_output, _chrSizeFile, _gauss, _min, _max, _resolution, _saturatedPixel, _thresholdMax,
					_diagSize, _matrixSize, _nbZero,_factor,_fdr);
			sip.setIsDroso(_isDroso);
			sip.setIsProcessed(_isProcessed);
			sip.setIsGui(_gui);
		}
		System.out.println("Start loop detction step");
		MultiResProcess multi = new MultiResProcess(sip, _cpu, _delImages,_chrSizeFile);
		multi.run();
		System.out.println("###########End loop detction step");

	*/
	}

	
}
