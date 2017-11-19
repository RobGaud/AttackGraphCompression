package likelihood;

import attackpaths.IAttackPath;
import graphmodels.graph.IGraph;
import graphmodels.graph.IHostNode;
import graphmodels.hypergraph.IHyperGraph;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import utils.Constants;

import java.util.*;

/**
 * Created by Roberto Gaudenzi on 15/10/17.
 */
public class ComputePathsMTAO {

    private static Map<Integer, String> nodesIndices;

    public static Map<String, Double> execute(IHyperGraph graph, Collection<IAttackPath> paths, IHostNode targetNode, boolean fullPath){

        Map<String, Double> mtaoMap = new HashMap<>();

        /* Idea: try to include nodes' position in exit rates computation.
         * Problem : this implies that exitRates (and hence matrix A) has to be computed for each attack path,
         *           resulting in a (probably) big impact on performance.
         */
        for(IAttackPath path : paths){
            Set<IHostNode> pathSubgraph = getPathSubgraph(graph, path);
            assignIndices(targetNode, pathSubgraph);
            double[][] P = ComputeTransitionProb.execute(graph, nodesIndices, Constants.EPSILON);
            double[] S = ComputeStateVector.execute(path, nodesIndices, fullPath);

            double[] exitRates = ComputeExitRates.execute(graph, nodesIndices, path, S);
            double[][] A = ComputeGeneratorMatrix.execute(exitRates, P);
            double[][] invertedAu = computeInvertedMatrix(A);

            mtaoMap.put(path.getID(), computeMTAO(invertedAu, S));
        }

        return mtaoMap;
    }

    private static double computeMTAO(double[][] inverted_A_u, double[] S){
        int length = inverted_A_u.length;

        // First, compute the product of -S and (A_u)^-1
        double[] temp = new double[length];
        for(int j = 0; j < length; j++){
            temp[j] = 0.0f;
            for(int k = 0; k < length; k++){
                temp[j] += -1 * S[k] * inverted_A_u[k][j];
            }
        }

        // Then, compute the product of the result vector and the 1s vector
        double mtao = 0.0;
        for(int i = 0; i < length; i++){
            mtao += temp[i];
        }

        return mtao;
    }

    private static double[][] computeInvertedMatrix(double[][] A){
        double[][] A_u = new double[A.length-1][A[0].length-1];
        for(int i = 0; i < A_u.length; i++){
            for(int j = 0; j < A_u[0].length; j++){
                A_u[i][j] = A[i][j];
            }
        }

        RealMatrix invertRmA = MatrixUtils.inverse(MatrixUtils.createRealMatrix(A_u));
        return invertRmA.getData();
    }

    private static Set<IHostNode> getPathSubgraph(IGraph graph, IAttackPath path){
        Set<IHostNode> pathSubgraph = new HashSet<>();
        for(int i = 0; i < path.getLength(); i++){
            if(i == 0)
                pathSubgraph.add(graph.getNode(path.getEdge(0).getTailID()));

            pathSubgraph.add(graph.getNode(path.getEdge(i).getHeadID()));
        }

        return pathSubgraph;
    }

    private static void assignIndices(IHostNode target, Set<IHostNode> subgraph){
        nodesIndices = new HashMap<>();
        // Assign index 'n' to the target
        nodesIndices.put(subgraph.size()-1, target.getID());
        int i = 0;
        for(IHostNode node : subgraph){
            if(!node.equals(target)){
                nodesIndices.put(i, node.getID());
                i++;
            }
        }
    }

}
