package utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static utils.Constants.*;

/**
 * Created by Roberto Gaudenzi on 08/10/17.
 */
public class JacksonACUtils {

    private static Map<String, String> extractCVEDataListFromFile(String dataFolderPath, String filename){
        // Create an empty map
        Map<String, String> cveData = new HashMap<>();

        // Get the Json from the file
        try{
            JsonFactory jsonFactory = new JsonFactory();
            JsonParser jp = jsonFactory.createParser(new File(dataFolderPath + filename));
            jp.setCodec(new ObjectMapper());
            JsonNode cvesJson = jp.readValueAsTree();

            // Extract the JsonArray from the JsonObject
            JsonNode cvesArray = cvesJson.get("CVE_Items");
            if(cvesArray.isArray()){
                // for each object in it, extract the CVE code and the CVSS score and add them to the map
                for(JsonNode cveJson : cvesArray){
                    String[] cveEntry = extractCVEDataFromJson(cveJson);
                    cveData.put(cveEntry[0], cveEntry[1]);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        // return the map
        return cveData;
    }

    private static String[] extractCVEDataFromJson(JsonNode cveJson){
        String cve = cveJson.get("cve").get("CVE_data_meta").get("ID").asText();
        String complexity;
        try{
            complexity = cveJson.get("impact").get("baseMetricV2").get("cvssV2").get("accessComplexity").asText();
        }
        catch(NullPointerException e){
            System.out.println("-------------------------------------------");
            System.out.println("ERROR: COMPLEXITY NOT FOUND FOR CVE: " + cve + ". ASSIGNING DEFAULT VALUE.");
            System.out.println("-------------------------------------------");
            complexity = COMPLEXITY_DEFAULT_VALUE;
        }
        String[] cveData = new String[2];
        cveData[0] = cve;
        cveData[1] = complexity;
        return cveData;
    }

    public static Map<String, String> loadCVEJson(String filename){
        String cveDataFolder = Constants.getCveDataHome();
        Map<String, String> cveMap = new HashMap<>();
        try{
            JsonFactory jsonFactory = new JsonFactory();
            JsonParser jp = jsonFactory.createParser(new File(cveDataFolder + filename));
            jp.setCodec(new ObjectMapper());
            JsonNode json = jp.readValueAsTree();

            JsonNode cveListObject = json.get("cve-cvss");
            if(cveListObject.isArray()){
                for(JsonNode pairObject : cveListObject){
                    String cve = pairObject.get("cve").asText();
                    String complexity = pairObject.get("complexity").asText();
                    cveMap.put(cve, complexity);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return cveMap;
    }

    public static void main(String[] args){
        //String[] filenames = {"cve-2001.json", "cve-2003.json", "cve-2004.json", "cve-2005.json",
        //                      "cve-2006.json", "cve-2007.json", "cve-2008.json", "cve-2009.json"};
        String[] filenames = {"cve-2004.json"};

        String cveDataFolderPath = Constants.getIdeaHome() + PROJECT_HOME + DATA_HOME + CVE_DATA_HOME;
        String cveDataFilename = "access-complexity-data.json";

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode cvesJson = mapper.createObjectNode();

        // For each file, call extractCVEDataFromFile
        for(String filename : filenames){
            ArrayNode cveListJson = mapper.createArrayNode();
            Map<String, String> cveMap = extractCVEDataListFromFile(cveDataFolderPath, filename);
            // Add all the entries to the JsonNode
            for(Map.Entry<String, String> cve : cveMap.entrySet()){
                ObjectNode cveJson = mapper.createObjectNode();
                cveJson.put("cve", cve.getKey());
                cveJson.put("complexity", cve.getValue());
                cveListJson.add(cveJson);
            }

            // Add the array to the final json
            cvesJson.putPOJO("cve-complexity", cveListJson);
        }

        // Store the Json in a file
        try{
            String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(cvesJson);
            PrintWriter out = new PrintWriter(cveDataFolderPath + cveDataFilename);
            out.print(jsonString);
            out.close();
            System.out.println("Successfully copied JSON Object to File named \""+cveDataFilename+"\".");
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("An error occurred while copying JSON Object to File named \""+cveDataFilename+"\".");
        }
    }
}
