/**
 * 
 */
package jvr.engine;

import java.util.HashSet;
import java.util.SortedMap;
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
	private SortedSet<String> deliveredActions; 
	private SortedMap<Character, SortedSet<String>> receivedActions;//Value SortedSet<String> represents the verbs, key Character is the perpetrator of that verb.
	//	private SortedMap<String, Character> deliveredActions; //String, Character (or just the character's name might suffice)
	//	private SortedMap<String, Character> receivedActions;  //String, Character (or just the character's name might suffice)
	private Integer occurrences; //represents the number of times this character appears in the story.
	private SortedSet<String> iveMadeAHugeMistake = new TreeSet<String>();
	private double probProtagonist;
	private double probAntagonist;
	private static final Double adjectiveWeight = 0.5; //might have to determine this empirically.
	private static final Double verbWeight = 0.5;

	public static void main(String[] arg){ //just for testing
		String nounClause = " (NP (DT The) (JJ evil) (NN sorcerer))";
		//		String nounClause = "(NP (DT The) (JJ beautiful) (NNS princess)) (VP (VBD played) (PP (IN in) (NP (DT the) (NN park))))";
		//		System.out.println("NounClause: "+nounClause);
		String nounClause2 = "(NP (DT The) (JJ evil) (NN sorcerer)) (VP (VBD killed) (NP (DT the) (JJ little) (NN girl)))";
		//		String nounClause = "(NP (DT The) (NN wizard)) (VP (VBD walked) (ADVP (RB outside)))";
		Character personaje = new Character(nounClause, true);
		System.out.println("Original character: \n"+personaje.toString());
		//				String nounClause2 = "(NP (PRP He)) (VP (VBN ruined) (NP (NN everything))";
		//		String nounClause2 = "(NP (PRP She)) (VP (VBD was) (ADJP (JJ happy)))";
		//		System.out.println("nounClause2: "+nounClause2);
		personaje.updateCharacter(nounClause2);
		String nounClause3 = "(NP (PRP He)) (VP (MD should) (VP (VB be) (VP (VBN punished) (VBN destroyed)))";
		//		String nounClause3 = "";
		personaje.updateCharacter(nounClause3);
		System.out.println("Updated character: \n"+personaje.toString());
		
	}

	/**
	 * Given a nounClause containing a character's name and possibly
	 * other information about adjectives and verbs related to the 
	 * character, constructs a Character.
	 * @param nounClause
	 * @param delivering: true, if the VP contains verbs that this character is responsible for.
	 */
	public Character(String nounClause, boolean delivering){
		new WordTrainingSet(false); //Generates information about the known words in our collection.
		this.name = findName(nounClause);
		this.adjectives = findAdjectives(nounClause);
		this.deliveredActions = delivering ? findVerbs(nounClause) : findVerbs("");
		this.occurrences = 0;
		this.iveMadeAHugeMistake.addAll(BasicParserFunctionality.extractVerbPhrases(nounClause));
		setProbabilities();
	}

	/**
	 * Called when a different character performs an action on this character.
	 * @param verbClause
	 * @param characterName
	 */
	public void updateReceivedActions(String verbClause, String perp){
		Character perpetrator=Characters.characters.get(perp);
		System.out.println("This character is: "+perpetrator.toString()+"\n");
		System.out.println("verbClause: "+verbClause+"\n");
		SortedSet<String> actions = findVerbs(verbClause);
//		System.out.println("actions: "+actions+"\n");
		receivedActions.put(perpetrator, actions);
		occurrences=occurrences+actions.size();
		setProbabilities();
	}

	/** 
	 * Updates an existing character, or creates a new character.
	 * @param nounClause
	 * @param newKidInTown: true, if this character is not already in Characters.characters
	 */
	public void updateCharacter(String nounClause){
		adjectives.addAll(findAdjectives(nounClause));
		deliveredActions.addAll(findVerbs(nounClause));
		occurrences++;
		this.iveMadeAHugeMistake.addAll(onlyIfItAffectsSomeone(BasicParserFunctionality.extractVerbPhrases(nounClause)));
		setProbabilities();
	}
	
	private SortedSet<String> onlyIfItAffectsSomeone(SortedSet<String> verbPhrases){
		for (String verbPhrase : verbPhrases){
			String[] posArr = {"(NN ", "(NNS "};
			int startIndex = 0;
			int index = BasicParserFunctionality.chooseNextIndex(posArr, verbPhrase, startIndex);
			if (index < 0){//So this is an action that affects someone
				verbPhrases.remove(verbPhrase);	
			}
		}
		return verbPhrases;
	}

	//Getters
	public String getName(){
		return this.name;
	}

	public SortedSet<String> getDeliveredActions(){
		return this.deliveredActions;
	}

	public SortedMap<Character, SortedSet<String>> getReceivedActions(){
		return this.receivedActions;
	}

	public Integer getOccurrences(){
		return this.occurrences;
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
		//		System.out.println("Now in findAdjectives.\n");
		String[] posArr = {"(JJ ", "(JJS ", "(JJR "};
		return finderHelper(nounClause, posArr);
	}

	/**
	 * Finds all of the actions of this character. Also updates the 
	 * receivedActions field of any other character that this character
	 * does something to. Initializes the other character if they are not
	 * already initialized (they are not already in Characters.characters). 
	 * @param nounClause
	 * @return
	 */
	private SortedSet<String> findVerbs(String nounClause){
		//		System.out.println("Now in findVerbs.\n");
		String[] posArr = {"(VB ","(VBD ","(VBG ","(VBN ", "(VBP ","(VBZ "};
		return finderHelper(nounClause, posArr);
	}
	
	/**
	 * Called after this character has already been added to Characters.characters.
	 * Updates all characters that this character has done something to.
	 * @param phrase
	 */
	public void dealWithRepercussions (String phrase){
		SortedSet<String> allTheseThingsIveDone = BasicParserFunctionality.extractVerbPhrases(phrase);
		for (String vPhrase : allTheseThingsIveDone){
			SortedSet<String> laGente = BasicParserFunctionality.extractNounPhrases(vPhrase);
			for (String peeps : laGente){
				String personnage = findName(peeps);
				System.out.println("personnage: "+personnage);
				System.out.println("Characters.characters: "+Characters.characters.toString());
				if (!Characters.characters.containsKey(personnage)){
					Characters.characters.put(personnage, new Character(phrase, false));
				}else{
					Characters.characters.get(personnage).updateReceivedActions(vPhrase, this.name);
				}
			}
		}
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

	// Note that the probabilities of being a protagonist/antagonist will not necessarily add to 1. There is a lot of 
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

	public void dealWithTheirActions (String sentence){

	}

	/**
	 * Returns a ratio of positive words to classifiable (words that can be clearly marked as positive or negative)
	 * words.
	 * @param pos_or_neg
	 * @return
	 */
	public Double get_itivity(boolean positive){
		SortedSet<String> sss = new TreeSet<String>();
		sss.addAll(deliveredActions);
		sss.addAll(adjectives);
		Double num = 0.0;
		Double total = 0.0;
		HashSet<String> wordSet = positive? WordTrainingSet.positiveWords : WordTrainingSet.negativeWords;
		HashSet<String> otherSet = positive? WordTrainingSet.negativeWords : WordTrainingSet.positiveWords;
		for (String pos : sss){
			if (wordSet.contains(pos)){
				num++;
				total++;
			}
			if (otherSet.contains(pos)) total++;
		}
		return num/Math.max(1.0,total);
	}

	//for testing only.
	public String toString(){
		return ("Name: "+this.name+"\nAdjectives: "+this.adjectives.toString()+
				"\nActions: "+this.deliveredActions.toString()+"\nProbability that \""+this.name+"\" is a protagonist: "
				+probProtagonist+"\nProbability that \""+this.name+"\" is a antagonist: "+probAntagonist+"\niveMadeAHugeMistake: "+iveMadeAHugeMistake
				/*+"\nReceived Actions; "+this.receivedActions.toString()*/+"\n");
	}
}
