package htmlparser;

import lucene.MyIndexUpdater;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Peter on 2017/6/4 0004.
 */
public class HtmlUpdater {
    //历史文档
    private final static String historyDoc="D:\\gwt\\history.htm";
   // private final static String docPath="D:\\gwt\\materials\\";
    private final static String docPath="D:\\gwt\\document\\";
    //从公文通获取新的文档
    private final static String docUrl="http://www.szu.edu.cn/board/";


    public static void main(String[] args) {
        update();
    }

    public static void update() {
        Date date=new Date();
        String newPath=docPath+date.toString().replace(" ","").replace(":","")+"\\";
        File newFile=new File(newPath);
        if(!newFile.exists())
            newFile.mkdir();
       // System.out.println(newPath);
       Set<String> newDoc=updateDoc(newPath);
       int size=newDoc.size();
       File file=new File(newPath);
       while(file.listFiles().length<size){

       }
        MyIndexUpdater.updateIndex(newPath);
    }

    public static Set<String> updateDoc(String out_dir){
        Set<String> oldDoc=getOldDoc();

        Set<String> newDoc=getNewDoc();
        //去除已经有的document
        newDoc.removeAll(oldDoc);
        System.out.println("newDoc size:"+newDoc.size());
        HtmlDownloader.downloadHtml(newDoc,out_dir);

        return newDoc;
    }

    //获取已经有的文档
    public static Set<String> getOldDoc(){
        Set<String> result=new HashSet<>();
        File root=new File(docPath);
        File[] rootFiles=root.listFiles();

        for(File root_dir:rootFiles){
            //System.out.println(root_dir);
            if(root_dir.isDirectory()){
                File[] child_file=root_dir.listFiles();
                for(File fFile:child_file){
                    String fileName=fFile.getName();
                    int ind=fileName.indexOf(".");
                    result.add(fileName.substring(0,ind));
                }
            }
        }
        return result;
    }

    //获取新的文档
    public static Set<String> getNewDoc(){
        return HtmlDownloader.getUrlSet(docUrl);
    }
}
