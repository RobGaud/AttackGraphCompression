package utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import graphmodels.graph.HostNode;
import graphmodels.graph.IEdge;
import graphmodels.graph.IGraph;
import graphmodels.graph.IHostNode;
import graphmodels.graph.sccmodels.ISCCNode;
import graphmodels.graph.sccmodels.SCCNode;
import graphmodels.hypergraph.*;

import java.io.File;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

/**
 * Created by Roberto Gaudenzi on 23/09/17.
 */
public class JacksonSCCUtils {

    public static final String HOST_NODE_TYPE = "host_node";
    public static final String SCC_NODE_TYPE = "scc_node";

    public IGraph loadCompressedHAG(String dataFolderPath, String filename){
        //TODO
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

            // Get the host nodes and the SCC nodes
            JsonNode nodeListJson = graphJson.get("host_nodes");
            if(nodeListJson.isArray()){
                for(JsonNode nJson : nodeListJson){
                    String nodeType = nJson.get("node_Type").asText();
                    if(nodeType.equals(HOST_NODE_TYPE)){
                        String nID = nJson.get("node_Ident").asText();
                        String nData = nJson.get("node_Data").asText();
                        IHostNode n = new HostNode(nID, nData);
                        hyperGraph.addHostNode(n);
                    }
                    else{
                        hyperGraph.addHostNode(loadSCCNode(nJson));
                    }
                }
            }

            // Get the vulnerabilities
            JsonNode vulnListJson = graphJson.get("vulnerabilities");
            if(vulnListJson.isArray()){
                for(JsonNode vJson : vulnListJson){
                    String vID = vJson.get("node_Ident").asText();
                    String vData = vJson.get("node_Data").asText();
                    IVulnNode v = new VulnerabilityNode(vID, vData);
                    hyperGraph.addVulnNode(v);
                }
            }

            // Get the edges
            JsonNode edgeListJson = graphJson.get("edges");
            if(edgeListJson.isArray()){
                for(JsonNode eJson : edgeListJson){
                    String edgeID   = eJson.get("edge_Ident").asText();
                    String edgeData = eJson.get("edge_Data").asText();
                    String edgeTail = eJson.get("tail").asText();
                    String edgeHead = eJson.get("head").asText();
                    String edgeVuln = eJson.get("vulnerability").asText();

                    IEdge hyperEdge = new HyperEdge(edgeID, edgeTail, edgeHead, edgeVuln, edgeData);
                    hyperGraph.addEdge(hyperEdge);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return hyperGraph;
    }

    public void saveCompressedHAG(IHyperGraph graph, String dataFolderPath, String filename){

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
        ArrayNode sccsArray = mapper.createArrayNode();
        for(IHostNode node : nodes.values()){
            ObjectNode nodeJson;
            if(isSCCNode(node)){
                nodeJson = createSCCJson(mapper, (ISCCNode)node);
            }
            else{
                nodeJson = mapper.createObjectNode();
                nodeJson.put("node_Ident", node.getID());
                nodeJson.put("node_Type", HOST_NODE_TYPE);
                nodeJson.put("node_Data", node.getData());
            }
            nodesArray.add(nodeJson);
        }
        graphJson.putPOJO("host_nodes", nodesArray);
        graphJson.putPOJO("scc_nodes", sccsArray);

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

    private boolean isSCCNode(IHostNode node){
        Class[] interfaces = node.getClass().getInterfaces();
        for(Class i : interfaces){
            if(i.equals(ISCCNode.class))
                return true;
        }
        return false;
    }

    private ObjectNode createSCCJson(ObjectMapper mapper, ISCCNode sccNode){
        ObjectNode sccJson = mapper.createObjectNode();
        sccJson.put("node_Ident", sccNode.getID());
        sccJson.put("node_Data", sccNode.getData());
        sccJson.put("node_Type", SCC_NODE_TYPE);


        // Add all the inner nodes
        Map<String, IHostNode> innerNodes = sccNode.getInnerNodes();
        ArrayNode nodesArray = mapper.createArrayNode();
        for(IHostNode innerNode : innerNodes.values()){
            ObjectNode nodeJson = mapper.createObjectNode();
            nodeJson.put("node_Ident", innerNode.getID());
            nodeJson.put("node_Data", innerNode.getData());
            nodesArray.add(nodeJson);
        }
        sccJson.putPOJO("inner_nodes", nodesArray);

        // Add all the inner edges
        Map<String, Collection<IEdge>> innerEdgesMap = sccNode.getInnerEdges();
        ArrayNode edgesArray = mapper.createArrayNode();
        for(Collection<IEdge> innerEdgeList : innerEdgesMap.values()) {
            for (IEdge innerEdge : innerEdgeList) {
                ObjectNode edgeJson = mapper.createObjectNode();
                edgeJson.put("edge_Data", innerEdge.getData());
                edgeJson.put("tail", innerEdge.getTailID());
                edgeJson.put("head", innerEdge.getHeadID());

                IHyperEdge outHyperEdge = (HyperEdge) innerEdge;
                edgeJson.put("vulnerability", outHyperEdge.getVulnNodeID());
                edgesArray.add(edgeJson);
            }
        }
        sccJson.putPOJO("inner_edges", edgesArray);

        return sccJson;
    }

    private ISCCNode loadSCCNode(JsonNode sccJson){
        String sccNodeID = sccJson.get("node_Ident").asText();
        String sccNodeData = sccJson.get("node_Data").asText();

        SCCNode sccNode = new SCCNode(sccNodeID, sccNodeData);
        // Get the inner nodes
        JsonNode nodesJson = sccJson.get("inner_nodes");
        if(nodesJson.isArray()){
            for(JsonNode nJson : nodesJson){
                String nID = nJson.get("node_Ident").asText();
                String nData = nJson.get("node_Data").asText();
                IHostNode n = new HostNode(nID, nData);
                sccNode.addInnerNode(n);
            }
        }

        // Get the inner edges
        JsonNode edgeListJson = sccJson.get("inner_edges");
        if(edgeListJson.isArray()){
            for(JsonNode eJson : edgeListJson){
                String edgeID   = eJson.get("edge_Ident").asText();
                String edgeData = eJson.get("edge_Data").asText();
                String edgeTail = edgeListJson.get("tail").asText();
                String edgeHead = edgeListJson.get("head").asText();
                String edgeVuln = edgeListJson.get("vulnerability").asText();

                IEdge hyperEdge = new HyperEdge(edgeID, edgeTail, edgeHead, edgeVuln, edgeData);
                sccNode.addInnerEdge(hyperEdge);
            }
        }

        return sccNode;
    }
}
