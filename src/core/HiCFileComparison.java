package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import ij.ImagePlus;
import utils.DumpData;
import utils.Loop;
import utils.PeakAnalysisScore;
import utils.TupleFileImage;
import utils.WholeGenomeAnalysis;


public class HiCFileComparison {
	private String m_log = "";
	private WholeGenomeAnalysis m_wga;
	private Iterator<String> m_key;
	private DumpData m_dumpData1;
	private DumpData m_dumpData2;
	private HashMap<String,Integer> m_chrSize = new HashMap<String,Integer>();

	/**
	 * 
	 * @param hicFile1
	 * @param hicFile2
	 * @param chrSize
	 * @param juiceBoxTools
	 * @param normJuiceBox
	 * @param wga
	 */
	public HiCFileComparison(String hicFile1, String hicFile2, HashMap<String,Integer> chrSize, String juiceBoxTools, String normJuiceBox, WholeGenomeAnalysis wga){
		m_key = chrSize.keySet().iterator();
		m_chrSize = chrSize;
		m_wga = wga;
		m_dumpData1 = new DumpData(juiceBoxTools, hicFile1, normJuiceBox, m_wga.getResolution());
		m_dumpData2 = new DumpData(juiceBoxTools, hicFile2, normJuiceBox, m_wga.getResolution());
		System.out.println(hicFile1+"\t"+hicFile2+"\n");
	}
	
	
	
	/**
	 * dump observed KR https://hicfiles.s3.amazonaws.com/hiseq/gm12878/in-situ/combined.hic 1:20480000:40960000 1:20480000:40960000 BP 10000 combined_10Kb.txt
	 * 
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public void runOmE() throws IOException, InterruptedException{
		boolean juicerTools;
		while(m_key.hasNext()){
			ArrayList<String> file1 = new ArrayList<String>();
			ArrayList<String> file2 = new ArrayList<String>();
			String chr = m_key.next();
			String outdir = m_wga.getOutputDir()+File.separator+chr+File.separator;
			File file = new File(outdir);
			if (file.exists()==false){file.mkdir();}
			int chrsize = m_chrSize.get(chr);
			int step = m_wga.getStep()*m_wga.getResolution();
			int j = m_wga.getMatrixSize()*m_wga.getResolution();
			String test = chr+":0:"+j;
			String nameExpected = outdir+chr+"_0_"+j+"plopo.txt";
			m_dumpData1.getExpected(test,nameExpected);
			nameExpected = outdir+chr+"_0_"+j+"plopi.txt";
			m_dumpData2.getExpected(test,nameExpected);
			for(int i = 0 ; j < chrsize; i+=step,j+=step){
				int end =j-1;
				String dump = chr+":"+i+":"+end;
				String name = outdir+chr+"_"+i+"_"+end+"_1.txt";
				String outputName1 = outdir+"diff1_"+chr+"_"+i+"_"+end+".txt";
				System.out.println("start dump "+chr+" size "+chrsize+" dump "+dump);
				juicerTools = m_dumpData1.dumpObservedMExpected(dump,name);
				String name2 = outdir+chr+"_"+i+"_"+end+"_2.txt";
				String outputName2 = outdir+"diff2_"+chr+"_"+i+"_"+end+".txt";
				juicerTools = m_dumpData2.dumpObservedMExpected(dump,name2);
				compare(name,name2,outputName1,outputName2);
				File rm = new File(name);
				rm.delete();
				rm = new File(name2);
				rm.delete();
				if (juicerTools == false){
					System.out.print(dump+" "+"\n"+juicerTools+"\n"+m_log);
					System.exit(0);
				}
				if(j+step>chrsize){
					j= chrsize;
					i+=step;
					dump = chr+":"+i+":"+end;
					name = outdir+chr+"_"+i+"_"+end+"_1.txt";
					outputName1 = outdir+"diff1_"+chr+"_"+i+"_"+end+".txt";
					System.out.println("start dump "+chr+" size "+chrsize+" dump "+dump);
					juicerTools = m_dumpData1.dumpObservedMExpected(dump,name);
					name2 = outdir+chr+"_"+i+"_"+end+"_2.txt";
					outputName2 = outdir+"diff2_"+chr+"_"+i+"_"+end+".txt";
					juicerTools = m_dumpData2.dumpObservedMExpected(dump,name2);
					compare(name,name2,outputName1,outputName2);
					rm = new File(name);
					rm.delete();
					rm = new File(name2);
					rm.delete();
				}	
				
				file1.add(createImageFile(outputName1));
				file2.add(createImageFile(outputName2));
			}
			
			callLoop(file1);
		}
	}
	
	/**
	 * 
	 * @param fileList
	 * @param chr
	 * @throws IOException
	 */
	private String createImageFile(String input) throws IOException{
		TupleFileImage readFile = new TupleFileImage(input,m_wga.getMatrixSize(),m_wga.getStep(),m_wga.getResolution());
		String imageName = input.replaceAll(".txt", ".tif");
		ImagePlus imgRaw = readFile.readTupleFile();
		imgRaw.setTitle(imageName);
		m_wga.saveFile(imgRaw,imageName);
		return imageName;
	}

	/**
	 * 
	 * @param file
	 */
	private void callLoop(ArrayList<String> file){
		for(int i =0; i < file.size();++i){
			String[] tfile = file.get(i).split("_");
			int numImage = Integer.parseInt(tfile[tfile.length-2])/(m_wga.getStep()*m_wga.getResolution());
			tfile = file.get(i).split(File.separator);
			System.out.println(tfile[tfile.length-2]);
			/*ImagePlus img = imgRaw.duplicate();
			readFile.correctImage(img);
			ImagePlus imgFilter = img.duplicate();
			imageProcessing(imgFilter,fileList[i].toString());	
			
			
			HashMap<String,Loop> temp = ens.getDataMaxima();
			System.out.println("before "+ temp.size());
			removeMaximaCloseToZero(imgRaw,temp);
			System.out.println("after "+ temp.size());
		
			PeakAnalysisScore pas = new PeakAnalysisScore(imgRaw,temp,countNonZero);
			pas.computeScore();
		
			coord.setData(m_data);
			if(i == 0){	m_data = coord.imageToGenomeCoordinate(temp, true, false, numImage,countNonZero);}
			else if(i == fileList.length-1){	m_data = coord.imageToGenomeCoordinate(temp, false, true, numImage, countNonZero);}
			else m_data = coord.imageToGenomeCoordinate(temp, false, false, numImage, countNonZero)*/
		}
	}
	
	
	/**
	 * 
	 * @param file1
	 * @param file2
	 * @param output1
	 * @param output2
	 * @throws IOException
	 */
	
	private void compare(String file1, String file2, String output1, String output2) throws IOException{
		BufferedWriter writer1 = new BufferedWriter(new FileWriter(new File(output1)));
		BufferedWriter writer2 = new BufferedWriter(new FileWriter(new File(output2)));
		HashMap<String,Float> data =  new HashMap<String,Float>();
		BufferedReader br = new BufferedReader(new FileReader(file1));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();
		while (line != null){
			sb.append(line);
			String[] parts = line.split("\\t");				
			String name = parts[0]+"\t"+parts[1]; 
			if(parts[2].equals("NAN")) parts[2] = "0";
			float value = Float.parseFloat(parts[2]);
			if (value < 0 ) value = 0;
			data.put(name, value);
			sb.append(System.lineSeparator());
			line = br.readLine();
		}
		br.close();
		
		br = new BufferedReader(new FileReader(file2));
		sb = new StringBuilder();
		line = br.readLine();
		while (line != null){
			sb.append(line);
			String[] parts = line.split("\\t");				
			String name = parts[0]+"\t"+parts[1]; 
			if(parts[2].equals("NAN")) parts[2] = "0";
			float value = Float.parseFloat(parts[2]);
			if (value < 0 ) value = 0;
			if(data.containsKey(name)){
				float compare = data.get(name)-value;
				if(compare > 0 ){ writer1.write(name+"\t"+compare+"\n");}
				else if (compare < 0){writer2.write(name+"\t"+Math.abs(compare)+"\n");}
				data.remove(name);
			}
			else
				writer2.write(name+"\t"+value+"\n"); 
			sb.append(System.lineSeparator());
			line = br.readLine();
		}
		br.close();
		
		Iterator<String> key = data.keySet().iterator();
		while(key.hasNext()){
			String name = key.next();
			writer1.write(name+"\t"+data.get(name)+"\n");
		}
		writer1.close();
		writer2.close();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getLog(){ return m_log; }

}
