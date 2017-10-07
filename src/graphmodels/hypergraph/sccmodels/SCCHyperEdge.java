package graphmodels.hypergraph.sccmodels;

import graphmodels.graph.sccmodels.ISCCEdge;
import graphmodels.hypergraph.HyperEdge;

/**
 * Created by Roberto Gaudenzi on 16/09/17.
 */
//public class SCCHyperEdge extends HyperEdge implements ISCCEdge{
public class SCCHyperEdge extends HyperEdge implements ISCCHyperEdge{
    private String innerNodeID;

    public SCCHyperEdge(String id, String fromNodeID, String toNodeID, String vulnNodeID, String data, String innerNodeID) {
        super(id, fromNodeID, toNodeID, vulnNodeID, data);
        this.innerNodeID = innerNodeID;
    }

    @Override
    public String getInnerNode() {
        return this.innerNodeID;
    }

    @Override
    public int hashCode(){ return super.hashCode() + this.innerNodeID.hashCode(); }

    @Override
    public boolean equals(Object o){
        if(this.getClass().equals(o.getClass()) && super.equals(o)){
            SCCHyperEdge edge = (SCCHyperEdge)o;
            return this.innerNodeID.equals(edge.innerNodeID);
        }
        else return false;
    }
}
