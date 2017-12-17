package main;

import graphmodels.graph.IHostNode;
import graphmodels.hypergraph.IHyperGraph;
import utils.constants.LikelihoodConstants;
import utils.json.HAGConversionUtils;
import utils.json.JacksonHAGUtils;

import static utils.constants.FilesConstants.getDataHome;

public class HAGBuildingMain {

    public static void main(String[] args){
        String dataFolderPath = getDataHome();
        String GRAPH_JSON_NAME = "attack_graph.json";

        String hag_filename = HAGConversionUtils.convertJson(dataFolderPath, GRAPH_JSON_NAME);

        IHyperGraph hyperGraph = JacksonHAGUtils.loadHAG(dataFolderPath, hag_filename);
        JacksonHAGUtils.storeHAG(hyperGraph, dataFolderPath, hag_filename);
    }

    private static void printHAG(IHyperGraph hyperGraph){
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
