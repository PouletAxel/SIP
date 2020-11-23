package utils;

import loops.Loop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * Compute the cut off  for AP and Regional AP score for loops gave in input
 *
 * @author  axel poulet and m. jordan rowley
 *
 */
public class FDR {
	/**	double regional fdr cutoff */
	private double _RFDRCutoff = 10000;
	/** double fdr cutoff */
	private double _FDRCutoff = 10000;
	/** fdr value*/
	private double _fdr;
	/** hashmap of loop loopName=> Loop object*/
	private HashMap<String, Loop> _data;

	/**
	 * Constructor
	 * @param fdr fdr value use to compute the two cutoff
	 * @param data loops data
	 */
	public FDR(double fdr,HashMap<String,Loop> data){
		this._fdr = fdr;
		this._data = data;
	}
	
	/**
	 *
	 */
	public void run(){
		Set<String> key = _data.keySet();
		Iterator<String> it = key.iterator();
		List<Float> myFDRValues = new ArrayList<Float>();
		List<Float> myRFDRValues = new ArrayList<Float>();
		
		while (it.hasNext()) {
			String loopName = it.next();
			Loop loop = _data.get(loopName);

			float dubFDR = loop.getPaScoreAvgFDR();
			float dubFDR2 = loop.getPaScoreAvgFDR2();
			float dubFDR3 = loop.getPaScoreAvgFDR3();


			if(dubFDR > 0)  myFDRValues.add(dubFDR);
			if(dubFDR2 > 0) myFDRValues.add(dubFDR2);
			if(dubFDR3 > 0) myFDRValues.add(dubFDR3);
		

			float dubRFDR = loop.getRegionalPaScoreAvgFDR();
			float dubRFDR2 = loop.getRegionalPaScoreAvgFDR2();
			float dubRFDR3 = loop.getRegionalPaScoreAvgFDR3();
				
			if(dubRFDR > 0)	 myRFDRValues.add(dubRFDR);
			if(dubRFDR2 > 0) myRFDRValues.add(dubRFDR2);
			if(dubRFDR3 > 0) myRFDRValues.add(dubRFDR3);
		}
			
		Collections.sort(myFDRValues);
		int topFDRs = (int)(myFDRValues.size()*_fdr);
		if(topFDRs != 0){
			List<Float> topFDRList = new ArrayList<Float>(myFDRValues.subList(myFDRValues.size() -topFDRs,  myFDRValues.size() -(topFDRs-1)));
			this._FDRCutoff = topFDRList.get(0);
			Collections.sort(myRFDRValues);
			int topRFDRs = (int)(myRFDRValues.size()*_fdr);
			List<Float> topRFDRList = new ArrayList<Float>(myRFDRValues.subList(myRFDRValues.size() -topRFDRs,  myRFDRValues.size() -(topRFDRs-1)));
			this._RFDRCutoff = topRFDRList.get(0);
		}
	}

	/**
	 * getter of regional fdr cutoff
	 * @return regional fdr cutoff
	 */
	public double getRFDRCutoff() {	return _RFDRCutoff; }

	/**
	 * getter of regional fdr cutoff
	 * @return fdr cutoff
	 */
	public double getFDRCutoff() {	return _FDRCutoff; }
}
