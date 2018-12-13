package core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import utils.DumpData;
import utils.HiCExperimentAnalysis;

/**
 * 
 * 
 * @author Axel Poulet
 *
 */
public class HicFileProcessing {
	/** Stock in String problem detected in juicerToolsBox*/
	private String m_log = "";
	/** WholeGenomeAnalysis object with all the information to creat intermidiary files and loops detection*/
	private HiCExperimentAnalysis m_wga;
	/** Name of the chromosome do create the file and dump the data*/
	private Iterator<String> m_chrName;
	/** DumpData object*/
	private DumpData m_dumpData;
	/** hashmap for the size and nome chromosome information*/
	private HashMap<String,Integer> m_chrSize = new HashMap<String,Integer>();
	
	
	/**
	 * Constructor of HicFileProcessing class
	 * @param hicFile String of the .hic file
	 * @param wga WholeGenomeAnalysis object which contain the info of the processing
	 * @param chrSize hashmap containing the chromosomes information name and size
	 * @param juiceBoxTools path to juicerTools.jar
	 * @param normJuiceBox Normalization (NONE, KR, VC_SQRT or SQRT) used to dump the data
	 */
	public HicFileProcessing(String hicFile, HiCExperimentAnalysis wga, HashMap<String,Integer> chrSize, String juiceBoxTools, String normJuiceBox){
		m_wga = wga;
		m_chrName = chrSize.keySet().iterator();
		m_chrSize = chrSize;
		m_dumpData = new DumpData(juiceBoxTools,hicFile,normJuiceBox, wga.getResolution());
		System.out.println(hicFile+"\n");
	}
	
	
	
	/**
	 * Run the method to dump the data on function of the different parameter,
	 * resolution
	 * size of the image => allow to run the chromosome by step
	 * create the file used then for the loops detection.
	 * 
	 * dump observed KR https://hicfiles.s3.amazonaws.com/hiseq/gm12878/in-situ/combined.hic 1:20480000:40960000 1:20480000:40960000 BP 10000 combined_10Kb.txt
	 * 
	 * @throws IOException catch exception if file problem
	 * @throws InterruptedException catch exception of program pb 
	 */
	
	public void run() throws IOException, InterruptedException{
		boolean juicerTools;
		while(m_chrName.hasNext()){
			String expected ="";
			String chr = m_chrName.next();
			String outdir = m_wga.getOutputDir()+File.separator+chr+File.separator;
			File file = new File(outdir);
			if (file.exists()==false){file.mkdir();}
			int chrsize = m_chrSize.get(chr);
			int step = m_wga.getStep()*m_wga.getResolution();
			int j = m_wga.getMatrixSize()*m_wga.getResolution();
			System.out.println(chrsize+" "+step+" "+j);
			String test = chr+":0:"+j;
			String name = outdir+chr+"_0_"+j+".txt";
			m_dumpData.getExpected(test,name);
			String normOutput = m_wga.getOutputDir()+File.separator+"normVector";
			file = new File(normOutput);
			if (file.exists()==false){file.mkdir();}
			m_dumpData.getNormVector(chr,normOutput+File.separator+chr+".norm");
			System.out.println(normOutput+File.separator+chr+".norm");
			System.out.println("start dump "+chr+" size "+chrsize);
			for(int i = 0 ; j < chrsize; i+=step,j+=step){
				int end =j-1;
				String dump = chr+":"+i+":"+end;
				name = outdir+chr+"_"+i+"_"+end+".txt";
				System.out.println("\tstart dump "+chr+" size "+chrsize+" dump "+dump);
				System.out.println(expected);
				juicerTools = m_dumpData.dumpObservedMExpected(dump,name);
				
				m_log = m_log+"\n"+m_dumpData.getLog();
				if (juicerTools == false){
					System.out.print(dump+" "+"\n"+juicerTools+"\n"+m_log);
					System.exit(0);
				}
				if(j+step > chrsize){
					j= chrsize;
					i+=step;
					dump = chr+":"+i+":"+j;
					name = outdir+chr+"_"+i+"_"+j+".txt";
					System.out.println("\tstart dump "+chr+" size "+chrsize+" dump "+dump);
					System.out.println(expected);
					juicerTools = m_dumpData.dumpObservedMExpected(dump,name);
					m_log = m_dumpData.getLog();
					if (juicerTools == false){
						System.out.print(dump+" "+"\n"+juicerTools+"\n"+m_log);
						System.exit(0);
					}
				}
			}	
			System.out.println("end dump "+chr);
		}
		m_wga.run();
	}
	
	/**
	 * Getter m_log stocking the juicertoolBox problems
	 * @return String m_log
	 * 
	 */
	public String getLog(){
		return m_log;
	}
}
