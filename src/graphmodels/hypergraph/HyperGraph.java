package graphmodels.hypergraph;

import graphmodels.graph.Graph;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Roberto Gaudenzi on 10/09/17.
 */
public class HyperGraph extends Graph implements IHyperGraph{

    private Map<String, IVulnNode> vulnerabilities;

    public HyperGraph(String data){
        super(data);
        this.vulnerabilities = new HashMap<>();
    }

    @Override
    public void addVulnNode(IVulnNode vulnNode) {
        if(!this.vulnerabilities.containsKey(vulnNode.getID()))
            this.vulnerabilities.put(vulnNode.getID(), vulnNode);
    }

    @Override
    public void removeVulnNode(String vID) { this.vulnerabilities.remove(vID); }

    @Override
    public Map<String, IVulnNode> getVulnNodes() { return this.vulnerabilities; }
}
