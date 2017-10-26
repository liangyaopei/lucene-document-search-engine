package lucene;

import beans.DocResult;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by Peter on 2017/6/5 0005.
 */
public class DocSearcher {
    private static String input_dir="D:\\gwt\\materials\\";
    private static String path="D:\\gwt\\indexes\\";
    private static Analyzer analyzer=MySmartChineseAnalyzer.getSmartChineseAnalyzer();
    private static Sort sort=new Sort(
            new SortField("title", SortField.Type.SCORE),
            new SortField("body", SortField.Type.SCORE),
            //按时间排序，最近发布的文档排在前面
            new SortField("date", SortField.Type.INT,true),
            //按点击数排序，点击数大的排在前面
            new SortField("click",SortField.Type.INT,true)
    );

    public static void main(String[] args) {
        //searchTerm("attachment","表",20170000,20180000);
        searchTerm("body","梁耀培",-1,-1);
    }

    public static ArrayList<DocResult> searchTerm(String type, String text,int beginTime,int endTime){
        ArrayList<DocResult> documentArrayList=new ArrayList<>();
        try{
            //索引所在的文件夹
            Directory directory = FSDirectory.open(Paths.get(path));
            DirectoryReader ireader= DirectoryReader.open(directory);
            //建立
            IndexSearcher isearcher=new IndexSearcher(ireader);


            QueryParser parser=new QueryParser(type,analyzer);
            Query query;
            Query textQuery=null;
            Query timeQuery=null;
            Query apartmetQuery=null;
            //部门需要全字匹配
            if(type.equals("apartment")){
                apartmetQuery=queryByApartment(parser,text);
            } else{
                textQuery=parser.parse(text);
            }
            //没有时间限制
            if(beginTime==-1 && endTime==-1){
                if(apartmetQuery!=null){
                    query=apartmetQuery;
                }
                else{
                    query=textQuery;
                }
            }else{
                //使用布尔检索
                BooleanQuery.Builder builder=new BooleanQuery.Builder();
                //加入时间限制
                timeQuery=queryByDate(beginTime,endTime);
                builder.add(timeQuery,BooleanClause.Occur.MUST);
                if(textQuery!=null)
                    builder.add(textQuery,BooleanClause.Occur.MUST);
                else
                    builder.add(apartmetQuery,BooleanClause.Occur.MUST);
                BooleanQuery boolQuery=builder.build();

                query=boolQuery;
            }
            //查询
            ScoreDoc[] hits=isearcher.search(query,100,sort).scoreDocs;
            String title,body,apartment,time,attachement,click;

            for(int i=0;i<hits.length;++i){
                //获得文档的标题，正文，时间，等
                Document doc=isearcher.doc(hits[i].doc);
                title=doc.getField("title").toString();
                body=doc.getField("body").toString();
                apartment=doc.getField("apartment").toString();
                time=doc.getField("time").toString();
                attachement=doc.getField("attachment").toString();
                click=doc.getField("click").toString();

                title=title.substring(title.indexOf(":")+1,title.length()-1);

                body=body.substring(body.indexOf(":")+1);
                int bgn=body.indexOf("<?xml");

                if(bgn!=-1){
                    int end=body.indexOf("/>");
                    body=body.substring(0,bgn)+body.substring(end+2);
                }
                apartment=apartment.substring(apartment.indexOf(":")+1,apartment.length()-1);
                time=time.substring(time.indexOf(":")+1,time.length()-1);

                if(attachement.length()!=-1){
                    attachement=attachement.substring(attachement.indexOf(":")+1,attachement.length()-1);
                }

                click=click.substring(click.indexOf(":")+1,click.length()-1);
                //将文档写入结果
                DocResult result=new DocResult();
                result.setTitle(title);
                result.setBody(body);
                result.setApartment(apartment);
                result.setTime(time);
                result.setAttachment(attachement);
                result.setClick(click);
                documentArrayList.add(result);
            }

            System.out.println("size:"+hits.length);
            ireader.close();
            directory.close();
        }catch (IOException e){
            e.printStackTrace();
        }catch (ParseException e){
            e.printStackTrace();
        }
        return documentArrayList;
    }

    private static Query getQuery(String type,String text,int time) throws ParseException{
        String[] queries={text,String.valueOf(time)};
        String[] fields={type,"date"};
        BooleanClause.Occur[] flags={BooleanClause.Occur.MUST,BooleanClause.Occur.MUST};
        return MultiFieldQueryParser.parse(queries,fields,flags,analyzer);
    }

    private static Query queryByDate(int min,int max){
        return NumericRangeQuery.newIntRange("date",min,max,true,true);
    }

    private static Query queryByApartment(QueryParser parser,String apart){
        return parser.createPhraseQuery("apartment",apart);
    }
}
