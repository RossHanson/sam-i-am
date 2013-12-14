/**
 * 
 */
package jvr.engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import jvr.parser.BasicParserFunctionality;

/**
 * @author vesha
 *
 */
public class Character {
	public String name;
	private SortedSet<String> adjectives;
	private SortedSet<String> actions;
	private SortedSet<String> receivedActions;
	private double probProtagonist;
	private double probAntagonist;
	private static final Double adjectiveWeight = 0.5; //might have to determine this empirically.
	private static final Double verbWeight = 0.5;

	public static void main(String[] arg){ //just for testing
		String nounClause = " (NP (DT The) (JJ evil) (JJ good) (NN sorcerer))";
		Character personaje = new Character(nounClause);
		System.out.println("Original character: \n"+personaje.toString());
		String nounClause2 = "(NP (PRP He)) (VP (VBN ruined) (NP (NN everything))";
		personaje.updateCharacter(nounClause2);
		String nounClause3 = "(NP (PRP He)) (VP (MD should) (VP (VB be) (VP (VBN punished) (VBN destroyed)))";
		personaje.updateCharacter(nounClause3);
		System.out.println("Updated character: \n"+personaje.toString());
	}

	/**
	 * Given a nounClause containing a character's name and possibly
	 * other information about adjectives and verbs related to the 
	 * character, constructs a Character.
	 * @param nounClause
	 */
	public Character(String nounClause){
		new WordTrainingSet(false); //Generates information about the known words in our collection.
		this.name = findName(nounClause);
		this.adjectives = findAdjectives(nounClause);
		this.actions = findVerbs(nounClause);
		setProbabilities();
	}

	//Getters
	public String getName(){
		return this.name;
	}

	public SortedSet<String> getActions(){
		return this.actions;
	}

	public SortedSet<String> getReceivedActions(){
		return this.receivedActions;
	}

	public double getProbProtagonist(){
		return this.probProtagonist;
	}

	public double getProbAntagonist(){
		return this.probAntagonist;
	}

	public static String findName(String nounClause){
		String[] posArr = {"(NN ", "(NNS "};
		int startIndex = 0;
		int index = BasicParserFunctionality.chooseNextIndex(posArr, nounClause, startIndex);
		int closeParenthesis = nounClause.indexOf(")", index);
		return nounClause.substring(index, closeParenthesis);
	}

	// Can possibly add feature that adds weight to an adjective if it is a superlative. 
	private SortedSet<String> findAdjectives(String nounClause){
		String[] posArr = {"(JJ ", "(JJS ", "(JJR "};
		return finderHelper(nounClause, posArr);
	}

	private SortedSet<String> findVerbs(String nounClause){
		String[] posArr = {"(VB ","(VBD ","(VBG ","(VBN ", "(VBP ","(VBZ "};
		return finderHelper(nounClause, posArr);
	}

	private SortedSet<String> finderHelper(String nounClause, String[] posArr){
		SortedSet<String> returnSet = new TreeSet<String>();
		int index = BasicParserFunctionality.chooseNextIndex(posArr, nounClause, 0);
		int fin = nounClause.indexOf(")", index);
		while(index>-1 && fin<nounClause.length()){
			returnSet.add(nounClause.substring(index, fin));
			index = BasicParserFunctionality.chooseNextIndex(posArr, nounClause, fin);
			fin = nounClause.indexOf(")", index);
		}
		return returnSet;
	}

	/**
	 * Called when a different character performs an action on this character.
	 * @param verbClause
	 */
	public void updateReceivedActions(String verbClause){
		SortedSet<String> actions = findVerbs(verbClause);
		receivedActions.addAll(actions);
		setProbabilities();
	}

	public void updateCharacter(String nounClause){
		adjectives.addAll(findAdjectives(nounClause));
		actions.addAll(findVerbs(nounClause));
		setProbabilities();
	}
	
	//Note that the probabilities of being a protagonist/antagonist will not necessarily add to 1. There is a lot of 
	// uncertainty about whether or not the character is just a neutral character. The difference in positivity/negativity 
	// and 1 represents the probability that the character is just neutral.
	public void setProbabilities(){
		Double adjPositivity = get_itivity(true);
		Double adjNegativity = get_itivity(false);
		Double doPositivity = get_itivity(true);
		Double doNegativity = get_itivity(false);
		probProtagonist = (0.5*adjPositivity) + (0.5*doPositivity);//*adjectiveWeight+(doPositivity)*verbWeight;//+receivePositivity)*verbWeight;
		probAntagonist = (0.5*adjNegativity) + (0.5*doNegativity);
	}

	/**
	 * Returns a ratio of positive words to classifiable (words that can be clearly marked as positive or negative)
	 * words.
	 * @param pos_or_neg
	 * @return
	 */
	public Double get_itivity(boolean positive){
		SortedSet<String> sss = actions;
		sss.addAll(adjectives);
		Double num = 0.0;
		Double total = (double) sss.size();
		HashSet<String> wordSet = positive? WordTrainingSet.positiveWords : WordTrainingSet.negativeWords;
		for (String pos : sss){
			if (wordSet.contains(pos)){
				num++;
			}
		}
		return num/Math.max(1.0,total);
	}

	//for testing only.
	public String toString(){
		return ("Name: "+this.name+"\nAdjectives: "+this.adjectives.toString()+
				"\nActions: "+this.actions.toString()+"\nProbability that is a protagonist: "
				+probProtagonist+"\nProbability that is a antagonist: "+probAntagonist
				/*+"\nReceived Actions; "+this.receivedActions.toString()*/+"\n");
	}
}
