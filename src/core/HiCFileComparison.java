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
import java.util.Set;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import utils.CoordinatesCorrection;
import utils.FindMaxima;
import utils.Loop;
import utils.PeakAnalysisScore;
import utils.ProcessMethod;
import utils.TupleFileToImage;

/**
 * Class to compare two experiments of HiC with the same resolution. The loops for the individual file have to be call. Then the program dump the observed and expected value
 * with the help juicer tools box. Foreach chromosome the program first compute the observed minu expected matrices for the two experiments, then we do the difference (hic1-hic2).
 * Two files are created, one for the positive value (higher value in hic1)  and the other for the negative value (higher value for hic2). the file contains the value for the image.
 * The size of the image is dependent of the matrix size parameter,
 * 
 * 
 * @author axel poulet
 *
 */
public class HiCFileComparison {
	/** */
	private int m_resolution;
	/** */
	private int m_step;
	/** */
	private int m_matrixSize;
	/** */
	private int m_diagSize = 4;
	/** */
	private double m_gauss =0.5;
	/** */
	private double m_min = 1;
	/** */
	private double m_threshold = 3;
	/** */
	private String m_dir1;
	/** */
	private String m_dir2;
	/** */
	private String m_outdir1;
	/** */
	private String m_outdir2;
	/** */
	private HashMap<String,Loop> m_data1 = new HashMap<String,Loop>();
	/** */
	private HashMap<String,Loop> m_data2 = new HashMap<String,Loop>();
	/** */
	private HashMap<String,String> m_ref1 = new HashMap<String,String>();
	/** */
	private HashMap<String,String> m_ref2 = new HashMap<String,String>();
	
	/**
	 * Constrcutor 
	 * 
	 * @param dir1 String: path of the first condition
	 * @param bedFile1 String: path of the loops file results for the first condition
	 * @param dir2 String path of the second condition
	 * @param bedFile2 String: path of the loops file results for the second condition
	 * @param outdir String: path of the output directory
	 * @param res int: bin resolution
	 * @param matrixSize int: size of the image
	 * @throws IOException
	 */
	public HiCFileComparison(String dir1, String bedFile1, String dir2, String bedFile2, String outdir, int res, int matrixSize) throws IOException{
		m_step =matrixSize/2;
		m_resolution = res;
		m_matrixSize =matrixSize;
		m_outdir1 = outdir+File.separator+"1";
		m_outdir2 = outdir+File.separator+"2";
		m_dir1 = dir1;
		m_dir2 = dir2;
		File file = new File(m_outdir1);
		if (file.exists()==false)
			file.mkdir();
		file = new File(m_outdir2);
		if (file.exists()==false)
			file.mkdir();
		m_ref1 = readFile(bedFile1);
		m_ref2 = readFile(bedFile2);
	}
	
	
	
	/**
	 * run the comparison between the two conditions.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void run() throws IOException, InterruptedException{
		File file = new File(m_dir1);
        File[] files = file.listFiles();
        for(int i = 0; i < files.length; ++i ){
        	ArrayList<String> file1 = new ArrayList<String>();
			ArrayList<String> file2 = new ArrayList<String>();
			if (files[i].isDirectory() == true){
				String [] tab = files[i].toString().split(File.separator);
				String chr = tab[tab.length-1];
				String outDir1Chr = m_outdir1+File.separator+chr;
				File fileOut = new File(outDir1Chr);
				if (fileOut.exists() == false)
					fileOut.mkdir();
				String outDir2Chr = m_outdir2+File.separator+tab[tab.length-1];
				file = new File(outDir2Chr);
				if (file.exists() == false)
					file.mkdir();
				outDir2Chr = outDir2Chr.replaceAll(m_dir1, m_dir2);
				File fileChr = new File(files[i].toString());
				File[] tFileChr = fileChr.listFiles();
				for(int j = 0; j < tFileChr.length; ++j){
					if (tFileChr[j].isFile() == true && tFileChr[j].toString().contains(".txt")){
						String bisFile = tFileChr[j].toString();
						tab = bisFile.split(File.separator);
						String fileName = tab[tab.length-1];
						bisFile = bisFile.replaceAll(m_dir1, m_dir2);
						String outputName1 = outDir1Chr+File.separator+fileName;
						String outputName2 = outDir2Chr+File.separator+fileName;
						compare(tFileChr[j].toString(),bisFile,outputName1,outDir2Chr+File.separator+fileName);
						file1.add(makingOfImageFile(outputName1));
						file2.add(makingOfImageFile(outputName2));
					}
				}
				callLoop(file1,chr.toString(),m_outdir1+File.separator+"loopsDiff1.bed",m_data1,m_ref1);
				callLoop(file2,chr,m_outdir2+File.separator+"loopsDiff2.bed",m_data2,m_ref2);
			}
		}
	}
	
	/**
	 * method making the image after comaparison, value positiove are the pixel higher in the first condition, and negative higher
	 * value in the second condition 
	 * @param input path of the input file (tuple file)
	 * @return the path of the image
	 * @throws IOException
	 */
	private String makingOfImageFile(String input) throws IOException{
		TupleFileToImage readFile = new TupleFileToImage(input,m_matrixSize,m_resolution);
		String imageName = input.replaceAll(".txt", ".tif");
		ImagePlus imgRaw = readFile.readTupleFile();
		imgRaw.setTitle(imageName);
		readFile.correctImage(imgRaw);
		FileSaver fileSaver = new FileSaver(imgRaw);
	    fileSaver.saveAsTiff(imageName);
		return imageName;
	}

	/**
	 * Detection of the loop of the differential images
	 * 
	 * @param file liste of string; stock the path of each file of interest
	 * @param chr name of the chr
	 * @param output path of the output directory
	 * @param data colection of loop
	 * @param ref reference loop 
	 * @throws IOException
	 */
	private void callLoop(ArrayList<String> file, String chr, String output,HashMap<String,Loop> data,HashMap<String,String> ref) throws IOException{
		CoordinatesCorrection coord = new CoordinatesCorrection(m_resolution);
		for(int i =0; i < file.size();++i){
			String[] tfile = file.get(i).split("_");
			int numImage = Integer.parseInt(tfile[tfile.length-2])/(m_step*m_resolution);
			tfile = file.get(i).split(File.separator);
			ImagePlus img = IJ.openImage(file.get(i));
			ImagePlus imgFilter = img.duplicate();
			ProcessMethod pm = new ProcessMethod(imgFilter,m_gauss);
			pm.runGaussian();
			pm.runMin(m_min);
			FindMaxima findLoop = new FindMaxima(img, imgFilter, chr, m_threshold, m_diagSize, m_resolution);
			HashMap<String,Loop> temp = findLoop.findloopCompare();
			//System.out.println("before: "+temp.size());
			//temp = removeMaximaCloseToZero(img,temp,false);
			
			PeakAnalysisScore pas = new PeakAnalysisScore(img,temp);
			pas.computeScoreCompareMethod();
			coord.setData(data);
			System.out.println("prout: "+data.size());
			data = coord.imageToGenomeCoordinateCompare(temp, numImage);
		}
		data = testLoop(data,ref);
		saveFile(output,data);
	}

	
	/**
	 * Test loop calles with the reference loop file of interest. The reference file is stock in HashMap
	 * @param data loop collection to test
	 * @param ref reference loop collection
	 * @return collection of loops tested
	 */
	private HashMap<String,Loop> testLoop(HashMap<String,Loop> data, HashMap<String,String> ref){
		Set<String> key = data.keySet();
		HashMap<String,Loop> filtered = new HashMap<String,Loop>();
		Iterator<String> it = key.iterator();
		while (it.hasNext()){
			String cle = it.next();
			String name = data.get(cle).getChr()+"\t"+data.get(cle).getY()+"\t"+data.get(cle).getX();
			if (ref.containsKey(name))
				filtered.put(cle, data.get(cle));
			else{
				int x = data.get(cle).getX();
				int y = data.get(cle).getY();
				for (int i = x-2*m_resolution; i <= x+2*m_resolution; i = i+m_resolution){
					for (int j = y-2*m_resolution ; j <= y+2*m_resolution; j = j+m_resolution){
						name = data.get(cle).getChr()+"\t"+j+"\t"+i;
						if(ref.containsKey(name)){
							Loop loop= new Loop(name,i,j,data.get(cle).getChr());
							int i_end = i+m_resolution;
							int j_end = j+m_resolution;
							loop.setCoordinates(i, i_end, j, j_end);
							filtered.put(name, loop);
						}
					}
				}
				
			}
		}
		return filtered;
	}
	
	/**
	 * Save the results file in tab format
	 * chromosome1	x1	x2	chromosome2	y1	y2	color	APScoreAVG	RegAPScoreAVG	%OfPixelInfToTheCenter\n
	 * @param pathFile path of the output file
	 * @param data loops collection
	 * @throws IOException
	 */
	public void saveFile (String pathFile,HashMap<String,Loop> data) throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(pathFile)));
		Set<String> key = data.keySet();
		Iterator<String> it = key.iterator();
		System.out.println(data.size());
		writer.write("chromosome1\tx1\tx2\tchromosome2\ty1\ty2\tcolor\tAPScoreAVG\tRegAPScoreAVG\t%OfPixelInfToTheCenter\n");
		while (it.hasNext()){
			String cle = it.next();
			Loop loop = data.get(cle);
			ArrayList<Integer> coord = loop.getCoordinates();
			//System.out.println("plopi "+loop.getChr()+"\t"+coord.get(2)+"\t"+coord.get(3));
			writer.write(loop.getChr()+"\t"+coord.get(2)+"\t"+coord.get(3)+"\t"+loop.getChr()+"\t"+coord.get(0)+"\t"+coord.get(1)+"\t255,125,255"
				+"\t"+loop.getPaScoreAvg()+"\t"+loop.getRegionalPaScoreAvg()+"\n"); 
		}
		writer.close();
	}
	
	/**
	 * Method comapring the two condition, two file are done one for the positive number (higher value in the first condition), the other for teh negative value
	 *  (higher value in the second condition)
	 * @param file1 input file for the first condition
	 * @param file2 input file for the second condition
	 * @param output1	output directory for the first condition
	 * @param output2 output directory for the second condition
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
		System.out.println(file2);
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
	 * method stocking the reference file of the loops in hashMAP of string and string, 
	 * @param file
	 * @return HashMap<String,String>
	 * @throws IOException
	 */
	private HashMap<String,String> readFile( String file) throws IOException{
		HashMap<String,String> resu = new HashMap<String,String>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();
		sb.append(System.lineSeparator());
		line = br.readLine();
		while (line != null){
			sb.append(line);
			String[] parts = line.split("\\t");				
			String loops = parts[0].replaceAll("chr","")+"\t"+parts[1]+"\t"+parts[4]; 
			resu.put(loops, "");
			sb.append(System.lineSeparator());
			line = br.readLine();
		}
		br.close();
		return resu;
	} 
	
	/**
	 * Setter for the strength of the min filter
	 * @param min
	 */
	public void setMin(double min){
		this.m_min = min;
	}
	
	/**
	 * Setter of the gaussain blur strength
	 * @param gauss
	 */
	public void setGauss(double gauss){
		this.m_gauss = gauss;
	}
	
	/**
	 * Setter of the diagonal size
	 * @param diag
	 */
	public void setDiag(int diag){
		this.m_diagSize = diag;
	}
	
	/**
	 * Setter of the threshold for the maxima detection
	 * @param thresh
	 */
	public void setThreshold(int thresh){
		this.m_threshold = thresh;
	}
}
