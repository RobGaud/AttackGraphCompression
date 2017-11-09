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

    public static double[][] execute(IHyperGraph graph, Map<Integer, String> indicesMatrix, float epsilon){

        int n = indicesMatrix.values().size();

        double[][] probMatrix = new double[n][n];

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

        if(edgesToJ == null || edgesToJ.size() == 0){
            return 0.0f;
        }

        //System.out.println("Computing transition prob between nodes "+i.getID() + " and " + j.getID());

        // Compute m_num and m_den
        float m_num = edgesToJ.size();
        float m_den = 0.0f + outboundEdges.size();

        //System.out.println("ComputeTransitionProb: m_num="+m_num+", m_den"+m_den);
        float m_ij = m_num/m_den;

        Map<String, IVulnNode> vulnerabilities = graph.getVulnNodes();

        // Compute n_num and n_den
        float n_den = 0.0f;
        float n_num = 0.0f;

        for(IEdge e : edgesToJ){
            IHyperEdge he = (IHyperEdge)e;

            IVulnNode vuln = vulnerabilities.get(he.getVulnNodeID());
            n_num +=  getAccessComplexityScore(vuln.getComplexityScore());
        }

        for(IEdge e : outboundEdges){
            IHyperEdge he = (IHyperEdge)e;

            IVulnNode vuln = vulnerabilities.get(he.getVulnNodeID());
            n_den += getAccessComplexityScore(vuln.getComplexityScore());
        }
        float n_ij = n_num / n_den;

        //System.out.println("ComputeTransitionProb: n_num="+n_num+", n_den"+n_den);
        //System.out.println("ComputeTransitionProb: n=" + n_ij +", m=" + m_ij);

        return (1-epsilon)*m_ij + epsilon*n_ij;
    }
}
