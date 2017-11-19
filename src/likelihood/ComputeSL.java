package likelihood;

import attackpaths.IAttackPath;
import graphmodels.graph.IHostNode;
import graphmodels.hypergraph.IHyperGraph;

import java.util.*;

/**
 * Created by Roberto Gaudenzi on 15/10/17.
 */
public class ComputeSL {

    public static Map<String, Double> execute(IHyperGraph graph, Set<IHostNode> subgraph, Collection<IAttackPath> paths, IHostNode targetNode){

        Map<String, Double> mtaoMap = ComputePathsMTAO.execute(graph, subgraph, paths, targetNode, false);
        Map<String, Double> mtaoMinMap = ComputePathsMTAO.execute(graph, subgraph, paths, targetNode, true);

        /* TODO remove
        for(String id : mtaoMap.keySet()){
            System.out.println("------------------------------------------------------------------------");
            System.out.println("Mtao    of path + " + id + " = " + mtaoMap.get(id));
            System.out.println("MtaoMin of path + " + id + " = " + mtaoMinMap.get(id));
        }
        */

        //double mtaoMin = getMTAOMin(mtaoMap.values());

        Map<String, Double> slMap = new HashMap<>();

        int wrongCount = 0;
        for(String pathID : mtaoMap.keySet()){
            double pathMTAO = mtaoMap.get(pathID);
            double mtaoMin = mtaoMinMap.get(pathID);
            double sl = computeSL(pathMTAO, mtaoMin);
            if(Double.isInfinite(sl) || Double.isNaN(sl))
                wrongCount++;
            slMap.put(pathID, sl);
        }

        System.out.println("InfCount = " + wrongCount);

        return slMap;
    }

    private static double computeSL(double pathMTAO, double mtaoMin){
        return -20 * Math.log10((pathMTAO - mtaoMin)/pathMTAO);
    }

    private static double getMTAOMin(Collection<Double> mtaoValues){
        Iterator<Double> it = mtaoValues.iterator();

        double min = it.next();
        while(it.hasNext()){
            double mtao = it.next();
            if(mtao < min)
                min = mtao;
        }

        System.out.println("ComputeSL: minMtao = " + min);
        return min;
    }
}
