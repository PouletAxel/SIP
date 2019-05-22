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
		Set<String> key2 = data.keySet();
		Iterator<String> it2 = key2.iterator();
		List<Float> myFDRvals = new ArrayList<Float>();
			
		while (it2.hasNext()) {
			String FDRcalc = it2.next();
			Loop FDRloopy = data.get(FDRcalc);
						
			float dubFDR = FDRloopy.getPaScoreAvgFDR();
			float dubFDR2 = FDRloopy.getPaScoreAvgFDR2();
			float dubFDR3 = FDRloopy.getPaScoreAvgFDR3();
				
			if(dubFDR > 0)  myFDRvals.add(dubFDR);
			if(dubFDR2 > 0) myFDRvals.add(dubFDR2);
			if(dubFDR3 > 0) myFDRvals.add(dubFDR3);
		}
		Set<String> key3 = data.keySet();
		Iterator<String> it3 = key3.iterator();
		List<Float> myRFDRvals = new ArrayList<Float>();
			
		while (it3.hasNext()) {
			String RFDRcalc = it3.next();
			Loop RFDRloopy = data.get(RFDRcalc);
			
			float dubRFDR = RFDRloopy.getRegionalPaScoreAvgFDR();
			float dubRFDR2 = RFDRloopy.getRegionalPaScoreAvgFDR2();
			float dubRFDR3 = RFDRloopy.getRegionalPaScoreAvgFDR3();
				
			if(dubRFDR > 0)	 myRFDRvals.add(dubRFDR);
			if(dubRFDR2 > 0) myRFDRvals.add(dubRFDR2);
			if(dubRFDR3 > 0) myRFDRvals.add(dubRFDR3);
		}
			
		Collections.sort(myFDRvals);
		int topFDRs = (int)(myFDRvals.size()*fdr);
		if(topFDRs != 0){
			List<Float> topFDRlist = new ArrayList<Float>(myFDRvals.subList(myFDRvals.size() -topFDRs,  myFDRvals.size() -(topFDRs-1)));
			this.setFDRcutoff(topFDRlist.get(0));
			Collections.sort(myRFDRvals);
			int topRFDRs = (int)(myRFDRvals.size()*fdr);
			List<Float> topRFDRlist = new ArrayList<Float>(myRFDRvals.subList(myRFDRvals.size() -topRFDRs,  myRFDRvals.size() -(topRFDRs-1)));
			this.setRFDRcutoff(topRFDRlist.get(0));
		}
	}

	/**
	 * 
	 * @return
	 */
	public double getRFDRcutoff() {	return _RFDRcutoff; }

	/**
	 * 
	 * @param _RFDRcutoff
	 */
	public void setRFDRcutoff(double _RFDRcutoff) { this._RFDRcutoff = _RFDRcutoff; }
	
	/**
	 * 
	 * @return
	 */
	public double getFDRcutoff() {	return _FDRcutoff; }
	/**
	 * 
	 * @param _FDRcutoff
	 */
	public void setFDRcutoff(double _FDRcutoff) {	this._FDRcutoff = _FDRcutoff;}
}
