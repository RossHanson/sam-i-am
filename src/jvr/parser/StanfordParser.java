package jvr.parser;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.*;
import jvr.content.RawStory;
import jvr.content.TrainingStory;

import java.io.StringReader;
import java.util.List;
import java.util.Properties;
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
        System.out.println("Use enginge.java instead");
	}


	private StanfordCoreNLP pipeline;

	public StanfordParser(){
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse");
		pipeline= new StanfordCoreNLP(props);
	}

    public TrainingStory parseStory(RawStory story){
        Annotation document = parseStory(story.getContents());
        return new TrainingStory(document);
    }

	public Annotation parseStory(String content){
        Annotation document = new Annotation(content);
        pipeline.annotate(document);
        return document;
	}

    public TreeGraphNode getSentenceSubject(Tree parsedSentence){
        EnglishGrammaticalStructure egs = new EnglishGrammaticalStructure(parsedSentence);
        return EnglishGrammaticalStructure.getSubject(egs.root());
    }





}
