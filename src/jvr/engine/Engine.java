package jvr.engine;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.parser.Parser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import jvr.content.RawStory;
import jvr.parser.ParsedStory;
import jvr.parser.StanfordParser;
import jvr.parser.StoryParser;
import jvr.parser.TestStory;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;


/**
 * Created with IntelliJ IDEA.
 * User: ross
 * Date: 10/17/13
 * Time: 6:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class Engine {

    public static void main(String args[]) throws IOException {
        System.out.println("Hey! I compiled! Add some stuff");
        TestStory story = new TestStory("Test Story", "");
        story.addSentence("The evil robber killed the bank. ");
        story.addSentence("Superman chased the robber. ");
        story.addSentence("Superman beat up the robber. ");
        story.addSentence("Then the police came and arrested him. ");
        System.out.println("Story is: " + story.getContents());
        Tree[] sentenceTree;
        StanfordParser sp = new StanfordParser();
        Annotation document = new Annotation("Rob has a nice car.  He likes it a lot");
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        Scanner in = new Scanner(System.in);
        String line = story.getContents();
        System.out.println("Input line");
        do{
            System.out.println("Processing....");
            document = new Annotation(line);
            pipeline.annotate(document);
            List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
            SingleSentenceGraph[] graphs = new SingleSentenceGraph[sentences.size()];
            int i = 0;
            for (CoreMap c: sentences){
                SemanticGraph sentence = c.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
                for (SemanticGraphEdge edge : sentence.edgeIterable()){

                    System.out.println("I found this edge: " );
                    System.out.println("Dependent: " + edge.getDependent());
                    System.out.println("Governor: " + edge.getGovernor());
                    System.out.println("Relation: " + edge.getRelation());
                }
                System.out.println("Sentence Graph: ");
                graphs[i] = new SingleSentenceGraph(sentence);
                System.out.println(graphs[i].toString());
                i++;
            }
            SingleSentenceGraph total = graphs[0];
            for (i=1;i<graphs.length;i++){
                System.out.println("Merging graph " + (i-1)+ " and " + i);
                total = SingleSentenceGraph.mergeGraphs(total,graphs[i]);
            }
            System.out.println("||||||||TOTAL GRAPH|||||");
            System.out.println(total.toString());

            System.out.println("Input line");
        } while ((line = in.nextLine()) !=null);


        //pipeline.prettyPrint(document,System.out);



//        Tree testTree = sp.parseStory("Rob has a nice car. He also has two arms. Now, he has three.");

//        sentenceTree  =  sp.parseStory(story);
//        Characters chars = new Characters();
//        for (Tree sentence : sentenceTree){
//            chars.updateCharacters(sentence);
//        }
//        System.out.println("Characters.characters: \n" + Characters.characters.toString());

    }


}
