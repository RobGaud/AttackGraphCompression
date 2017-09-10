package graphmodels.graph;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Roberto Gaudenzi on 10/09/17.
 */
public class HostNode implements IHostNode {

    private String id;                        // This ID could be the IP address of the node for simple ones
    private String data;
    private Map<String, IEdge> inboundEdges;  // List of all the hyper-edges this node is the end-point of
    private Map<String, IEdge> outboundEdges; // List of all the hyper-edges this node is the starting point of

    public HostNode(String id, String data){
        this.id = id;
        this.data = data;
        this.inboundEdges = new HashMap<>();
        this.outboundEdges = new HashMap<>();
    }

    @Override
    public String getID() { return this.id; }

    @Override
    public String getData() { return this.data; }

    @Override
    public void addInboundEdge(IEdge inEdge) { this.inboundEdges.put(inEdge.getFromNodeID(), inEdge); }

    @Override
    public void removeInboundEdge(IEdge inEdge) { this.inboundEdges.remove(inEdge.getFromNodeID()); }

    @Override
    public Collection<IEdge> getInboundEdges() { return this.inboundEdges.values(); }

    @Override
    public void addOutboundEdge(IEdge outEdge) { this.outboundEdges.put(outEdge.getToNodeID(), outEdge); }

    @Override
    public void removeOutboundEdge(IEdge outEdge) { this.outboundEdges.remove(outEdge.getToNodeID()); }

    @Override
    public Collection<IEdge> getOutboundEdges() { return this.outboundEdges.values(); }

    public int hashCode(){ return this.id.hashCode(); }

    public boolean equals(Object o){
        if(o.getClass().equals(this.getClass())){
            HostNode node = (HostNode)o;
            return this.id.equals(node.id);
        }
        else return false;
    }
}
