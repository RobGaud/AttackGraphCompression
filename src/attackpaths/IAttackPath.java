package attackpaths;

import graphmodels.graph.IEdge;

import java.util.Map;

/**
 * Created by Roberto Gaudenzi on 23/09/17.
 */
public interface IAttackPath {

    String getID();

    void addEdge(IEdge edge);
    IEdge replaceEdgeAtRank(int rank, IEdge edge);
    Map<Integer, IEdge> getEdges();
    IEdge getEdge(int rank);

    int getLength();
}
