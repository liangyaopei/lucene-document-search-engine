package run;

import beans.DocResult;
import htmlparser.HtmlUpdater;
import lucene.DocSearcher;

import java.util.List;
import java.util.Scanner;

/**
 * Created by lenovo on 2017/6/14.
 */
public class RunTest {
    public static void main(String[] args) {
        int option;
        //类型选择
        String type;
        //查询关键词
        String keyword;
        //发文单位
        String apartment;
        int bgn=-1;
        int end=-1;
        String time;
        int num;
        //获得输入
        Scanner scanner=new Scanner(System.in);
        while(true){
            System.out.println("输入选择：1.查询；2.更新索引；3.退出");
            option=Integer.parseInt(scanner.nextLine());
           if(option==1){
               System.out.println("输入搜索的域：title,body,apartment,attachment");
               type=scanner.nextLine();
               System.out.println("输入搜索关键词：");
               keyword=scanner.nextLine();
               System.out.println("是否增加时间限制？y-yes,n-no");

               time=scanner.nextLine();

               if(time.equals("y")|| time.equals("yes")){
                   System.out.println("输入起始时间：格式：20170712");
                   bgn=Integer.parseInt(scanner.nextLine());
                   System.out.println("输入结束时间：格式：20170712");
                   end=Integer.parseInt(scanner.nextLine());
                   type="title";
               }else{
                   bgn=end=-1;
               }
               //调用自定义的DocSearcher函数查询
               List<DocResult> result= DocSearcher.searchTerm(type,keyword,bgn,end);

               int i=1;
               for(DocResult doc:result){
                   System.out.println(i+"："+"title:"+doc.getTitle()+",apartment:"+doc.getApartment()
                   +",time"+doc.getTime()+",click"+doc.getClick());
                   i++;
               }
               System.out.println("需要查看body吗？输入对应数字,输入-1退出");
               num=Integer.parseInt(scanner.nextLine());

               while(num!=-1){
                   //输出查询结果的正文
                   System.out.println(result.get(num-1).getBody());
                   System.out.println("需要查看body吗？输入对应数字,输入-1退出");
                   num=Integer.parseInt(scanner.nextLine());
               }

           }else if(option==2){
               //下载新的文档，并且更新索引
               HtmlUpdater.update();
               System.out.println("更新完成");
           }else{
               break;
           }
        }
    }
}
