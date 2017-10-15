package main;

import attackpaths.IAttackPath;
import graphmodels.graph.IHostNode;
import graphmodels.hypergraph.IHyperGraph;
import likelihood.ComputeSL;
import utils.Constants;
import utils.JacksonHAGUtils;
import utils.JacksonPathUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Roberto Gaudenzi on 15/10/17.
 */
public class LikelihoodMain {

    private static Map<String, IAttackPath> paths;

    public static void main(String[] args){
        String dataFolderPath = Constants.getDataHome();
        String attackGraphName = "HAG_attack_graph";

        String attackGraphFile = attackGraphName + ".json";
        String attackPathsFile = attackGraphName + "_paths.json";

        //Load hypergraph
        IHyperGraph graph = JacksonHAGUtils.loadCompressedHAG(dataFolderPath, attackGraphFile);

        //Load the related paths
        paths = JacksonPathUtils.loadPaths(dataFolderPath, attackPathsFile);

        //TODO Load the subgraphs associated with each target node
        Map<String, Set<IHostNode>> subgraphsMap = null;

        Map<String, Set<IAttackPath>> pathsMap = filterPathsByTarget(paths.values());
        //For each target T, call ComputeSL and obtain the Success Likelihood for all the paths that lead to T itself.
        for(String targetID : pathsMap.keySet()){
            Map<String, Float> pathsSL = ComputeSL.execute(graph, subgraphsMap.get(targetID), pathsMap.get(targetID), graph.getNode(targetID));

            //Store the SL value into the associated path
            setSL(pathsSL);
        }
    }

    private static void setSL(Map<String, Float> pathsSL){
        for(String pathID: pathsSL.keySet()){
            paths.get(pathID).setLikelihood(pathsSL.get(pathID));
        }
    }

    private static Map<String, Set<IAttackPath>> filterPathsByTarget(Collection<IAttackPath> paths){
        Map<String, Set<IAttackPath>> pathsMap = new HashMap<>();

        //TODO For each path, look at the Head of the last edge: this is the target.

        return pathsMap;
    }
}
