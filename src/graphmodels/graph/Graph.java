package graphmodels.graph;

import java.util.*;

/**
 * Created by Roberto Gaudenzi on 10/09/17.
 */
public class Graph implements IGraph {

    private String data;

    private Map<String, IHostNode> nodes;
    private List<IHostNode> entryPoints, targets;

    public Graph(String data){
        this.data = data;
        this.nodes = new HashMap<>();
        this.entryPoints = new LinkedList<>();
        this.targets = new LinkedList<>();
    }

    @Override
    public String getData() { return this.data; }

    @Override
    public void addHostNode(IHostNode hostNode) {
        if(!this.nodes.containsKey(hostNode.getID()))
            this.nodes.put(hostNode.getID(), hostNode);
    }

    @Override
    public void removeHostNode(String nID) {
        for(IEdge InEdge : this.nodes.get(nID).getInboundEdges())
            this.removeEdge(InEdge);

        for(IEdge outEdge : this.nodes.get(nID).getOutboundEdges())
            this.removeEdge(outEdge);

        this.nodes.remove(nID);
    }

    @Override
    public IHostNode getNode(String nID) { return this.nodes.get(nID); }

    @Override
    public Map<String, IHostNode> getHostNodes() { return this.nodes; }

    @Override
    public void addEntryPoint(IHostNode entryPoint) {
        this.entryPoints.add(entryPoint);
        this.addHostNode(entryPoint);
    }

    @Override
    public void removeEntryPoint(String epID) {
        IHostNode entryPoint = this.nodes.get(epID);
        this.entryPoints.remove(entryPoint);
        this.removeHostNode(epID);
    }

    @Override
    public List<IHostNode> getEntryPoints() { return this.entryPoints; }

    @Override
    public boolean isEntryPoint(String nID) {
        if(this.nodes.containsKey(nID)){
            IHostNode node = this.nodes.get(nID);
            return this.entryPoints.contains(node);
        }
        else return false;
    }

    @Override
    public void addTarget(IHostNode target) {
        this.targets.add(target);
        this.addHostNode(target);
    }

    @Override
    public void removeTarget(String tID) {
        IHostNode target = this.nodes.get(tID);
        this.targets.remove(target);
        this.removeHostNode(tID);
    }

    @Override
    public List<IHostNode> getTargets() { return this.targets; }

    @Override
    public boolean isTarget(String nID) {
        if(this.nodes.containsKey(nID)){
            IHostNode node = this.nodes.get(nID);
            return this.targets.contains(node);
        }
        else return false;
    }

    @Override
    public void addEdge(IEdge edge) {
        String fromID = edge.getFromNodeID();
        String toID = edge.getToNodeID();

        this.nodes.get(fromID).addOutboundEdge(edge);
        this.nodes.get(toID).addInboundEdge(edge);
    }

    @Override
    public void removeEdge(IEdge edge) {
        String fromID = edge.getFromNodeID();
        String toID = edge.getToNodeID();

        this.nodes.get(fromID).removeOutboundEdge(edge);
        this.nodes.get(toID).removeInboundEdge(edge);
    }
}
