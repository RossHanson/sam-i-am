/**
 * 
 */
package jvr.engine;

import java.util.*;

import jvr.graph.Relation;
import jvr.graph.Vertex;
import jvr.parser.BasicParserFunctionality;
import jvr.parser.StanfordParser;

import edu.stanford.nlp.trees.Tree;

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
//		for (Tree subtree : story){
//			if (subtree.label().value().equals("NP")){//extracting all noun phrases to initialize all of the characters.
//				addCharacterToMap(subtree);
//			}
//		}
//		getAllVerbPhrases(story);
	}






}
