package jvr.engine;

/**
 * Created with IntelliJ IDEA.
 * User: ross
 * Date: 12/19/13
 * Time: 5:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class ArtificialEdge extends  Relation {

    private int subjectIndex;
    private int objectIndex;

    public ArtificialEdge(Character subject, Character object){
        this.subject = subject;
        this.subjectIndex = subject.getMainWord().index();
        this.object = object;
        this.objectIndex = object.getMainWord().index();
    }

    @Override
    public Character getObject(){
        return object;
    }

    @Override
    public Character getSubject() {
        return subject;
    }

    @Override
    public String getValue(){
        return "EQUALS";
    }

    @Override
    public double getWeight(){
        return 1.0;
    }

    @Override
    public Action.ActionType getType(){
        return Action.ActionType.NEUTRAL;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Equivalence: ").append(subject.getName()).append("-").append(subjectIndex);
        sb.append(" IS ").append(object.getName()).append("-").append(objectIndex);
        return sb.toString();
    }
}
