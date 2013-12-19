/**
 * 
 */
package jvr.engine;

import java.util.*;

import edu.stanford.nlp.trees.EnglishGrammaticalStructure;
import jvr.parser.BasicParserFunctionality;
import jvr.parser.StanfordParser;

import edu.stanford.nlp.trees.Tree;
import jvr.parser.TestStory;

//The wicked witch hit the little girl. The evil, wicked wolf likes to bite nice little children. The beautiful princess saved the evil witch.
/**
 * @author vesha
 *
 */
public class Characters {
	public static SortedMap<String, Character> characters= new TreeMap<String, Character>();
	//////////////////////LOOK BELOW!////////////////////////
	/* Example strings you can pass in separated across the following lines: 
	   The wicked witch hit the little girl. The mean wolf likes to bite nice little children. The witch and the wolf are friends. The beautiful princess saved the evil witch.
	   The wicked witch hit the little girl.
	   The little girl cried out.
	   The wolf likes to bite little children.
	   The bad wolf is mean.
	   The witch and the wolf are friends.
	   The beautiful princess saved the evil witch.
	 */
	//////////////////////LOOK ABOVE!////////////////////////
	public static void main(String[] arg){
		StanfordParser sp = new StanfordParser();
		System.out.println("Welcome to Characters.java! In this class, you can enter information about multiple characters!");
//		Scanner in = new Scanner(System.in);
//		System.out.println("Let's start with the very first character. Please enter the sentence that you want to use "+
//				"(one sentence at a time or things get weird). Or enter \"exit\" to...well...exit.");

//		if (!sentence.toLowerCase().equals("exit")) {
//			sentenceTree = sp.parseStory(sentence);
//			Characters chars = new Characters(sentenceTree);
//			System.out.println("\nYou can either enter another sentence containing more information"+
//					" about this character, or a sentence that discusses a completely new character. "+
//					"Otherwise, you can enter \"exit\" to leave this step and see what happens "+
//					"when we see how the characters affect each other.");
//			sentence = in.nextLine();
//			while (!sentence.equals("exit")){
//				sentenceTree = sp.parseStory(sentence);
//                EnglishGrammaticalStructure structure = new EnglishGrammaticalStructure(sentenceTree);
//                EnglishGrammaticalStructure.getSubject(structure.root());
//				chars.updateCharacters(sentenceTree);
//				System.out.println("\nEnter another sentence or enter \"exit\".");
//				sentence = in.nextLine();
//			}
//			System.out.println("You have selected to stop entering information. Here is the current "+
//					"inventory of characters, before looking at their relationships with each other:\n");
//			chars.twoNegativesMakeAPositive();

//		}
//		System.out.println("\nAll done now. Thanks for visiting!\n");
//		in.close();
	}

	/**
	 * Constructs a SortedMap of Character objects
	 * @param story: parsed story in Tree representation
	 */
	public Characters(Tree story){//Will eventually automate the creation of Character objects when given a story. So really, the bulk of the work in terms of Character analysis will be done here in this class/in Character.
		WordTrainingSet.importWordSet(false);
		characters = new TreeMap<String, Character>(); //Character's name (String) , all of its qualities are stored in the Character object which is the value.
		initializeAllCharacters(story);
	}

    public Characters(){
        WordTrainingSet.importWordSet(false);
        characters = new TreeMap<String,Character>();
    }

	/**
	 * Updates the Characters as necessary when a new sentence is passed in.
	 * @param sentence
	 */
	public void updateCharacters(Tree sentence){
		initializeAllCharacters(sentence);
	}

	/**
	 * Initializes all Characters using only name and adjective information 
	 * (all of which is stored in noun phrases).
	 * @param story
	 */
	public void initializeAllCharacters(Tree story){
		for (Tree subtree : story){
			if (subtree.label().value().equals("NP")){//extracting all noun phrases to initialize all of the characters.
				addCharacterToMap(subtree);
			}
		}
		getAllVerbPhrases(story);
	}

	/**
	 * For each declarative clause in the Tree "story," finds the subject of the clause (the perpetrator
	 * of actions), the actions contained within the clause. Then attributes those clauses to 
	 * Characters that are the direct object of those actions.
	 * @param story
	 */
	public void getAllVerbPhrases(Tree story){
		String[] verbLabels = {"VB","VBD","VBG","VBN", "VBP","VBZ"};
		String[] nounLabels = {"NN", "NNS", "NNP", "NNPS"};
		ArrayList<Tree> receivedActions = new ArrayList<Tree>();
		ArrayList<Tree> affectedCharacters = new ArrayList<Tree>();
		Character perpetrator = null;
		for (Tree subtree : story){
			if (subtree.label().value().equals("S")){//Simple declarative statement (a complete phrase).
				for (Tree subSubTree : subtree){
					if (!subSubTree.parent(story).label().value().equals("VP") && subSubTree.label().value().equals("NP")){//Ignores direct objects, only cares about the subject of the declarative statement.
						perpetrator = characters.get(Character.findName(subSubTree));
					}
					if (subSubTree.label().value().equals("VP")){//the verb phrase in the sentence
						receivedActions = new ArrayList<Tree>(); //will store all received actions from this subtree.
						affectedCharacters = new ArrayList<Tree>();
						for (Tree littleTree : subSubTree){
							if (BasicParserFunctionality.equalsLabel(littleTree, verbLabels)){//a verb that potentially affects another character.

								receivedActions.add(littleTree);
							}
							if (BasicParserFunctionality.equalsLabel(littleTree, nounLabels)){//A direct/indirect object
//                                for (Tree c: littleTree.children())
//                                    affectedCharacters.add(c);
								affectedCharacters.add(littleTree);
							}

						}
						if (!affectedCharacters.isEmpty() && !receivedActions.isEmpty() && !(perpetrator==null)){//Someone was actually affected.
                            for (Tree actionTree: receivedActions){
                                for (Tree characterTree: affectedCharacters){
//                                    Action a = new Action(perpetrator,resolveCharacter(characterTree),actionTree,Action.ActionType.NEUTRAL);
//                                    a.notifyParticipants();
                                }
                            }
//							manageAffectedCharacters(affectedCharacters, receivedActions, perpetrator.getName());
						}

					}
					if (!(perpetrator==null)&&!(receivedActions==null)){//Add the subject's actions to their action set.
						perpetrator.updateCharacter(null, receivedActions);
						receivedActions = null;//Remove previously used actions for the next character.
					}
				}
			}
		}
	}


	/**
	 * Either adds the character to the map charactersAndSentiments if it is not
	 * already in that map, or updates the character with newly found information
	 * if it is already in the map.
	 * @param nounPhrase
	 */
	public void addCharacterToMap (Tree nounPhrase){
		String characterName = Character.findName(nounPhrase);
		if (!(characterName==null)){
            if (characters.containsKey(characterName)){ //Have already recorded this character
				characters.get(characterName).updateCharacter(nounPhrase, null);
				characters.get(characterName).incrementOccurrences();
			}else{
				Character personaje = new Character(nounPhrase);
				characters.put(personaje.getName(), personaje);
			}
		}
	}

    public Character resolveCharacter(Tree nounPhrase){
        String characterName = Character.findName(nounPhrase);
        if (characterName == null){
            System.err.println("Error! Could not resolve character name for " + nounPhrase.value());
            return null; //Probably should throw an exception....
        }
        Character c;
        if (characters.containsKey(characterName)){
            c =  characters.get(characterName);
        } else {
            c = new Character(nounPhrase);
            characters.put(c.getName(), c);
        }
        return c;
    }

	/**
	 * The beginning of an effort to incorporate relationships between
	 * the status of protagonists and antagonists in the story. Unfinished.
	 */
	public void twoNegativesMakeAPositive (){
		for (Character personnage : characters.values()){
			Map<Character, SortedSet<Relation>> negativeActions= personnage.getNegativeReceivedActions();
			Character perpetrator;
			SortedSet<Relation> actions;
			for (Character perp : negativeActions.keySet()){
				perpetrator = characters.get(perp);
				actions = negativeActions.get(perp);
				for (Relation act : actions){
					System.out.println("act: "+act);
					if (personnage.getProbAntagonist()>personnage.getProbProtagonist()){
							perpetrator.moveNegativeActionToPositive(act);
					}
					//TODO: Maybe even somehow incorporate just how large the difference between probProtagonist and probAntagonist is.
					/* How we can deal with interpreting actions: 
					 * If they do something good to a good character, that action 
					 * will remain in the set of positive verbs. If they do something
					 * bad to a good character, that verb will be added to the set of 
					 * negative verbs. If they do something bad to a bad character,
					 * that verb will be added to the set of positive verbs. If they do something good to a bad character, 
					 * things get a bit murky. This might have to be determined with
					 * more testing. On one hand, this person might just be a very good
					 * person who is helping another in spite of their "badness." On the 
					 * other hand, the character might just be doing something wrong.
					 */
				}
			}
		}
	}
}
