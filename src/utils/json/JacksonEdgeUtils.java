package utils.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import graphmodels.graph.AttackEdge;
import graphmodels.graph.IAttackEdge;
import graphmodels.graph.sccmodels.ISCCAttackEdge;

import graphmodels.graph.sccmodels.SCCAttackEdge;
import graphmodels.hypergraph.HyperEdge;
import graphmodels.hypergraph.IHyperEdge;
import graphmodels.hypergraph.sccmodels.ISCCHyperEdge;
import graphmodels.hypergraph.sccmodels.SCCHyperEdge;

import static utils.constants.JsonConstants.*;

/**
 * Created by Roberto Gaudenzi on 07/10/17.
 */
class JacksonEdgeUtils {

    /** METHODS FOR LOADING EDGES **/
    static IAttackEdge loadAttackEdge(JsonNode aeJson){
        String edgeID   = aeJson.get("edge_Ident").asText();
        String edgeData = aeJson.get("edge_Data").asText();
        String edgeTail = aeJson.get("tail").asText();
        String edgeHead = aeJson.get("head").asText();
        IAttackEdge attackEdge = new AttackEdge(edgeID, edgeTail, edgeHead, edgeData);

        JsonNode vulnList = aeJson.get("vulnerabilities");
        if(vulnList.isArray()){
            for(JsonNode v : vulnList){
                String vulnID = v.asText();
                attackEdge.addVulnerability(vulnID);
            }
        }

        return attackEdge;
    }

    static IHyperEdge loadHyperEdge(JsonNode heJson){
        String edgeID   = heJson.get("edge_Ident").asText();
        String edgeData = heJson.get("edge_Data").asText();
        String edgeTail = heJson.get("tail").asText();
        String edgeHead = heJson.get("head").asText();
        String edgeVuln = heJson.get("vulnerability").asText();

        return new HyperEdge(edgeID, edgeTail, edgeHead, edgeVuln, edgeData);
    }

    static ISCCAttackEdge loadSCCAttackEdge(JsonNode sccaeJson){
        String edgeID   = sccaeJson.get("edge_Ident").asText();
        String edgeData = sccaeJson.get("edge_Data").asText();
        String edgeTail = sccaeJson.get("tail").asText();
        String edgeHead = sccaeJson.get("head").asText();
        String innerTail = sccaeJson.get("inner_Tail").asText();
        String innerHead = sccaeJson.get("inner_Head").asText();

        return new SCCAttackEdge(edgeID, edgeTail, edgeHead, edgeData, innerTail, innerHead);
    }

    static ISCCHyperEdge loadSCCHyperEdge(JsonNode sccheJson){
        String edgeID   = sccheJson.get("edge_Ident").asText();
        String edgeData = sccheJson.get("edge_Data").asText();
        String edgeTail = sccheJson.get("tail").asText();
        String edgeHead = sccheJson.get("head").asText();
        String edgeVuln = sccheJson.get("vulnerability").asText();
        String innerTail = sccheJson.get("inner_Tail").asText();
        String innerHead = sccheJson.get("inner_Head").asText();

        return new SCCHyperEdge(edgeID, edgeTail, edgeHead, edgeVuln, edgeData, innerTail, innerHead);
    }

    /** METHODS FOR STORING EDGES **/
    static ObjectNode storeAttackEdge(ObjectMapper mapper, IAttackEdge attackEdge){
        ObjectNode edgeJson = mapper.createObjectNode();
        edgeJson.put("edge_Type", ATTACK_EDGE_TYPE);
        edgeJson.put("edge_Ident", attackEdge.getID());
        edgeJson.put("edge_Data", attackEdge.getData());
        edgeJson.put("tail", attackEdge.getTailID());
        edgeJson.put("head", attackEdge.getHeadID());

        ArrayNode vulnArray = mapper.createArrayNode();
        for(String v : attackEdge.getVulnerabilities()){
            vulnArray.add(v);
        }
        edgeJson.putPOJO("vulnerabilities", vulnArray);

        return edgeJson;
    }

    static ObjectNode storeHyperEdge(ObjectMapper mapper, IHyperEdge hyperEdge){
        ObjectNode edgeJson = mapper.createObjectNode();
        edgeJson.put("edge_Type", HYPER_EDGE_TYPE);
        edgeJson.put("edge_Ident", hyperEdge.getID());
        edgeJson.put("edge_Data", hyperEdge.getData());
        edgeJson.put("tail", hyperEdge.getTailID());
        edgeJson.put("head", hyperEdge.getHeadID());
        edgeJson.put("vulnerability", hyperEdge.getVulnNodeID());

        return edgeJson;
    }

    static ObjectNode storeSCCAttackEdge(ObjectMapper mapper, ISCCAttackEdge sccaeEdge){
        ObjectNode edgeJson = mapper.createObjectNode();
        edgeJson.put("edge_Type", SCC_ATTACK_EDGE_TYPE);
        edgeJson.put("edge_Ident", sccaeEdge.getID());
        edgeJson.put("edge_Data", sccaeEdge.getData());
        edgeJson.put("tail", sccaeEdge.getTailID());
        edgeJson.put("head", sccaeEdge.getHeadID());
        edgeJson.put("inner_Tail", sccaeEdge.getInnerTail());
        edgeJson.put("inner_Head", sccaeEdge.getInnerHead());

        ArrayNode vulnArray = mapper.createArrayNode();
        for(String v : sccaeEdge.getVulnerabilities()){
            vulnArray.add(v);
        }
        edgeJson.putPOJO("vulnerabilities", vulnArray);

        return edgeJson;
    }

    static ObjectNode storeSCCHyperEdge(ObjectMapper mapper, ISCCHyperEdge sccheEdge){
        ObjectNode edgeJson = mapper.createObjectNode();
        edgeJson.put("edge_Type", SCC_HYPER_EDGE_TYPE);
        edgeJson.put("edge_Ident", sccheEdge.getID());
        edgeJson.put("edge_Data", sccheEdge.getData());
        edgeJson.put("tail", sccheEdge.getTailID());
        edgeJson.put("head", sccheEdge.getHeadID());
        edgeJson.put("vulnerability", sccheEdge.getVulnNodeID());
        edgeJson.put("inner_Tail", sccheEdge.getInnerTail());
        edgeJson.put("inner_Head", sccheEdge.getInnerHead());

        return edgeJson;
    }
}
