package graphmodels.graph.sccmodels;

import graphmodels.graph.HostNode;
import graphmodels.graph.IEdge;
import graphmodels.graph.IHostNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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
        boolean isSCCEdge = false;
        Class[] interfaces = inEdge.getClass().getInterfaces();
        for(Class i : interfaces){
            if(i.equals(ISCCEdge.class))
                isSCCEdge = true;
        }
        if(isSCCEdge)
            super.addInboundEdge(inEdge);

    }

    @Override
    public void addOutboundEdge(IEdge inEdge) {
        // An SCCNode can only have edges implementing ISCCEdge interface
        boolean isSCCEdge = false;
        Class[] interfaces = inEdge.getClass().getInterfaces();
        for(Class i : interfaces){
            if(i.equals(ISCCEdge.class))
                isSCCEdge = true;
        }
        if(isSCCEdge)
            super.addOutboundEdge(inEdge);

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
            this.innerEdges.put(edge.getTailID(), new HashSet<>());
        }
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
