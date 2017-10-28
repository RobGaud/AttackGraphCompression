package main;

import attackpaths.AttackPathComputation;
import attackpaths.IAttackPath;
import graphmodels.hypergraph.IHyperGraph;
import utils.Constants;
import utils.JacksonHAGUtils;
import utils.JacksonPathUtils;

import java.util.Collection;

/**
 * Created by Roberto Gaudenzi on 15/10/17.
 */
public class AttackPathsMain {
    public static void main(String[] args){
        String dataFolderPath = Constants.getDataHome();

        String attackGraphName = "HAG_attack_graph";

        String attackGraphFile = attackGraphName + ".json";
        String attackPathsFile = attackGraphName + "_paths_" + Constants.MAX_PATH_LENGTH + ".json";

        //Load hypergraph
        IHyperGraph graph = JacksonHAGUtils.loadCompressedHAG(dataFolderPath, attackGraphFile);

        //Compute paths
        AttackPathComputation pathComputer = new AttackPathComputation(graph, Constants.MAX_PATH_LENGTH);
        Collection<IAttackPath> paths = pathComputer.computePaths();

        //Store them in a json file
        System.out.println("# Storing attack paths in file " + attackGraphFile);
        JacksonPathUtils.storePaths(attackGraphName, paths, dataFolderPath, attackPathsFile);
    }
}
