package jvr.crawler.baldwin;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: ross
 * Date: 10/27/13
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainPageCrawler {


    private static String PAGE_URL = "http://www.mainlesson.com/displaystoriesbytitle.php";

    public static void main(String args[]) throws IOException, ParserConfigurationException {
        Document titlePage = readPage(PAGE_URL);
        Elements newsHeadlines = titlePage.select("body table tbody tr td table tbody tr td:nth-child(1) a");
        for (Iterator<Element> elements = newsHeadlines.iterator(); elements.hasNext();){
            System.out.println("Element is :" + elements.next().attr("href"));
            StoryScraper story = new StoryScraper(elements.next().attr("href"));
            story.downloadFile();
        }
    }



    public static Document readPage(String uri) throws ParserConfigurationException, IOException {
        Document doc = Jsoup.connect(uri).get();

        return doc;
    }
}
