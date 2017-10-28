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
    private Map<String, Collection<IEdge>> inboundEdgesMap;  // List of all the hyper-edges this node is the end-point of
    private Map<String, Collection<IEdge>> outboundEdgesMap; // List of all the hyper-edges this node is the starting point of

    public HostNode(String id, String data){
        this.id = id;
        this.data = data;
        this.inboundEdgesMap = new HashMap<>();
        this.outboundEdgesMap = new HashMap<>();
    }

    @Override
    public String getID() { return this.id; }

    @Override
    public String getData() { return this.data; }

    @Override
    public void addInboundEdge(IEdge inEdge) {
        // One cannot add an inbound edge to node N if the head of the edge is not N itself
        if(!inEdge.getHeadID().equals(this.id)) {
            System.err.println("ERROR: INSERTING AN INBOUND EDGE NOT BELONGING TO THIS NODE.");
            return;
        }

        if(!this.inboundEdgesMap.containsKey(inEdge.getTailID())){
            //System.out.println("Creating a new list for inbound edges from "+inEdge.getTailID());
            this.inboundEdgesMap.put(inEdge.getTailID(), new LinkedList<>());
        }
        this.inboundEdgesMap.get(inEdge.getTailID()).add(inEdge);

        //System.out.println("DEBUG INBOUND EDGE: " + this.inboundEdgesMap);
    }

    @Override
    public void removeInboundEdge(IEdge inEdge) {
        this.inboundEdgesMap.get(inEdge.getTailID()).remove(inEdge);
    }

    @Override
    public Collection<IEdge> getInboundEdges() {
        //System.out.println("getinbound called" + this.id);
        //System.out.println(this.id + " mapEdges=" + this.inboundEdgesMap);

        Collection<Collection<IEdge>> edgeLists = this.inboundEdgesMap.values();
        //System.out.println(this.id + " edgeLists=" + edgeLists);
        Collection<IEdge> inboundEdges = new LinkedList<>();
        for(Collection<IEdge> c : edgeLists){
            inboundEdges.addAll(c);
            //System.out.println("Collection" + inboundEdges);
        }
        return inboundEdges;
    }

    @Override
    public Collection<IEdge> getInboundEdgesFrom(IHostNode tail) {
        return this.inboundEdgesMap.get(tail.getID());
    }

    @Override
    public void addOutboundEdge(IEdge outEdge) {
        // One cannot add an outbound edge to node N if the tail of the edge is not N itself
        if(!outEdge.getTailID().equals(this.id)){
            System.err.println("ERROR: INSERTING AN OUTBOUND EDGE NOT BELONGING TO THIS NODE.");
            return;
        }

        if(!this.outboundEdgesMap.containsKey(outEdge.getHeadID())){
            //System.out.println("Creating a new list for outbound edges from " + outEdge.getHeadID());
            this.outboundEdgesMap.put(outEdge.getHeadID(), new LinkedList<>());
        }
        this.outboundEdgesMap.get(outEdge.getHeadID()).add(outEdge);
    }

    @Override
    public void removeOutboundEdge(IEdge outEdge) {
        this.outboundEdgesMap.get(outEdge.getHeadID()).remove(outEdge);
    }

    @Override
    public Collection<IEdge> getOutboundEdges() {
        Collection<Collection<IEdge>> edgeLists = this.outboundEdgesMap.values();
        Collection<IEdge> outboundEdges = new LinkedList<>();
        for(Collection<IEdge> c : edgeLists){
            outboundEdges.addAll(c);
        }
        return outboundEdges;
    }

    @Override
    public Collection<IEdge> getOutboundEdgesTo(IHostNode head) {
        return this.outboundEdgesMap.get(head.getID());
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
