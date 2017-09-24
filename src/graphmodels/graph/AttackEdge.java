package graphmodels.graph;


import java.util.HashSet;
import java.util.Set;

/**
 * Created by Roberto Gaudenzi on 24/09/17.
 */
public class AttackEdge extends Edge implements IAttackEdge{

    private Set<String> vulnerabilities;

    public AttackEdge(String id, String fromNodeID, String toNodeID, String data){
        super(id, fromNodeID, toNodeID, data);
        this.vulnerabilities = new HashSet<>();
    }

    @Override
    public void addVulnerability(String vulnID) {
        this.vulnerabilities.add(vulnID);
    }

    @Override
    public void removeVulnerability(String vulnID) {
        this.vulnerabilities.remove(vulnID);
    }

    @Override
    public Set<String> getVulnerabilities() {
        return this.vulnerabilities;
    }
}
