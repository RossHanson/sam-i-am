package jvr.engine;

import edu.stanford.nlp.ling.IndexedWord;
import jvr.graph.Relation;
import jvr.graph.Vertex;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ross
 * Date: 12/18/13
 * Time: 4:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class Action extends Relation implements Comparable<Action>{
//    private Vertex subject;
//    private Vertex object;
    private IndexedWord action;
    private Set<IndexedWord> decorators = new HashSet<IndexedWord>();
//    public ActionType type;



    private static int ACTION_COUNT = 0;
    private int actionId;


    private synchronized  int getActionId(){
        int id = ACTION_COUNT;
        ACTION_COUNT++;
        return id;
    }

    /**
     * Create a new action object with an explicit sentiment
     * @param subject
     * @param object
     * @param action
     * @param type
     */
    public Action(Vertex subject, Vertex object, IndexedWord action, ActionType type){
        this.subject = subject;
        this.object = object;
        this.action = action;
        this.type = type;
        this.actionId = getActionId();
    }

    public Action(Vertex subject, Vertex object, IndexedWord action, Collection<IndexedWord> decorators){
        this.subject = subject;
        this.object = object;
        this.action = action;
        this.type = ActionType.NEUTRAL;
        this.decorators.addAll(decorators);
    }

    /**
     * Create a new action object. Action type is determined from word training sets
     * @param subject
     * @param object
     * @param action
     */
    public Action(Vertex subject, Vertex object, IndexedWord action){
        this.subject = subject;
        this.object = object;
        this.action = action;
        this.type = ActionType.NEUTRAL; //TODO IMPLEMENT VERB LOOKUPS
        this.actionId = getActionId();
    }

    /**
     * Count the total number of actions in a map of sets.
     * @param actions
     * @return count
     */
    public static int countTotalActions(Map<Vertex,? extends  Collection<Relation>> actions){ //God I hate generics syntax
        int i = 0;
        for (Collection<Relation> set: actions.values()){
            i += set.size();
        }
        return i;
    }

    /**
     * Return the count of the number of filtered actions
     * @param actions
     * @param targetType
     * @return
     */
    public static int getActionTypeCount(Map<Vertex,SortedSet<Relation>> actions, ActionType targetType){
        return countTotalActions(getActionsOfType(actions, targetType));
    }


    public Relation makeCopy(Vertex source, Vertex object){
        return new Action(source,object,this.action,this.decorators);
    }

    /**
     * Basically filter on the target action type
     * @param actions
     * @param targetType
     * @return
     */
    public static Map<Vertex,SortedSet<Relation>> getActionsOfType(Map<Vertex, SortedSet<Relation>> actions, ActionType targetType){
        Map<Vertex,SortedSet<Relation>> targetActions = new HashMap<Vertex,SortedSet<Relation>>();
        for (Map.Entry<Vertex, SortedSet<Relation>> entry: actions.entrySet()){
            SortedSet actionSet = targetActions.get(entry.getKey());
            if (actionSet ==null){
                actionSet = new TreeSet<Action>();
                targetActions.put(entry.getKey(),actionSet);
            }
            for (Relation a: entry.getValue()){
                if (a.getType()==targetType)
                    actionSet.add(a);
            }
        }
        return targetActions;
    }


    public Vertex getSubject(){
        return this.subject;
    }

    public Vertex getObject(){
        return this.object;
    }

    public IndexedWord getAction(){
        return this.action;
    }

    public String getValue(){
        return this.action.lemma();
    }

    public ActionType getType(){
        return this.type;
    }

    public int getWeight(){
        return 3;
    }

    public static String prettyPrint(Map<Vertex,? extends Collection<Relation>> actions){
        if (actions.isEmpty())
            return "None\n";
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<Vertex, ? extends  Collection<Relation>> e: actions.entrySet()){
            Vertex c = e.getKey();
            Collection<Relation> cActions = e.getValue();
            if (cActions.isEmpty())
                continue;
            sb.append("The following actions are associated with vertex: ").append(c.getName()).append("\n");
            for (Relation a: cActions){
                sb.append("Action: ").append(a.getValue()).append("\n");
            }
        }
        return sb.toString();
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Subject: ").append(this.subject.getName()).append("\n");
        sb.append("Relation: ").append(this.action.value()).append("\n");
        sb.append("Object: ").append(this.object.getName());
        return sb.toString();
    }


    @Override
    public int compareTo(Action a) {
        return Integer.compare(this.actionId,a.actionId);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public static enum  ActionType{
        POSITIVE,
        NEGATIVE,
        NEUTRAL;
    }
}
