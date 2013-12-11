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

    private static int LIMIT = 200;

    private static String PAGE_URL = "http://www.mainlesson.com/displaystoriesbytitle.php";
    private static String HEADLINE_SELECTOR = "body table tbody tr td table tbody tr td:nth-child(1) a";

    public static void main(String args[]) throws IOException, ParserConfigurationException {
        int counter = 0;
        Document titlePage = readPage(PAGE_URL);
        Elements newsHeadlines = titlePage.select(HEADLINE_SELECTOR);
        for (Iterator<Element> elements = newsHeadlines.iterator(); counter <= LIMIT && elements.hasNext();){
            System.out.println("Element is :" + elements.next().attr("href"));
            String targ = elements.next().attr("href");
            StoryScraper story = new StoryScraper("data/" + StoryScraper.parseFName(targ),targ);
            story.downloadFile();
            counter++;
        }
    }



    public static Document readPage(String uri) throws ParserConfigurationException, IOException {
        Document doc = Jsoup.connect(uri).get();

        return doc;
    }
}
