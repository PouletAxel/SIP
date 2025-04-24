package plop.multiProcesing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import plop.process.CoolerDumpData;
import plop.utils.CoolerExpected;

/**
 * 
 * @author axel poulet
 *
 */
public class RunnableDumpDataCooler extends Thread implements Runnable{
	/**String: path where save the dump data  */
	private String _outdir ="";
	/**String: name of the chr*/
	private String _chrName = "";
	/**int: chr size */
	private int _chrSize = 0;
	/** DumpData object run juicertoolbox.jar*/
	private CoolerDumpData _coolerDumpData;
	/**int: bin resolution*/
	private int _res = 0;
	/**int: image Size */
	private int _matrixSize = 0;
	/**int: size of the step to run a chr */
	private int _step = 0;
	/** */
	private ArrayList<Integer> _listFactor;
	
	
	/**
	 * Constructor, initialize the variables of interest
	 *  
	 * @param outDir path to output folder
 	 * @param chrName chromosome name
	 * @param chrSize chromosome size
	 * @param dumpData DumpData object
	 * @param res resolution (bin size)
	 * @param matrixSize image size
	 * @param step ster of teh analysis
	 */
	public RunnableDumpDataCooler (String outDir, String chrName,
								   int chrSize, CoolerDumpData dumpData,
								   int res, int matrixSize, int step,
								   ArrayList<Integer> listFactor){
		this._outdir = outDir;
		this._chrName = chrName;
		this._chrSize = chrSize;
		this._res = res;
		this._matrixSize = matrixSize;
		this._step = step;
		this._coolerDumpData = dumpData;
		this._listFactor = listFactor;
	}

	/**
	 * Dump teh data by chr
	 */
	public void run(){
		boolean coolerTools;
		for(int indexFact = 0; indexFact < this._listFactor.size(); ++indexFact) {
			int res = _res*this._listFactor.get(indexFact);
			int matrixSize = _matrixSize/this._listFactor.get(indexFact);
			_step = matrixSize/2;
			String resName = String.valueOf(res);
			resName = resName.replace("000", "");
			resName = resName+"kb";
			if(res < 1000){
				resName = String.valueOf(res);
				resName = resName+"b";
			}
			String expectedFile = this._outdir+File.separator+resName+".expected";
			CoolerExpected expected = new CoolerExpected(expectedFile, matrixSize);
			try {
				expected.parseExpectedFile();
				ArrayList<Double> lExpected = expected.getExpected(_chrName);
				_coolerDumpData.setExpected(lExpected);
				String outdir = this._outdir+File.separator+resName+File.separator+this._chrName+File.separator;
				File file = new File(outdir);
				if (!file.exists())
					file.mkdirs();
				int step = this._step*res;
				int j = matrixSize*res;
				String name = outdir+this._chrName+"_0_"+j+".txt";
				if (!file.exists())
					file.mkdir();
				System.out.println("start dump "+this._chrName+" size "+this._chrSize+" res "+ resName);
				if(j > this._chrSize) j = this._chrSize;
				for(int i = 0 ; j-1 <= this._chrSize; i+=step,j+=step){
					int end =j-1;
					String dump = this._chrName+":"+i+"-"+end;
					name = outdir+this._chrName+"_"+i+"_"+end+".txt";
					System.out.println("start dump "+this._chrName+" size "+this._chrSize+" dump "+dump+" res "+ resName);
					coolerTools = this._coolerDumpData.dumpObservedMExpected(dump,name,res);
					if (!coolerTools){
						System.out.print(dump+" "+"\n"+coolerTools+"\n");
						System.exit(0);
					}		
					if(j+step > this._chrSize && j < this._chrSize){
						j= this._chrSize;
						i+=step;
						dump = this._chrName+":"+i+"-"+j;
						name = outdir+this._chrName+"_"+i+"_"+j+".txt";
						System.out.println("start dump "+this._chrName+" size "+this._chrSize+" dump "+dump+" res "+ resName);
						coolerTools = this._coolerDumpData.dumpObservedMExpected(dump,name,res);
						if (!coolerTools){
							System.out.print(dump+" "+"\n"+coolerTools+"\n");
							System.exit(0);
						}
					}
				}
			} catch (IOException e) {	e.printStackTrace();}
			System.out.println("##### End dump "+this._chrName+" "+resName);
			System.gc();
		}
	}

}
