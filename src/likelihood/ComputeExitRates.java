package likelihood;

import attackpaths.IAttackPath;
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
            double vulnComplexity = Constants.getAccessComplexityScore(vulnNode.getComplexityScore());
            if(vulnComplexity < sk){
                sk = vulnComplexity;
            }
        }
        return sk/2;
    }

    /* NEW VERSION (INCLUDE PRECONDITIONS) */

    public static double[] execute(IHyperGraph graph, Map<Integer, String> indicesMatrix, IAttackPath path, double[] stateVector){
        double[] exitRates = new double[indicesMatrix.values().size()];

        for(int nodeIndex : indicesMatrix.keySet()){
            if(nodeIndex < stateVector.length && stateVector[nodeIndex] != 0.0)
                exitRates[nodeIndex] = Constants.OBSERVED_EXIT_RATE;
            else{
                IHostNode node = graph.getNode(indicesMatrix.get(nodeIndex));
                int nodeRank = getNodeRank(node, path);
                if(nodeRank != 0)
                    exitRates[nodeIndex] = getExitRate(graph, node, nodeRank);
                else
                    exitRates[nodeIndex] = getExitRate(graph, node);
            }
        }

        /*TODO remove
        System.out.println("Exitrates:");
        for(int i = 0; i < exitRates.length; i++){
            System.out.print(exitRates[i] + " ");
        }
        System.out.println();
        */

        return exitRates;
    }

    private static double getExitRate(IHyperGraph graph, IHostNode node, int nodeRank){
        double sk = 1.0;
        for(IEdge inEdge : node.getInboundEdges()){
            String vulnID = ((IHyperEdge)inEdge).getVulnNodeID();
            IVulnNode vulnNode = graph.getVulnNodes().get(vulnID);
            double vulnComplexity = Constants.getAccessComplexityScore(vulnNode.getComplexityScore());
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

    private static int getNodeRank(IHostNode node, IAttackPath path){
        for(int i = 0; i < path.getLength(); i++){
            String nodeID = node.getID();
            String edgeHeadID = path.getEdge(i).getHeadID();
            if(nodeID.equals(edgeHeadID))
                return i+1;
        }

        //System.err.println("ComputeExitRates.getNodeRank: node with ID " + node.getID() + " not in path with ID " + path.getID());
        return 0;
    }
}
