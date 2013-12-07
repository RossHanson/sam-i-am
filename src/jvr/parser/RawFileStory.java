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
        DocumentBuilder db = dbFactory.newDocumentBuilder();
        Document dom = db.parse(storyFile);

        Element contentEl = dom.getElementById(CONTENT_ELEMENT_NAME);
        Element titleEl = dom.getElementById(TITLE_ELEMENT_NAME);

        if (contentEl==null){
            throw new IOException("Improperly formatted file");
        }

        if (titleEl!=null){
            contents = titleEl.getTextContent();
        }

    }

    public String getTitle(){
        return title;
    }

    public String getContents(){
        return contents;
    }




}
