package jvr.graph;

import jvr.engine.Action;
import jvr.engine.Character;

/**
 * Created with IntelliJ IDEA.
 * User: ross
 * Date: 12/19/13
 * Time: 5:07 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Relation {

    protected Vertex subject;
    protected Vertex object;
    protected Action.ActionType type;

    public abstract Vertex getSubject();
    public abstract Vertex getObject();
    public abstract Action.ActionType getType();
    public abstract String getValue();
    public abstract int getWeight();
    public abstract Relation makeCopy(Vertex subject, Vertex object);


    public void notifyParticipants(){
        subject.registerDeliveredAction(this);
        object.registerReceivedAction(this);
    }




}
