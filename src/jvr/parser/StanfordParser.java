package jvr.parser;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.*;
import jvr.content.RawStory;

import java.io.StringReader;
import java.util.List;
import java.util.Scanner;


public class StanfordParser extends StoryParser{
//////////////////////LOOK BELOW!//////////////////////////
	/*Example strings you can pass in separated across lines: 
	  The wicked witch hit the little girl.
	  The little girl cried out.
	  The wolf likes to bite little children.
	  The wolf is mean.
	  The witch and the wolf are friends.
	  The beautiful princess killed the evil witch.
	 */
	//You can also use:
	/*
	  The wizard walked outside. 
	  He liked the snow. 
	  The wizard lied to the little girl. 
	  The wolf ate the sad girl.
	 */
//////////////////////LOOK ABOVE!////////////////////////
	public static void main(String[] arg){
		Scanner in = new Scanner(System.in);
		System.out.println("Please enter the sentence you want to have analyzed.");
		String story = in.nextLine();
		StanfordParser sp = new StanfordParser();
		while (!story.toLowerCase().equals("exit")){
			Tree parse = sp.parseStory(story);
			System.out.println(parse);
			for (Tree subtree: parse){
				if(subtree.label().value().equals("NN")){
                    //To implement
				}
			}
			System.out.println("Would you like to parse another sentence? If so, enter the sentence. If not, enter \"exit.\"");
			story = in.nextLine();
		}
		in.close();
	}

	private static StanfordParser parser;

	private LexicalizedParser lp;





	public StanfordParser(){
		lp = LexicalizedParser.loadModel(); //Should load the default grammar



	}

    public Tree[] parseStory(RawStory story){
        String[] contents = story.getContents().split("\\.|\\?");
        Tree[] trees = new Tree[contents.length];
        for (int i = 0; i< contents.length;i++){
            trees[i] = parseStory(contents[i]);
        }
        return trees;
    }

	public Tree parseStory(String content){
		TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
		List<CoreLabel> rawWords = tokenizerFactory.getTokenizer(new StringReader(content.toLowerCase())).tokenize();

		return lp.apply(rawWords);
	}

    public TreeGraphNode getSentenceSubject(Tree parsedSentence){
        EnglishGrammaticalStructure egs = new EnglishGrammaticalStructure(parsedSentence);
        return EnglishGrammaticalStructure.getSubject(egs.root());
    }

	public static StanfordParser getInstance(){
		if (parser==null)
			parser = new StanfordParser();
		return parser;
	}


}
