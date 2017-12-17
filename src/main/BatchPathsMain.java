package main;

import attackpaths.BatchPathComputation;
import graphmodels.hypergraph.IHyperGraph;
import utils.constants.LikelihoodConstants;
import utils.json.JacksonHAGUtils;

import static utils.constants.FilesConstants.getDataHome;

/**
 * Created by Roberto Gaudenzi on 15/10/17.
 */
public class BatchPathsMain {
    public static void main(String[] args){
        String dataFolderPath = getDataHome();

        String attackGraphName = "C-HAG_attack_graph";

        int maxPathLength = LikelihoodConstants.MAX_PATH_LENGTHS[0];

        String attackGraphFile = attackGraphName + ".json";
        String attackPathsFileRoot = attackGraphName + "_paths_" + maxPathLength;

        //Load hypergraph
        IHyperGraph graph = JacksonHAGUtils.loadHAG(dataFolderPath, attackGraphFile);

        BatchPathComputation pathComputer = new BatchPathComputation(graph, maxPathLength, dataFolderPath, attackPathsFileRoot);
        pathComputer.computeAndStorePaths();
    }
}
