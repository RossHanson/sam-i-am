package jvr.engine;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeGraphNode;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ross
 * Date: 12/18/13
 * Time: 4:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class Action implements Comparable<Action>{
    private Character subject;
    private Character object;
    private Tree action;
    public ActionType type;


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
    public Action(Character subject, Character object, Tree action, ActionType type){
        this.subject = subject;
        this.object = object;
        this.action = action;
        this.type = type;
        this.actionId = getActionId();
    }

    /**
     * Create a new action object. Action type is determined from word training sets
     * @param subject
     * @param object
     * @param action
     */
    public Action(Character subject, Character object, Tree action){
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
    public static int countTotalActions(Map<Character,? extends  Collection<Action>> actions){ //God I hate generics syntax
        int i = 0;
        for (Collection<Action> set: actions.values()){
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
    public static int getActionTypeCount(Map<Character,SortedSet<Action>> actions, ActionType targetType){
        return countTotalActions(getActionsOfType(actions, targetType));
    }

    /**
     * Basically filter on the target action type
     * @param actions
     * @param targetType
     * @return
     */
    public static Map<Character,SortedSet<Action>> getActionsOfType(Map<Character, SortedSet<Action>> actions, ActionType targetType){
        Map<Character,SortedSet<Action>> targetActions = new HashMap<Character,SortedSet<Action>>();
        for (Map.Entry<Character, SortedSet<Action>> entry: actions.entrySet()){
            SortedSet actionSet = targetActions.get(entry.getKey());
            if (actionSet ==null){
                actionSet = new TreeSet<Action>();
                targetActions.put(entry.getKey(),actionSet);
            }
            for (Action a: entry.getValue()){
                if (a.type==targetType)
                    actionSet.add(a);
            }
        }
        return targetActions;
    }


    public Character getSubject(){
        return this.subject;
    }

    public Character getObject(){
        return this.object;
    }

    public Tree getAction(){
        return this.action;
    }

    public static String prettyPrint(Map<Character,? extends Collection<Action>> actions){
        if (actions.isEmpty())
            return "None\n";
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<Character, ? extends  Collection<Action>> e: actions.entrySet()){
            Character c = e.getKey();
            Collection<Action> cActions = e.getValue();
            if (cActions.isEmpty())
                continue;
            sb.append("The following actions are associated with character: ").append(c.getName()).append("\n");
            for (Action a: cActions){
                sb.append("Action: ").append(a.action.firstChild().value()).append("\n");
            }
        }
        return sb.toString();
    }

    public void notifyParticipants(){
        subject.registerDeliveredAction(this);
        object.registerReceivedAction(this);
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
