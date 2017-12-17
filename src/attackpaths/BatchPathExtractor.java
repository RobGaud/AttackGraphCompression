package attackpaths;

import graphmodels.graph.IGraph;
import utils.constants.LikelihoodConstants;
import utils.json.JacksonPathUtils;

import java.io.File;
import java.util.*;

/**
 * Created by Roberto Gaudenzi on 12/11/17.
 */
public class BatchPathExtractor {

    private IGraph graph;
    private Map<String, IAttackPath> pathsMap;
    private String dataFolderPath;
    private String fileNamesRoot;
    private int maxInnerLength;

    private Collection<IAttackPath> extractedPaths;
    private int currentBatchID;

    public BatchPathExtractor(IGraph graph, Map<String, IAttackPath> pathsMap, int maxInnerLength, String dataFolderPath, String fileNamesRoot){
        this.graph = graph;
        this.pathsMap = pathsMap;
        this.maxInnerLength = maxInnerLength;

        this.dataFolderPath = dataFolderPath;
        this.fileNamesRoot = fileNamesRoot + "_E-" + maxInnerLength;

        this.extractedPaths = new LinkedList<>();
        this.currentBatchID = 0;
    }

    public void extractAndStore(){

        for(String pathID : pathsMap.keySet()){

            List<IAttackPath> resultList = AttackPathExtractor.extractPaths(graph, pathsMap.get(pathID), maxInnerLength);
            System.out.println("BatchPathExtractor.extractAndStore: result.size() = " + resultList.size());

            for(IAttackPath ePath : resultList){
                extractedPaths.add(ePath);

                if(extractedPaths.size() == LikelihoodConstants.MAX_PATHS_PER_FILE) {
                    System.out.println("BatchPathExtractor.extractAndStore: storing batch number " + currentBatchID);
                    storeAndReset();
                }
            }
        }

        if(extractedPaths.size() > 0){
            //System.out.println("BatchPathExtractor: size = " + extractedPaths.size());
            storeAndReset();
        }
    }

    private void storeAndReset(){
        /* We need to:
         *  - Call the method in JacksonPathUtils to store the paths in a file;
         *  - increase the batchID;
         *  - flush the content of paths variable
         */

        // e.g., "C-HAG_attack_graph_paths_4_E-3"
        String filesFolderPath = dataFolderPath + fileNamesRoot + File.separator;
        if(this.currentBatchID == 0) {
            File folder = new File(filesFolderPath);
            folder.mkdirs();
        }

        String filename = fileNamesRoot + "_" + this.currentBatchID + ".json";

        System.out.println("BatchPathExtractor.storeAndReset: fileFolder = " + filesFolderPath + ", filename = " + filename + ".");
        JacksonPathUtils.storePaths(graph.getData(), extractedPaths, filesFolderPath, filename);

        this.extractedPaths = new LinkedList<>();
        this.currentBatchID++;
    }
}
