package graphmodels.graph;

import java.util.Set;

/**
 * Created by Roberto Gaudenzi on 24/09/17.
 */
public interface IAttackEdge extends IEdge{

    void addVulnerability(String vulnID);
    void removeVulnerability(String vulnID);
    Set<String> getVulnerabilities();

    static boolean isAttackEdge(IEdge edge){
        if(edge == null)
            return false;

        Class[] interfaces = edge.getClass().getInterfaces();
        for(Class i : interfaces){
            if(i.equals(IAttackEdge.class))
                return true;
        }

        return false;
    }
}
