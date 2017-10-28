package graphmodels.graph;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Roberto Gaudenzi on 10/09/17.
 */
public interface IGraph {

    String getData();

    /*** NODES MANIPULATION METHODS ***/
    void addEntryPoint(IHostNode entryPoint);
    void removeEntryPoint(String epID);
    Collection<IHostNode> getEntryPoints();
    boolean isEntryPoint(String nID);

    void addTarget(IHostNode target);
    void removeTarget(String tID);
    Collection<IHostNode> getTargets();
    boolean isTarget(String nID);

    void addHostNode(IHostNode hostNode);
    void removeHostNode(String nID);
    IHostNode getNode(String nID);
    Map<String, IHostNode> getHostNodes();
    boolean containsHostNode(String nodeID);

    /*** EDGES MANIPULATION METHODS ***/
    void addEdge(IEdge edge);
    void removeEdge(IEdge edge);
}
