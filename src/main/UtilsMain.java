package main;

import graphmodels.graph.IHostNode;
import graphmodels.hypergraph.HyperGraph;
import utils.Constants;
import utils.HAGConversionUtils;
import utils.HAGJacksonUtils;

public class UtilsMain {

    public static void main(String[] args){
        String dataFolderPath = Constants.getDataHome();
        String GRAPH_JSON_NAME = "attack_graph.json";

        String hag_filename = HAGConversionUtils.convertJson(dataFolderPath, GRAPH_JSON_NAME);
        //String hag_filename = "HAG_62ceab57-afe9-4ee7-9a4c-1f03b2767120.json";

        HyperGraph hyperGraph = HAGJacksonUtils.loadHAG(dataFolderPath, hag_filename);
        //printHAG(hyperGraph);
        // HyperGraph compressed_hag = GraphCompression.compress(hyperGraph);
        HAGJacksonUtils.saveHAG(hyperGraph, dataFolderPath, hag_filename);
    }

    private static void printHAG(HyperGraph hyperGraph){
        System.out.println("HyperGraph data = " + hyperGraph.getData());

        System.out.println("HyperGraph entry points = ");
        for(IHostNode ep : hyperGraph.getEntryPoints()){
            System.out.println("    Entry point ID   = " + ep.getID());
            System.out.println("    Entry point data = " + ep.getData());
        }
        System.out.println("__________________________________________________");

        System.out.println("HyperGraph targets = ");
        for(IHostNode t : hyperGraph.getTargets()){
            System.out.println("    Target ID   = " + t.getID());
            System.out.println("    Target data = " + t.getData());
        }
        System.out.println("__________________________________________________");
    }
}
