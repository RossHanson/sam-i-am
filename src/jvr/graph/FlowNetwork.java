package jvr.graph;

import java.io.PrintStream;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ross
 * Date: 12/19/13
 * Time: 2:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class FlowNetwork extends Graph {

    private Map<Integer,FlowNode> nodeIdMap;
    private Map<Vertex,FlowNode> nodeMapping;

    private int[][] capacityMatrix;

    private int vertCount;

    public static void main(String args[]){
        Graph g = new TestGraph();
        System.out.println("Built the graph");
        FlowNetwork fn = makeFlowNetwork(g);
        visualize(fn.capacityMatrix);
        System.out.println("Built the flow network");
        int[][] maxFlow = fn.findMaxFlow(0,6);
        System.out.println("Found the max flow");
        visualize(maxFlow);

    }

    public static FlowNetwork makeFlowNetwork(Graph g){
        Collection<? extends Vertex> vertices = g.getVertices();
        Collection<? extends Relation> edges = g.getEdges();

        int vertCount = vertices.size();

        //Make node mapping
        Map<Vertex,FlowNode> nodeMapping = new HashMap<Vertex, FlowNode>(vertCount); //Not strictly speaking necessary but probably useful for visualizing
        Map<Integer, FlowNode> nodeIdMap = new HashMap<Integer,FlowNode>(vertCount);
        int[][] capacityMatrix = makeArray(vertCount, vertCount, 0);

        int currentId = 0;

        //Add edge value to capacity
        for (Relation r: edges){
            Vertex sub = r.getSubject();
            Vertex ob = r.getObject();
            FlowNode s = nodeMapping.get(sub);
            if (s==null){
                s = new FlowNode(sub,currentId);
                nodeIdMap.put(currentId,s);
                currentId++;
                nodeMapping.put(sub,s);
            }
            FlowNode t = nodeMapping.get(ob);
            if (t==null){
                t = new FlowNode(ob,currentId);
                nodeIdMap.put(currentId,t);
                currentId++;
                nodeMapping.put(ob,t);
            }
            //Set forward and backward capacities
            capacityMatrix[s.id][t.id] += r.getWeight();
            capacityMatrix[t.id][s.id] += r.getWeight();

        }
        return new FlowNetwork(capacityMatrix,nodeMapping,nodeIdMap);
    }


    private static int[][] makeArray(int columns, int rows, int value){
        int[][] matrix = new int[columns][rows];

        for (int i=0;i<columns;i++)
            for (int j=0;j<rows;j++)
                matrix[i][j] = value;
        return matrix;
    }

    private static BFSResult breadthFirstSearch(int[][] capacityMatrix, int[][] flowMatrix, int source, int sink){
        Queue<Integer> queue = new LinkedList<Integer>();
        queue.add(source);
        int vertCount = capacityMatrix[0].length;
        BFSResult res = new BFSResult(vertCount,source,sink);

        while(!queue.isEmpty()){
            int target = queue.poll();
            for (int i =0;i<vertCount;i++){
                if (capacityMatrix[target][i]==0)
                    continue;
                int diff = capacityMatrix[target][i] -flowMatrix[target][i];
                if (!res.addNode(i,target,diff))
                    continue;
                if (target==sink)
                    return res;
                queue.add(i);
            }
        }
        return res;
    }

    public FlowNetwork(int[][] capacityMatrix, Map<Vertex, FlowNode> nodeMapping, Map<Integer, FlowNode> nodeIdMap){
        this.capacityMatrix = capacityMatrix;
//        this.flowMatrix = flowMatrix; //NOT NECESSARILY SAVED
        this.nodeMapping = nodeMapping;
        this.vertCount = capacityMatrix[0].length;
        this.nodeIdMap = nodeIdMap;
    }

    public int[][] findMaxFlow(Vertex source, Vertex sink){
        int sourceId = nodeMapping.get(source).getId();
        int sinkId = nodeMapping.get(sink).getId();
        return findMaxFlow(sourceId,sinkId);
    }

    public int[][] findMaxFlow(int source, int sink){
        int[][] flowMatrix = makeArray(vertCount,vertCount,0);
        int totalFlow = 0;
        while(true){
            BFSResult res = breadthFirstSearch(capacityMatrix,flowMatrix,source,sink);
            int deltaFlow = res.capacityTable[sink];
            if (deltaFlow==0)
                break;
            totalFlow += deltaFlow;
            int intermediary = sink;
            while (intermediary!=source){
                int back = res.parentTable[intermediary];
                flowMatrix[back][intermediary] = flowMatrix[back][intermediary] + deltaFlow;
                flowMatrix[intermediary][back] = flowMatrix[intermediary][back] - deltaFlow;
                intermediary = back;
            }
        }
        System.out.println("Total flow: " + totalFlow);
        return flowMatrix;
    }

    public static void visualize(FlowNetwork fn){
        visualize(fn.capacityMatrix);
    }

    public static void visualize(int[][] network){
        int vertCount = network[0].length;
        System.out.print("    ");
        for (int i =0;i<vertCount;i++){
            System.out.print(i + " " );

        }
        System.out.println();
        for (int i =0;i<vertCount;i++){
            String sideColumn = (i<10 ? "  |" : " |");
            System.out.print(i + sideColumn);
            for (int j =0;j<vertCount;j++){
                if (network[i][j]==Integer.MAX_VALUE){
                    System.out.print("!");
                }else if (network[i][j]!=0)
                    System.out.print(network[i][j]);
                else
                    System.out.print(" ");
                String spaces = (j>=9? "  ": " ");
                System.out.print(spaces);
            }
            System.out.println("|");
        }
    }

    public void printStructure( PrintStream ps, int[][] matrix){
        int vertCount = matrix[0].length;
        for(int i  =0;i<vertCount;i++){
            for(int j =i;j<vertCount;j++){
                if (matrix[i][j]==0)
                    continue;
                Vertex v1 = this.nodeIdMap.get(i).root;
                Vertex v2 = this.nodeIdMap.get(j).root;
                int value = matrix[i][j];
                if (value==Integer.MAX_VALUE)
                    value=1000;
                ps.println(v1.getName()+v1.id + "," + v2.getName()+v2.id+","+value);
            }
        }
    }

    public void printStructure( PrintStream ps){
        printStructure(ps,this.capacityMatrix);
    }

    private static class FlowNode {

        private Vertex root;

        int id;

        public FlowNode(Vertex root, int id){
            this.root = root;
            this.id = id;
        }

        public int getId(){
            return id;
        }
    }

    private static class BFSResult {
        int[] parentTable;
        int[] capacityTable;

        public BFSResult(int vertCount, int source, int sink){
            parentTable = new int[vertCount];
            Arrays.fill(parentTable,-1);
            parentTable[source] = -2;

            capacityTable = new int[vertCount];
            Arrays.fill(capacityTable, Integer.MAX_VALUE);
            capacityTable[sink] = 0;
        }

        public boolean addNode(int node, int parent, int diff){
            if (diff<=0 || parentTable[node]!=-1)
                return false;
            parentTable[node] = parent;
            capacityTable[node] = Math.min(capacityTable[parent],diff);
            return true;
        }

    }


}
