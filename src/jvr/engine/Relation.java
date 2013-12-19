package jvr.engine;

/**
 * Created with IntelliJ IDEA.
 * User: ross
 * Date: 12/19/13
 * Time: 5:07 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Relation {

    protected  Character subject;
    protected  Character object;
    protected Action.ActionType type;

    public abstract Character getSubject();
    public abstract Character getObject();
    public abstract Action.ActionType getType();
    public abstract String getValue();
    public abstract double getWeight();


    public void notifyParticipants(){
        subject.registerDeliveredAction(this);
        object.registerReceivedAction(this);
    }




}
