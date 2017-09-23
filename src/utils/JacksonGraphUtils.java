package utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import graphmodels.graph.HostNode;
import graphmodels.graph.IEdge;
import graphmodels.graph.IHostNode;
import graphmodels.hypergraph.*;

import java.io.File;
import java.io.PrintWriter;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Roberto Gaudenzi on 15/09/17.
 */
public class JacksonGraphUtils {

    /**
     * This methods takes an HyperGraph and stores it into a Json file.
     * @param graph: the HyperGraph object to be stored into the Json file.
     * @param filename: the name of the Json file that will contain the HyperGraph object.
     */
    public static void saveHAG(HyperGraph graph, String dataFolderPath, String filename) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode graphJson = mapper.createObjectNode();

        graphJson.put("attackGraph_Ident", graph.getData());

        Collection<IHostNode> entryPoints = graph.getEntryPoints();
        Collection<IHostNode> targets = graph.getTargets();
        Map<String, IHostNode> nodes = graph.getHostNodes();
        Map<String, IVulnNode> vulnerabilities = graph.getVulnNodes();

        // Create the array contaning all the entry points.
        ArrayNode epArray = mapper.createArrayNode();
        for(IHostNode ep : entryPoints){
            ObjectNode epJson = mapper.createObjectNode();
            epJson.put("node_Ident", ep.getID());
            epJson.put("node_Data", ep.getData());
            epArray.add(epJson);
        }
        graphJson.putPOJO("entry_points", epArray);

        // Create the array contaning all the targets.
        ArrayNode targetArray = mapper.createArrayNode();
        for(IHostNode t : targets){
            ObjectNode targetJson = mapper.createObjectNode();
            targetJson.put("node_Ident", t.getID());
            targetJson.put("node_Data", t.getData());
            targetArray.add(targetJson);
        }
        graphJson.putPOJO("targets", targetArray);

        // Create the array containing all the host nodes.
        ArrayNode nodesArray = mapper.createArrayNode();
        for(IHostNode node : nodes.values()){
            ObjectNode nodeJson = mapper.createObjectNode();
            nodeJson.put("node_Ident", node.getID());
            nodeJson.put("node_Data", node.getData());
            nodesArray.add(nodeJson);
        }
        graphJson.putPOJO("host_nodes", nodesArray);

        // Create the array containing all the vulnerability nodes.
        ArrayNode vulnArray = mapper.createArrayNode();
        for(IVulnNode vuln : vulnerabilities.values()){
            ObjectNode vulnJson = mapper.createObjectNode();
            vulnJson.put("node_Ident", vuln.getID());
            vulnJson.put("node_Data", vuln.getData());
            vulnArray.add(vulnJson);
        }
        graphJson.putPOJO("vulnerabilities", vulnArray);

        // Create the Json containing all the edges in the graph.
        ArrayNode edgesArray = mapper.createArrayNode();
        for(IHostNode node : nodes.values()) {
            for(IEdge outEdge : node.getOutboundEdges()){
                ObjectNode edgeJson = mapper.createObjectNode();
                edgeJson.put("edge_Data", outEdge.getData());
                edgeJson.put("tail", outEdge.getTailID());
                edgeJson.put("head", outEdge.getHeadID());

                IHyperEdge outHyperEdge = (HyperEdge)outEdge;
                edgeJson.put("vulnerability", outHyperEdge.getVulnNodeID());
                edgesArray.add(edgeJson);
            }
        }
        graphJson.putPOJO("edges", edgesArray);



        // Save Json to file
        try{
            String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(graphJson);
            PrintWriter out = new PrintWriter(dataFolderPath + filename);
            out.print(jsonString);
            out.close();
            System.out.println("Successfully copied JSON Object to File named \""+filename+"\".");
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("An error occurred while copying JSON Object to File named \""+filename+"\".");
        }
    }

    /**
     * This method loads an HyperGraph from a Json file.
     * @param filename: the name of the Json file that contains the data of the HyperGraph.
     * @return an HyperGraph object built from the data contained into the Json file.
     */
    public static HyperGraph loadHAG(String dataFolderPath, String filename){
        HyperGraph hyperGraph = null;
        try {
            JsonFactory jsonFactory = new JsonFactory();
            JsonParser jp = jsonFactory.createParser(new File(dataFolderPath + filename));
            jp.setCodec(new ObjectMapper());
            JsonNode graphJson = jp.readValueAsTree();

            // Get the graph data.
            JsonNode hagDataJson = graphJson.get("attackGraph_Ident");
            String hagData = hagDataJson.asText();

            hyperGraph = new HyperGraph(hagData);

            // Get the entry points
            JsonNode epListJson = graphJson.get("entry_points");
            if(epListJson.isArray()){
                for(JsonNode epJson : epListJson){
                    String epID = epJson.get("node_Ident").asText();
                    String epData = epJson.get("node_Data").asText();
                    IHostNode ep = new HostNode(epID, epData);
                    hyperGraph.addEntryPoint(ep);
                }
            }

            // Get the targets
            JsonNode targetListJson = graphJson.get("targets");
            if(targetListJson.isArray()){
                for(JsonNode tJson : targetListJson){
                    String tID = tJson.get("node_Ident").asText();
                    String tData = tJson.get("node_Data").asText();
                    IHostNode t = new HostNode(tID, tData);
                    hyperGraph.addTarget(t);
                }
            }

            // Get the host nodes
            JsonNode nodeListJson = graphJson.get("host_nodes");
            if(nodeListJson.isArray()){
                for(JsonNode nJson : nodeListJson){
                    String nID = nJson.get("node_Ident").asText();
                    String nData = nJson.get("node_Data").asText();
                    IHostNode n = new HostNode(nID, nData);
                    hyperGraph.addHostNode(n);
                }
            }

            // Get the vulnerabilities
            JsonNode vulnListJson = graphJson.get("vulnerabilities");
            if(vulnListJson.isArray()){
                for(JsonNode nJson : vulnListJson){
                    String vID = nJson.get("node_Ident").asText();
                    String vData = nJson.get("node_Data").asText();
                    IVulnNode v = new VulnerabilityNode(vID, vData);
                    hyperGraph.addVulnNode(v);
                }
            }

            // Get the edges
            JsonNode edgeListJson = graphJson.get("edges");
            if(vulnListJson.isArray()){
                for(JsonNode eJson : edgeListJson){
                    String edgeData = eJson.get("edge_Data").asText();
                    String edgeTail = edgeListJson.get("tail").asText();
                    String edgeHead = edgeListJson.get("head").asText();
                    String edgeVuln = edgeListJson.get("vulnerability").asText();

                    IEdge hyperEdge = new HyperEdge(edgeTail, edgeHead, edgeVuln, edgeData);
                    hyperGraph.addEdge(hyperEdge);
                }
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return hyperGraph;
    }
}
