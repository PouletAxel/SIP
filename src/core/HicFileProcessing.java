package core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import utils.DumpData;
import utils.WholeGenomeAnalysis;

public class HicFileProcessing {
	
	private String m_log = "";
	private WholeGenomeAnalysis m_wga;
	private Iterator<String> m_key;
	private DumpData m_dumpData;
	private HashMap<String,Integer> m_chrSize = new HashMap<String,Integer>();
	
	public HicFileProcessing(String hicFile, WholeGenomeAnalysis wga, HashMap<String,Integer> chrSize, String juiceBoxTools, String normJuiceBox){
		m_wga = wga;
		m_key = chrSize.keySet().iterator();
		m_chrSize = chrSize;
		m_dumpData = new DumpData(juiceBoxTools,hicFile,normJuiceBox, wga.getResolution());
		System.out.println(hicFile+"\n");
	}
	
	
	
	/**
	 * dump observed KR https://hicfiles.s3.amazonaws.com/hiseq/gm12878/in-situ/combined.hic 1:20480000:40960000 1:20480000:40960000 BP 10000 combined_10Kb.txt
	 * 
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public void run(boolean isObserved) throws IOException, InterruptedException{
		boolean juicerTools;
		while(m_key.hasNext()){
			String expected ="";
			String chr = m_key.next();
			String outdir = m_wga.getOutputDir()+File.separator+chr+File.separator;
			File file = new File(outdir);
			if (file.exists()==false){file.mkdir();}
			int chrsize = m_chrSize.get(chr);
			int step = m_wga.getStep()*m_wga.getResolution();
			int j = m_wga.getMatrixSize()*m_wga.getResolution();
			if(isObserved == false){	
				String test = chr+":0:"+j;
				String name = outdir+chr+"_0_"+j+".txt";
				m_dumpData.getExpected(test,name);
			}
			System.out.println("start dump "+chr+" size "+chrsize);
			for(int i = 0 ; j < chrsize; i+=step,j+=step){
				int end =j-1;
				String dump = chr+":"+i+":"+end;
				String name = outdir+chr+"_"+i+"_"+end+".txt";
				System.out.println("\tstart dump "+chr+" size "+chrsize+" dump "+dump);
				if(isObserved) 
					juicerTools = m_dumpData.dumpObserved(dump,name);
				else{
					System.out.println(expected);
					juicerTools = m_dumpData.dumpObservedMExpected(dump,name);
				}
				m_log = m_log+"\n"+m_dumpData.getLog();
				if (juicerTools == false){
					System.out.print(dump+" "+"\n"+juicerTools+"\n"+m_log);
					System.exit(0);
				}
				if(j+step>chrsize){
					j= chrsize;
					i+=step;
					dump = chr+":"+i+":"+j;
					name = outdir+chr+"_"+i+"_"+j+".txt";
					System.out.println("\tstart dump "+chr+" size "+chrsize+" dump "+dump);
					juicerTools = m_dumpData.dumpObserved(dump,name);
					m_log = m_dumpData.getLog();
					if (juicerTools == false){
						System.out.print(dump+" "+"\n"+juicerTools+"\n"+m_log);
						System.exit(0);
					}
				}
			}
			
			System.out.println("end dump "+chr);
		}
		if (isObserved) m_wga.run("o");
		else m_wga.run("oMe");
	}
	
	/**
	 * 
	 * @return
	 */
	public String getLog(){ return m_log; }
}
