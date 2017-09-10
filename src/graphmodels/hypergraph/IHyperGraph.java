package graphmodels.hypergraph;

import graphmodels.graph.IGraph;

import java.util.Map;

/**
 * Created by Roberto Gaudenzi on 10/09/17.
 */
public interface IHyperGraph extends IGraph {

    void addVulnNode(IVulnNode vulnNode);
    void removeVulnNode(String vID);
    Map<String, IVulnNode> getVulnNodes();
}
