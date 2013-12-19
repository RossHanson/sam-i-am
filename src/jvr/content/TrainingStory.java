package jvr.content;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.CoreMap;
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


    private Annotation document;
    private List<CoreMap> sentences;

    public TrainingStory(Annotation document){
        this.document = document;
        this.sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

    }

    public Tree[] getTrees(){
        Tree[] trees = new Tree[sentences.size()];
        int i = 0;
        for (CoreMap c: sentences){
            trees[i] = c.get(TreeCoreAnnotations.TreeAnnotation.class);
            i++;
        }
        return trees;
    }

    public SemanticGraph[] getGraphs(){
        SemanticGraph[] graphs = new SemanticGraph[sentences.size()];
        int i = 0;
        for (CoreMap c: sentences){
            graphs[i] = c.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
            i++;
        }
        return graphs;
    }

    public void printStructure(){
        TreePrint tp = new TreePrint("penn,typedDependenciesCollapsed");
        for (Tree t: this.getTrees()){
            System.out.println("======");
            tp.printTree(t);
            System.out.println("======");
        }
    }


}
