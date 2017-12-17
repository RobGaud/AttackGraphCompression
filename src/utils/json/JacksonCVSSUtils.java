package utils.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static utils.constants.FilesConstants.*;
import static utils.constants.VulnerabilityConstants.CVSS_DEFAULT_VALUE;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Roberto Gaudenzi on 30/09/17.
 */
public class JacksonCVSSUtils {

    static Map<String, String> loadCVSSMap(String filename){
        String cveDataFolder = getCveDataHome();
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
                    String cvss = pairObject.get("cvss").asText();
                    cveMap.put(cve, cvss);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return cveMap;
    }

    /** METHODS TO BUILD AND STORE THE MAP IN A JSON FILE **/

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
        String cvss;
        try{
            cvss = cveJson.get("impact").get("baseMetricV2").get("cvssV2").get("baseScore").asText();
        }
        catch(NullPointerException e){
            System.out.println("-------------------------------------------");
            System.out.println("ERROR: CVSS NOT FOUND FOR CVE: " + cve + ". ASSIGNING DEFAULT VALUE.");
            System.out.println("-------------------------------------------");
            cvss = CVSS_DEFAULT_VALUE;
        }
        String[] cveData = new String[2];
        cveData[0] = cve;
        cveData[1] = cvss;
        return cveData;
    }

    public static void main(String[] args){
        String[] filenames = {"nvdcve-1.0-2002.json", "nvdcve-1.0-2003.json", "nvdcve-1.0-2004.json", "nvdcve-1.0-2005.json",
                              "nvdcve-1.0-2006.json", "nvdcve-1.0-2007.json", "nvdcve-1.0-2008.json", "nvdcve-1.0-2009.json"};

        String cveDataFolderPath = getIdeaHome() + PROJECT_HOME + DATA_HOME + CVE_DATA_HOME;
        String cveDataFilename = "cvss-data.json";

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode cvesJson = mapper.createObjectNode();

        ArrayNode cveListJson = mapper.createArrayNode();
        // For each file, call extractCVEDataFromFile
        for(String filename : filenames){
            // Add all the entries to the JsonNode
            Map<String, String> cveMap = extractCVEDataListFromFile(cveDataFolderPath, filename);
            for(Map.Entry<String, String> cve : cveMap.entrySet()){
                ObjectNode cveJson = mapper.createObjectNode();
                cveJson.put("cve", cve.getKey());
                cveJson.put("cvss", cve.getValue());
                cveListJson.add(cveJson);
            }

            // Add the array to the final json
            cvesJson.putPOJO("cve-cvss", cveListJson);
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
