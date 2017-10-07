package graphmodels.graph.sccmodels;

import graphmodels.graph.AttackEdge;

/**
 * Created by Roberto Gaudenzi on 16/09/17.
 */
public class SCCAttackEdge extends AttackEdge implements ISCCAttackEdge{

    private String innerNodeID;

    public SCCAttackEdge(String id, String fromNodeID, String toNodeID, String data, String innerNodeID) {
        super(id, fromNodeID, toNodeID, data);
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
            SCCAttackEdge edge = (SCCAttackEdge)o;
            return this.innerNodeID.equals(edge.innerNodeID);
        }
        else return false;
    }
}
