package htmlparser;

import lucene.MySmartChineseAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Peter on 2017/6/20 0020.
 */
public class Sentence2Term {
    public static Set<String> getTerm(String sentence)throws IOException{
        //terms集合
        Set<String> result=new HashSet<>();
        //获得分词器
        Analyzer analyzer= MySmartChineseAnalyzer.getSmartChineseAnalyzer();
        //获得token流
        TokenStream ts=analyzer.tokenStream("text",sentence);
        CharTermAttribute term=ts.addAttribute(CharTermAttribute.class);
        ts.reset();
        //通过Set，去除重复的token，所有token只出现一次，就是terms
        while (ts.incrementToken()){
            result.add(term.toString());
        }
        ts.end();
        ts.close();
        return  result;
    }
}
