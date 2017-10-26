package lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Peter on 2017/5/29 0029.
 */
public class MyIndexUpdater {

    //private static String path="D:\\gwt\\index\\";
    private static String path="D:\\gwt\\indexes\\";
    private static Analyzer analyzer=MySmartChineseAnalyzer.getSmartChineseAnalyzer();

    public static void main(String[] args) {

       //updateIndex("D:\\gwt\\document\\attaches\\");
        updateIndex("D:\\gwt\\document\\past");
        /*
       updateIndex("D:\\gwt\\document\\FriJun16160106CST2017");
        updateIndex("D:\\gwt\\document\\MonJun19142945CST2017");
        updateIndex("D:\\gwt\\document\\MonJun19221503CST2017");
        updateIndex("D:\\gwt\\document\\ThuJun15173518CST2017");
        */
       //updateIndex("D:\\gwt\\document\\TueJun20144343CST2017");
        //updateIndex("D:\\gwt\\document\\WedJun21203542CST2017");
        //updateIndex("D:\\gwt\\document\\WedJun21205342CST2017");
    }


    public static void updateIndex(String input_dir) {
        //System.out.println("new doc Path:"+input_dir);
        String ex="";
        try{
            //开启directory
            Directory directory = FSDirectory.open(Paths.get(path));
            IndexWriterConfig indexWriterConfig=new IndexWriterConfig(analyzer);
            //这里设置为创建或增加
            indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            IndexWriter indexWriter=new IndexWriter(directory,indexWriterConfig);
            //获得要跟新的文件的路径
            File newFile=new File(input_dir);
            File[] newFiles=newFile.listFiles();
            System.out.println("FileSize:"+newFiles.length);
            for(File f:newFiles){
                ex=f.toString();
                //输入六读取文件
                try(BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(f)))){
                    Document document=new Document();
                    //读取标题
                    String title=reader.readLine();
                    if(title!=null && title.startsWith("深大官网　English")){
                        title=reader.readLine();
                    }

                    //读取时间戳
                    String timeStamp=reader.readLine();

                    if(timeStamp==null)
                        continue;
                    //j将时间戳转化为发文单位和Int想数据
                    List<String> re=parseTime(timeStamp);
                    String apartment=re.get(0);
                    String time=re.get(1);
                    int date=time2Int(time);

                    //存储标题，发文单位，时间
                    document.add(new Field("title",title, TextField.TYPE_STORED));
                    document.add(new Field("apartment",apartment, TextField.TYPE_STORED));
                    document.add(new Field("time",time, TextField.TYPE_STORED));
                    document.add(new IntField("date",date,Field.Store.YES));
                    document.add(new NumericDocValuesField("date",date));

                    StringBuilder builder=new StringBuilder();
                    String line;
                    StringBuilder attachment=new StringBuilder();
                    int clickCount=0;
                    while((line=reader.readLine())!=null){
                        //判断是否为附件
                        if(line.endsWith(".doc") || line.endsWith(".xls")
                                || line.endsWith(".pdf")){
                            String cleanLine=line.substring(1);
                         //   System.out.println(f+":"+cleanLine);
                            attachment.append(cleanLine);
                        }else if(line.contains("更新于") && line.contains("点击数")){
                            //判断是否否问点击数
                            int bgn=line.lastIndexOf(":");
                            clickCount=Integer.parseInt(line.substring(bgn+1,line.length()-1));
                        }
                        builder.append(line);
                    }
                    //存储正文
                    document.add(new Field("body",builder.toString(),TextField.TYPE_STORED));
                    //存储附件
                    document.add(new Field("attachment",attachment.toString(),TextField.TYPE_STORED));
                    //存储点击数
                    document.add(new IntField("click",clickCount,Field.Store.YES));
                    document.add(new NumericDocValuesField("click",clickCount));
                    indexWriter.addDocument(document);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            indexWriter.close();
            directory.close();
        }catch (IOException e){
            System.out.println("shit");
            e.printStackTrace();
        }catch (IndexOutOfBoundsException e){
            System.out.println(ex);
        }
    }

    private static List<String> parseTime(String str){
        List<String> result=new ArrayList<>();
        int i;
        for(i=0;i<str.length();++i){
            char ch=str.charAt(i);
            if(Character.isDigit(ch)){
                result.add(str.substring(0,i-1));
                break;
            }
        }
        String[] re=str.substring(i).split(" ");
        result.add(re[0]);
        return result;
    }

    private static int time2Int(String str){
        String[] re=str.split("-");
        StringBuilder builder=new StringBuilder();
        for(String s:re){
            if(s.length()>1){
                builder.append(s);
            }else{
                builder.append("0").append(s);
            }
        }
        return Integer.parseInt(builder.toString());
    }

}
