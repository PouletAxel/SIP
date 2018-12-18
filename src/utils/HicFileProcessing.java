package utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import gui.Progress;

/**
 * Object for the hic or hichip, dump the data then find the loop
 * Use juicer tools box.jar
 * Neva C. Durand, Muhammad S. Shamim, Ido Machol, Suhas S. P. Rao, Miriam H. Huntley, Eric S. Lander, and Erez Lieberman Aiden.
 *  "Juicer provides a one-click system for analyzing loop-resolution Hi-C experiments." Cell Systems 3(1), 2016.
 * @author Axel Poulet
 *
 */
public class HicFileProcessing {
	/** Stock in String problem detected in juicerToolsBox*/
	private String _log = "";
	/** WholeGenomeAnalysis object with all the information to creat intermidiary files and loops detection*/
	private HiCExperimentAnalysis _hicExp;
	/** Name of the chromosome do create the file and dump the data*/
	private Iterator<String> _chrName;
	/** DumpData object*/
	private DumpData _dumpData;
	/** hashmap for the size and nome chromosome information*/
	private HashMap<String,Integer> _chrSize = new HashMap<String,Integer>();
	
	
	/**
	 * Constructor of HicFileProcessing class
	 * @param hicFile String of the .hic file
	 * @param hicExp WholeGenomeAnalysis object which contain the info of the processing
	 * @param chrSize hashmap containing the chromosomes information name and size
	 * @param juiceBoxTools path to juicerTools.jar
	 * @param normJuiceBox Normalization (NONE, KR, VC_SQRT or SQRT) used to dump the data
	 */
	public HicFileProcessing(String hicFile, HiCExperimentAnalysis hicExp, HashMap<String,Integer> chrSize, String juiceBoxTools, String normJuiceBox){
		this._hicExp = hicExp;
		this._chrName = chrSize.keySet().iterator();
		this._chrSize = chrSize;
		this._dumpData = new DumpData(juiceBoxTools,hicFile,normJuiceBox, hicExp.getResolution());
		System.out.println(hicFile+"\n");
	}
	
	/**
	 * Run the method to dump the data on function of the different parameter,
	 * resolution
	 * size of the image => allow to run the chromosome by step
	 * create the file used then for the loops detection.
	 * dump observed KR https://hicfiles.s3.amazonaws.com/hiseq/gm12878/in-situ/combined.hic 1:20480000:40960000 1:20480000:40960000 BP 10000 combined_10Kb.txt
	 * 
	 * @param gui true => run with gui else command line
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void run(boolean gui) throws IOException, InterruptedException{
		boolean juicerTools;
		Progress p = new Progress("Dump data step",_chrSize.size());
		int nb = 0;
		p.bar.setValue(nb);
		while(this._chrName.hasNext()){
			String expected ="";
			String chr = this._chrName.next();
			String outdir = this._hicExp.getOutputDir()+File.separator+chr+File.separator;
			File file = new File(outdir);
			if (file.exists()==false){file.mkdir();}
			int chrsize = this._chrSize.get(chr);
			int step = this._hicExp.getStep()*this._hicExp.getResolution();
			int j = this._hicExp.getMatrixSize()*this._hicExp.getResolution();
			System.out.println(chrsize+" "+step+" "+j);
			String test = chr+":0:"+j;
			String name = outdir+chr+"_0_"+j+".txt";
			this._dumpData.getExpected(test,name);
			String normOutput = this._hicExp.getOutputDir()+File.separator+"normVector";
			file = new File(normOutput);
			if (file.exists()==false){file.mkdir();}
			this._dumpData.getNormVector(chr,normOutput+File.separator+chr+".norm");
			System.out.println(normOutput+File.separator+chr+".norm");
			System.out.println("start dump "+chr+" size "+chrsize);
			for(int i = 0 ; j < chrsize; i+=step,j+=step){
				int end =j-1;
				String dump = chr+":"+i+":"+end;
				name = outdir+chr+"_"+i+"_"+end+".txt";
				System.out.println("\tstart dump "+chr+" size "+chrsize+" dump "+dump);
				System.out.println(expected);
				juicerTools = this._dumpData.dumpObservedMExpected(dump,name);
				
				_log = _log+"\n"+_dumpData.getLog();
				if (juicerTools == false){
					System.out.print(dump+" "+"\n"+juicerTools+"\n"+_log);
					System.exit(0);
				}
				if(j+step > chrsize){
					j= chrsize;
					i+=step;
					dump = chr+":"+i+":"+j;
					name = outdir+chr+"_"+i+"_"+j+".txt";
					System.out.println("\tstart dump "+chr+" size "+chrsize+" dump "+dump);
					System.out.println(expected);
					juicerTools = this._dumpData.dumpObservedMExpected(dump,name);
					this._log = this._dumpData.getLog();
					if (juicerTools == false){
						System.out.print(dump+" "+"\n"+juicerTools+"\n"+_log);
						System.exit(0);
					}
				}
			}
			++nb;
			p.bar.setValue(nb);
			System.out.println("end dump "+chr);
		}
		if (gui) this._hicExp.runGUI();
		else this._hicExp.run();
	}
	
	/**
	 * Getter m_log stocking the juicertoolBox problems
	 * @return String m_log
	 * 
	 */
	public String getLog(){	return this._log;}
}
