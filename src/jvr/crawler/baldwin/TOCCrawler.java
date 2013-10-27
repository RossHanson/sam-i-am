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
 * Created with IntelliJ IDEA.
 * User: ross
 * Date: 10/27/13
 * Time: 3:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class TOCCrawler {

    private String url;
    private String fName;
    private StringBuffer content = new StringBuffer();

    public TOCCrawler(String url){
        this.url = url;
        this.fName = parseFName(url);

    }

    private static String parseFName(String url){
        //Assume we have a php file w/ args
        return url.substring(url.lastIndexOf('?'));
    }

    public void downloadFile() throws IOException {
        File output = new File(fName);
        if (output.exists())
            return;
        output.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        bw.write(getText(url));
        bw.close();
    }

    public static String getText(String url) throws IOException{
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
