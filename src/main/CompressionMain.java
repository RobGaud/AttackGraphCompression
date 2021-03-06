package main;

import compression.GraphCompression;
import compression.IGraphCompression;
import graphmodels.graph.IGraph;
import graphmodels.hypergraph.IHyperGraph;
import sccfinder.ISCCFinder;
import sccfinder.KosarajuSCCFinder;
import utils.constants.LikelihoodConstants;
import utils.json.JacksonHAGUtils;

import static utils.constants.CompressionConstants.MIN_SCC_SIZE;
import static utils.constants.FilesConstants.getDataHome;

/**
 * Created by Roberto Gaudenzi on 04/11/17.
 */
public class CompressionMain {

    public static void main(String[] args){
        System.out.println("CompressionMain.main: starting.");

        String dataFolderPath = getDataHome();

        String attackGraphFilename = "HAG_attack_graph.json";
        String filename = "C-HAG_attack_graph.json";

        IGraph hyperGraph = JacksonHAGUtils.loadHAG(dataFolderPath, attackGraphFilename);
        System.out.println("This graph contains " + hyperGraph.getHostNodes().keySet().size() + " nodes.");

        ISCCFinder sccFinder = new KosarajuSCCFinder(hyperGraph);
        IGraphCompression graphCompressor = new GraphCompression(sccFinder, MIN_SCC_SIZE);

        long start = System.currentTimeMillis();
        IGraph newGraph = graphCompressor.compress();
        long stop = System.currentTimeMillis();
        double execTime = (stop - start + 0.0)/1000;

        System.out.println("Time required: " + execTime + " seconds.");

        System.out.println("Now the graph contains " + newGraph.getHostNodes().keySet().size() + " nodes.");

        if(IHyperGraph.isInstance(newGraph)){
            IHyperGraph newHyperGraph = (IHyperGraph)newGraph;
            JacksonHAGUtils.storeHAG(newHyperGraph, dataFolderPath, filename);
        }
    }
}
