package likelihood;

import attackpaths.IAttackPath;
import graphmodels.graph.IEdge;

import java.util.Map;

/**
 * Created by Roberto Gaudenzi on 15/10/17.
 */
public class ComputeStateVector {

    public static double[] execute(IAttackPath path, Map<Integer, String> nodesIndices){

        double[] stateVector = computeStartVector(path, nodesIndices);
        //double[] stateVector = computeFullVector(path, nodesIndices);

        return stateVector;
    }

    private static double[] computeStartVector(IAttackPath path, Map<Integer, String> nodesIndices){
        int n = nodesIndices.values().size();
        double s = 1.0;

        double[] stateVector = new double[n-1];
        IEdge edge = path.getEdges().get(0);
            String tailID = edge.getTailID();
            stateVector[getNodeIndex(nodesIndices, tailID)] = s;

        return stateVector;
    }

    private static double[] computeFullVector(IAttackPath path, Map<Integer, String> nodesIndices){
        int pathLength = path.getLength();
        int n = nodesIndices.values().size();
        double s = 1.0 / pathLength;

        Map<Integer, IEdge> edges = path.getEdges();

        double[] stateVector = new double[n-1];
        for(int rank : edges.keySet()){
            IEdge edge = edges.get(rank);
            if(rank == 0){
                String tailID = edge.getTailID();
                stateVector[getNodeIndex(nodesIndices, tailID)] = s;
            }
            if(rank != n){
                String headID = edge.getHeadID();
                int index = getNodeIndex(nodesIndices, headID);
                if(index < n-1)
                    stateVector[index] = s;
            }
        }
        return stateVector;
    }

    private static int getNodeIndex(Map<Integer, String> nodesIndices, String nodeID){
        for(int key : nodesIndices.keySet()){
            if(nodesIndices.get(key).equals(nodeID)) {
                return key;
            }
        }

        System.out.println("ERROR: getNodeIndex did not found nodeID index.");
        return -1;
    }
}
