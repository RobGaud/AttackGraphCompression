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

    public static double[] execute(IHyperGraph graph, Map<Integer, String> indicesMatrix){
        double[] exitRates = new double[indicesMatrix.values().size()];

        for(int nodeIndex : indicesMatrix.keySet()){
            IHostNode node = graph.getNode(indicesMatrix.get(nodeIndex));

            exitRates[nodeIndex] = getExitRate(graph, node);
        }

        return exitRates;
    }

    private static double getExitRate(IHyperGraph graph, IHostNode node){
        double sk = 1.0;
        // for(IEdge inEdge : node.getInboundEdges()){
        for(IEdge outEdge : node.getOutboundEdges()){
            String vulnID = ((IHyperEdge)outEdge).getVulnNodeID();
            IVulnNode vulnNode = graph.getVulnNodes().get(vulnID);
            float vulnComplexity = Constants.getAccessComplexityScore(vulnNode.getComplexityScore());
            if(vulnComplexity < sk){
                sk = vulnComplexity;
            }
        }
        return sk/2;
    }
}
