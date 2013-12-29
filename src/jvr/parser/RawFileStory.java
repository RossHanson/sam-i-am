package jvr.parser;

import jvr.content.RawStory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: ross
 * Date: 12/7/13
 * Time: 11:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class RawFileStory extends RawStory {

    private static DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

    private static final String CONTENT_ELEMENT_NAME="contents";
    private static final String TITLE_ELEMENT_NAME="title";


    public RawFileStory(String filename) throws IOException, ParserConfigurationException, SAXException {
        File storyFile = new File(filename);
        Scanner in = new Scanner(storyFile);
        contents = "";
        while(in.hasNext()){
            contents = contents + in.nextLine();
        }
    }

    public String getTitle(){
        return title;
    }

    public String getContents(){
        return contents;
    }




}
