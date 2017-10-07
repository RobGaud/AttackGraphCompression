package graphmodels.graph.sccmodels;

import graphmodels.graph.IAttackEdge;
import graphmodels.graph.IEdge;

/**
 * Created by Roberto Gaudenzi on 07/10/17.
 */
public interface ISCCAttackEdge extends IAttackEdge, ISCCEdge{

    static boolean isSCCAttackEdge(IEdge edge){
        if(edge == null)
            return false;

        Class[] interfaces = edge.getClass().getInterfaces();
        for(Class c : interfaces){
            if(c.equals(ISCCAttackEdge.class)){
                return true;
            }
        }

        return false;
    }
}
