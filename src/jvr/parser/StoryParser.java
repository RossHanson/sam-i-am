package jvr.parser;

import jvr.content.RawStory;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: ross
 * Date: 12/7/13
 * Time: 11:06 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class StoryParser {


    public abstract ParsedStory parseStory(String content) throws IOException;




}
