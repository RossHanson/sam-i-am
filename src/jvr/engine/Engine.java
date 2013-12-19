package jvr.engine;

import edu.stanford.nlp.parser.Parser;
import edu.stanford.nlp.trees.Tree;
import jvr.content.RawStory;
import jvr.parser.ParsedStory;
import jvr.parser.StanfordParser;
import jvr.parser.StoryParser;
import jvr.parser.TestStory;

import java.io.IOException;
import java.text.Annotation;

/**
 * Created with IntelliJ IDEA.
 * User: ross
 * Date: 10/17/13
 * Time: 6:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class Engine {

    public static void main(String args[]) throws IOException {
        System.out.println("Hey! I compiled! Add some stuff");
        TestStory story = new TestStory("Test Story", "");
        story.addSentence("The evil robber killed the bank. ");
        story.addSentence("Superman chased the robber. ");
        story.addSentence("Superman beat up the robber. ");
        story.addSentence("Then the police came and arrested him. ");
        System.out.println("Story is: " + story.getContents());
        Tree[] sentenceTree;
        StanfordParser sp = new StanfordParser();
        Annotation document = new Annotation("Rob has a nice car. He also has two arms. Now, he has three.");



        Tree testTree = sp.parseStory("Rob has a nice car. He also has two arms. Now, he has three.");

        sentenceTree  =  sp.parseStory(story);
        Characters chars = new Characters();
        for (Tree sentence : sentenceTree){
            chars.updateCharacters(sentence);
        }
        System.out.println("Characters.characters: \n" + Characters.characters.toString());

    }


}
