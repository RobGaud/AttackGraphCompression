package main;

import attackpaths.IAttackPath;
import graphmodels.hypergraph.IHyperGraph;
import likelihood.ComputeSL;
import utils.constants.LikelihoodConstants;
import utils.json.JacksonHAGUtils;
import utils.json.JacksonPathUtils;

import java.util.*;

import static utils.constants.FilesConstants.getDataHome;

/**
 * Created by Roberto Gaudenzi on 15/10/17.
 */
public class LikelihoodMain {

    private static Map<String, IAttackPath> pathsMap;

    public static void main(String[] args){
        int pathLength = LikelihoodConstants.MAX_PATH_LENGTHS[1];
        String dataFolderPath = getDataHome();
        String attackGraphName = "HAG_attack_graph";
        String pathsFolderPath = dataFolderPath + attackGraphName + "_paths_" + pathLength + "/";

        String attackGraphFile = attackGraphName + ".json";
        String attackPathsFile = attackGraphName + "_paths_" + pathLength + "_0.json";

        //Load hypergraph
        IHyperGraph graph = JacksonHAGUtils.loadHAG(dataFolderPath, attackGraphFile);

        //Load the related pathsMap
        System.out.println("Calling loadPaths().");
        pathsMap = JacksonPathUtils.loadPaths(pathsFolderPath, attackPathsFile);

        Map<String, Collection<IAttackPath>> pathsByTarget = labelPathsByTarget(pathsMap.values());

        long startTime = System.currentTimeMillis();

        //For each target T, call ComputeSL and obtain the Success Likelihood for all the pathsMap that lead to T itself.
        for(String targetID : pathsByTarget.keySet()){

            Map<String, Double> pathsSL = ComputeSL.execute(graph, pathsByTarget.get(targetID), graph.getNode(targetID));

            //Store the SL value into the associated path
            setSL(pathsSL);
        }

        long endTime = System.currentTimeMillis();
        double execTime = (0.0+endTime-startTime)/1000;
        System.out.println("Exec time = " + execTime + " seconds.");

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
