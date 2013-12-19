package jvr.engine;

import java.util.*;

import edu.stanford.nlp.trees.Tree;

import jvr.parser.BasicParserFunctionality;

/**
 * @author vesha
 */
public class Character {
	public String name;
	private SortedSet<String> positiveAdjectives = new TreeSet<String>();
	private SortedSet<String> negativeAdjectives = new TreeSet<String>();

    private HashMap<Character,SortedSet<Action>> deliveredActions = new HashMap<Character,SortedSet<Action>>();
    private Map<Character, SortedSet<Action>> receivedActions = new HashMap<Character, SortedSet<Action>>();

//	private SortedSet<String> positiveDeliveredActions = new TreeSet<String>();
//	private SortedSet<String> negativeDeliveredActions = new TreeSet<String>();
//	private SortedMap<String, SortedSet<String>> positiveReceivedActions = new TreeMap<String, SortedSet<String>>();//key Character is the perpetrator of the value, value SortedSet<String> represents the verbs.
//	private SortedMap<String, SortedSet<String>> negativeReceivedActions = new TreeMap<String, SortedSet<String>>();
	private Integer occurrences; //represents the number of times this character appears in the story.
	private double probProtagonist;
	private double probAntagonist;

	/**
	 * Given a nounClause containing a character's name and possibly
	 * other information about adjectives related to the 
	 * character, constructs a Character. Verbs are handled separately.
	 * @param nounPhrase: a Tree representing a noun phrase (NP).
	 */
	public Character(Tree nounPhrase){
		this.name = findName(nounPhrase);
		findAdjectives(nounPhrase);
		setProbabilities();
		occurrences = 1;
	}

	/**
	 * Updates the adjectives and actions of this character.
	 * @param nounPhrase: A Tree with label NP which contains information
	 *                    about this Character. null if you don't want to
	 *                    update the adjectives of this Character.
	 * @param actions:    An ArrayList<String> which contains actions
	 *                    performed by this Character. null if you don't
	 *                    want to update the delivered actions of this 
	 *                    Character.
	 */
	public void updateCharacter(Tree nounPhrase, ArrayList<Tree> actions){
		if (!(nounPhrase==null)) findAdjectives(nounPhrase);
//		if (!(actions==null)) handleActions(actions);
		setProbabilities();
	}

	/**
	 * Returns the name of this Character. It is assumed that the noun phrase
	 * containing this character will be the first NP in the sentence. 
	 * @param nounPhrase
	 * @return
	 */
	//TODO: Have to update this so that the subject of the sentence does not necessarily have to be the first NP in the sentence.
	public static String findName(Tree nounPhrase){
		String[] nounLabels = {"NN", "NNS", "NNP", "NNPS"};
		for (Tree subtree : nounPhrase){
			if (BasicParserFunctionality.equalsLabel(subtree, nounLabels)){
				return subtree.firstChild().toString();
			}
		}
		return null;
	}

	/**
	 * Adds all classifiable (in terms of sentiment) adjectives contained in
	 * the Tree nounPhrase describing a character to the appropriate sets.
	 * @param nounPhrase
	 */
	// TODO: Note to self: Can possibly add feature that adds weight to an adjective if it is a superlative.
	public void findAdjectives(Tree nounPhrase){
		String[] adjectiveLabels = {"JJ", "JJS", "JJR"};
		String adjective;
		for (Tree subtree : nounPhrase){
			if (BasicParserFunctionality.equalsLabel(subtree,adjectiveLabels)){
				adjective = subtree.firstChild().toString();
				if (WordTrainingSet.positiveWords.contains(adjective)){
					positiveAdjectives.add(adjective);
				}
				if (WordTrainingSet.negativeWords.contains(adjective)){
					negativeAdjectives.add(adjective);
				}
			}
		}
	}

	/**
	 * Adds all classifiable (in terms of sentiment) actions of a character to 
	 * the appropriate sets.
	 * @param actions
	 */
//	public void handleActions(ArrayList<Tree> actions){
//		for (Tree action : actions){
//			if (WordTrainingSet.positiveWords.contains(action)){
//                deliveredActions.put(null, new Action(null, ))
//				positiveDeliveredActions.add(action);
//			}
//			if (WordTrainingSet.negativeWords.contains(action)){
//				negativeDeliveredActions.add(action);
//			}
//		}
//	}

//	/**
//	 * Adds all classifiable (in terms of sentiment) actions that this character
//	 * receives to the appropriate sets. Also records the Character who
//	 * is the perpetrator of these actions.
//	 * @param receivedActions
//     * @param perpetrator
//	 */
//	public void handleReceivedActions(ArrayList<String> receivedActions, String perpetrator){
//		SortedSet<String> tempPositive = new TreeSet<String>();
//		SortedSet<String> tempNegative = new TreeSet<String>();
//		for (String action : receivedActions){
//			if (WordTrainingSet.positiveWords.contains(action)){
//				tempPositive.add(action);
//			}
//			if (WordTrainingSet.negativeWords.contains(action)){
//				tempNegative.add(action);
//			}
//		}
//		if (!tempPositive.isEmpty()){
//			if (!positiveReceivedActions.containsKey(perpetrator)){
//				positiveReceivedActions.put(perpetrator, tempPositive);
//			}
//			if (!positiveReceivedActions.containsKey(perpetrator)){
//				positiveReceivedActions.get(perpetrator).addAll(tempPositive);
//			}
//		}
//		if (!tempNegative.isEmpty()){
//			if (!negativeReceivedActions.containsKey(perpetrator)){
//				negativeReceivedActions.put(perpetrator, tempNegative);
//			}
//			if(negativeReceivedActions.containsKey(perpetrator)){
//				negativeReceivedActions.get(perpetrator).addAll(tempNegative);
//			}
//		}
//	}

	/**
	 * Called when a Character does something negative to a negative
	 * Character. Makes the action positive and calls setProbabilities().
	 * Called in a case like "The good girl killed the evil witch."
	 * @param action
	 */
	public void moveNegativeActionToPositive(Action action){
        action.type = Action.ActionType.POSITIVE;
//		if (negativeDeliveredActions.contains(action)){//just to be safe
//			negativeDeliveredActions.remove(action);
//			positiveDeliveredActions.add(action);
//		}
		setProbabilities();
	}


	/* Note that the probabilities of being a protagonist/antagonist will not necessarily 
	 * add up to 1. There is a lot of uncertainty about whether or not the character is 
	 * just a neutral character. The difference in positivity/negativity and 1 represents 
	 * the probability that the character is just neutral.
	 */
	/**
	 * Sets the probability that this Character object is a protagonist
	 * and the probability that it is an antagonist.
	 */
	public void setProbabilities(){
		probProtagonist = get_itivity(true);
		probAntagonist = get_itivity(false);
	}

	/**
	 * Returns a ratio of positive words to classifiable (words that can be clearly marked as positive or negative)
	 * words.
	 * @param positive
	 * @return
	 */
	public double get_itivity(boolean positive){
		double posAdj = this.positiveAdjectives.size();
		double negAdj = this.negativeAdjectives.size();
		double posVb  = Action.getActionTypeCount(receivedActions, Action.ActionType.POSITIVE);//this.positiveDeliveredActions.size();
		double negVb  = Action.getActionTypeCount(receivedActions, Action.ActionType.NEGATIVE);//this.negativeDeliveredActions.size();
		double total  = posAdj + negAdj + posVb + negVb;
		double numerator = positive? posAdj+posVb : negAdj+negVb;
		return numerator/Math.max(1.0,total);
	}

	//Getters
	public String getName(){
		return this.name;
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

    public void registerReceivedAction(Action a){
        Character perp = a.getSubject();
        SortedSet<Action> actions = this.receivedActions.get(perp);
        if (actions == null){
            actions = new TreeSet<Action>();
            this.receivedActions.put(perp,actions);
        }
        actions.add(a);
    }

    public void registerDeliveredAction(Action a){
        Character victim = a.getObject();
        SortedSet<Action> actions = this.deliveredActions.get(victim);
        if (actions == null){
            actions = new TreeSet<Action>();
            this.deliveredActions.put(victim,actions);
        }
        actions.add(a);
    }

	public Map<Character, SortedSet<Action>> getPositiveDeliveredActions(){
		return Action.getActionsOfType(deliveredActions, Action.ActionType.POSITIVE);
	}


	public Map<Character, SortedSet<Action>> getNegativeDeliveredActions(){
        return Action.getActionsOfType(deliveredActions, Action.ActionType.POSITIVE);
	}

	public Map<Character, SortedSet<Action>> getPositiveReceivedActions(){
        return Action.getActionsOfType(receivedActions, Action.ActionType.POSITIVE);
	}

    public Map<Character, SortedSet<Action>> getNegativeReceivedActions(){
        return Action.getActionsOfType(receivedActions, Action.ActionType.NEGATIVE);
    }
	
	public void incrementOccurrences(){
		this.occurrences = this.occurrences + 1;
	}

	//for testing only.
	public String toString(){
		return ("Name: "+this.name+
				"\nPositive adjectives: "+this.positiveAdjectives.toString()+
				"\nNegative adjectives: "+this.negativeAdjectives.toString()+
                "\nReceived Actions: " + Action.prettyPrint(this.receivedActions) +
                "\nDeliveredActions: " + Action.prettyPrint(this.deliveredActions) +
//				"\nPositive delivered actions: "+this.positiveDeliveredActions.toString()+
//				"\nNegative delivered actions: "+this.negativeDeliveredActions.toString()+
//				"\nPositive received actions: "+this.positiveReceivedActions.toString()+
//				"\nNegative received actions: "+this.negativeReceivedActions.toString()+
				"\nOccurrences: "+this.occurrences+
				"\nProbability that \""+this.name+"\" is a protagonist: "+probProtagonist+
				"\nProbability that \""+this.name+"\" is an antagonist: "+probAntagonist+"\n\n");
	}
}
