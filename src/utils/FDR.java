package utils;

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
	private double _RFDRcutoff = 10000;
	/** */
	private double _FDRcutoff = 10000;
	
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
		List<Float> myFDRvals = new ArrayList<Float>();
		List<Float> myRFDRvals = new ArrayList<Float>();
		
		while (it.hasNext()) {
			String loopName = it.next();
			Loop loop = data.get(loopName);
						
			float dubFDR = loop.getPaScoreAvgFDR();
			float dubFDR2 = loop.getPaScoreAvgFDR2();
			float dubFDR3 = loop.getPaScoreAvgFDR3();
				
			if(dubFDR > 0)  myFDRvals.add(dubFDR);
			if(dubFDR2 > 0) myFDRvals.add(dubFDR2);
			if(dubFDR3 > 0) myFDRvals.add(dubFDR3);
		
	
			float dubRFDR = loop.getRegionalPaScoreAvgFDR();
			float dubRFDR2 = loop.getRegionalPaScoreAvgFDR2();
			float dubRFDR3 = loop.getRegionalPaScoreAvgFDR3();
				
			if(dubRFDR > 0)	 myRFDRvals.add(dubRFDR);
			if(dubRFDR2 > 0) myRFDRvals.add(dubRFDR2);
			if(dubRFDR3 > 0) myRFDRvals.add(dubRFDR3);
		}
			
		Collections.sort(myFDRvals);
		int topFDRs = (int)(myFDRvals.size()*fdr);
		if(topFDRs != 0){
			List<Float> topFDRlist = new ArrayList<Float>(myFDRvals.subList(myFDRvals.size() -topFDRs,  myFDRvals.size() -(topFDRs-1)));
			this._FDRcutoff = topFDRlist.get(0);
			Collections.sort(myRFDRvals);
			int topRFDRs = (int)(myRFDRvals.size()*fdr);
			List<Float> topRFDRlist = new ArrayList<Float>(myRFDRvals.subList(myRFDRvals.size() -topRFDRs,  myRFDRvals.size() -(topRFDRs-1)));
			this._RFDRcutoff = topRFDRlist.get(0);
		}
	}

	/**
	 * 
	 * @return
	 */
	public double getRFDRcutoff() {	return _RFDRcutoff; }

	/**
	 * 
	 * @return
	 */
	public double getFDRcutoff() {	return _FDRcutoff; }
}
