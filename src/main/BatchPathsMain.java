package main;

import attackpaths.AttackPathComputation;
import attackpaths.BatchPathComputation;
import attackpaths.IAttackPath;
import graphmodels.hypergraph.IHyperGraph;
import utils.Constants;
import utils.JacksonHAGUtils;
import utils.JacksonPathUtils;

import java.io.File;
import java.util.Collection;

/**
 * Created by Roberto Gaudenzi on 15/10/17.
 */
public class BatchPathsMain {
    public static void main(String[] args){
        String dataFolderPath = Constants.getDataHome();

        String attackGraphName = "HAG_attack_graph";

        int maxPathLength = Constants.MAX_PATH_LENGTHS[2];

        String attackGraphFile = attackGraphName + ".json";
        String attackPathsFileRoot = attackGraphName + "_paths_" + maxPathLength;
        String attackPathsFile = attackPathsFileRoot + ".json";

        //Load hypergraph
        IHyperGraph graph = JacksonHAGUtils.loadCompressedHAG(dataFolderPath, attackGraphFile);

        BatchPathComputation pathComputer = new BatchPathComputation(graph, maxPathLength, dataFolderPath, attackPathsFileRoot);
        pathComputer.computeAndStorePaths();
    }
}
