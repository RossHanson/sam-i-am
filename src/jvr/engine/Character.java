package jvr.engine;

import java.util.*;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.trees.Tree;

import jvr.parser.BasicParserFunctionality;

/**
 * @author vesha
 */
public class Character {
	public String name;
    private IndexedWord mainWord;
    private List<IndexedWord> modifiers;

	private SortedSet<String> positiveAdjectives = new TreeSet<String>();
	private SortedSet<String> negativeAdjectives = new TreeSet<String>();

    private Map<Character,SortedSet<Relation>> deliveredActions = new HashMap<Character,SortedSet<Relation>>();
    private Map<Character, SortedSet<Relation>> receivedActions = new HashMap<Character, SortedSet<Relation>>();

	private Integer occurrences; //represents the number of times this character appears in the story.
	private double probProtagonist;
	private double probAntagonist;

    private int ID;
    private static int TOTAL_COUNT = 0;

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
        this.ID = getID();
	}

    private static synchronized  int getID(){
        int id = TOTAL_COUNT;
        TOTAL_COUNT++;
        return id;
    }

    public IndexedWord getMainWord(){
        return mainWord;
    }

    public static Character createCharacter(IndexedWord word){
        return createCharacter(word, new LinkedList<IndexedWord>());
    }

    public static Character createCharacter(IndexedWord word, List<IndexedWord> nounModifiers){
        StringBuilder sb = new StringBuilder();
        if (nounModifiers!=null){
            for (IndexedWord w: nounModifiers)
                sb.append(w.lemma()).append(" ");
        }
        sb.append(word.lemma());
        return new Character(word, nounModifiers, sb.toString());
    }

    private Character(IndexedWord word, List<IndexedWord> nounModifiers, String name){
        this.mainWord = word;
        this.modifiers = nounModifiers;
        this.name = name;
        this.ID = getID();
    }

    public void addModifier(IndexedWord modifier){
        this.modifiers.add(modifier);
        this.name = modifier.lemma() + " " + this.name;
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

    public boolean isSameCharacter(Character c){
        return isSameCharacter(c,false);
    }

    public boolean isSameCharacter(Character c, boolean strict){
        boolean result =  c.mainWord.lemma().equals(this.mainWord.lemma());
        if (result && strict){
            for (IndexedWord wC: c.modifiers){
                boolean containsMod = false;
                for (IndexedWord wMe: this.modifiers){
                    containsMod = containsMod || wMe.lemma().equals(wC.lemma());
                }
                if (!containsMod)
                    return false;
            }
        }
        return result;
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
	 * Called when a Character does something negative to a negative
	 * Character. Makes the action positive and calls setProbabilities().
	 * Called in a case like "The good girl killed the evil witch."
	 * @param action
	 */
	public void moveNegativeActionToPositive(Relation action){
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

    public void registerReceivedAction(Relation a){
        Character perp = a.getSubject();
        SortedSet<Relation> actions = this.receivedActions.get(perp);
        if (actions == null){
            actions = new TreeSet<Relation>();
            this.receivedActions.put(perp,actions);
        }
        actions.add(a);
    }

    public void registerDeliveredAction(Relation a){
        Character victim = a.getObject();
        SortedSet<Relation> actions = this.deliveredActions.get(victim);
        if (actions == null){
            actions = new TreeSet<Relation>();
            this.deliveredActions.put(victim,actions);
        }
        actions.add(a);
    }

	public Map<Character, SortedSet<Relation>> getPositiveDeliveredActions(){
		return Action.getActionsOfType(deliveredActions, Action.ActionType.POSITIVE);
	}


	public Map<Character, SortedSet<Relation>> getNegativeDeliveredActions(){
        return Action.getActionsOfType(deliveredActions, Action.ActionType.POSITIVE);
	}

	public Map<Character, SortedSet<Relation>> getPositiveReceivedActions(){
        return Action.getActionsOfType(receivedActions, Action.ActionType.POSITIVE);
	}

    public Map<Character, SortedSet<Relation>> getNegativeReceivedActions(){
        return Action.getActionsOfType(receivedActions, Action.ActionType.NEGATIVE);
    }
	
	public void incrementOccurrences(){
		this.occurrences = this.occurrences + 1;
	}

    public boolean equals(Character c){
        return this.ID == c.ID;
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
