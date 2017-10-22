package graphmodels.graph.sccmodels;

import graphmodels.graph.AttackEdge;

/**
 * Created by Roberto Gaudenzi on 16/09/17.
 */
public class SCCAttackEdge extends AttackEdge implements ISCCAttackEdge{

    private String innerTailID, innerHeadID;

    public SCCAttackEdge(String id, String fromNodeID, String toNodeID, String data, String innerTailID, String innerHeadID) {
        super(id, fromNodeID, toNodeID, data);
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
            SCCAttackEdge edge = (SCCAttackEdge)o;
            return this.innerTailID.equals(edge.innerTailID) && this.innerHeadID.equals(edge.innerHeadID);
        }
        else return false;
    }
}
