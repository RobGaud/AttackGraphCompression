package likelihood;

import attackpaths.IAttackPath;
import graphmodels.graph.IHostNode;
import graphmodels.hypergraph.IHyperGraph;
import utils.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Roberto Gaudenzi on 15/10/17.
 */
public class ComputePathsMTAO {

    private static Map<Integer, String> nodesIndices;

    public static Map<String, Float> execute(IHyperGraph graph, Set<IHostNode> subgraph, Set<IAttackPath> paths, IHostNode targetNode){

        Map<String, Float> mtaoMap = new HashMap<>();

        assignIndices(targetNode, subgraph);
        Float[][] P = ComputeTransitionProb.execute(graph, targetNode, nodesIndices, Constants.EPSILON);
        Float[] exitRates = ComputeExitRates.execute(graph, nodesIndices);
        Float[][] A = ComputeGeneratorMatrix.execute(exitRates, P);

        for(IAttackPath path : paths){
            Float[] S = ComputeStateVector.execute(path, nodesIndices);

            mtaoMap.put(path.getID(), computeMTAO(A, S));
        }

        return mtaoMap;
    }

    private static float computeMTAO(Float[][] A, Float[] S){
        int n = A.length;

        // First, compute the product of S and A_u
        Float[] temp = new Float[n-1];
        for(int j = 0; j < n-1; j++){
            for(int k = 0; k < n-1; k++){
                temp[j] += S[k] * A[k][j];
            }
        }

        // Then, compute the product of the result vector and the 1s vector
        float mtao = 0;
        for(int i = 0; i < n-1; i++){
            mtao += temp[i];
        }

        return mtao;
    }


    private static void assignIndices(IHostNode target, Set<IHostNode> subgraph){
        nodesIndices = new HashMap<>();
        // Assign index 'n' to the target
        nodesIndices.put(subgraph.size(), target.getID());
        int i = 0;
        for(IHostNode node : subgraph){
            if(!node.equals(target)){
                nodesIndices.put(i, node.getID());
                i++;
            }
        }
    }

}
