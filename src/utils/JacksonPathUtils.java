package utils;

import attackpaths.AttackPath;
import attackpaths.IAttackPath;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import graphmodels.graph.AttackEdge;
import graphmodels.graph.Edge;
import graphmodels.graph.IAttackEdge;
import graphmodels.graph.IEdge;
import graphmodels.graph.sccmodels.ISCCAttackEdge;
import graphmodels.hypergraph.HyperEdge;
import graphmodels.hypergraph.IHyperEdge;
import graphmodels.hypergraph.sccmodels.ISCCHyperEdge;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

import static utils.Constants.*;

/**
 * Created by Roberto Gaudenzi on 23/09/17.
 */
public class JacksonPathUtils {

    public static Map<String, IAttackPath> loadPaths(String dataFolderPath, String filename){
        Map<String, IAttackPath> paths = new HashMap<>();
        try {
            JsonFactory jsonFactory = new JsonFactory();
            JsonParser jp = jsonFactory.createParser(new File(dataFolderPath + filename));
            jp.setCodec(new ObjectMapper());
            JsonNode pathsJson = jp.readValueAsTree();

            // Get the edges
            JsonNode edgeListJson = pathsJson.get("edges");
            Map<String, IEdge> edgesMap = new HashMap<>();
            if(edgeListJson.isArray()) {
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

                    if(edge != null)
                        edgesMap.put(edge.getID(), edge);
                }
            }

            // Get the paths
            JsonNode pathListJson = pathsJson.get("paths");
            if(pathListJson.isArray()) {
                for(JsonNode pathJson : pathListJson){
                    IAttackPath path = loadAttackPath(pathJson, edgesMap);
                    paths.put(path.getID(), path);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return paths;
    }

    public static void storePaths(String graphName, Collection<IAttackPath> paths, String dataFolderPath, String filename){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode finalJson = mapper.createObjectNode();

        finalJson.put("attackGraph_Ident", graphName);

        ArrayNode pathsListJson = mapper.createArrayNode();
        Set<IEdge> edgeSet = new HashSet<>();
        for(IAttackPath path : paths){
            ObjectNode pathJson = storeAttackPath(mapper, path);
            edgeSet.addAll(getEdgesFromPath(path));
            pathsListJson.add(pathJson);
        }
        finalJson.putPOJO("paths", pathsListJson);


        ArrayNode edgeListJson = mapper.createArrayNode();
        for(IEdge edge : edgeSet){
            ObjectNode edgeJson;
            if(ISCCAttackEdge.isSCCAttackEdge(edge)){
                edgeJson = JacksonEdgeUtils.storeSCCAttackEdge(mapper, (ISCCAttackEdge)edge);
            }
            else if(ISCCHyperEdge.isSCCHyperEdge(edge)){
                edgeJson = JacksonEdgeUtils.storeHyperEdge(mapper, (ISCCHyperEdge)edge);
            }
            else if(IAttackEdge.isAttackEdge(edge)){
                edgeJson = JacksonEdgeUtils.storeAttackEdge(mapper, (IAttackEdge)edge);
            }
            else{
                edgeJson = JacksonEdgeUtils.storeHyperEdge(mapper, (IHyperEdge)edge);
            }

            edgeListJson.add(edgeJson);
        }
        finalJson.putPOJO("edges", edgeListJson);

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

    /** METHOD FOR LOADING SINGLE PATH **/
    static IAttackPath loadAttackPath(JsonNode pathJson, Map<String, IEdge> edgesMap){
        String pathID = pathJson.get("path_Ident").asText();
        float likelihood = (float)pathJson.get("likelihood").asDouble();
        IAttackPath path = new AttackPath(pathID, likelihood);

        JsonNode pathEdgesJson = pathJson.get("path_Edges");
        if(pathEdgesJson.isArray()){
            for(JsonNode edgeJson : pathEdgesJson){
                int rank = Integer.parseInt(edgeJson.get("rank").asText());

                String edgeID = edgeJson.get("edge_Ident").asText();
                IEdge pathEdge = edgesMap.get(edgeID);
                path.addEdge(rank, pathEdge);
            }
        }

        return path;
    }

    /** METHOD FOR STORING SINGLE PATH **/
    static ObjectNode storeAttackPath(ObjectMapper mapper, IAttackPath path) {
        ObjectNode pathJson = mapper.createObjectNode();
        pathJson.put("path_Ident", path.getID());
        pathJson.put("likelihood", path.getLikelihood());

        ArrayNode edgeListJson = mapper.createArrayNode();
        for(Map.Entry<Integer, IEdge> entry : path.getEdges().entrySet()){
            int rank = entry.getKey();
            IEdge edge = entry.getValue();

            ObjectNode edgeJson = mapper.createObjectNode();
            edgeJson.put("rank", rank);
            edgeJson.put("edgeID", edge.getID());

            edgeListJson.add(edgeJson);
        }
        pathJson.putPOJO("path_edges", edgeListJson);

        return pathJson;
    }

    static Collection<IEdge> getEdgesFromPath(IAttackPath path){
        return path.getEdges().values();
    }
}
