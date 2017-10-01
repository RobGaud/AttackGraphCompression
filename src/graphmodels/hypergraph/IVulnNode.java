package graphmodels.hypergraph;

import java.util.List;

/**
 * Created by Roberto Gaudenzi on 10/09/17.
 */
public interface IVulnNode {

    String getID();
    String getData();
    void setCVSS(String cvss);
    String getCVSS();

    /*** EDGE MANIPULATION METHODS ***/
    void addEdge(IHyperEdge edge);
    void removeEdge(IHyperEdge edge);
    List<IHyperEdge> getEdges();
}
