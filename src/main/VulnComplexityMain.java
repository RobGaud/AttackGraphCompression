package main;

import graphmodels.hypergraph.IHyperGraph;
import graphmodels.hypergraph.IVulnNode;
import utils.Constants;
import utils.JacksonACUtils;
import utils.JacksonHAGUtils;

import java.util.Map;

/**
 * Created by Roberto Gaudenzi on 28/10/17.
 */
public class VulnComplexityMain {
    public static void main(String[] args){
        String dataFolderPath = Constants.getDataHome();
        String HAG_JSON_NAME = "HAG_attack_graph.json";
        String AC_JSON_NAME  = "access-complexity-data.json";

        IHyperGraph hyperGraph = JacksonHAGUtils.loadCompressedHAG(dataFolderPath, HAG_JSON_NAME);

        Map<String, String> acMap = JacksonACUtils.loadCVEJson(AC_JSON_NAME);

        Map<String, IVulnNode> hagVulns = hyperGraph.getVulnNodes();

        for(String vulnID : hagVulns.keySet()){
            String acCode = acMap.get(vulnID);
            if(acCode == null){
                System.err.println("ERROR: " + vulnID + " is not present in Access Complexity knowledge base.");
                hagVulns.get(vulnID).setComplexityScore(Constants.COMPLEXITY_DEFAULT_VALUE);
            }
            else
                hagVulns.get(vulnID).setComplexityScore(acCode);

            // No need to re-add manually the vulnerabilities: we're updating the object using its reference.
        }

        // Store again the graph
        JacksonHAGUtils.storeCompressedHAG(hyperGraph, dataFolderPath, HAG_JSON_NAME);
    }
}
