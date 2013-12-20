package jvr.graph;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ross
 * Date: 12/20/13
 * Time: 1:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestVertex extends Vertex {

    private String name;

    public TestVertex(String name){
        this.name = name;
    }

    public static Map<String,TestVertex> massCreate(String[] names){
        Map<String,TestVertex> vertMap = new HashMap<String,TestVertex>();
        for (String name: names){
            vertMap.put(name, new TestVertex(name));
        }
        return vertMap;
    }

    @Override
    public Collection<Relation> getOutbound() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<Relation> getInbound() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Vertex cleanCopy() {
        return new TestVertex(name);
    }

    @Override
    public void registerDeliveredAction(Relation r) {
        throw new NotImplementedException();
    }

    @Override
    public void registerReceivedAction(Relation r) {
        throw new NotImplementedException();//To change body of implemented methods use File | Settings | File Templates.
    }
}
