package likelihood;

import graphmodels.graph.IEdge;
import graphmodels.graph.IHostNode;
import graphmodels.hypergraph.IHyperEdge;
import graphmodels.hypergraph.IHyperGraph;
import graphmodels.hypergraph.IVulnNode;

import java.util.Collection;
import java.util.Map;

import static utils.Constants.getAccessComplexityScore;

/**
 * Created by Roberto Gaudenzi on 08/10/17.
 */
public class ComputeTransitionProb {

    public static Float[][] execute(IHyperGraph graph, IHostNode target, Map<Integer, String> indicesMatrix, float epsilon){

        int n = indicesMatrix.values().size();

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
}
