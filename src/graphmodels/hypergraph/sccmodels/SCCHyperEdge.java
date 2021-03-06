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
    public int hashCode(){
        int hashCode = super.hashCode();
        if(this.innerTailID != null)
            hashCode += this.innerTailID.hashCode();
        if(this.innerHeadID != null)
            hashCode += this.innerHeadID.hashCode();

        return hashCode;
    }

    @Override
    public boolean equals(Object o){
        if(this.getClass().equals(o.getClass()) && super.equals(o)){
            SCCHyperEdge edge = (SCCHyperEdge)o;
            boolean areEqual;
            if(this.innerTailID != null)
                areEqual = this.innerTailID.equals(edge.innerTailID);
            else
                areEqual = edge.innerTailID == null;

            if(this.innerHeadID != null)
                areEqual = areEqual && this.innerHeadID.equals(edge.innerHeadID);
            else
                areEqual = areEqual && edge.innerHeadID == null;

            return areEqual;
        }
        else return false;
    }
}
