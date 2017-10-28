package utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import graphmodels.graph.*;
import graphmodels.graph.sccmodels.ISCCAttackEdge;
import graphmodels.graph.sccmodels.ISCCNode;

import graphmodels.hypergraph.*;
import graphmodels.hypergraph.sccmodels.ISCCHyperEdge;

import static utils.Constants.*;

import java.io.File;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

/**
 * Created by Roberto Gaudenzi on 07/10/17.
 */
public class JacksonHAGUtils {

    /**
     * This method loads an HyperGraph from a Json file.
     * @param dataFolderPath: the path of the folder that contains the file.
     * @param filename: the file that contains the graph
     * @return the graph parsed from the json file.
     */
    public static IHyperGraph loadCompressedHAG(String dataFolderPath, String filename){
        HyperGraph hyperGraph = null;
        try{
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
                    IHostNode ep = JacksonNodeUtils.loadHostNode(epJson);
                    hyperGraph.addEntryPoint(ep);
                }
            }

            // Get the targets
            JsonNode targetListJson = graphJson.get("targets");
            if(targetListJson.isArray()){
                for(JsonNode tJson : targetListJson){
                    IHostNode t = JacksonNodeUtils.loadHostNode(tJson);
                    hyperGraph.addTarget(t);
                }
            }

            // Get the host nodes and the SCC nodes
            JsonNode nodeListJson = graphJson.get("host_nodes");
            if(nodeListJson.isArray()){
                for(JsonNode nJson : nodeListJson){
                    String nodeType = nJson.get("node_Type").asText();
                    if(nodeType.equals(HOST_NODE_TYPE)){
                        IHostNode n = JacksonNodeUtils.loadHostNode(nJson);
                        hyperGraph.addHostNode(n);
                    }
                    else{
                        hyperGraph.addHostNode(JacksonNodeUtils.loadSCCNode(nJson));
                    }
                }
            }

            // Get the vulnerabilities
            JsonNode vulnListJson = graphJson.get("vulnerabilities");
            if(vulnListJson.isArray()){
                for(JsonNode vJson : vulnListJson){
                    IVulnNode v = JacksonNodeUtils.loadVulnNode(vJson);
                    hyperGraph.addVulnNode(v);
                }
            }

            // Get the edges
            JsonNode edgeListJson = graphJson.get("edges");
            if(edgeListJson.isArray()){
                for(JsonNode eJson : edgeListJson){
                    String edgeType = eJson.get("edge_Type").asText();

                    IEdge edge = null;
                    switch (edgeType){
                        case ATTACK_EDGE_TYPE:
                            edge = JacksonEdgeUtils.loadAttackEdge(eJson);
                            break;
                        case HYPER_EDGE_TYPE:
                            edge = JacksonEdgeUtils.loadHyperEdge(eJson);
                            break;
                        case SCC_ATTACK_EDGE_TYPE:
                            edge = JacksonEdgeUtils.loadSCCAttackEdge(eJson);
                            break;
                        case SCC_HYPER_EDGE_TYPE:
                            edge = JacksonEdgeUtils.loadSCCHyperEdge(eJson);
                            break;
                        default:
                            System.err.print("ERROR: unexpected edge type.");
                    }
                    hyperGraph.addEdge(edge);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return hyperGraph;
    }

    /**
     * This methods takes an HyperGraph and stores it into a Json file.
     * @param graph: the HyperGraph object to be stored into the Json file.
     * @param filename: the name of the Json file that will contain the HyperGraph object.
     * @param dataFolderPath: the path of the folder that will contain the file.
     */
    public static void storeCompressedHAG(IHyperGraph graph, String dataFolderPath, String filename){

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode graphJson = mapper.createObjectNode();

        graphJson.put("attackGraph_Ident", graph.getData());

        Collection<IHostNode> entryPoints = graph.getEntryPoints();
        Collection<IHostNode> targets = graph.getTargets();
        Map<String, IHostNode> nodes = graph.getHostNodes();
        Map<String, IVulnNode> vulnerabilities = graph.getVulnNodes();

        // Create the array containing all the entry points.
        ArrayNode epArray = mapper.createArrayNode();
        for(IHostNode ep : entryPoints){
            epArray.add(JacksonNodeUtils.storeHostNode(mapper, ep));
        }
        graphJson.putPOJO("entry_points", epArray);

        // Create the array containing all the targets.
        ArrayNode targetArray = mapper.createArrayNode();
        for(IHostNode t : targets){
            targetArray.add(JacksonNodeUtils.storeHostNode(mapper, t));
        }
        graphJson.putPOJO("targets", targetArray);

        // Create the array containing all the host nodes.
        ArrayNode nodesArray = mapper.createArrayNode();
        for(IHostNode node : nodes.values()){
            ObjectNode nodeJson;
            if(ISCCNode.isSCCNode(node)){
                nodeJson = JacksonNodeUtils.storeSCCNode(mapper, (ISCCNode)node);
            }
            else{
                nodeJson = JacksonNodeUtils.storeHostNode(mapper, node);
            }
            nodesArray.add(nodeJson);
        }
        graphJson.putPOJO("host_nodes", nodesArray);

        // Create the array containing all the vulnerability nodes.
        ArrayNode vulnArray = mapper.createArrayNode();
        for(IVulnNode vuln : vulnerabilities.values()){
            vulnArray.add(JacksonNodeUtils.storeVulnNode(mapper, vuln));
        }
        graphJson.putPOJO("vulnerabilities", vulnArray);

        // Create the Json containing all the edges in the graph.
        ArrayNode edgesArray = mapper.createArrayNode();
        System.out.println(nodes.values());
        for(IHostNode node : nodes.values()) {
            System.out.println(node.getID()+": "+node.getOutboundEdges());
            for(IEdge outEdge : node.getOutboundEdges()){
                ObjectNode edgeJson;
                if(ISCCAttackEdge.isSCCAttackEdge(outEdge)){
                    System.out.println("JacksonHAGUtils.storeHAG/addingEdges/" + node.getID() + "/Storing scc attack edge.");
                    edgeJson = JacksonEdgeUtils.storeSCCAttackEdge(mapper, (ISCCAttackEdge)outEdge);
                }
                else if(ISCCHyperEdge.isSCCHyperEdge(outEdge)){
                    System.out.println("JacksonHAGUtils.storeHAG/addingEdges/" + node.getID() + "/Storing scc hyper edge.");
                    edgeJson = JacksonEdgeUtils.storeHyperEdge(mapper, (ISCCHyperEdge)outEdge);
                }
                else if(IAttackEdge.isAttackEdge(outEdge)){
                    System.out.println("JacksonHAGUtils.storeHAG/addingEdges/" + node.getID() + "/Storing attack edge.");
                    edgeJson = JacksonEdgeUtils.storeAttackEdge(mapper, (IAttackEdge)outEdge);
                }
                else{
                    System.out.println("JacksonHAGUtils.storeHAG/addingEdges/" + node.getID() + "/Storing hyper edge.");
                    edgeJson = JacksonEdgeUtils.storeHyperEdge(mapper, (IHyperEdge)outEdge);
                }

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
}
