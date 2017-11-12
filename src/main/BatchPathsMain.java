package main;

import attackpaths.BatchPathComputation;
import graphmodels.hypergraph.IHyperGraph;
import utils.Constants;
import utils.JacksonHAGUtils;

/**
 * Created by Roberto Gaudenzi on 15/10/17.
 */
public class BatchPathsMain {
    public static void main(String[] args){
        String dataFolderPath = Constants.getDataHome();

        String attackGraphName = "C-HAG_attack_graph";

        int maxPathLength = Constants.MAX_PATH_LENGTHS[1];

        String attackGraphFile = attackGraphName + ".json";
        String attackPathsFileRoot = attackGraphName + "_paths_" + maxPathLength;

        //Load hypergraph
        IHyperGraph graph = JacksonHAGUtils.loadCompressedHAG(dataFolderPath, attackGraphFile);

        BatchPathComputation pathComputer = new BatchPathComputation(graph, maxPathLength, dataFolderPath, attackPathsFileRoot);
        pathComputer.computeAndStorePaths();
    }
}
