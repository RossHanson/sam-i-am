package jvr.graph;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: ross
 * Date: 12/19/13
 * Time: 1:45 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Vertex implements Comparable<Vertex>{



    public abstract Collection<Relation> getOutbound();
    public abstract  Collection<Relation> getInbound();

    public abstract String getName();


    protected boolean marked = false;
    protected static int TOTAL_COUNT = 0;
    protected int id;

    public void mark(){
        marked = true;
    }

    public void unmark(){
        marked = false;
    }

    public boolean isMarked(){
        return marked;
    }

    public abstract Vertex cleanCopy();

    protected static synchronized int getNewId(){
        int id = TOTAL_COUNT;
        TOTAL_COUNT++;
        return id;
    }

    @Override
    public int compareTo(Vertex v){
        return v.id-this.id;
    }

    public abstract void registerDeliveredAction(Relation r);
    public abstract void registerReceivedAction(Relation r);




}
