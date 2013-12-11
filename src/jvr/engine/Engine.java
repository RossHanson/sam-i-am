package jvr.engine;

import edu.stanford.nlp.parser.Parser;
import jvr.content.RawStory;
import jvr.parser.ParsedStory;
import jvr.parser.StanfordParser;
import jvr.parser.StoryParser;
import jvr.parser.TestStory;

import java.io.IOException;

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
        RawStory story = new TestStory();
        StoryParser parser = StanfordParser.getInstance();
        ParsedStory parsedStory = parser.parseStory(story.getContents());
        parsedStory.printStructure();

    }


}
