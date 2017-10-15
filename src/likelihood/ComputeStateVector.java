package likelihood;

import attackpaths.IAttackPath;
import graphmodels.graph.IEdge;
import graphmodels.graph.IGraph;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Roberto Gaudenzi on 15/10/17.
 */
public class ComputeStateVector {

    public static Float[] execute(IAttackPath path, Map<Integer, String> nodesIndices){
        int pathLength = path.getLength();
        int n = nodesIndices.values().size();
        float s = 1 / pathLength;
        Map<Integer, IEdge> edges = path.getEdges();

        Float[] stateVector = new Float[n-1];

        for(int rank : edges.keySet()){
            IEdge edge = edges.get(rank);
            if(rank == 0){
                String tailID = edge.getTailID();
                stateVector[getNodeIndex(nodesIndices, tailID)] = s;
            }
            if(rank != n){
                String headID = edge.getHeadID();
                stateVector[getNodeIndex(nodesIndices, headID)] = s;
            }
        }

        return stateVector;
    }

    private static int getNodeIndex(Map<Integer, String> nodesIndices, String nodeID){
        for(int key : nodesIndices.keySet()){
            if(nodesIndices.get(key).equals(nodeID))
                return key;
        }

        System.out.println("ERRORE: getNodeIndex did not found nodeID index.");
        return -1;
    }
}
