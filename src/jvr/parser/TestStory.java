package jvr.parser;

import jvr.content.RawStory;

/**
 * Created with IntelliJ IDEA.
 * User: ross
 * Date: 12/7/13
 * Time: 12:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestStory extends RawStory {

    private static final String PUNCTATION_REGEX = "[!?]";


    public TestStory(){
        title = "Test Story";
        contents = "Once upon a time there were four little rabbits, and their"+
                " names were Flopsy, Mopsy, Cotton-tail and Peter. They lived with their"+
                "mother in a sand-bank underneath the root of a very big fir tree."+
                "\"Now, my dears\", said old Mrs. Rabbit one morning, \"You may go into the"+
                " fields or down the land, but don't go into Mr. McGregor's garden."+
                " your father had an accident there; and he was put in a pie by Mrs. McGregor. Accidental \"";
    }

    public TestStory(String title, String contents){
        this.title = title;
        this.contents = contents;
    }


    /**
     * Add a sentence to the existing contents string. Please try to include punctation as it is not automatically added
     * at the moment
     * @param sentence Sentence to add
     */
    public void addSentence(String sentence){
        this.contents = this.contents + sentence;
    }

    private static String[] processContentString(String content){
        content = content.replaceAll(PUNCTATION_REGEX,".");
        return content.split(".");
    }



    public String getContents(){
        return contents;
    }


    public String getTitle() {
        return title;
    }

}
