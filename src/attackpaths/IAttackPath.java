package attackpaths;

import graphmodels.graph.IEdge;

import java.util.Map;

/**
 * Created by Roberto Gaudenzi on 23/09/17.
 */
public interface IAttackPath {

    String getID();

    String getTargetID();

    void addEdge(IEdge edge);
    void addEdge(int rank, IEdge edge);
    IEdge replaceEdgeAtRank(int rank, IEdge edge);
    Map<Integer, IEdge> getEdges();
    IEdge getEdge(int rank);

    int getLength();

    float getLikelihood();
    void setLikelihood(float likelihood);

    int hashCode();
    boolean equals(Object o);
}
