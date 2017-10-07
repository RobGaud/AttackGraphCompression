package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import graphmodels.graph.HostNode;
import graphmodels.graph.IAttackEdge;
import graphmodels.graph.IEdge;
import graphmodels.graph.IHostNode;
import graphmodels.graph.sccmodels.ISCCNode;
import graphmodels.graph.sccmodels.SCCNode;

import graphmodels.hypergraph.IHyperEdge;
import graphmodels.hypergraph.IVulnNode;
import graphmodels.hypergraph.VulnerabilityNode;

import java.util.Collection;
import java.util.Map;

import static utils.Constants.HOST_NODE_TYPE;
import static utils.Constants.SCC_NODE_TYPE;

/**
 * Created by Roberto Gaudenzi on 07/10/17.
 */
class JacksonNodeUtils {

    /** METHODS FOR LOADING NODES **/
    static IHostNode loadHostNode(JsonNode nodeJson){
        String epID = nodeJson.get("node_Ident").asText();
        String epData = nodeJson.get("node_Data").asText();
        return new HostNode(epID, epData);

    }

    static IVulnNode loadVulnNode(JsonNode vulnJson){
        String vID = vulnJson.get("node_Ident").asText();
        String vData = vulnJson.get("node_Data").asText();
        String cvssScore = vulnJson.get("cvss_score").asText();
        return new VulnerabilityNode(vID, vData, cvssScore);
    }

    public static ISCCNode loadSCCNode(JsonNode sccJson){
        String sccNodeID = sccJson.get("node_Ident").asText();
        String sccNodeData = sccJson.get("node_Data").asText();

        SCCNode sccNode = new SCCNode(sccNodeID, sccNodeData);
        // Get the inner nodes
        JsonNode nodesJson = sccJson.get("inner_nodes");
        if(nodesJson.isArray()){
            for(JsonNode nJson : nodesJson){
                IHostNode n = JacksonNodeUtils.loadHostNode(nJson);
                sccNode.addInnerNode(n);
            }
        }

        // Get the inner edges
        JsonNode edgeListJson = sccJson.get("inner_edges");
        if(edgeListJson.isArray()){
            for(JsonNode eJson : edgeListJson){
                String edgeType = eJson.get("edge_Type").asText();

                IEdge edge;
                if(edgeType.contains(Constants.ATTACK_EDGE_TYPE)){
                    //Treat it as attack edge
                    edge = JacksonEdgeUtils.loadAttackEdge(eJson);
                }
                else{
                    //Treat it as hyper edge
                    edge = JacksonEdgeUtils.loadHyperEdge(eJson);
                }

                sccNode.addInnerEdge(edge);
            }
        }

        return sccNode;
    }

    /** METHODS FOR STORING NODES **/
    static ObjectNode storeHostNode(ObjectMapper mapper, IHostNode node){
        ObjectNode nodeJson = mapper.createObjectNode();
        nodeJson.put("node_Ident", node.getID());
        nodeJson.put("node_Data", node.getData());
        nodeJson.put("node_Type", HOST_NODE_TYPE);

        return nodeJson;
    }

    static ObjectNode storeVulnNode(ObjectMapper mapper, IVulnNode vuln){
        ObjectNode vulnJson = mapper.createObjectNode();
        vulnJson.put("node_Ident", vuln.getID());
        vulnJson.put("node_Data", vuln.getData());
        vulnJson.put("cvss_score", vuln.getCVSS());
        return vulnJson;
    }

    static ObjectNode storeSCCNode(ObjectMapper mapper, ISCCNode sccNode){
        ObjectNode sccJson = mapper.createObjectNode();
        sccJson.put("node_Ident", sccNode.getID());
        sccJson.put("node_Data", sccNode.getData());
        sccJson.put("node_Type", SCC_NODE_TYPE);

        // Add all the inner nodes
        Map<String, IHostNode> innerNodes = sccNode.getInnerNodes();
        ArrayNode nodesArray = mapper.createArrayNode();
        for(IHostNode innerNode : innerNodes.values()){

            ObjectNode nodeJson = storeHostNode(mapper, innerNode);
            nodesArray.add(nodeJson);
        }
        sccJson.putPOJO("inner_nodes", nodesArray);

        // Add all the inner edges
        Map<String, Collection<IEdge>> innerEdgesMap = sccNode.getInnerEdges();
        ArrayNode edgesArray = mapper.createArrayNode();
        for(Collection<IEdge> innerEdgeList : innerEdgesMap.values()) {

            for (IEdge innerEdge : innerEdgeList) {

                ObjectNode edgeJson;
                if(IAttackEdge.isAttackEdge(innerEdge)){
                    //Treat it as an attack edge
                    edgeJson = JacksonEdgeUtils.storeAttackEdge(mapper, (IAttackEdge)innerEdge);
                }
                else{
                    //Treat it as a hyper edge
                    edgeJson = JacksonEdgeUtils.storeHyperEdge(mapper, (IHyperEdge)innerEdge);
                }

                edgesArray.add(edgeJson);
            }
        }
        sccJson.putPOJO("inner_edges", edgesArray);

        return sccJson;
    }
}
