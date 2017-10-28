package attackpaths;

import graphmodels.graph.IEdge;
import graphmodels.graph.IGraph;
import graphmodels.graph.IHostNode;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by Roberto Gaudenzi on 23/09/17.
 */
public class AttackPathComputation{
    // TODO use Dynamic Programming approach to keep the complexity low and reduce the computation of path already computed.

    private IGraph graph;
    private Collection<IAttackPath> paths;
    private LinkedList<IEdge> currentPath;
    private int MAX_PATH_LENGTH;

    private int nextPathID;

    public AttackPathComputation(IGraph graph, int maxPathLength){
        this.graph = graph;
        this.paths = new LinkedList<>();
        this.nextPathID = 0;
        this.MAX_PATH_LENGTH = maxPathLength;
    }

    public Collection<IAttackPath> computePaths() {

        for(IHostNode entryPoint : graph.getEntryPoints()){
            System.out.println("##### START COMPUTING PATHS FROM ENTRY POINT: " + entryPoint.getID());
            this.currentPath = new LinkedList<>();
            // Inserting a dummy edge
            computePathsFrom(entryPoint, 0);
        }

        return this.paths;
    }

    private void computePathsFrom(IHostNode node, int currentPathLength){
        // If we reached a target node, we need to store the path.
        if(this.graph.isTarget(node.getID())){
            storePath();
        }
        // NOTE: we can also have that a target node is an intermediate node in a longer path.
        if(currentPathLength < this.MAX_PATH_LENGTH) {
            for (IEdge edge : node.getOutboundEdges()) {
                IHostNode neighbor = graph.getNode(edge.getHeadID());

                if(this.checkIfNewNode(neighbor)){
                    this.currentPath.push(edge);
                    this.computePathsFrom(neighbor, currentPathLength + 1);
                }
            }
        }
        // When we exit from the for cycle, all the paths computable from node have been computed.
        // Hence, we can remove it from the current path.
        if(this.currentPath.size() > 0)
            this.currentPath.pop();
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
        }

        this.paths.add(newPath);
    }

    private String getNextID(){
        String pathID = graph.getData() + " - path #" + this.nextPathID;
        this.nextPathID++;
        return pathID;
    }
}
