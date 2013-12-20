package jvr.engine;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import jvr.graph.ArtificialEdge;
import jvr.graph.Graph;
import jvr.graph.Relation;
import jvr.graph.Vertex;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ross
 * Date: 12/19/13
 * Time: 3:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class SingleSentenceGraph extends Graph {

    private Map<IndexedWord, Character> characterMap = new HashMap<IndexedWord,Character>();
    private Set<Relation> edges = new HashSet<Relation>();

    public SingleSentenceGraph(SemanticGraph sg){
        edges = new HashSet<Relation>();
        vertices = characterMap.values();
        Map<IndexedWord, List<Character>> verbSubjectMap = new HashMap<IndexedWord,List<Character>>();
        Map<IndexedWord, List<Character>> verbObjectMap = new HashMap<IndexedWord,List<Character>>();
        Map<IndexedWord, List<Character>> copVerbMap = new HashMap<IndexedWord, List<Character>>();
        Map<IndexedWord,List<IndexedWord>> verbDecoratorMap = new HashMap<IndexedWord,List<IndexedWord>>();
        for (SemanticGraphEdge edge: sg.edgeIterable()){
            addMatchingRelationToMap(edge, "nsubj agent",edge.getGovernor(), edge.getDependent(),verbSubjectMap);
            addMatchingRelationToMap(edge, "dobj", edge.getGovernor(), edge.getDependent(), verbObjectMap);
            addMatchingRelationToMap(edge, "cop", edge.getGovernor(), edge.getDependent(), copVerbMap);
            //Not gonna deal with decorators yet
            addCompoundNounToCharacter(edge,"nn amod", edge.getGovernor(),edge.getDependent());
        }
        for (IndexedWord verb: verbSubjectMap.keySet()){
            List<Character> subjects = verbSubjectMap.get(verb);
            List<Character> objects = verbObjectMap.get(verb);
            if (subjects==null){
                System.err.println("UH OH! Verb no subject: " + verb.value());
                continue;
            }
            if (objects == null){
                System.err.println("UH OH! Verb no object: " + verb.value());
                continue;
            }
            for (Character subject: subjects){
                for (Character object: objects){
                    Action newAction = new Action(subject, object, verb);
                    edges.add(newAction);
                    newAction.notifyParticipants();
                }
            }

        }
        for (Map.Entry<IndexedWord,List<Character>> entry: copVerbMap.entrySet()){
            List<Character> chars = entry.getValue();
            edges.add(new Action(chars.get(0), chars.get(1), entry.getKey())); //Not sure what to do for more than one equivalence;
        }
        System.out.println("Finished building single sentence graph| Character set size: " + characterMap.size());
    }

    private SingleSentenceGraph(Map<IndexedWord,Character> characterMap, Set<Relation> edges){
        this.characterMap = characterMap;
        this.edges = edges;
    }

    /**
     * Conveince method to easily find matching constructs and add them to map
     * @param edge SemanticGraphEdge to evaluate
     * @param desTypeString Desired type of relation, ie nsubj, nn, etc
     * @param targetKey The indexedWord to use as the key in the map. Should be verb name
     * @param targetValue The indexedWord to use to find the affected character
     * @param targetMap The map to put the result in. Shoudl be verbSubject or verbObject map at the moment
     */
    private void addMatchingRelationToMap(SemanticGraphEdge edge, String desTypeString, IndexedWord targetKey,
                                          IndexedWord targetValue, Map<IndexedWord, List<Character>> targetMap){
        String[] desTypes = desTypeString.split(" ");
        if (Arrays.asList(desTypes).contains(edge.getRelation().getShortName())){
            System.out.println("Found match for: " + edge.toString());
            Character targetCharacter = characterMap.get(targetValue);
            if (targetCharacter == null){
                targetCharacter = Character.createCharacter(targetValue);
                characterMap.put(targetValue,targetCharacter);
            }
            List<Character> targetList = targetMap.get(targetKey);
            if (targetList == null){
                targetList = new LinkedList<Character>();
                targetMap.put(targetKey,targetList);
            }
            targetList.add(targetCharacter);
        }
    }

    /**
     * Conveince method to catch decorators for verbs. Probably doesn't work for much outside of single adverbs
     * at the moment
     * @param edge Edge to evaluate
     * @param desTypeString Relation type to match, ie nsubj, nn, etc
     * @param targetKey Key to use in the target map. Should be verb
     * @param targetValue IndexedWord to add as decorator.
     * @param targetMap Map to insert into.
     */
    private void addMatchingDecoratorToMap(SemanticGraphEdge edge, String desTypeString, IndexedWord targetKey,
                                          IndexedWord targetValue, Map<IndexedWord, List<IndexedWord>> targetMap){
        String[] desType = desTypeString.split(" " );
        if (Arrays.asList(desType).contains(edge.getRelation().getShortName())){
            List<IndexedWord> targetList = targetMap.get(targetKey);
            if (targetList == null){
                targetList = new LinkedList<IndexedWord>();
                targetMap.put(targetKey,targetList);
            }
            targetList.add(targetValue);
        }
    }

    private void addCompoundNounToCharacter(SemanticGraphEdge edge, String desTypeString, IndexedWord characterKey, IndexedWord modifier){
        String[] desType = desTypeString.split(" ");
        if (Arrays.asList(desType).contains(edge.getRelation().getShortName())){
            Character targetCharacter = characterMap.get(characterKey);
            if(targetCharacter==null){
                targetCharacter = Character.createCharacter(characterKey);
                characterMap.put(characterKey,targetCharacter);
            }
            targetCharacter.addModifier(modifier);
        }
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(Relation a: edges)
            sb.append("Action:\n").append(a.toString()).append("\n");
        return sb.toString();
    }

    public static List<ArtificialEdge> mergeGraphs(SingleSentenceGraph g1, SingleSentenceGraph g2){
        List<ArtificialEdge> equivalenceSet = new LinkedList<ArtificialEdge>();
        for (Map.Entry<IndexedWord,Character> e1 : g1.characterMap.entrySet()){
            for (Map.Entry<IndexedWord,Character> e2: g2.characterMap.entrySet()){
                Character c1 = e1.getValue();
                Character c2 = e2.getValue();
                System.out.print("Considering " + c1.getName() + " equals " + c2.getName() + " ??? ");
                if (c1.isSameCharacter(c2)){
                    equivalenceSet.add(new ArtificialEdge(c1,c2));
                    System.out.println(" YES" );
                } else {
                    System.out.println(" NO ");
                }
            }
        }
        return equivalenceSet;
    }

    @Override
    public Collection<? extends Vertex> getVertices(){
        return characterMap.values();
    }

    @Override
    public Set<Relation> getEdges(){
        return edges;
    }


}
