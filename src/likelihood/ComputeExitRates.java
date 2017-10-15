package likelihood;

import graphmodels.graph.IEdge;
import graphmodels.graph.IHostNode;
import graphmodels.hypergraph.IHyperEdge;
import graphmodels.hypergraph.IHyperGraph;
import graphmodels.hypergraph.IVulnNode;
import utils.Constants;

import java.util.Map;

/**
 * Created by Roberto Gaudenzi on 08/10/17.
 */
public class ComputeExitRates {

    public static Float[] execute(IHyperGraph graph, Map<Integer, String> indicesMatrix){
        Float[] exitRates = new Float[indicesMatrix.values().size()];

        for(int nodeIndex : indicesMatrix.keySet()){
            IHostNode node = graph.getNode(indicesMatrix.get(nodeIndex));

            exitRates[nodeIndex] = getExitRate(graph, node);
        }

        return exitRates;
    }

    private static float getExitRate(IHyperGraph graph, IHostNode node){
        float exitRate = 1;
        for(IEdge inEdge : node.getInboundEdges()){
            String vulnID = ((IHyperEdge)inEdge).getVulnNodeID();
            IVulnNode vulnNode = graph.getVulnNodes().get(vulnID);
            float vulnComplexity = Constants.getAccessComplexityScore(vulnNode.getComplexityScore());
            if(vulnComplexity < exitRate){
                exitRate = vulnComplexity;
            }
        }

        return exitRate;
    }
}
