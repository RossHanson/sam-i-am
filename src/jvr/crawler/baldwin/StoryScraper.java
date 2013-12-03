package jvr.crawler.baldwin;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class to handle the parsing and downloading of a target
 *
 * Created with IntelliJ IDEA.
 * User: ross
 * Date: 10/27/13
 * Time: 3:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class StoryScraper {

    private String url;
    private String fName;
    private StringBuffer content = new StringBuffer();

    /**
     * Instantiates a new StoryScraper with the target url, attempts to determine a unique filename to output to from
     * the url
     * @param url Target url
     */
    public StoryScraper(String url){
        this.url = url;
        this.fName = parseFName(url);

    }

    /**
     * Instantiates a new Story scraper object with the given filename to output to and the url to download from
     * @param fName    Output filename
     * @param url      Target url
     */
    public StoryScraper(String fName, String url){
        this.url = url;
        this.fName = fName;
    }

    /**
     * Simple method to parse url into usable filename
     * @param url Url of target
     * @return Encoded Filename
     */
    public static String parseFName(String url){
        //Assume we have a php file w/ args
        return url.substring(url.lastIndexOf('?'));
    }

    /**
     * Downloads and saves the Story at this objects url
     * @throws IOException Error connecting to page or writing to disk
     */
    public void downloadFile() throws IOException {
        File output = new File(fName);
        if (output.exists())
            return;
        output.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        bw.write(getText(url));
        bw.close();
    }

    /**
     * Parses the target page and pulls all 'p' elements, which are stored as a long string. Potentially should
     * introduce newlines later
     * @param url Target url to download
     * @return String of the paragraphs stiched together
     * @throws IOException If there is an issue connecting to the webpage
     */
    private static String getText(String url) throws IOException{
        Document doc = Jsoup.connect(url).get();
        Iterator<Element> links = doc.getElementsByTag("p").iterator();
        StringBuffer sb = new StringBuffer();
        for (;links.hasNext();){
            Element link = links.next();
            sb.append(link.text());
        }
        return sb.toString();
    }

}
