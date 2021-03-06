package likelihood;

import java.util.Map;

import attackpaths.IAttackPath;

import graphmodels.graph.IEdge;
import graphmodels.graph.IHostNode;
import graphmodels.hypergraph.IHyperEdge;
import graphmodels.hypergraph.IHyperGraph;
import graphmodels.hypergraph.IVulnNode;

import static utils.constants.LikelihoodConstants.OBSERVED_EXIT_RATE;
import static utils.constants.VulnerabilityConstants.getAccessComplexityScore;


/**
 * Created by Roberto Gaudenzi on 08/10/17.
 */
class ComputeExitRates {

    /* NEW VERSION (INCLUDE PRECONDITIONS) */

    static double[] execute(IHyperGraph graph, Map<Integer, String> indicesMatrix, IAttackPath path, double[] stateVector){
        double[] exitRates = new double[indicesMatrix.values().size()];

        for(int nodeIndex : indicesMatrix.keySet()){
            if(nodeIndex < stateVector.length && stateVector[nodeIndex] != 0.0)
                exitRates[nodeIndex] = OBSERVED_EXIT_RATE;
            else{
                IHostNode node = graph.getNode(indicesMatrix.get(nodeIndex));
                int nodeRank = getNodeRank(node, path);
                if(nodeRank != 0)
                    exitRates[nodeIndex] = getExitRate(graph, node, nodeRank);
                else
                    exitRates[nodeIndex] = getExitRate(graph, node);
            }
        }

        return exitRates;
    }

    private static double getExitRate(IHyperGraph graph, IHostNode node, int nodeRank){
        double sk = 0.0;
        for(IEdge inEdge : node.getInboundEdges()){
            String vulnID = ((IHyperEdge)inEdge).getVulnNodeID();
            IVulnNode vulnNode = graph.getVulnNodes().get(vulnID);
            double vulnComplexity = getAccessComplexityScore(vulnNode.getComplexityScore());
            if(vulnComplexity > sk){
                sk = vulnComplexity;
            }
        }

        // Idea:
        //  - the value of |Pre(i)| is the number of nodes to be hacked before arriving to 'node'.
        //  - the value of H(i) is |Pre(i)|-1, because we call this function only when we compute MTAO(X).
        //    In fact, when we compute MTAO(min), all the nodes are considered hacked, so H(i)=0.
        double predRatio = 1 - ((nodeRank-1)/nodeRank);

        return (sk + predRatio)/2;
    }

    private static double getExitRate(IHyperGraph graph, IHostNode node){
        double sk = 0.0;
        for(IEdge inEdge : node.getInboundEdges()){
            String vulnID = ((IHyperEdge)inEdge).getVulnNodeID();
            IVulnNode vulnNode = graph.getVulnNodes().get(vulnID);
            double vulnComplexity = getAccessComplexityScore(vulnNode.getComplexityScore());
            if(vulnComplexity > sk){
                sk = vulnComplexity;
            }
        }
        return sk/2;
    }


    private static int getNodeRank(IHostNode node, IAttackPath path){
        for(int i = 0; i < path.getLength(); i++){
            String nodeID = node.getID();
            String edgeHeadID = path.getEdge(i).getHeadID();
            if(nodeID.equals(edgeHeadID))
                return i+1;
        }

        return 0;
    }
}
