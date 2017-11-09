package attackpaths;

import graphmodels.graph.IEdge;
import graphmodels.graph.IGraph;
import graphmodels.graph.IHostNode;
import utils.Constants;
import utils.JacksonPathUtils;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Roberto Gaudenzi on 01/11/17.
 */
public class BatchPathComputation{

    private IGraph graph;
    private Collection<IAttackPath> paths;
    private Map<String, IEdge> edges;
    private LinkedList<IEdge> currentPath;
    private int MAX_PATH_LENGTH;

    private int nextPathID;

    // New approach for high path lengths: store them in batches during the computation,
    // in order to keep memory usage as low as possible.
    private int currentBatchID;
    private String dataFolderPath, fileNameRoot;

    public BatchPathComputation(IGraph graph, int maxPathLength, String dataFolderPath, String fileNameRoot){
        this.graph = graph;
        this.paths = new LinkedList<>();
        this.edges = new HashMap<>();
        this.nextPathID = 0;
        this.MAX_PATH_LENGTH = maxPathLength;

        this.currentBatchID = 0;
        this.dataFolderPath = dataFolderPath;
        this.fileNameRoot = fileNameRoot;
    }

    public void computeAndStorePaths() {

        for(IHostNode entryPoint : graph.getEntryPoints()){
            System.out.println("##### START COMPUTING PATHS FROM ENTRY POINT: " + entryPoint.getID());
            this.currentPath = new LinkedList<>();
            // Inserting a dummy edge
            computePathsFrom(entryPoint, 0);
        }

        if(paths.size() > 0){
            storeAndReset();
        }
    }

    private void computePathsFrom(IHostNode node, int currentPathLength){
        // If we reached a target node, we need to store the path.
        if(this.graph.isTarget(node.getID())){
            storePath();

            // NEW: if we filled up the current batch, store it in a file and flush the collection of paths.
            if(paths.size() == Constants.MAX_PATHS_PER_FILE){
                storeAndReset();
            }
        }
        // NOTE: we can also have that a target node is an intermediate node in a longer path.
        if(currentPathLength < this.MAX_PATH_LENGTH) {
            for (IEdge edge : node.getOutboundEdges()) {
                IHostNode neighbor = graph.getNode(edge.getHeadID());

                if(this.checkIfNewNode(neighbor)){
                    this.currentPath.addLast(edge);
                    this.computePathsFrom(neighbor, currentPathLength + 1);
                }
            }
        }
        // When we exit from the for cycle, all the paths computable from node have been computed.
        // Hence, we can remove it from the current path.
        if(this.currentPath.size() > 0)
            this.currentPath.removeLast();
    }

    private boolean checkIfNewNode(IHostNode node){
        //Check whether the path is still empty or not
        if(this.currentPath.size() == 0)
            return true;

        //Check whether it is the tail of the first edge
        if(this.currentPath.get(0).getTailID().equals(node.getID()))
            return false;

        //Check whether it is the head of any edge
        for(IEdge edge : this.currentPath){
            if(edge.getHeadID().equals(node.getID())){
                return false;
            }
        }

        return true;
    }

    private void storePath(){
        IAttackPath newPath = new AttackPath(getNextID());

        for(IEdge edge : this.currentPath){
            newPath.addEdge(edge);

            // Add its edges to the map to store them later
            if(!this.edges.containsKey(edge.getID())){
                this.edges.put(edge.getID(), edge);
            }
        }

        this.paths.add(newPath);
    }

    private String getNextID(){
        String pathID = graph.getData() + " - path #" + this.nextPathID;
        this.nextPathID++;
        return pathID;
    }

    private void storeAndReset(){
        /* We need to:
         *  - Call the method in JacksonPathUtils to store the paths in a file;
         *  - increase the batchID;
         *  - flush the content of paths variable
         */

        String filesFolderPath = dataFolderPath + fileNameRoot + File.separator;
        if(this.currentBatchID == 0) {
            File folder = new File(filesFolderPath);
            folder.mkdirs();
        }
        String filename = fileNameRoot + "_" + this.currentBatchID + ".json";

        JacksonPathUtils.storePaths(graph.getData(), paths, filesFolderPath, filename);

        this.currentBatchID++;

        this.paths = new LinkedList<>();
    }
}
