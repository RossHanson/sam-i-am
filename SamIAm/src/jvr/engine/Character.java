/**
 * 
 */
package jvr.engine;

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
	private static final Double adjectiveWeight = 0.0; //might have to determine this empirically.
	private static final Double verbWeight = 0.0;

	public static void main(String[] arg){ //just for testing
		String nounClause = "(NP (DT The) (JJ hungry) (JJ hungry) (JJ ravenous) (NN catepillar))";
		Character personaje = new Character(nounClause);
		System.out.println("Original character: \n"+personaje.toString());
		String nounClause2 = "(S (NP (ADJP (JJ happy) (JJ sad)) (JJ funny)"+
				" (ADJP (JJ crazy)) (NN running)) (VP (VBD ran) (SBAR (S (VP "+
				"(VBD jumped) (SBAR (S (NP (NNP yelled)) (VP (VBD loved)"+
				"))))))) (. .))";
		personaje.updateCharacter(nounClause2);
		System.out.println("Updated character: \n"+personaje.toString());
	}

	/**
	 * Given a nounClause containing a character's name and possibly
	 * other information about adjectives and verbs related to the 
	 * character, constructs a Character.
	 * @param nounClause
	 */
	public Character(String nounClause){
		this.name = findName(nounClause);
		this.adjectives = findAdjectives(nounClause);
		this.actions = findVerbs(nounClause);
		//		setProbabilities();
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
	//

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
	}

	public void updateCharacter(String nounClause){
		adjectives.addAll(findAdjectives(nounClause));
		actions.addAll(findVerbs(nounClause));
	}

	public void setProbabilities(){
		Double adjPositivity = get_positivity(adjectives);
		Double adjNegativity = 1-adjPositivity;
		Double doPositivity = get_positivity(actions);
		Double doNegativity = 1-doPositivity;
		Double receivePositivity = get_positivity(receivedActions);
		Double receiveNegativity = 1-receivePositivity;
		probProtagonist = adjPositivity*adjectiveWeight+(doPositivity+receivePositivity)*verbWeight;
		probAntagonist = adjNegativity*adjectiveWeight+(doNegativity+receiveNegativity)*verbWeight;
	}


	/**
	 * Input: 1 if you want to evaluate the 
	 * @param pos_or_neg
	 * @return
	 */
	public Double get_positivity(SortedSet<String> sss){
		Double total = (double) sss.size();
		Double totalPos = 0.0;
		for (String pos : sss){
			if (SingleWordClassifier.classifyWord(pos)==1){
				totalPos++;
			}
		}
		return totalPos/total;
	}

	//for testing only.
	public String toString(){
		return ("Name: "+this.name+"\nAdjectives: "+this.adjectives.toString()+"\nActions: "+this.actions.toString()/*+"\nReceived Actions; "+this.receivedActions.toString()*/);
	}

}
