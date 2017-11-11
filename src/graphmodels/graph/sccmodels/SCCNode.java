package graphmodels.graph.sccmodels;

import graphmodels.graph.HostNode;
import graphmodels.graph.IEdge;
import graphmodels.graph.IHostNode;
import graphmodels.hypergraph.sccmodels.ISCCHyperEdge;

import java.util.*;

/**
 * Created by Roberto Gaudenzi on 15/09/17.
 */
public class SCCNode extends HostNode implements ISCCNode{

    private Map<String, IHostNode> innerNodes;
    private Map<String, Collection<IEdge>> innerEdges; // The key is the tail, the value is a list of outbound edges

    public SCCNode(String id, String data){
        super(id, data);

        this.innerNodes = new HashMap<>();
        this.innerEdges = new HashMap<>();
    }

    @Override
    public void addInboundEdge(IEdge inEdge) {
        // An SCCNode can only have edges implementing ISCCEdge interface
        if(ISCCAttackEdge.isSCCAttackEdge(inEdge) || ISCCHyperEdge.isSCCHyperEdge(inEdge))
            super.addInboundEdge(inEdge);
        else
            System.err.println("SCCNode.addInboundEdge: adding a non-SCCEdge!");
    }

    @Override
    public void addOutboundEdge(IEdge outEdge) {
        // An SCCNode can only have edges implementing ISCCEdge interface
        if(ISCCAttackEdge.isSCCAttackEdge(outEdge) || ISCCHyperEdge.isSCCHyperEdge(outEdge))
            super.addOutboundEdge(outEdge);
        else
            System.err.println("SCCNode.addOutboundEdge: adding a non-SCCEdge!");

    }

    @Override
    public void addInnerNode(IHostNode node) {
        if(!this.innerNodes.containsKey(node.getID()))
            this.innerNodes.put(node.getID(), node);
    }

    @Override
    public void removeInnerNode(IHostNode node) {
        this.innerNodes.remove(node.getID());
    }

    @Override
    public Map<String, IHostNode> getInnerNodes() {
        return this.innerNodes;
    }

    @Override
    public boolean hasInnerNode(String nodeID){
        return this.innerNodes.keySet().contains(nodeID);
    }

    @Override
    public void addInnerEdge(IEdge edge) {
        if(!this.innerEdges.containsKey(edge.getTailID())){
            this.innerEdges.put(edge.getTailID(), new LinkedList<>());
        }

        if(this.innerEdges.get(edge.getTailID()).contains(edge))
            System.err.println("SCCNode.addInnerEdge: inner edge already inserted! "
                    + edge.getID() + ", from " + edge.getTailID() + " to " + edge.getHeadID());
        else
            this.innerEdges.get(edge.getTailID()).add(edge);
    }

    @Override
    public void removeInnerEdge(IEdge edge) {
        this.innerEdges.get(edge.getTailID()).remove(edge);
    }

    @Override
    public Map<String, Collection<IEdge>> getInnerEdges() {
        return this.innerEdges;
    }
}
