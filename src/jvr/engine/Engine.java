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
import jvr.graph.*;
import jvr.parser.ParsedStory;
import jvr.parser.StanfordParser;
import jvr.parser.StoryParser;
import jvr.parser.TestStory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: ross
 * Date: 10/17/13
 * Time: 6:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class Engine {

    public static void oldMain(String args[]) throws IOException {
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
            Vertex source;
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

            System.out.println("||||||||TOTAL GRAPH|||||");
            FlowNetwork fn = FlowNetwork.makeFlowNetwork(total);
            System.out.println("Built flow network");
            System.out.println(Arrays.deepToString(fn.findMaxFlow(0, graphs[0].getVertices().size() - 1)));


            System.out.println("Input line");
        } while ((line = in.nextLine()) !=null);

    }


    public static void main(String[] args) throws FileNotFoundException {
        TestStory story = new TestStory("Test Story", "");
        story.addSentence("The evil robber killed the bank. ");
        story.addSentence("Superman chased the robber. ");
        story.addSentence("Superman beat up the robber. ");
        story.addSentence("Then the police came to the bank and arrested the robber. ");
        story.addSentence("In jail, the robber made new friends. ");
        story.addSentence("The robber and his friends played a lot of basketball. ");
        story.addSentence("The robber and his friends also ate food. ");
        System.out.println("Story is: " + story.getContents());
        Engine e = new Engine();
        File flowOut = new File("/home/ross/Dropbox/CS4701/flow.csv");
        FlowNetwork fn = e.runFlowAnalysis(story.getContents(),flowOut );
        File capacityOut = new File("/home/ross/Dropbox/CS4701/capacity.csv");
        PrintStream ps = new PrintStream(capacityOut);
        fn.printStructure(ps);
        ps.close();


    }


    private StanfordCoreNLP pipeline;

    public Engine(){
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse");
        pipeline = new StanfordCoreNLP(props);


    }


    public SingleSentenceGraph[] parseSentences(String story){

        Annotation document = new Annotation(story);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        SingleSentenceGraph[] graphs = new SingleSentenceGraph[sentences.size()];
        int i = 0;
        for (CoreMap c: sentences){
            SemanticGraph sentence = c.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
            graphs[i] = new SingleSentenceGraph(sentence);
            System.out.println("Successfully parsed sentence " + (i+1));
            i++;
        }
        return graphs;
    }


    public static List<ArtificialEdge> findKEquivalence(SingleSentenceGraph[] graphs){
        return findKEquivalence(graphs,3);
    }

    public static List<ArtificialEdge> findKEquivalence(SingleSentenceGraph[] graphs, int k){
        List<ArtificialEdge> eqList = new LinkedList<ArtificialEdge>();
        int count = graphs.length;
        //Initial equivalence run
        for (int i = 0;i<count;i++){
            for(int offset = 1;offset<=k&&(offset+i)<count;offset++){//Extra and statement avoids extra for loop
                eqList.addAll(SingleSentenceGraph.mergeGraphs(graphs[i],graphs[i+offset]));
            }
        }
        System.out.println("For k=" + k + ", I found " + eqList.size() + " equivalences");
        return eqList;
    }

    public static List<Relation> buildCoreferences(List<ArtificialEdge> equivalences){
        List<Relation> newRelations = new LinkedList<Relation>();
        for (ArtificialEdge ae: equivalences){
            Vertex firstChar = ae.getSubject();
            Vertex secondChar = ae.getObject();
            for (Relation inFirst: firstChar.getInbound())
                newRelations.add(inFirst.makeCopy(inFirst.getSubject(),secondChar));
            for (Relation outFirst: firstChar.getOutbound())
                newRelations.add(outFirst.makeCopy(secondChar,outFirst.getObject()));
            for (Relation inSecond: secondChar.getInbound())
                newRelations.add(inSecond.makeCopy(inSecond.getSubject(),firstChar));
            for (Relation outSecond: secondChar.getOutbound())
                newRelations.add(outSecond.makeCopy(firstChar,outSecond.getObject()));
        }
        System.out.println("Coreference count: " + newRelations.size());
        return newRelations;
    }

    public static Collection<Vertex> combineCharacterList(SingleSentenceGraph[] graphs){
        Collection<Vertex> globalCharacters = new LinkedList<Vertex>();

        for (SingleSentenceGraph g: graphs)
            globalCharacters.addAll(g.getVertices());

        return globalCharacters;
    }

    public static Collection<Relation> combineActions(SingleSentenceGraph[] graphs){
        Collection<Relation> globalEdges = new LinkedList<Relation>();

        for(SingleSentenceGraph g: graphs)
            globalEdges.addAll(g.getEdges());

        return globalEdges;
    }

    public Graph runKCollapse(String story){
        SingleSentenceGraph[] graphs = parseSentences(story); //Build sentence graphs
        List<ArtificialEdge> eqs = findKEquivalence(graphs,3); //Run k-collapse
        List<Relation> newActions = buildCoreferences(eqs); //Find co-references across the graphs
        Collection<Vertex> globalCharacters = combineCharacterList(graphs); //Put all the characters together
        newActions.addAll(combineActions(graphs)); //Combine all the new actions

        Graph g = new TestGraph(globalCharacters,newActions); //Create final graph containing combined everything

        return g;
    }

    public FlowNetwork runFlowAnalysis(String story, File outputFile) throws FileNotFoundException {
        SingleSentenceGraph[] graphs = parseSentences(story); //Build sentence graphs
        List<ArtificialEdge> eqs = findKEquivalence(graphs, 3); //Run k-collapse
        List<Relation> newActions = buildCoreferences(eqs); //Find co-references across the graphs

        Collection<Vertex> globalCharacters = combineCharacterList(graphs); //Put all the characters together

        Vertex source = Graph.createSpannerGraph(graphs[0], newActions,"Source");//Create source and sink nodes
        Vertex sink = Graph.createSpannerGraph(graphs[graphs.length-1],newActions,"Sink");  //"" ""

        globalCharacters.add(source); //include in character list
        globalCharacters.add(sink); // "" ""

        newActions.addAll(combineActions(graphs)); //Combine all the new actions
        Graph g = new TestGraph(globalCharacters,newActions); //Create final graph containing combined everything
        FlowNetwork fn = FlowNetwork.makeFlowNetwork(g);
        FlowNetwork.visualize(fn);
        int[][] flowGraph = fn.findMaxFlow(source,sink);
        PrintStream ps = new PrintStream(outputFile);
        fn.printStructure(ps,flowGraph);

        return fn;
    }




}
