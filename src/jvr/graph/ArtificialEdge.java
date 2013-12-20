package jvr.graph;

import jvr.engine.Character;
import jvr.engine.Action;

/**
 * Created with IntelliJ IDEA.
 * User: ross
 * Date: 12/19/13
 * Time: 5:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class ArtificialEdge extends Relation {

    private int subjectIndex = 0;
    private int objectIndex = 0;
    private int weight = 0;

    public ArtificialEdge(Vertex subject, Vertex object){
        this.subject = subject;
        if (subject instanceof Character){
            this.subjectIndex = ((Character) subject).getMainWord().index();
        }

        this.object = object;
        if (object instanceof Character){
            this.objectIndex = ((Character)object).getMainWord().index();
        }

        weight = 1;

    }

    public ArtificialEdge(Vertex subject, Vertex object, int weight){
        this.subject = subject;
        this.object = object;
        this.weight = weight;
    }

    @Override
    public Vertex getObject(){
        return object;
    }

    @Override
    public Vertex getSubject() {
        return subject;
    }

    @Override
    public String getValue(){
        return "EQUALS";
    }

    @Override
    public int getWeight(){
        return weight;
    }

    @Override
    public Relation makeCopy(Vertex subject, Vertex object) {
        return new ArtificialEdge(subject,object);
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
