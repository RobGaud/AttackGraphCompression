package graphmodels.graph.sccmodels;

import graphmodels.graph.IEdge;
import graphmodels.graph.IHostNode;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Roberto Gaudenzi on 15/09/17.
 */
public interface ISCCNode extends IHostNode{

    void addInnerNode(IHostNode node);
    void removeInnerNode(IHostNode node);
    Map<String, IHostNode> getInnerNodes();
    boolean hasInnerNode(String nodeID);

    void addInnerEdge(IEdge edge);
    void removeInnerEdge(IEdge edge);
    Map<String, Collection<IEdge>> getInnerEdges();

    static boolean isSCCNode(IHostNode node){
        if(node == null)
            return false;

        Class[] interfaces = node.getClass().getInterfaces();
        for(Class c : interfaces){
            if(c.equals(ISCCNode.class)){
                return true;
            }
        }

        return false;
    }
}
