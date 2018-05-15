package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


public class ProcessTuplesFile {
	
	private  ArrayList<String> m_matrixFileList = new ArrayList<String>();
	private  ArrayList<BufferedWriter> m_lbw = new ArrayList<BufferedWriter>();
	private String m_file;
	private int m_nbFile = 0;
	private int m_step = 0;
	private int m_matrixSize = 0;
	private int m_res = 0;
	private String m_outdir = "";
	/**
	 * @param inputFile
	 * @param res
	 * @param matrixSize
	 * @param binNumber
	 * @param outdir
	 * @throws IOException 
	 */
	public ProcessTuplesFile(String inputFile, int res, int matrixSize, int step, int binNumber, String outdir) throws IOException{
		m_file = inputFile;
		double plop = (double) binNumber/(double)matrixSize*2;
		
		m_nbFile =( int)Math.round(plop);
		if(m_nbFile ==0 ) m_nbFile = 1;
		System.out.println("nb of image\t"+m_nbFile);
		m_step = step;
		m_matrixSize = matrixSize;
		m_res = res;
		m_outdir = outdir;
		File file = new File(m_outdir);
		if (file.exists()==false){file.mkdir();}
		this.readTupleInMatrix();
	}
	
	/**
	 * 
	 * @param res
	 * @param matrixSize
	 * @param binNumber
	 * @param outdir
	 * @throws IOException 
	 */
	public ProcessTuplesFile(int res, int matrixSize, int step, int binNumber, String outdir) throws IOException{
		double plop = (double) binNumber/(double)matrixSize*2;
		System.out.println(plop);
		m_nbFile =( int)Math.round(plop);
		m_step = step;
		m_matrixSize = matrixSize;
		m_res = res;
		m_outdir = outdir;
		fillList(false);
	}
	
	/**
	 * 
	 * @param sizeMat
	 * @return
	 * @throws IOException 
	 */
	private void  readTupleInMatrix() throws IOException{
		fillList(true);
		BufferedReader br = Files.newBufferedReader(Paths.get(m_file), StandardCharsets.UTF_8);
		for (String line = null; (line = br.readLine()) != null;){
			String[] parts = line.split("\\t");
			int a = Integer.parseInt(parts[0])/m_res;
			int b = Integer.parseInt(parts[1])/m_res;
			String newLine = a+"\t"+b+"\t"+parts[2];
			int dist = Math.abs(b-a);
			
			if(a <= b && dist < m_matrixSize){
				int test = (int)(b/m_step);
				if(test == 0){m_lbw.get(test).write(newLine+"\n"); }
				else if(test == m_nbFile && a >= test*m_step && b >= test*m_step){ m_lbw.get(test-1).write(newLine+"\n"); }
				else{
					if(a >= test*m_step && b >= test*m_step){ m_lbw.get(test).write(newLine+"\n"); }
					if(a >= (test-1)*m_step && b >= (test-1)*m_step){ m_lbw.get(test-1).write(newLine+"\n"); }
				}
			}	
		}
		br.close();
		for(int i = 0; i< m_lbw.size();++i){m_lbw.get(i).close();}
	}
	
	/**
	 * 
	 * @param createFile
	 * @param m_nbFile
	 * @param matrixSize
	 * @param step
	 * @param outdir
	 * @return
	 * @throws IOException
	 */
	private void fillList(boolean createFile) throws IOException
	{
		for(int i=0; i < m_nbFile;++i){
			String fileName = m_outdir;
			if(i == 0){	fileName = fileName+"0_"+m_matrixSize+".txt";}
			else{
				int a = i*m_step;
				int b = a+m_matrixSize;
				fileName = fileName+a+"_"+b+".txt";
			}
			if(createFile){
				BufferedWriter 	writer = new BufferedWriter(new FileWriter(new File(fileName)));
				m_lbw.add(writer);
			}
			m_matrixFileList.add(fileName);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public  ArrayList<String> getList(){ return m_matrixFileList; }
}