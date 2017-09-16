package graphmodels.graph.sccmodels;

import graphmodels.graph.Edge;

/**
 * Created by Roberto Gaudenzi on 16/09/17.
 */
public class SCCEdge extends Edge implements ISCCEdge{

    private String innerNodeID;

    public SCCEdge(String fromNodeID, String toNodeID, String data, String innerNodeID) {
        super(fromNodeID, toNodeID, data);
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
            SCCEdge edge = (SCCEdge)o;
            return this.innerNodeID.equals(edge.innerNodeID);
        }
        else return false;
    }
}
