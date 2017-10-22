package graphmodels.hypergraph.sccmodels;

import graphmodels.graph.sccmodels.ISCCEdge;
import graphmodels.hypergraph.HyperEdge;

/**
 * Created by Roberto Gaudenzi on 16/09/17.
 */
//public class SCCHyperEdge extends HyperEdge implements ISCCEdge{
public class SCCHyperEdge extends HyperEdge implements ISCCHyperEdge{

    private String innerTailID, innerHeadID;

    public SCCHyperEdge(String id, String fromNodeID, String toNodeID, String vulnNodeID, String data, String innerTailID, String innerHeadID) {
        super(id, fromNodeID, toNodeID, vulnNodeID, data);
        this.innerTailID = innerTailID;
        this.innerHeadID = innerHeadID;
    }

    @Override
    public String getInnerTail() {
        return this.innerTailID;
    }

    @Override
    public String getInnerHead() {
        return this.innerHeadID;
    }

    @Override
    public int hashCode(){ return super.hashCode() + this.innerTailID.hashCode() + this.innerHeadID.hashCode(); }

    @Override
    public boolean equals(Object o){
        if(this.getClass().equals(o.getClass()) && super.equals(o)){
            SCCHyperEdge edge = (SCCHyperEdge)o;
            return this.innerTailID.equals(edge.innerTailID) && this.innerHeadID.equals(edge.innerHeadID);
        }
        else return false;
    }
}
