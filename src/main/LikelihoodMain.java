package main;

import attackpaths.IAttackPath;
import graphmodels.graph.IHostNode;
import graphmodels.hypergraph.IHyperGraph;
import likelihood.ComputeSL;
import utils.Constants;
import utils.JacksonHAGUtils;
import utils.JacksonPathUtils;
import utils.JacksonSubgraphUtils;

import java.util.*;

/**
 * Created by Roberto Gaudenzi on 15/10/17.
 */
public class LikelihoodMain {

    private static Map<String, IAttackPath> pathsMap;

    public static void main(String[] args){
        int pathLength = Constants.MAX_PATH_LENGTHS[0];
        String dataFolderPath = Constants.getDataHome();
        String pathsFolderPath = dataFolderPath + "C-HAG_attack_graph_paths_" + pathLength + "/";

        String attackGraphName = "C-HAG_attack_graph";

        String attackGraphFile = attackGraphName + ".json";
        String attackPathsFile = attackGraphName + "_paths_" + pathLength + "_0.json";
        String subgraphsFile   = attackGraphName + "_subgraphs.json";

        //Load hypergraph
        IHyperGraph graph = JacksonHAGUtils.loadCompressedHAG(dataFolderPath, attackGraphFile);

        //Load the related pathsMap
        System.out.println("Calling loadPaths().");
        pathsMap = JacksonPathUtils.loadPaths(pathsFolderPath, attackPathsFile);

        System.out.println("Calling loadSubgraphs().");
        Map<String, Set<IHostNode>> subgraphsMap = JacksonSubgraphUtils.loadSubgraphs(dataFolderPath, subgraphsFile, graph.getHostNodes());

        Map<String, Collection<IAttackPath>> pathsByTarget = labelPathsByTarget(pathsMap.values());

        //For each target T, call ComputeSL and obtain the Success Likelihood for all the pathsMap that lead to T itself.
        for(String targetID : pathsByTarget.keySet()){

            Map<String, Double> pathsSL = ComputeSL.execute(graph, subgraphsMap.get(targetID), pathsByTarget.get(targetID), graph.getNode(targetID));

            //Store the SL value into the associated path
            setSL(pathsSL);
        }

        // Finally, store the pathsMap with the updated SL value in a file
        JacksonPathUtils.storePaths(attackGraphName, pathsMap.values(), dataFolderPath, attackPathsFile);
    }

    private static void setSL(Map<String, Double> pathsSL){
        for(String pathID: pathsSL.keySet()){
            pathsMap.get(pathID).setLikelihood(pathsSL.get(pathID));
        }
    }

    private static Map<String, Collection<IAttackPath>> labelPathsByTarget(Collection<IAttackPath> paths){
        Map<String, Collection<IAttackPath>> pathsMap = new HashMap<>();

        for(IAttackPath path : paths){
            String targetID = path.getTargetID();
            if(!pathsMap.keySet().contains(targetID)){
                pathsMap.put(targetID, new LinkedList<>());
            }
            pathsMap.get(path.getTargetID()).add(path);
        }

        return pathsMap;
    }
}
