package lucene;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;


import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

/**
 * Created by Peter on 2017/5/30 0030.
 */
public class MySmartChineseAnalyzer {
    private static SmartChineseAnalyzer smartChineseAnalyzer=null;

    public static SmartChineseAnalyzer getSmartChineseAnalyzer() {
        if(smartChineseAnalyzer==null){
            //英语的停用词集
            CharArraySet stop_wrod_set=new CharArraySet(StopAnalyzer.ENGLISH_STOP_WORDS_SET,true);

            //加入自定义的中文的停用词
            String[] self_stop_words = { "的", "了", "呢"};

        for(int i=0;i<self_stop_words.length;++i){
            stop_wrod_set.add(self_stop_words[i]);
        }
            Iterator<Object> itor= SmartChineseAnalyzer.getDefaultStopSet().iterator();
            while(itor.hasNext()){
                stop_wrod_set.add(itor.next());
            }
            //构造SmartChineseAnalyzer时，加入停用词集
            smartChineseAnalyzer=new SmartChineseAnalyzer(stop_wrod_set);
        }
        return smartChineseAnalyzer;
    }

    public static void main(String[] args) {

        String text="我的第一天(pl65535@gmail.com)I am a person";
        System.out.println(text.toLowerCase());
        CharArraySet cas=new CharArraySet(StopAnalyzer.ENGLISH_STOP_WORDS_SET,true);
    /*    String[] self_stop_words = { "的", "了", "呢", "，", "0", "：", ",", "是", "流" };

        for(int i=0;i<self_stop_words.length;++i){
            cas.add(self_stop_words[i]);
        }*/
        Iterator<Object> itor= SmartChineseAnalyzer.getDefaultStopSet().iterator();
        while(itor.hasNext()){
            cas.add(itor.next());
        }
        SmartChineseAnalyzer sca=new SmartChineseAnalyzer(cas);
        CJKAnalyzer ca=new CJKAnalyzer(cas);

        TokenStream ts=ca.tokenStream("field",new StringReader(text));
        CharTermAttribute term=ts.addAttribute(CharTermAttribute.class);

        try{
            ts.reset();
            while(ts.incrementToken()){
                System.out.println(term.toString());
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
