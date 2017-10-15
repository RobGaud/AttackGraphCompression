package likelihood;

import attackpaths.IAttackPath;
import graphmodels.graph.IHostNode;
import graphmodels.hypergraph.IHyperGraph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Roberto Gaudenzi on 15/10/17.
 */
public class ComputeSL {

    public static Map<String, Float> execute(IHyperGraph graph, Set<IHostNode> subgraph, Set<IAttackPath> paths, IHostNode targetNode){

        Map<String, Float> mtaoMap = ComputePathsMTAO.execute(graph, subgraph, paths, targetNode);

        float mtaoMin = getMTAOMin((Float[])mtaoMap.values().toArray());

        Map<String, Float> slMap = new HashMap<>();

        for(String pathID : mtaoMap.keySet()){
            float pathMTAO = mtaoMap.get(pathID);
            slMap.put(pathID, computeSL(pathMTAO, mtaoMin));
        }

        return slMap;
    }

    private static float computeSL(float pathMTAO, float mtaoMin){
        return -20 * ((pathMTAO - mtaoMin)/pathMTAO);
    }

    private static float getMTAOMin(Float[] mtaoValues){
        float min = mtaoValues[0];
        for(float mtao : mtaoValues){
            if(mtao < min)
                min = mtao;
        }

        return min;
    }
}
