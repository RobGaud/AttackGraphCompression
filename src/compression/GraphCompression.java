package compression;

import graphmodels.graph.IEdge;
import graphmodels.graph.IGraph;
import graphmodels.graph.IHostNode;
import graphmodels.graph.sccmodels.SCCEdge;
import graphmodels.graph.sccmodels.SCCNode;
import graphmodels.hypergraph.IHyperEdge;
import graphmodels.hypergraph.sccmodels.SCCHyperEdge;
import sccfinder.ISCCFinder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Roberto Gaudenzi on 16/09/17.
 *
 * This class take an HyperGraph and tries to compress it in a smaller one.
 * This is done by merging all nodes that compose a Strongly Connected Component (SCC) in a single SCCNode.
 * Note: the merging does not include those nodes designated either as "Entry Point" or as "Target".
 */
public class GraphCompression implements IGraphCompression{

    private static final int MIN_SCC_SIZE = 4;
    public static final String SCC_NODE_ID_PREFIX = "SCC_NODE_";
    public static final String SCC_EDGE_ID_PREFIX = "SCC_EDGE_";
    private static int nextID = 0;

    private IGraph graphToCompress;
    private ISCCFinder isccFinder;
    private Collection<String> compressedNodes; // It will contain the IDs of all the nodes already compressed

    public GraphCompression(ISCCFinder isccFinder){
        this.isccFinder = isccFinder;
        this.graphToCompress = isccFinder.getGraph();
        this.compressedNodes = new HashSet<>();
    }

    public IGraph compress(IGraph hyperGraph){
        // 1) Clone the graph to compress => OR MAYBE WE CAN DEMAND TO RECEIVE A CLONE?
        // For each entry point N, for each neighbor M of N, call the ISCCFinder to get the SCCs
        // For each SCC found, create an associated SCCNode and add it to the graph. Then, remove the compressed nodes.

        for(IHostNode ep : graphToCompress.getEntryPoints()) {
            for(IEdge epOutEdge : ep.getOutboundEdges()) {
                IHostNode epNeighbor = graphToCompress.getHostNodes().get(epOutEdge.getHeadID());

                // Find all the Strongly Connected Components reachable from epNeighbor
                Map<String, Collection<IHostNode>> sccs = isccFinder.findSCCs(epNeighbor);

                for( String sccID : sccs.keySet() ){
                    Collection<IHostNode> scc = sccs.get(sccID);

                    // Add all the nodes that compares within an SCC to the "compressed" ones.
                    for(IHostNode node : scc){
                        String nodeID = node.getID();
                        if(this.compressedNodes.contains(nodeID)){
                            // TODO replace with a better log
                            System.out.println("*** ERROR: THIS NODE SHOULD NOT BE ALREADY MARKED ***");
                        }
                        this.compressedNodes.add(nodeID);
                    }

                    if(scc.size() >= MIN_SCC_SIZE)
                        createSCCNode(scc);
                }
            }
        }
        return null;
    }

    private void createSCCNode(Collection<IHostNode> scc){
        /*  1) create a superNode that includes all nodes within the SCC
            2) for each node in it, for each of its edges, create an edge from that supernode that replace the original one
            3) Add every node withing the SCC into the compressedNodes map, so that those nodes won't be compressed twice.
         */
        String sccNodeID = getNextSCCNodeID();
        SCCNode sccNode = new SCCNode(sccNodeID, "");
        for(IHostNode n : scc){
            sccNode.addInnerNode(n);
        }
        this.graphToCompress.addHostNode(sccNode);

        // Convert all their edges into edges of the SCCNode
        for(IHostNode n : scc){
            for(IEdge e: n.getInboundEdges()){
                this.graphToCompress.removeEdge(e);
                if(!sccNode.hasInnerNode(e.getTailID())){
                    if(isHyperEdge(e))
                        this.graphToCompress.addEdge(new SCCEdge(SCC_EDGE_ID_PREFIX+e.getID(), e.getTailID(), sccNodeID, e.getData(), n.getID()));
                    else{
                        IHyperEdge he = (IHyperEdge)e;
                        this.graphToCompress.addEdge(new SCCHyperEdge(SCC_EDGE_ID_PREFIX+he.getID(), he.getTailID(), sccNodeID, he.getVulnNodeID(),
                                                     he.getData(), n.getID()));
                    }
                }
                else{
                    // We want to keep track of the internal edges between nodes contained by the SCC for Path analysis
                    sccNode.addInnerEdge(e);
                }
            }

            for(IEdge e: n.getOutboundEdges()){
                this.graphToCompress.removeEdge(e);
                if(!sccNode.hasInnerNode(e.getHeadID())){
                    if(isHyperEdge(e))
                        this.graphToCompress.addEdge(new SCCEdge(SCC_EDGE_ID_PREFIX+e.getID(), sccNodeID, e.getHeadID(), e.getData(), n.getID()));
                    else{
                        IHyperEdge he = (IHyperEdge)e;
                        this.graphToCompress.addEdge(new SCCHyperEdge(SCC_EDGE_ID_PREFIX+e.getID(), sccNodeID, he.getHeadID(), he.getVulnNodeID(),
                                                     he.getData(), n.getID()));
                    }
                }
            }

            // Finally, remove the host node from the graph
            this.graphToCompress.removeHostNode(n.getID());
        }
    }

    private static String getNextSCCNodeID(){
        return SCC_NODE_ID_PREFIX + nextID++;
    }

    private boolean isHyperEdge(IEdge edge){
        Class[] interfaces = edge.getClass().getInterfaces();
        for(Class i : interfaces){
            if(i.equals(IHyperEdge.class)){
                return true;
            }
        }
        return false;
    }
}
