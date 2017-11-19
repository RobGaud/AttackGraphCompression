package likelihood;

import attackpaths.IAttackPath;
import graphmodels.graph.IHostNode;
import graphmodels.hypergraph.IHyperGraph;

import java.util.*;

/**
 * Created by Roberto Gaudenzi on 15/10/17.
 */
public class ComputeSL {

    public static Map<String, Double> execute(IHyperGraph graph, Collection<IAttackPath> paths, IHostNode targetNode){

        Map<String, Double> mtaoMap = ComputePathsMTAO.execute(graph, paths, targetNode, false);
        Map<String, Double> mtaoMinMap = ComputePathsMTAO.execute(graph, paths, targetNode, true);

        Map<String, Double> slMap = new HashMap<>();

        for(String pathID : mtaoMap.keySet()){
            double pathMTAO = mtaoMap.get(pathID);
            double mtaoMin = mtaoMinMap.get(pathID);
            double sl = computeSL(pathMTAO, mtaoMin);
            slMap.put(pathID, sl);
        }

        /* TODO remove */
        double minSL = 1.0;
        double maxSL = 0.0;
        int tooHigh = 0;
        for(String pathID : slMap.keySet()){
            double pathSL = slMap.get(pathID);
            if(pathSL > maxSL && pathSL < 1.0)
                maxSL = pathSL;
            if(pathSL < minSL)
                minSL = pathSL;
            if(pathSL > 1.0)
                tooHigh++;
        }
        System.out.println("ComputeSL: minSL = " + minSL + ", maxSL = " + maxSL);
        System.out.println("ComputeSL: tooHigh = " + tooHigh);


        return slMap;
    }

    private static double computeSL(double pathMTAO, double mtaoMin){
        return -20 * Math.log((pathMTAO - mtaoMin)/pathMTAO);
    }
}
