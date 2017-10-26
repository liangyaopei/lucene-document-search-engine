package htmlparser;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.NotFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.ParagraphTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Peter on 2017/6/3 0003.
 */
public class SingleHtmlParser implements Runnable{
    private String fileName;
    private String url;
    private BufferedWriter writer;
    private String out_dir;


    public SingleHtmlParser(String fileName,String url,String out_dir){
        this.fileName=fileName;
        this.url=url;
        this.out_dir=out_dir;
        writer=null;
    }

    @Override
    public void run(){
        try{
           Parser parser=new Parser();
           parser.setURL(url);
              //标题过滤
               NodeFilter titleFilter=new NodeFilter(){
                   public boolean accept(Node node){
                       if(node.toHtml().startsWith("<td class=fontcolor3 align=center height=\"60\"><b><font size=\"4\">")){
                           return true;
                       }else {
                           return false;
                       }
                   }
               };
              //时间过滤
               NodeFilter timeFilter=new NodeFilter() {
                   @Override
                   public boolean accept(Node node) {
                       if(node.toHtml().startsWith("<td align=center height=30 style=\"font-size: 9pt\"><font color=#808080>"))
                           return true;
                       else{
                           return false;
                       }
                   }
               };
            //附件过滤
            NodeFilter attachFilter=new NodeFilter() {
                @Override
                public boolean accept(Node node) {
                    if(node.toHtml().startsWith("<a href=uploadfiles")){
                        return true;
                    }
                    else
                        return false;
                }
            };
            //点击数过滤
            NodeFilter clickFilter=new HasAttributeFilter("valign","bottom");

               NodeFilter[] filters=new NodeFilter[5];
               filters[0]=timeFilter;
               filters[1]=titleFilter;
               filters[2]=new NodeClassFilter(ParagraphTag.class);
               filters[3]=attachFilter;
               filters[4]=clickFilter;

               NodeFilter filter=new OrFilter(filters);
               NodeList list=parser.extractAllNodesThatMatch(filter);

               //请求的url不存在
               if(list.size()==0){
                   System.out.println("该文档不存在："+url);
                   return;
               }else{

                   //页面存在才创建文件
                   String out_path=out_dir+fileName+".txt";
                   writer=new BufferedWriter(new FileWriter(out_path));

                   for(int i=0;i<list.size();++i){
                       Node node=list.elementAt(i);

                       String str=node.toPlainTextString().trim();
                       if(str.contains("外部网　English") || str.startsWith("深大官网　English")){
                           continue;
                       }
                       //去除xml
                       if(str.contains("<?xml")){
                           int bgn=str.indexOf("<?xml");
                           if(bgn!=-1){
                               str=str.substring(0,str.indexOf("<?xml"));
                           }
                       }
                       writer.write(str.replace("&nbsp;","")+"\n");
                   }
               }

        }catch (IOException e){
            e.printStackTrace();
        }catch (ParserException e){
            e.printStackTrace();
        } finally {
            if(writer!=null){
                try{
                    writer.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        String url="http://www1.szu.edu.cn/board/view.asp?id=341298";
        String path="D:\\gwt\\materials\\test\\";
        singleHtmlPasrse(url,path);
    }

    public static void singleHtmlPasrse(String url,String path){
        try(BufferedWriter writer=new BufferedWriter(new FileWriter(path+"2.txt"))){
            ArrayList<String> attahment=new ArrayList();

            Parser parser=new Parser();
            parser.setURL(url);
            parser.setEncoding("gb2312");

            NodeFilter titleFilter=new NodeFilter(){
                public boolean accept(Node node){
                    if(node.toHtml().startsWith("<td class=fontcolor3 align=center height=\"60\"><b><font size=\"4\">")){
                        return true;
                    }else {
                        return false;
                    }
                }
            };

            NodeFilter timeFilter=new NodeFilter() {
                @Override
                public boolean accept(Node node) {
                    if(node.toHtml().startsWith("<td align=center height=30 style=\"font-size: 9pt\"><font color=#808080>"))
                        return true;
                    else{
                        return false;
                    }
                }
            };

            NodeFilter attachFilter=new NodeFilter() {
                @Override
                public boolean accept(Node node) {
                    if(node.toHtml().startsWith("<a href=uploadfiles")){
                        return true;
                    }
                    else
                        return false;
                }
            };

            NodeFilter clickFilter=new HasAttributeFilter("valign","bottom");

            NodeFilter[] filters=new NodeFilter[5];
            filters[0]=timeFilter;
            filters[1]=titleFilter;
            filters[2]=new NodeClassFilter(ParagraphTag.class);
            filters[3]=attachFilter;
            filters[4]=clickFilter;

            NodeFilter filter=new OrFilter(filters);
            NodeList list=parser.extractAllNodesThatMatch(filter);

            if(list.size()==0){
                System.out.println("该文档不存在："+url);
            }else{

                for(int i=0;i<list.size();++i){
                    Node node=list.elementAt(i);
                    String str=node.toPlainTextString();
                    if(str.contains("外部网　English")){
                        continue;
                    }
                    if(str.contains("<?xml")){
                        int bgn=str.indexOf("<?xml");
                        if(bgn!=-1){
                            str=str.substring(0,str.indexOf("<?xml"));
                            System.out.println(str);
                        }
                    }
                   writer.write(str.replace("&nbsp;","") +"\n");
                }

            }
        }catch (ParserException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
