package jvr.content;

/**
 * Created with IntelliJ IDEA.
 * User: ross
 * Date: 12/7/13
 * Time: 11:19 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class RawStory {

    protected String title;
    protected String contents;


    public abstract String getTitle();

    public abstract String getContents();
}
