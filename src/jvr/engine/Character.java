package jvr.engine;

import java.util.*;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.trees.Tree;

import jvr.graph.Relation;
import jvr.graph.Vertex;
import jvr.parser.BasicParserFunctionality;

/**
 * @author vesha
 */
public class Character extends Vertex{
	public String name;
    private IndexedWord mainWord;
    private List<IndexedWord> modifiers;

	private SortedSet<String> positiveAdjectives = new TreeSet<String>();
	private SortedSet<String> negativeAdjectives = new TreeSet<String>();

    private Map<Vertex,SortedSet<Relation>> deliveredActions = new HashMap<Vertex,SortedSet<Relation>>();
    private Map<Vertex, SortedSet<Relation>> receivedActions = new HashMap<Vertex, SortedSet<Relation>>();

	private Integer occurrences; //represents the number of times this character appears in the story.
	private double probProtagonist;
	private double probAntagonist;

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
        this.id = Vertex.getNewId();
    }

    public Collection<Relation> getOutbound(){
        Collection<Relation> outboundRelations = new HashSet<Relation>();
        for (Collection<Relation> relations : this.deliveredActions.values()){
            outboundRelations.addAll(relations);
        }
        return outboundRelations;
    }

    public Collection<Relation> getInbound(){
        Collection<Relation> inboundRelations = new HashSet<Relation>();
        for (Collection<Relation> relations : this.receivedActions.values()){
            inboundRelations.addAll(relations);
        }
        return inboundRelations;
    }

    public void addModifier(IndexedWord modifier){
        this.modifiers.add(modifier);
        this.name = modifier.lemma() + " " + this.name;
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


    @Override
    public Character cleanCopy(){
        return Character.createCharacter(this.mainWord,this.modifiers);
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
        Vertex perp = a.getSubject();
        SortedSet<Relation> actions = this.receivedActions.get(perp);
        if (actions == null){
            actions = new TreeSet<Relation>();
            this.receivedActions.put(perp,actions);
        }
        actions.add(a);
    }

    public void registerDeliveredAction(Relation a){
        Vertex victim = a.getObject();
        SortedSet<Relation> actions = this.deliveredActions.get(victim);
        if (actions == null){
            actions = new TreeSet<Relation>();
            this.deliveredActions.put(victim,actions);
        }
        actions.add(a);
    }


    public Map<Vertex, SortedSet<Relation>> getDeliveredActionsOfType(Action.ActionType type){
        return Action.getActionsOfType(deliveredActions,type);
    }

    public Map<Vertex, SortedSet<Relation>> getReceivedActions(Action.ActionType type){
        return Action.getActionsOfType(receivedActions,type);
    }

	public void incrementOccurrences(){
		this.occurrences = this.occurrences + 1;
	}

    public boolean equals(Character c){
        return this.id == c.id;
    }

	//for testing only.
	public String toString(){
		return ("Name: "+this.name+
				"\nPositive adjectives: "+this.positiveAdjectives.toString()+
				"\nNegative adjectives: "+this.negativeAdjectives.toString()+
                "\nReceived Actions: " + Action.prettyPrint(this.receivedActions) +
                "\nDeliveredActions: " + Action.prettyPrint(this.deliveredActions) +
				"\nOccurrences: "+this.occurrences+
				"\nProbability that \""+this.name+"\" is a protagonist: "+probProtagonist+
				"\nProbability that \""+this.name+"\" is an antagonist: "+probAntagonist+"\n\n");
	}
}
