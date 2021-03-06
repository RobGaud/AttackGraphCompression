package attackpaths;

import graphmodels.graph.IEdge;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Roberto Gaudenzi on 23/09/17.
 */
public class AttackPath implements IAttackPath{

    private String id;
    private double likelihood;
    private Map<Integer, IEdge> edges;

    public AttackPath(String id, double likelihood){
        this.id = id;
        this.likelihood = likelihood;
        this.edges = new HashMap<>();
    }

    public AttackPath(String id){
        this(id, 0.0);
    }

    public String getID(){
        return this.id;
    }

    @Override
    public String getTargetID(){
        int lastEdgeRank = edges.keySet().size()-1;
        return edges.get(lastEdgeRank).getHeadID();
    }

    @Override
    public void addEdge(IEdge edge) {
        if(!this.edges.values().contains(edge)){
            int rank = this.edges.keySet().size();
            this.edges.put(rank, edge);
        }
    }

    @Override
    public void addEdge(int rank, IEdge edge) {
        if(!this.edges.keySet().contains(rank)){
            this.edges.put(rank, edge);
        }
    }

    @Override
    public IEdge replaceEdgeAtRank(int rank, IEdge edge){
        if(!this.edges.keySet().contains(rank))
            return null;

        IEdge removedEdge = this.edges.remove(rank);
        this.edges.put(rank, edge);
        return removedEdge;
    }

    @Override
    public Map<Integer, IEdge> getEdges() {
        return this.edges;
    }

    @Override
    public IEdge getEdge(int rank) {
        if(!this.edges.keySet().contains(rank))
            return null;
        else
            return this.edges.get(rank);
    }

    @Override
    public int getLength(){
        return this.edges.keySet().size();
    }

    @Override
    public double getLikelihood() {
        return this.likelihood;
    }

    @Override
    public void setLikelihood(double likelihood) {
        this.likelihood = likelihood;
    }

    public int hashCode(){
        int hashCode = 0;
        for(int rank : edges.keySet()){
            hashCode += rank * edges.get(rank).hashCode();
        }
        return hashCode;
    }

    public boolean equals(Object o){
        if(this.getClass().equals(o.getClass())){
            AttackPath path = (AttackPath) o;

            if(this.getLength() != path.getLength())
                return false;

            for(int rank : this.edges.keySet()){
                if(!this.getEdge(rank).equals(path.getEdge(rank)))
                    return false;
            }

            // If we get here, then the paths have equal length and have equal edges at each step => they're equal
            return true;
        }
        else return false;
    }
}
