package jvr.graph;

import jvr.engine.*;
import jvr.engine.Character;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ross
 * Date: 12/20/13
 * Time: 1:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestGraph extends Graph {



    //Creates basic graph for testing
    public TestGraph(){
        String vertNames = "A B C D E F G";
        List<String> edgeDes = Arrays.asList("A B 3","A C 3","A D 3",
                "B C 4", "B E 1",
                "C D 1", "C E 2",
                "D E 2", "D F 6",
                "E G 1",
                "F G 9");


        Map<String,TestVertex> v = TestVertex.massCreate(vertNames.split(" "));
        vertices = v.values();
        edges = buildEdges(edgeDes,v);


    }

    public TestGraph(Collection<? extends Vertex> v, Collection<? extends Relation> e){
        this.vertices = v;
        this.edges = e;
    }

    public static List<TestEdge> buildEdges(List<String> edges, Map<String, ? extends Vertex> vertMap){
        List<TestEdge> edgeList = new LinkedList<TestEdge>();
        for (String edgeDescription: edges){
            String[] edgeInfo = edgeDescription.split(" ");
            Vertex v1 = vertMap.get(edgeInfo[0]);
            Vertex v2 = vertMap.get(edgeInfo[1]);
            edgeList.add(new TestEdge(v1,v2, Integer.parseInt(edgeInfo[2])));
        }
        return edgeList;
    }

    private  static class TestEdge extends  Relation {

        private Vertex v1;
        private Vertex v2;
        private int weight;


        public TestEdge(Vertex v1, Vertex v2, int weight){
            this.v1 = v1;
            this.v2 = v2;
            this.weight = weight;
        }

        @Override
        public Vertex getSubject() {
            return v1;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Vertex getObject() {
            return v2;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Action.ActionType getType() {
            return Action.ActionType.NEUTRAL;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public String getValue() {
            return "Test Edge from " + v1.getName() + " to " + v2.getName();  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public int getWeight() {
            return weight;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Relation makeCopy(Vertex subject, Vertex object) {
            return new TestEdge(subject,object,this.weight);
        }
    }

}
