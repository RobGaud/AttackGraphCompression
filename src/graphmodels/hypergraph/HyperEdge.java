package graphmodels.hypergraph;

import graphmodels.graph.Edge;

/**
 * Created by Roberto Gaudenzi on 10/09/17.
 */
public class HyperEdge extends Edge implements IHyperEdge {

    private String fromNodeID, toNodeID, vulnNodeID;
    private String data;

    public HyperEdge(String fromNodeID, String toNodeID, String vulnNodeID, String data){
        super(fromNodeID, toNodeID, data);
        this.vulnNodeID = vulnNodeID;
    }

    @Override
    public String getVulnNodeID() { return this.vulnNodeID; }

    @Override
    public int hashCode(){
        return super.hashCode() + this.getVulnNodeID().hashCode();
    }

    @Override
    public boolean equals(Object o){
        if(this.getClass().equals(o.getClass()) && super.equals(o)){
            HyperEdge edge = (HyperEdge)o;
            return this.vulnNodeID.equals(edge.vulnNodeID);
        }
        else return false;
    }
}
