package main;

import graphmodels.graph.IHostNode;
import graphmodels.hypergraph.IHyperGraph;
import likelihood.ComputeSubgraphs;
import utils.Constants;
import utils.JacksonHAGUtils;
import utils.JacksonSubgraphUtils;

import java.util.Map;
import java.util.Set;

/**
 * Created by Roberto Gaudenzi on 28/10/17.
 */
public class SubgraphsMain {
    public static void main(String[] args){
        String dataFolderPath = Constants.getDataHome();
        String attackGraphName = "C-HAG_attack_graph";

        String attackGraphFile = attackGraphName + ".json";
        String subgraphsFile   = attackGraphName + "_subgraphs.json";

        //Load hypergraph
        IHyperGraph graph = JacksonHAGUtils.loadCompressedHAG(dataFolderPath, attackGraphFile);
        //test(graph);


        // Compute subgraphs
        Map<String, Set<IHostNode>> subgraphsMap = ComputeSubgraphs.execute(graph);

        // Store them in a json file
        JacksonSubgraphUtils.storeSubgraphs(attackGraphName, subgraphsMap, dataFolderPath, subgraphsFile);

    }

    private static void test(IHyperGraph graph){
        for(IHostNode node : graph.getHostNodes().values()){
            System.out.println("OUTBOUND: " + node.getOutboundEdges());
            //System.out.println("INBOUND: " + node.getInboundEdges());
        }
    }
}
