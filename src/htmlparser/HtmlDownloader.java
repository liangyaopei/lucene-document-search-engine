package htmlparser;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.BaseHrefTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Peter on 2017/6/3 0003.
 */
public class HtmlDownloader {
    private final static int THREAD_COUNT=100;
    private final static String historyDoc="D:\\gwt\\history.htm";
    private final static String baseUrl="http://www.szu.edu.cn/board/view.asp?id=";

    public static void main(String[] args) {
      // Set<String> oldDoc=getUrlSet(historyDoc);
      // downloadHtml(oldDoc,"D:\\gwt\\document\\attaches\\");
        Set<String> set=new HashSet<>();
       for(int i=2472739;i<341493;++i){
           set.add(String.valueOf(i));
       }

       downloadHtml(set,"D:\\gwt\\document\\test\\");

     //  oldDoc.add(String.valueOf(241780));

    }

    public static void downloadHtml(Set<String> url_set,String out_dir) {
        ExecutorService pool= Executors.newFixedThreadPool(THREAD_COUNT);
        //要下载的文档集合
        for(String url:url_set){
            Runnable task=new SingleHtmlParser(url,baseUrl+url,out_dir);
            pool.submit(task);
        }
        pool.shutdown();
    }

    public static Set<String> getUrlSet(String url) {
        Set<String> url_set=new HashSet<>();

        try{
            Parser parser=new Parser();
            parser.setURL(url);

            NodeFilter[] filters=new NodeFilter[1];
            filters[0]=new HasAttributeFilter("class","fontcolor3");

            NodeFilter filter=new OrFilter(filters);
            NodeList list=parser.extractAllNodesThatMatch(filter);

            for(int i=0;i<list.size();++i){
                Node node=list.elementAt(i);
                String str=node.toHtml();
                if(str.contains("target")){
                    int bgn=str.indexOf("href=\"view.asp?");
                    int end=str.lastIndexOf("class=fontcolor3");
                    String targe_url=str.substring(bgn+18,end-2);
                    url_set.add(targe_url);
                }
            }
        }catch (ParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return url_set;
    }

    private static boolean verifyUrl(String url){
        for(int i=0;i<url.length();++i){
            if(!Character.isDigit(url.charAt(i))){
                return false;
            }
        }
        return true;
    }
}
