package plop.sipMain;
import java.io.IOException;
import java.util.Arrays;

import plop.cli.CLIHelper;
import plop.gui.GuiAnalysis;
import plop.process.HiC;
import plop.process.Cool;
import plop.process.Processed;

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
	 * Main function to run all the java.plop.process, can be run with java.plop.gui or in command line.
	 * With command line with 1 or less than 5 parameter => run only the help
	 * With zero parameter only java -jar SIP.jar  => java.plop.gui
	 * With more than 5 paramter => command line mode
	 * 
	 * @param args table with parameters for command line
	 * @throws IOException  exception
	 * @throws InterruptedException exception
	 */
	public static void main(String[] args) throws IOException, InterruptedException {


		//command line java.plop.test:
		// hic -i /home/plop/Desktop/SIP/GSE104333_Rao-2017-treated_6hr_combined_30.hic -o /home/plop/Desktop/java.plop.test -j /home/plop/Tools/juicer_tools_1.19.02.jar -c /home/plop/Desktop/SIP/testSize.tab -lt intra
		/*CLI */
		if(args.length == 1) {
			CLIHelper.getHelperAllInfos();
		}else if(args.length > 1) {
			String [] argsSubset = Arrays.copyOfRange(args, 1, args.length);
			if (args[0].equals("hic")) {
				if(args[1].equals("-h") || args[1].equals("--help")) {
					CLIHelper.CmdHelpHiC();
				}else if(args.length > 2){
					HiC hic = new HiC(argsSubset);
					hic.run();
				}else{
					CLIHelper.CmdHelpHiC();
				}

			}else if (args[0].equals("processed")) {
				if(args[1].equals("-h") || args[1].equals("--help")) {
					CLIHelper.CmdHelpProcessed();
				}else if(args.length > 2){
					Processed processed = new Processed(argsSubset);
					processed.run();
				}else{
					CLIHelper.CmdHelpProcessed();
				}
			}else if (args[0].equals("cool")) {
				if(args[1].equals("-h") || args[1].equals("--help")) {
					CLIHelper.CmdHelpCool();
				}else if(args.length > 2){
					Cool cool = new Cool(argsSubset);
					cool.run();
				}else{
					CLIHelper.CmdHelpCool();
				}
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
		
		SIPIntra java.plop.sip;
		if(_isHic){
			java.plop.sip = new SIPIntra(_output, _chrSizeFile, _gauss, _min, _max, _resolution, _saturatedPixel,
					_thresholdMax, _diagSize, _matrixSize, _nbZero, _factor,_fdr, _isProcessed,_isDroso);
			java.plop.sip.setIsGui(_gui);
			ProcessDumpHic processDumpData = new ProcessDumpHic();
			processDumpData.go(_input, java.plop.sip, _juiceBoxTools, _juiceBoXNormalisation, _cpu);
			System.out.println("########### End of the dump Step");
		}else if(_isCool){
			java.plop.sip = new SIPIntra(_output, _chrSizeFile, _gauss, _min, _max, _resolution, _saturatedPixel, _thresholdMax, _diagSize, _matrixSize, _nbZero, _factor,_fdr, _isProcessed,_isDroso);
			java.plop.sip.setIsCooler(_isCool);

			ProcessDumpCooler processDumpData = new ProcessDumpCooler();
			processDumpData.go(_cooltools, _cooler, java.plop.sip, _input, _cpu);
			
			}else{

			java.plop.sip = new SIPIntra(_input,_output, _chrSizeFile, _gauss, _min, _max, _resolution, _saturatedPixel, _thresholdMax,
					_diagSize, _matrixSize, _nbZero,_factor,_fdr);
			java.plop.sip.setIsDroso(_isDroso);
			java.plop.sip.setIsProcessed(_isProcessed);
			java.plop.sip.setIsGui(_gui);
		}
		System.out.println("Start loop detction step");
		MultiResProcess multi = new MultiResProcess(java.plop.sip, _cpu, _delImages,_chrSizeFile);
		multi.run();
		System.out.println("###########End loop detction step");

	*/
	}

	
}
