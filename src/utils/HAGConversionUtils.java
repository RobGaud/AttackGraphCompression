package utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphmodels.graph.HostNode;
import graphmodels.graph.IEdge;
import graphmodels.graph.IHostNode;
import graphmodels.hypergraph.HyperEdge;
import graphmodels.hypergraph.HyperGraph;
import graphmodels.hypergraph.IVulnNode;
import graphmodels.hypergraph.VulnerabilityNode;

import static utils.Constants.*;

import java.io.File;
import java.util.Map;

/**
 * Created by Roberto Gaudenzi on 15/09/17.
 */
public class HAGConversionUtils {

    /**
     * This method converts a graph taken from a Json file in an HyperGraph, and stores it in a new Json file.
     * @param filename: the name of the Json file that contains the graph to be converted.
     * @return the name of the file where the HyperGraph has been stored.
     *         Returns null in case of error.
     */
    public static String convertJson(String dataFolderPath, String filename){
        String newFilename = null;
        System.out.println(dataFolderPath+filename);
        try {
            JsonFactory jsonFactory = new JsonFactory();
            JsonParser jp = jsonFactory.createParser(new File(dataFolderPath + filename));
            jp.setCodec(new ObjectMapper());
            JsonNode loadedJson = jp.readValueAsTree();
            JsonNode graphJson = loadedJson.get("AttackGraph");

            String agID = graphJson.get("attackGraph_Ident").asText();
            HyperGraph hyperGraph = new HyperGraph(agID);

            //Add the sources to the hypergraph
            JsonNode sourcesArray = graphJson.get("sources");
            // Add the source nodes to the HAG
            for (JsonNode sourceJson : sourcesArray){

                String sourceID = sourceJson.get("node_Ident").asText();
                String sourceData = sourceJson.get("gainedprivilege").asText();
                IHostNode ep = new HostNode(sourceID, sourceData);
                hyperGraph.addEntryPoint(ep);
            }

            // Add the targets to the hypergraph
            JsonNode targetsArray = graphJson.get("targets");
            for (JsonNode targetJson : targetsArray){

                String targetID = targetJson.get("node_Ident").asText();
                String targetData = targetJson.get("gainedprivilege").asText();
                IHostNode t = new HostNode(targetID, targetData);
                hyperGraph.addTarget(t);
            }

            // Add the edges and the vulnerabilities to the hypergraph.
            // Since vulnerabilities are not stored into this graph, we need to extract them from the edges.
            // We also load the map of CVSS scores in order to store it into the VulnerabilityNode objects.
            Map<String, String> cveMap = JacksonCVEUtils.loadCVEJson(CVE_DATA_FILENAME);

            JsonNode edgesArray = graphJson.get("attackPathEdges");
            for(JsonNode edgeJson : edgesArray){
                String edgeID = edgeJson.get("edge_Ident").asText();

                // Extract the tail
                JsonNode tailJson = edgeJson.get("tail");
                String tailID = tailJson.get("node_Ident").asText();
                JsonNode tailPLJson = tailJson.get("gainedprivilege");
                String tailData = tailPLJson.get("level").asText();
                IHostNode t = new HostNode(tailID, tailData);
                hyperGraph.addHostNode(t);

                // Extract the head
                JsonNode headJson = edgeJson.get("head");
                String headID = headJson.get("node_Ident").asText();
                JsonNode headPLJson = headJson.get("gainedprivilege");
                String headData = headPLJson.get("level").asText();
                IHostNode h = new HostNode(headID, headData);
                hyperGraph.addHostNode(h);

                // Extract all the vulnerabilities and add the edges to the graph
                JsonNode vulnListJson = edgeJson.get("attackPathNodeVulnerabilityList");
                for(JsonNode vulnJson : vulnListJson){
                    JsonNode vulnDataJson = vulnJson.get("classification").get(0);
                    String vulnID = vulnDataJson.get("ident").asText();
                    String vulnData = vulnDataJson.get("name").asText();
                    // Get CVSS score
                    String cvssScore = cveMap.get(vulnID);
                    //TODO check this out
                    IVulnNode vulnNode = new VulnerabilityNode(vulnID, vulnData, cvssScore, "");
                    hyperGraph.addVulnNode(vulnNode);

                    /* PROBLEM: since we can create more than one hyperedge from a single edge,
                     * we can't simply copy the edge_Ident field from the Json
                     * IDEA: generate new IDs for the hyperedges using edge_Ident as root for all of them
                     */
                    String hyperedgeID = HyperEdgeIDGenerator.generateHyperEdgeID(edgeID);

                    IEdge hyperEdge = new HyperEdge(hyperedgeID, tailID, headID, vulnID, "");
                    hyperGraph.addEdge(hyperEdge);
                }
            }

            newFilename = "HAG_"+hyperGraph.getData()+".json";
            JacksonHAGUtils.storeCompressedHAG(hyperGraph, dataFolderPath, newFilename);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return newFilename;
    }


    private static class HyperEdgeIDGenerator{

        private static String currentEdgeID;
        private static int count;

        private static String generateHyperEdgeID(String edgeID){
            if(!edgeID.equals(currentEdgeID)){
                currentEdgeID = edgeID;
                count = 0;
            }

            return currentEdgeID + "-" + count++;
        }
    }

}
