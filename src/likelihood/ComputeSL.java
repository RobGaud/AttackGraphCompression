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

        Map<String, Double> mtaoMap = ComputePathsMTAO.execute(graph, subgraph, paths, targetNode);

        double mtaoMin = getMTAOMin(mtaoMap.values());

        Map<String, Double> slMap = new HashMap<>();

        int infCount = 0;
        for(String pathID : mtaoMap.keySet()){
            double pathMTAO = mtaoMap.get(pathID);
            double sl = computeSL(pathMTAO, mtaoMin);
            if(Double.isInfinite(sl))
                infCount++;
            slMap.put(pathID, sl);
        }

        System.out.println("InfCount = " + infCount);

        return slMap;
    }

    private static double computeSL(double pathMTAO, double mtaoMin){
        double sl = -20 * Math.log10((pathMTAO - mtaoMin)/pathMTAO);
        return sl;
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
