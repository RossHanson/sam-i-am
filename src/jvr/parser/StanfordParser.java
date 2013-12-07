package jvr.parser;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.*;
import jvr.content.RawStory;
import jvr.content.TrainingStory;

import java.io.StringReader;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ross
 * Date: 12/7/13
 * Time: 11:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class StanfordParser extends StoryParser{


    private static StanfordParser parser;

    private LexicalizedParser lp;

    private StanfordParser(){
        lp = LexicalizedParser.loadModel(); //Should load the default grammar
    }

    public TrainingStory parseStory(String content){

        TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
        List<CoreLabel> rawWords = tokenizerFactory.getTokenizer(new StringReader(content)).tokenize();
        Tree parse = lp.apply(rawWords);

        TreebankLanguagePack tlp = new PennTreebankLanguagePack();
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
        GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
        List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
        return new TrainingStory(parse,gs,tdl);
    }

    public static StanfordParser getInstance(){
        if (parser==null)
            parser = new StanfordParser();
        return parser;
    }


}
