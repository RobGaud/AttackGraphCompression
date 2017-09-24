package attackpaths;

import graphmodels.graph.IEdge;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Roberto Gaudenzi on 23/09/17.
 */
public class AttackPath implements IAttackPath{

    private String id;
    private Map<Integer, IEdge> edges;

    public AttackPath(String id){
        this.id = id;
        this.edges = new HashMap<>();
    }

    public String getID(){
        return this.id;
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
}
