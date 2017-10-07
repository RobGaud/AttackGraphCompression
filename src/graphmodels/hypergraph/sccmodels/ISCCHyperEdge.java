package graphmodels.hypergraph.sccmodels;

import graphmodels.graph.IEdge;
import graphmodels.graph.sccmodels.ISCCEdge;
import graphmodels.hypergraph.IHyperEdge;

/**
 * Created by Roberto Gaudenzi on 07/10/17.
 */
public interface ISCCHyperEdge extends IHyperEdge, ISCCEdge{

    static boolean isSCCHyperEdge(IEdge edge){
        if(edge == null)
            return false;

        Class[] interfaces = edge.getClass().getInterfaces();
        for(Class c : interfaces){
            if(c.equals(ISCCHyperEdge.class)){
                return true;
            }
        }

        return false;
    }
}
