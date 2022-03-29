package plop.loops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Method to filter the java.plop.loops close to white strip or java.plop.loops which don't reach the different threshold filter
 * @author axel poulet
 *
 */
public class FilterLoops {
	/** java.plop.loops resoluiton*/
	private int _resolution;
	
	
	/**
	 * Constructor
	 * @param resolution loop resolution
	 */
	public FilterLoops(int resolution){
		this._resolution = resolution;
	}
	
	
	/**
	 * Remove java.plop.loops which doesn't respect the rule
	 * 
	 * @param input loop collection before correction
	 * @return loop collection after correction
	 */
	public HashMap<String, Loop>  removedBadLoops(HashMap<String, Loop> input){
		Set<String> key = input.keySet();
		Iterator<String> it = key.iterator();
		ArrayList<String> removed = new ArrayList<String>();
		while (it.hasNext()){
			String name = it.next();
			Loop loop = input.get(name);
			if(!(removed.contains(name))){
				// filter on APA score and APA regional score
				if(loop.getPaScoreAvg() < 1.2 || loop.getRegionalPaScoreAvg() < 1 )
					removed.add(name);
				else
					removed = removeOverlappingLoops(loop,input,removed);
			}
		}
		for (int i = 0; i< removed.size(); ++i)
			input.remove(removed.get(i));
		return input;
	}
	
	/**
	 * Removed loop close to white  strip
	 * 
	 * @param hLoop loop collection before correction of the java.plop.loops
	 * @return loop collection sfter correction of the java.plop.loops
	 */
	public HashMap<String,Loop> removedLoopCloseToWhiteStrip(HashMap<String,Loop> hLoop, HashMap<Integer,String> normVector){
		//System.out.println("plop "+hLoop.size()+" debut Filter");
		Set<String> key = hLoop.keySet();
		Iterator<String> it = key.iterator();
		ArrayList<String> removed = new ArrayList<String>();
		while (it.hasNext()){
			String name = it.next();
			Loop loop = hLoop.get(name);
			Boolean testRemoved = removedVectoNorm(loop,normVector);
			boolean testBreak = false;
			if(testRemoved)
				removed.add(name);
			else{
				String [] tname = name.split("\t");
				//System.out.println(name);
				int x = Integer.parseInt(tname[1]);
				int y = Integer.parseInt(tname[2]);
				for(int i = x-5*this._resolution; i <= x+5*this._resolution; i+=this._resolution){
					for(int j = y-5*this._resolution; j <= y+5*this._resolution; j+=this._resolution){
						String test = tname[0]+"\t"+i+"\t"+j;
						if(!test.equals(name)){
							if(hLoop.containsKey(test)){
								if(hLoop.get(test).getResolution() < hLoop.get(name).getResolution()){
									removed.add(name);
									testBreak =true;
									break;
								}else if(hLoop.get(test).getResolution() == hLoop.get(name).getResolution()){
									if((Math.abs(x-hLoop.get(test).getX()) < this._resolution*3 || Math.abs(y-hLoop.get(test).getY()) < this._resolution*3)){
										if(hLoop.get(test).getAvg() > hLoop.get(name).getAvg()){
											removed.add(name);
											testBreak =true;
											break;
										}else if(hLoop.get(test).getAvg() < hLoop.get(name).getAvg())
											removed.add(test);
										else{
											if(hLoop.get(test).getPaScoreAvg() > hLoop.get(name).getPaScoreAvg()){
												removed.add(name);
												testBreak =true;
												break;
											}else
												removed.add(test);
										}
									}
								}else
									removed.add(test);
							}
						}
					}
					if(testBreak)
						break;
				}
			}
		}
		for (int i = 0; i< removed.size(); ++i)
			hLoop.remove(removed.get(i));
		//System.out.println("####### fin Filter "+hLoop.size());
		return hLoop;
	}
	
	
	/**
	 * Removed loop close to white  strip
	 * 
	 * @param hLoop loop collection before correction of the java.plop.loops
	 * @return loop collection sfter correction of the java.plop.loops
	 */
	public HashMap<String,Loop> removedLoopCloseToWhiteStrip(HashMap<String,Loop> hLoop){
		//System.out.println("plop "+hLoop.size()+" debut Filter");
		Set<String> key = hLoop.keySet();
		Iterator<String> it = key.iterator();
		ArrayList<String> removed = new ArrayList<String>();
		while (it.hasNext()){
			String name = it.next();
			//Loop loop = hLoop.get(name);
			boolean testBreak = false;
			String [] tname = name.split("\t");
			//System.out.println(name);
			int x = Integer.parseInt(tname[1]);
			int y = Integer.parseInt(tname[2]);
			for(int i = x-5*this._resolution; i <= x+5*this._resolution; i+=this._resolution){
				for(int j = y-5*this._resolution; j <= y+5*this._resolution; j+=this._resolution){
					String test = tname[0]+"\t"+i+"\t"+j;
					if(!test.equals(name)){
						if(hLoop.containsKey(test)){
							if(hLoop.get(test).getResolution() < hLoop.get(name).getResolution()){
								removed.add(name);
								testBreak =true;
								break;
							}else if(hLoop.get(test).getResolution() == hLoop.get(name).getResolution()){
								if((Math.abs(x-hLoop.get(test).getX()) < this._resolution*3 || Math.abs(y-hLoop.get(test).getY()) < this._resolution*3)){
									if(hLoop.get(test).getAvg() > hLoop.get(name).getAvg()){
										removed.add(name);
										testBreak =true;
										break;
									}else if(hLoop.get(test).getAvg() < hLoop.get(name).getAvg())
										removed.add(test);
									else{
										if(hLoop.get(test).getPaScoreAvg() > hLoop.get(name).getPaScoreAvg()){
											removed.add(name);
											testBreak =true;
											break;
										}else
											removed.add(test);
									}	
								}
							}else
								removed.add(test);
						}
					}
				}
				if(testBreak)
					break;
			}
		}
		for (int i = 0; i< removed.size(); ++i)
			hLoop.remove(removed.get(i));
		//System.out.println("####### fin Filter "+hLoop.size());
		return hLoop;
	}
	/**
	 * Removed java.plop.loops close to biased HiC signal
	 * @param loop Loop to java.plop.test
	 * @return boolean true if loop have to be removed else false
	 */
	private boolean removedVectoNorm(Loop loop, HashMap<Integer,String> normVector){
		boolean test = false;
		int x = loop.getCoordinates().get(0);
		int y = loop.getCoordinates().get(2);
		//System.out.println(loop.getName()+" "+loop.getResolution());
		if(loop.getResolution() == this._resolution){
			if(normVector.containsKey(x) || normVector.containsKey(y))
				test = true;
		}
		else if(loop.getResolution() == this._resolution*2){
			if(normVector.containsKey(x) || normVector.containsKey(y) ||normVector.containsKey(x+this._resolution) || normVector.containsKey(y+this._resolution))
				test = true;
		}
		else if(loop.getResolution() == this._resolution*5){
			for(int i = x; i <= x+5*this._resolution; i+=this._resolution){
				if(normVector.containsKey(i)){
					test = true;
					break;
				}
				for(int j = y; j <= y+5*this._resolution; j+=this._resolution){
					if(normVector.containsKey(j)){
						test = true;
						break;
					}
				}		
			}
		}
		return test;
	}

	
	/**
	 * Remove overlapping java.plop.loops
	 * @param loop loop to java.plop.test
	 * @param input loop collection
	 * @param removed arrayList of loop
	 * @return removed arrayList of loop 
	 */
	private ArrayList<String> removeOverlappingLoops(Loop loop, HashMap<String, Loop> input, ArrayList<String> removed){
		Set<String> key = input.keySet();
		Iterator<String> it = key.iterator();
		while (it.hasNext()){
			String name = it.next();
			Loop looptest = input.get(name);
			if(!(removed.contains(name)) && loop.getResolution() < looptest.getResolution()){
				int factor = looptest.getResolution()/loop.getResolution();
				int xtest = loop.getX()/factor;
				int ytest = loop.getY()/factor;
				for(int i = xtest-1; i <= xtest+1; ++i ){
					for(int j = ytest-1; j <= ytest+1; ++j ){
						if(i == looptest.getX() && j == looptest.getY()){
							removed.add(name);
						}
					}
				}
			}
		}
		return removed;
	}
}