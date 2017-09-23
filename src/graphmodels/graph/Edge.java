package graphmodels.graph;

/**
 * Created by Roberto Gaudenzi on 10/09/17.
 */
public class Edge implements IEdge {

    private String fromNodeID, toNodeID;
    private String data;

    public Edge(String fromNodeID, String toNodeID, String data){
        this.fromNodeID = fromNodeID;
        this.toNodeID = toNodeID;
        this.data = data;
    }

    @Override
    public String getTailID() { return this.fromNodeID; }

    @Override
    public String getHeadID() { return this.toNodeID; }

    @Override
    public String getData() { return this.data; }

    @Override
    public int hashCode(){
        return this.fromNodeID.hashCode() + this.getHeadID().hashCode();
    }

    @Override
    public boolean equals(Object o){
        if(this.getClass().equals(o.getClass())){
            Edge edge = (Edge)o;
            return this.fromNodeID.equals(edge.fromNodeID) && this.toNodeID.equals(edge.toNodeID);
        }
        else return false;
    }

}
