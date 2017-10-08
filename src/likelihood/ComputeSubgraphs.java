package likelihood;

import graphmodels.graph.IEdge;
import graphmodels.graph.IGraph;
import graphmodels.graph.IHostNode;

import java.util.*;

/**
 * Created by Roberto Gaudenzi on 08/10/17.
 */
public class ComputeSubgraphs {

    private static Map<String, Set<IHostNode>> subgraphs;

    public static Map<String, Set<IHostNode>> execute(IGraph graph){
        subgraphs = new HashMap<>();
        for(IHostNode target: graph.getTargets()){
            // Initialize subgraph associated with each target node
            Set<IHostNode> subgraph = new HashSet<>();
            subgraph.add(target);
            subgraphs.put(target.getID(), subgraph);

            // Then start the subgraph extraction
            recursive(target, target, graph);
        }

        return subgraphs;
    }

    private static void recursive(IHostNode target, IHostNode current,IGraph graph){
        Collection<IEdge> inboundEdges = current.getInboundEdges();
        for(IEdge edge : inboundEdges){
            IHostNode parent = graph.getNode(edge.getTailID());
            if(!subgraphs.get(target.getID()).contains(parent)){
                subgraphs.get(target.getID()).add(parent);
                recursive(target, parent, graph);
            }
        }
    }
}
