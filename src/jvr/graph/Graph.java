package jvr.graph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ross
 * Date: 12/19/13
 * Time: 1:55 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Graph {

    protected Collection<? extends Relation> edges;
    protected Collection<? extends Vertex> vertices;


    public Collection<? extends Relation> getEdges(){
        return edges;
    }

    public Collection<? extends Vertex>  getVertices(){
        return  vertices;
    }


    public static Vertex createSpannerGraph(Graph g, Collection<Relation> artificialEdges, String vertexName){
        Collection<? extends Vertex> verts = g.getVertices();

        Vertex spanner = new TestVertex(vertexName);
        for (Vertex v: verts)
            artificialEdges.add(new ArtificialEdge(spanner,v,Integer.MAX_VALUE));

        return spanner;

    }



}
