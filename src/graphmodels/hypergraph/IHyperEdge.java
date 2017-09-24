package graphmodels.hypergraph;

import graphmodels.graph.IEdge;

/**
 * Created by Roberto Gaudenzi on 10/09/17.
 */
public interface IHyperEdge extends IEdge {

    String getVulnNodeID();

    static boolean isHyperEdge(IEdge edge){
        if(edge == null)
            return false;

        Class[] interfaces = edge.getClass().getInterfaces();
        for(Class i : interfaces){
            if(i.equals(IHyperEdge.class))
                return true;
        }

        return false;
    }
}
