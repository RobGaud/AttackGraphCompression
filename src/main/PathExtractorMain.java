package main;

import attackpaths.BatchPathExtractor;
import attackpaths.IAttackPath;
import graphmodels.hypergraph.IHyperGraph;
import utils.Constants;
import utils.JacksonHAGUtils;
import utils.JacksonPathUtils;

import java.io.File;
import java.util.Map;

/**
 * Created by Roberto Gaudenzi on 09/11/17.
 */
public class PathExtractorMain {
    public static void main(String[] args){
        String dataFolderPath = Constants.getDataHome();

        String attackGraphName = "C-HAG_attack_graph";

        int maxPathLength = Constants.MAX_PATH_LENGTHS[0];
        int maxInnerLength = Constants.MAX_INNER_PATH_LENGTHS[0]; // {2, 3, 4}

        String attackGraphFile = attackGraphName + ".json";
        String attackPathsFileRoot = dataFolderPath + attackGraphName + "_paths_" + maxPathLength + File.separator;

        //Load hypergraph
        IHyperGraph graph = JacksonHAGUtils.loadHAG(dataFolderPath, attackGraphFile);

        File folder = new File(attackPathsFileRoot);
        File[] listOfFiles = folder.listFiles();

        if(listOfFiles == null){
            System.err.println("ERROR: no files to list! foldername = " + attackPathsFileRoot);
            return;
        }

        for(File file : listOfFiles){
            String fileName = file.getName();
            String fileNameRoot = fileName.substring(0, fileName.indexOf('.'));

            Map<String, IAttackPath> pathsMap = JacksonPathUtils.loadPaths(attackPathsFileRoot, fileName);

            BatchPathExtractor extractor = new BatchPathExtractor(graph, pathsMap, maxInnerLength, dataFolderPath, fileNameRoot);
            extractor.extractAndStore();
        }
    }
}
