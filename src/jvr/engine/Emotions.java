package jvr.engine;

import java.util.SortedMap;
import java.util.TreeMap;

/** 
 * This class maintains all currently known information about the emotional
 * state of the current Story, as well as the state of all stories in
 * general (class variables hold information about the averages).
 * @author vesha
 *
 */
public class Emotions {
	//Stores the value corresponding to the "most positive" emotional state.
	public static final Double MAX=0.0; 
	//Stores the value corresponding to the "most negative" emotional state.
	public static final Double MIN=0.0; 
	//Maps all integers between MIN and MAX to a string representation of an emotion.
	public static SortedMap<Double, String> emotionSet = new TreeMap<Double, String>();
	
	
	/**
	 * Returns -1.0 when the input word has a negative connotation, 0.0 when
	 * the word has a neutral connotation, and 1.0 when the word has a positive
	 * connotation.
	 * @param word
	 * @return
	 */
	public static Double positiveORnegative(String word){
		return 0.0;
	}
	
	/**
	 * Given a SortedMap<Integer, Double> that maps a sentence (denoted by a
	 * number representing the position of the sentence in a Story) to a value
	 * of type Double which describes some emotion falling in the range [MAX..MIN].
	 * @param sm
	 */
	public void graphEmotivePlot(SortedMap<Integer, String> sm){
		
	}
}
