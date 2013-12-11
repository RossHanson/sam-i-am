package jvr.content;

import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TypedDependency;
import jvr.parser.ParsedStory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ross
 * Date: 12/7/13
 * Time: 11:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class TrainingStory extends ParsedStory {

    private Tree parsedSentence;
    private GrammaticalStructure gs;
    private List<TypedDependency> tdl;

    public TrainingStory(Tree parsedSentence, GrammaticalStructure gs, List<TypedDependency> tdl){
        this.parsedSentence = parsedSentence;
        this.gs = gs;
        this.tdl = tdl;

    }

    public void printStructure(){
        TreePrint tp = new TreePrint("penn,typedDependenciesCollapsed");
        System.out.println("======");
        System.out.println(tdl);
        System.out.println();
        tp.printTree(parsedSentence);
        System.out.println("======");
    }


}
