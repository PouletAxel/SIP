package core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import utils.DumpData;

public class HicFileProcessing {
	
	
	WholeGenomeAnalysis m_wga;
	Iterator<String> m_key;
	DumpData m_dumpaData;
	String m_nameDumpFile;
	
	public HicFileProcessing(String hicFile, WholeGenomeAnalysis wga, HashMap<String,Integer> chrSize, String juiceBoxTools, String normJuiceBox){
		m_wga = wga;
		m_key = chrSize.keySet().iterator();
		m_nameDumpFile = wga.getOutputDir();
		m_dumpaData = new DumpData(juiceBoxTools,hicFile,normJuiceBox, wga.getResolution());
		
		System.out.println(hicFile+"\n");
	}
	
	public void runObserved() throws IOException{
		while(m_key.hasNext()){
			String chr = m_key.next();
			System.out.println("start dump "+chr);
			boolean prout = m_dumpaData.dumpObserved(chr,m_nameDumpFile+File.separator+chr+".txt");
			String plopLog = m_dumpaData.getLog();
			if (prout == false){
				System.out.print(chr+" "+m_nameDumpFile+"\n"+prout+"\n"+plopLog);
				System.exit(0);
			}
			System.out.println("end dump "+chr);
		}
		m_wga.runAll("o");
	}
	

	public void runOmE() throws IOException{
		while(m_key.hasNext()){
			String chr = m_key.next();
			System.out.println("start dump "+chr);
			boolean prout = m_dumpaData.dumpObservedMExpected(chr,m_nameDumpFile+File.separator+chr+".txt");
			String plopLog = m_dumpaData.getLog();
			if (prout == false){
				System.out.print(chr+"\n"+prout+"\n"+plopLog);
				System.exit(0);
			}
			System.out.println("end dump "+chr);
		}
		m_wga.runAll("oMe");
	}

}
