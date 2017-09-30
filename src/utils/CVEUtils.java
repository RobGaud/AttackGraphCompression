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

/**
 * Created by Roberto Gaudenzi on 30/09/17.
 */
public class CVEUtils {

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
        String cvss = cveJson.get("impact").get("baseMetricV2").get("cvssV2").get("baseScore").asText();
        String[] cveData = new String[2];
        cveData[0] = cve;
        cveData[1] = cvss;
        return cveData;
    }

    public static void main(String[] args){
        String[] filenames = {"cve-2001", "cve-2003", "cve-2004", "cve-2005",
                              "cve-2006", "cve-2007", "cve-2008", "cve-2009"};
        String dataFolderPath = "/home/roberto/ProgrammingProjects/IdeaProjects/cvedata";
        String cveDataFilename = "cve-data";

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode cvesJson = mapper.createObjectNode();

        // For each file, call extractCVEDataFromFile
        for(String filename : filenames){
            ArrayNode cveListJson = mapper.createArrayNode();
            Map<String, String> cveMap = extractCVEDataListFromFile(dataFolderPath, filename);
            // Add all the entries to the JsonNode
            for(Map.Entry<String, String> cve : cveMap.entrySet()){
                ObjectNode cveJson = mapper.createObjectNode();
                cveJson.put(cve.getKey(), cve.getValue());
                cveListJson.add(cveJson);
            }

            // Add the array to the final json
            cvesJson.putPOJO("filename", cveListJson);
        }

        // Store the Json in a file
        try{
            String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(cvesJson);
            PrintWriter out = new PrintWriter(dataFolderPath + cveDataFilename);
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
