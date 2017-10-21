package utils;

import attackpaths.IAttackPath;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import graphmodels.graph.IHostNode;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;


/**
 * Created by Roberto Gaudenzi on 15/10/17.
 */
public class JacksonSubgraphUtils {

    public static Map<String, Set<IHostNode>> loadSubgraphs(String dataFolderPath, String filename, Map<String, IHostNode> nodesMap){
        Map<String, Set<IHostNode>> subgraphsMap = new HashMap<>();
        try {
            JsonFactory jsonFactory = new JsonFactory();
            JsonParser jp = jsonFactory.createParser(new File(dataFolderPath + filename));
            jp.setCodec(new ObjectMapper());
            JsonNode finalJson = jp.readValueAsTree();

            // Get the subgraphs
            JsonNode subgraphsListJson = finalJson.get("subgraphs");
            if(subgraphsListJson.isArray()) {
                for(JsonNode subgraphJson : subgraphsListJson){
                    String targetID = subgraphJson.get("target_Ident").asText();
                    Set<IHostNode> subgraph = loadSubgraph(subgraphJson, nodesMap);
                    subgraphsMap.put(targetID, subgraph);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            subgraphsMap = null;
        }

        return subgraphsMap;
    }

    public static void storeSubgraphs(String graphName, Map<String, Set<IHostNode>> subgraphs, String dataFolderPath, String filename){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode finalJson = mapper.createObjectNode();

        finalJson.put("attackGraph_Ident", graphName);

        // For each target, build up a Json representing its subgraph
        ArrayNode subgraphsListJson = mapper.createArrayNode();
        for(String targetID : subgraphs.keySet()){
            Set<IHostNode> subgraph = subgraphs.get(targetID);
            ObjectNode subgraphJson = storeSubgraph(mapper, subgraph, targetID);
            subgraphsListJson.add(subgraphJson);
        }
        finalJson.putPOJO("subgraphs", subgraphsListJson);

        // Save Json to file
        try{
            String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(finalJson);
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

    /** METHODS FOR LOADING/STORING SINGLE SUBGRAPH **/
    static Set<IHostNode> loadSubgraph(JsonNode subgraphJson, Map<String, IHostNode> nodesMap){
        Set<IHostNode> subgraph = new HashSet<>();
        JsonNode nodesListJson = subgraphJson.get("nodes_Ident_list");
        if(nodesListJson.isArray()){
            for(JsonNode nodeJson : nodesListJson){
                String nodeID = nodeJson.asText();
                IHostNode node = nodesMap.get(nodeID);
                if(node == null){
                    System.err.println("ERROR during loadSubgraph: nodeID = " +nodeID + " not existent! ");
                }
                else
                subgraph.add(node);
            }
        }

        return subgraph;
    }

    static ObjectNode storeSubgraph(ObjectMapper mapper, Set<IHostNode> subgraph, String targetID){
        ObjectNode subgraphJson = mapper.createObjectNode();
        subgraphJson.put("target_Ident", targetID);

        ArrayNode nodesListJson = mapper.createArrayNode();
        for(IHostNode node : subgraph){
            nodesListJson.add(node.getID());
        }
        subgraphJson.set("nodes_Ident_list", nodesListJson);
        return subgraphJson;
    }
}
