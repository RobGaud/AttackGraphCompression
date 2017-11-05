package compression;

import graphmodels.graph.IEdge;
import graphmodels.graph.IGraph;
import graphmodels.graph.IHostNode;
import graphmodels.graph.sccmodels.ISCCAttackEdge;
import graphmodels.graph.sccmodels.SCCAttackEdge;
import graphmodels.graph.sccmodels.SCCNode;
import graphmodels.hypergraph.IHyperEdge;
import graphmodels.hypergraph.sccmodels.ISCCHyperEdge;
import graphmodels.hypergraph.sccmodels.SCCHyperEdge;
import sccfinder.ISCCFinder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import static utils.Constants.*;

/**
 * Created by Roberto Gaudenzi on 16/09/17.
 *
 * This class take an HyperGraph and tries to compress it in a smaller one.
 * This is done by merging all nodes that compose a Strongly Connected Component (SCC) in a single SCCNode.
 * Note: the merging does not include those nodes designated either as "Entry Point" or as "Target".
 */
public class GraphCompression implements IGraphCompression{

    private static int nextID = 0;

    private IGraph graphToCompress;
    private ISCCFinder isccFinder;
    private Collection<String> visitedNodes; // It will contain the IDs of all the nodes already compressed
    private int minSize;


    public GraphCompression(ISCCFinder isccFinder, int minSize){
        this.isccFinder = isccFinder;
        this.graphToCompress = isccFinder.getGraph();
        this.visitedNodes = new HashSet<>();
        this.minSize = minSize;
    }

    public IGraph compress(IGraph hyperGraph){
        // For each entry point N, for each neighbor M of N, call the ISCCFinder to get the SCCs
        // For each SCC found, create an associated SCCNode and add it to the graph. Then, remove the compressed nodes.

        for(IHostNode ep : graphToCompress.getEntryPoints()) {
            for(IEdge epOutEdge : ep.getOutboundEdges()) {

                IHostNode epNeighbor = graphToCompress.getHostNodes().get(epOutEdge.getHeadID());

                /* Since the list of outbound edges of node 'ep' is dynamically updated at each iteration
                 * the list could contain edges that have been deleted => risk of NullPointerException
                 */
                if(epNeighbor != null){
                    // Find all the Strongly Connected Components reachable from epNeighbor
                    Map<String, Collection<IHostNode>> sccs = isccFinder.findSCCs(epNeighbor);

                    if( sccs != null){
                        for( String sccID : sccs.keySet() ){
                            Collection<IHostNode> scc = sccs.get(sccID);
                            System.out.println("GraphCompression.compress: scc size = " + scc.size());

                            // Add all the nodes that compares within an SCC to the "compressed" ones.
                            for(IHostNode node : scc){
                                String nodeID = node.getID();
                                if(this.visitedNodes.contains(nodeID)){
                                    System.out.println("*** ERROR: THIS NODE SHOULD NOT BE ALREADY MARKED ***");
                                }
                                this.visitedNodes.add(nodeID);
                            }

                            if(scc.size() >= this.minSize)
                                createSCCNode(scc);
                        }
                    }
                }
            }
        }
        return this.graphToCompress;
    }

    private void createSCCNode(Collection<IHostNode> scc){
        /*  1) create a superNode that includes all nodes within the SCC
            2) for each node in it, for each of its edges, create an edge from that supernode that replace the original one
            3) Add every node withing the SCC into the visitedNodes map, so that those nodes won't be compressed twice.
         */
        String sccNodeID = getNextSCCNodeID();
        SCCNode sccNode = new SCCNode(sccNodeID, "");
        for(IHostNode n : scc){
            sccNode.addInnerNode(n);
        }
        this.graphToCompress.addHostNode(sccNode);

        // Convert all their edges into edges of the SCCNode
        for(IHostNode n : scc){

            // 1) for each inbound edge 'e' of node 'n', replace it with an SCCEdge/SCCHyperEdge where 'n' is the inner head
            for(IEdge e: n.getInboundEdges()){
                this.graphToCompress.removeEdge(e);

                /* Two cases here:
                 * 1) The tail does not belong to the SCC Node => add a new inbound edge to the SCC Node;
                 * 2) both tail and head of the edge belong to the SCC Node => just include it as internal edge.
                 */
                if(!sccNode.hasInnerNode(e.getTailID())){
                    // We need to handle the case when the edge will connect two SCC nodes
                    if(ISCCHyperEdge.isSCCHyperEdge(e)){
                        ISCCHyperEdge scche = (ISCCHyperEdge)e;
                        this.graphToCompress.addEdge(new SCCHyperEdge(SCC_EDGE_ID_PREFIX + scche.getID(), scche.getTailID(), sccNodeID,
                                scche.getVulnNodeID(), scche.getData(), scche.getTailID(), n.getID()));
                    }
                    else if(ISCCAttackEdge.isSCCAttackEdge(e)){
                        ISCCAttackEdge sccae = (ISCCAttackEdge)e;
                        this.graphToCompress.addEdge(new SCCAttackEdge(SCC_EDGE_ID_PREFIX + sccae.getID(), sccae.getTailID(), sccNodeID,
                                sccae.getData(), sccae.getInnerTail(), n.getID()));
                    }
                    else if(IHyperEdge.isHyperEdge(e)) {
                        IHyperEdge he = (IHyperEdge) e;
                        this.graphToCompress.addEdge(new SCCHyperEdge(SCC_EDGE_ID_PREFIX + he.getID(), he.getTailID(), sccNodeID,
                                he.getVulnNodeID(), he.getData(), null, n.getID()));
                    }
                    else{
                        this.graphToCompress.addEdge(new SCCAttackEdge(SCC_EDGE_ID_PREFIX + e.getID(), e.getTailID(), sccNodeID,
                                e.getData(), null, n.getID()));
                    }
                }
                else{
                    // We want to keep track of the internal edges between nodes contained by the SCC for Path analysis
                    sccNode.addInnerEdge(e);
                }
            }

            // 2) for each outbound edge 'e' of node 'n', replace it with an SCCEdge/SCCHyperEdge where 'n' is the inner tail
            for(IEdge e: n.getOutboundEdges()){
                this.graphToCompress.removeEdge(e);
                if(!sccNode.hasInnerNode(e.getHeadID())){
                    // We need to handle the case when the edge will connect two SCC nodes
                    if(ISCCHyperEdge.isSCCHyperEdge(e)){
                        ISCCHyperEdge scche = (ISCCHyperEdge)e;
                        this.graphToCompress.addEdge(new SCCHyperEdge(SCC_EDGE_ID_PREFIX+e.getID(), sccNodeID, scche.getHeadID(),
                                scche.getVulnNodeID(), scche.getData(), n.getID(), scche.getInnerHead()));
                    }
                    else if(ISCCAttackEdge.isSCCAttackEdge(e)){
                        ISCCAttackEdge sccae = (ISCCAttackEdge)e;
                        this.graphToCompress.addEdge(new SCCAttackEdge(SCC_EDGE_ID_PREFIX + sccae.getID(), sccNodeID, sccae.getHeadID(),
                                sccae.getData(), n.getID(), sccae.getInnerHead()));
                    }
                    else if(IHyperEdge.isHyperEdge(e)) {
                        IHyperEdge he = (IHyperEdge)e;
                        this.graphToCompress.addEdge(new SCCHyperEdge(SCC_EDGE_ID_PREFIX+e.getID(), sccNodeID, he.getHeadID(),
                                he.getVulnNodeID(), he.getData(), n.getID(), null));

                    }
                    else{
                        this.graphToCompress.addEdge(new SCCAttackEdge(SCC_EDGE_ID_PREFIX + e.getID(), sccNodeID, e.getHeadID(),
                                e.getData(), n.getID(), null));
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
}
