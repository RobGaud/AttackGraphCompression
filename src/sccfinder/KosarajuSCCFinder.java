package sccfinder;

import graphmodels.graph.IEdge;
import graphmodels.graph.IGraph;
import graphmodels.graph.IHostNode;
import graphmodels.graph.sccmodels.ISCCNode;

import java.util.*;

/**
 * Created by Roberto Gaudenzi on 10/09/17.
 * This class implements the interface scc.ISCCFinder and therefore aims to find SCC component within a given graph.
 * This is done by performing two different DFS: a classical one, and a DFS using reversed edges.
 * (Edges are not actually reversed during the second DFS, but it moves between nodes using outEdges instead of InEdges.)
 */
public class KosarajuSCCFinder implements ISCCFinder {

    private IGraph graph;

    private Set<String> nodesInPath;
    private Stack<String> visitOrder;
    private Map<String, String> parentsMap;

    public KosarajuSCCFinder(IGraph graph){
        this.graph = graph;
        this.visitOrder = new Stack<>();
        this.parentsMap = new HashMap<>();
    }

    public IGraph getGraph(){ return this.graph; }

    @Override
    public Map<String, Collection<IHostNode>> findSCCs(IHostNode startPoint) {
        reset();

        String spID = startPoint.getID();

        // We don't want to include entry points and target in the clustering process.
        if(graph.isEntryPoint(spID) || graph.isTarget(spID) || ISCCNode.isSCCNode(startPoint)){
            return null;
        }

        // Perform the first DFS required by the Kosaraju's algorithm.
        firstDfs(startPoint);

        // Perform the second DFS required by the Kosaraju's algorithm.
        secondDfs();

        // Build lists of SCCs and return it.
        return getSCCs();
    }

    private void firstDfs(IHostNode startPoint){
        this.nodesInPath = new HashSet<>();
        firstDfsAux(startPoint);
    }

    private void firstDfsAux(IHostNode nodeToVisit){
        String nodeID = nodeToVisit.getID();

        if(visitOrder.contains(nodeID) || nodesInPath.contains(nodeID))
            return;

        this.nodesInPath.add(nodeID);

        // Iterate on out-neighbors
        for(IEdge outEdge : nodeToVisit.getOutboundEdges()){
            String nextNodeID = outEdge.getHeadID();

            if(this.graph.getNode(nextNodeID) != null){
                // Again, we don't want to include entry points and target in the clustering process,
                // and we don't want to visit again already closed nodes either.
                if(!graph.isEntryPoint(nextNodeID) && !graph.isTarget(nextNodeID) && !visitOrder.contains(nextNodeID)){
                    firstDfsAux(nodeToVisit);
                }
            }
        }

        // Once the DFS rooted in nodeToVisit is ended, we mark it as visited.
        if(!visitOrder.contains(nodeID))
            visitOrder.push(nodeID);

        // We also need to remove it from the list of nodes in the current path.
        nodesInPath.remove(nodeID);
    }

    private void secondDfs(){
        while(!this.visitOrder.isEmpty()){
            String currentNodeID = this.visitOrder.pop();

            if(!this.parentsMap.containsKey(currentNodeID) && this.graph.getNode(currentNodeID) != null)
                secondDfsAux(currentNodeID, currentNodeID);

        }
    }

    private void secondDfsAux(String nodeID, String root){
        this.parentsMap.put(nodeID, root);
        // Iterate on in-neighbors
        for(IEdge inEdge : graph.getNode(nodeID).getInboundEdges()){
            String inNeighborID = inEdge.getTailID();

            IHostNode inNeighbor = graph.getNode(inNeighborID);

            if(isCompressible(inNeighbor)){
                secondDfsAux(inNeighborID, root);
            }
        }
    }

    private boolean isCompressible(IHostNode node){
        /* We don't want to add nodes that are either:
         * 1) entry points in the attack graph;
         * 2) target nodes in the attack graph;
         * 3) SCCNodes;
         */
        //TODO find a better solution
        if(node == null){
            System.out.println("KosarajuSCCFinder.isCompressible: trying to walk on a removed node during second Dfs.");
            return false;
        }

        boolean alreadyInSCC = parentsMap.containsKey(node.getID());
        boolean isEntryPoint = graph.isEntryPoint(node.getID());
        boolean isTarget = graph.isTarget(node.getID());

        boolean isSCCNode = ISCCNode.isSCCNode(node);
        return !alreadyInSCC && !isEntryPoint && !isTarget && !isSCCNode;
    }

    private Map<String, Collection<IHostNode>> getSCCs(){
        Map<String, Collection<IHostNode>> sccMap = new HashMap<>();
        for(String nodeID : this.parentsMap.keySet()){
            String parentID = this.parentsMap.get(nodeID);
            IHostNode node = graph.getNode(nodeID);
            if(node != null){
                // If this is the first time we touch the SCC related to parentID, then create it.
                if(!sccMap.containsKey(parentID))
                    sccMap.put(parentID, new LinkedList<>());

                // In both cases, add the node to the SCC
                sccMap.get(parentID).add(node);
            }
        }

        return sccMap;
    }

    private void reset(){
        this.visitOrder = new Stack<>();
        this.parentsMap = new HashMap<>();
    }
}
