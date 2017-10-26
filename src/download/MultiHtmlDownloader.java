package download;

import htmlparser.HtmlUpdater;
import htmlparser.SingleHtmlParser;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created by Peter on 2017/6/3 0003.
 */
public class MultiHtmlDownloader {
    private final static int THREAD_COUNT=500;
    private final static String historyDoc="D:\\gwt\\history.htm";
    private final static String baseUrl="http://www.szu.edu.cn/board/view.asp?id=";

    public static void main(String[] args) {
        Set<String> oldDoc= HtmlUpdater.getOldDoc();
        Set<String> set=new HashSet<>();
       // Set<String> notDoc=getNotDoc();
        //一年：319623-342383
        //两年：298960-319623
        //三年281020-298960
        //五年 243279-281020
       for(int i=243279;i<281020;++i){
           set.add(String.valueOf(i));
       }
       //文件存储路径
       String path="D:\\gwt\\document\\old4\\";
       set.removeAll(oldDoc);
       System.out.println("set size:"+set.size());
       downloadHtml(set,path);
    }

    public static void downloadHtml(Set<String> url_set,String out_dir) {
        ExecutorService pool= Executors.newFixedThreadPool(THREAD_COUNT);
        String errPath="D:\\gwt\\err.txt";
        int num=400;
        int cur=0;
        int ret=0;
        //获得返回结果
        Future<Integer>[] futures=new Future[num+1];

        BufferedWriter writer=null;

        try{
            //在末尾补上
            writer=new BufferedWriter(new FileWriter(errPath,true));
            for(String url:url_set){
                Callable<Integer> task=new SingleHtmlDownloader(url,baseUrl+url,out_dir,writer);
                futures[cur++]=pool.submit(task);
                if(cur==num){
                    cur-=1;
                    while (futures[cur].get()!=1)
                        ;
                    cur=0;
                }
            }
        }catch(IOException e){

        }catch (InterruptedException e){

        }catch (ExecutionException e){

        }finally {
            if(writer!=null){
                try{
                    writer.close();
                }catch (IOException e){

                }
            }
        }

        pool.shutdown();
    }

    private static Set<String> getNotDoc(){
        Set<String> set=new HashSet<>();
        String path="D:\\gwt\\document\\err.txt";
        try(BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(path)))){
            String line;
            while((line=reader.readLine())!=null){

               set.add(line.substring(line.lastIndexOf("=")+1));
            }
        }catch (IOException e){

        }
        return set;
    }
}
