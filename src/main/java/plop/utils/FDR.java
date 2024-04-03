package plop.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author m. jordan rowley and axel poulet
 *
 */
public class FDR {
	/**	 */
	private double _rFdrCutoff = 10000;
	/** */
	private double _fdrCutoff = 10000;
	
	/**
	 * 
	 */
	public FDR(){}
	
	/**
	 * 
	 * @param fdr
	 * @param data
	 */
	public void run(double fdr,HashMap<String,Loop> data){
		Set<String> key = data.keySet();
		Iterator<String> it = key.iterator();
		List<Float> myFdrVals = new ArrayList<Float>();
		List<Float> myRFdrVals = new ArrayList<Float>();
		
		while (it.hasNext()) {
			String loopName = it.next();
			Loop loop = data.get(loopName);
						
			float dubFDR = loop.getPaScoreAvgFDR();
			float dubFDR2 = loop.getPaScoreAvgFDR2();
			float dubFDR3 = loop.getPaScoreAvgFDR3();
				
			if(dubFDR > 0)  myFdrVals.add(dubFDR);
			if(dubFDR2 > 0) myFdrVals.add(dubFDR2);
			if(dubFDR3 > 0) myFdrVals.add(dubFDR3);
		
	
			float dubRFDR = loop.getRegionalPaScoreAvgFDR();
			float dubRFDR2 = loop.getRegionalPaScoreAvgFDR2();
			float dubRFDR3 = loop.getRegionalPaScoreAvgFDR3();
				
			if(dubRFDR > 0)	 myRFdrVals.add(dubRFDR);
			if(dubRFDR2 > 0) myRFdrVals.add(dubRFDR2);
			if(dubRFDR3 > 0) myRFdrVals.add(dubRFDR3);
		}
			
		Collections.sort(myFdrVals);
		int topFDRs = (int)(myFdrVals.size()*fdr);
		if(topFDRs != 0){
			List<Float> topFDRlist = new ArrayList<Float>(myFdrVals.subList(myFdrVals.size() -topFDRs,
					myFdrVals.size() -(topFDRs-1)));
			this._fdrCutoff = topFDRlist.get(0);
			Collections.sort(myRFdrVals);
			int topRFDRs = (int)(myRFdrVals.size()*fdr);
			List<Float> topRFDRlist = new ArrayList<Float>(myRFdrVals.subList(myRFdrVals.size() -topRFDRs,
					myRFdrVals.size() -(topRFDRs-1)));
			this._rFdrCutoff = topRFDRlist.get(0);
		}
	}

	/**
	 * 
	 * @return
	 */
	public double getRFDRcutoff() {	return _rFdrCutoff; }

	/**
	 * 
	 * @return
	 */
	public double getFDRcutoff() {	return _fdrCutoff; }
}
