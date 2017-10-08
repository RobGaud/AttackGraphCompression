package likelihood;

import graphmodels.graph.IEdge;
import graphmodels.graph.IGraph;
import graphmodels.graph.IHostNode;
import graphmodels.hypergraph.IHyperEdge;
import graphmodels.hypergraph.IHyperGraph;
import graphmodels.hypergraph.IVulnNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Roberto Gaudenzi on 08/10/17.
 */
public class ComputeTransitionProb {

    private static Map<Integer, String> indicesMatrix;


    public static Float[][] computeProbMatrix(IHyperGraph graph, IHostNode target, Set<IHostNode> subgraph, float epsilon){

        assignIndices(target, subgraph);

        int n = subgraph.size();

        Float[][] probMatrix = new Float[n][n];

        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                IHostNode node_i = graph.getNode(indicesMatrix.get(i));
                IHostNode node_j = graph.getNode(indicesMatrix.get(j));
                probMatrix[i][j] = computeTransitionProb(graph, node_i, node_j, epsilon);
            }
        }

        return probMatrix;

    }

    private static float computeTransitionProb(IHyperGraph graph, IHostNode i, IHostNode j, float epsilon){
        Collection<IEdge> outboundEdges = i.getOutboundEdges();
        Collection<IEdge> edgesToJ = i.getOutboundEdgesTo(j);

        int m_num = edgesToJ.size();
        int m_den = outboundEdges.size();
        float m_ij = m_num/m_den;

        Map<String, IVulnNode> vulnerabilities = graph.getVulnNodes();

        float n_num = 0.0f;
        float n_den = 0.0f;

        for(IEdge e : edgesToJ){
            IHyperEdge he = (IHyperEdge)e;

            IVulnNode vuln = vulnerabilities.get(he.getVulnNodeID());
            n_den +=  getAccessComplexityScore(vuln.getComplexityScore());
        }

        for(IEdge e : outboundEdges){
            IHyperEdge he = (IHyperEdge)e;

            IVulnNode vuln = vulnerabilities.get(he.getVulnNodeID());
            n_den += getAccessComplexityScore(vuln.getComplexityScore());
        }
        float n_ij = n_num / n_den;

        return (1-epsilon)*m_ij + epsilon*n_ij;
    }

    private static void assignIndices(IHostNode target, Set<IHostNode> subgraph){
        indicesMatrix = new HashMap<>();
        // Assign index 'n' to the target
        indicesMatrix.put(subgraph.size(), target.getID());
        int i = 0;
        for(IHostNode node : subgraph){
            if(!node.equals(target)){
                indicesMatrix.put(i, node.getID());
                i++;
            }
        }
    }

    public static float getAccessComplexityScore(String ac){
        float score;
        switch (ac){
            case "LOW":
                score = 0.33f;
                break;
            case "MEDIUM":
                score = 0.50f;
                break;
            case "HIGH":
                score = 0.67f;
                break;
            default:
                System.err.println("ERROR: UNEXPECTED ACCESS COMPLEXITY VALUE = " + ac);
                score = 0.50f;
        }
        return score;
    }
}
