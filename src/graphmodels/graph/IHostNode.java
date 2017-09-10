package graphmodels.graph;

import java.util.Collection;

/**
 * Created by Roberto Gaudenzi on 10/09/17.
 */
public interface IHostNode {

    String getID();
    String getData();

    /*** INBOUND EDGE MANIPULATION METHODS ***/
    void addInboundEdge(IEdge inEdge);
    void removeInboundEdge(IEdge inEdge);
    Collection<IEdge> getInboundEdges();


    /*** OUTBOUND EDGE MANIPULATION METHODS ***/
    void addOutboundEdge(IEdge outEdge);
    void removeOutboundEdge(IEdge outEdge);
    Collection<IEdge> getOutboundEdges();

    int hashCode();
    boolean equals(Object o);
}
