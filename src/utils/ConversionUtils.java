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

import java.io.File;

/**
 * Created by Roberto Gaudenzi on 15/09/17.
 */
public class ConversionUtils {

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
            JsonNode edgesArray = graphJson.get("attackPathEdges");
            for(JsonNode edgeJson : edgesArray){
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

                    IVulnNode vulnNode = new VulnerabilityNode(vulnID, vulnData);
                    hyperGraph.addVulnNode(vulnNode);

                    IEdge hyperEdge = new HyperEdge(tailID, headID, vulnID, "");
                    hyperGraph.addEdge(hyperEdge);
                }
            }

            newFilename = "HAG_"+hyperGraph.getData()+".json";
            JacksonGraphUtils.saveHAG(hyperGraph, dataFolderPath, newFilename);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return newFilename;
    }
}
