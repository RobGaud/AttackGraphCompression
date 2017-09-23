package graphmodels.graph;


import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Roberto Gaudenzi on 10/09/17.
 */
public class HostNode implements IHostNode {

    private String id;                        // This ID could be the IP address of the node for simple ones
    private String data;
    private Map<String, Collection<IEdge>> inboundEdges;  // List of all the hyper-edges this node is the end-point of
    private Map<String, Collection<IEdge>> outboundEdges; // List of all the hyper-edges this node is the starting point of

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
    public void addInboundEdge(IEdge inEdge) {
        // One cannot add an inbound edge to node N if the head of the edge is not N itself
        if(!inEdge.getTailID().equals(this.id))
            return;

        if(!this.inboundEdges.containsKey(inEdge.getTailID())){
            this.inboundEdges.put(inEdge.getTailID(), new LinkedList<>());
        }
        this.inboundEdges.get(inEdge.getTailID()).add(inEdge);
    }

    @Override
    public void removeInboundEdge(IEdge inEdge) {
        this.inboundEdges.get(inEdge.getTailID()).remove(inEdge);
    }

    @Override
    public Collection<IEdge> getInboundEdges() {
        Collection<Collection<IEdge>> edgeLists = this.outboundEdges.values();
        Collection<IEdge> outboundEdges = new LinkedList<>();
        for(Collection<IEdge> c : edgeLists){
            outboundEdges.addAll(c);
        }
        return outboundEdges;
    }

    @Override
    public void addOutboundEdge(IEdge outEdge) {
        // One cannot add an outbound edge to node N if the tail of the edge is not N itself
        if(!outEdge.getTailID().equals(this.id))
            return;

        if(!this.inboundEdges.containsKey(outEdge.getHeadID())){
            this.inboundEdges.put(outEdge.getHeadID(), new LinkedList<>());
        }
        this.inboundEdges.get(outEdge.getHeadID()).add(outEdge);
    }

    @Override
    public void removeOutboundEdge(IEdge outEdge) {
        this.outboundEdges.get(outEdge.getHeadID()).remove(outEdge);
    }

    @Override
    public Collection<IEdge> getOutboundEdges() {
        Collection<Collection<IEdge>> edgeLists = this.inboundEdges.values();
        Collection<IEdge> inboundEdges = new LinkedList<>();
        for(Collection<IEdge> c : edgeLists){
            inboundEdges.addAll(c);
        }
        return inboundEdges;
    }

    public int hashCode(){ return this.id.hashCode(); }

    public boolean equals(Object o){
        if(o.getClass().equals(this.getClass())){
            HostNode node = (HostNode)o;
            return this.id.equals(node.id);
        }
        else return false;
    }
}
