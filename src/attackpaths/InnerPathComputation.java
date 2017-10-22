package attackpaths;

import graphmodels.graph.IEdge;
import graphmodels.graph.sccmodels.ISCCNode;
import utils.Constants;

import java.util.*;

/**
 * Created by Roberto Gaudenzi on 22/10/17.
 */
public class InnerPathComputation {

    public static Set<LinkedList<IEdge>> execute(ISCCNode sccNode, String from, String to){
        Set<String> currentPathNodes = new HashSet<>();
        currentPathNodes.add(from);
        return computeInnerPaths(sccNode, from, to, Constants.MAX_INNER_PATH_LENGTH, currentPathNodes);
    }

    private static Set<LinkedList<IEdge>> computeInnerPaths(ISCCNode sccNode, String currentNode, String target, int spareLength, Set<String> pathNodes){
        Set<LinkedList<IEdge>> innerPaths = new HashSet<>();

        Collection<IEdge> edges = sccNode.getInnerEdges().get(currentNode);
        if(spareLength == 1){
            for(IEdge e: edges){
                if(e.getHeadID().equals(target)){
                    LinkedList<IEdge> newIP = new LinkedList<>();
                    newIP.add(e);
                    innerPaths.add(newIP);
                }
            }
        }
        // Intermediate step
        else{
            for(IEdge e: edges){
                String edgeHead = e.getHeadID();
                if(edgeHead.equals(target)){
                    LinkedList<IEdge> newIP = new LinkedList<>();
                    newIP.add(e);
                    innerPaths.add(newIP);
                }
                // Else, continue vising the inner edges only if the new edge brings us to a new node (avoid cycles)
                else if(!pathNodes.contains(edgeHead)){
                    Set<String> updatedPathNodes = new HashSet<>(pathNodes);
                    updatedPathNodes.add(edgeHead);
                    Set<LinkedList<IEdge>> nextStepPaths = computeInnerPaths(sccNode, e.getHeadID(), target, spareLength-1, updatedPathNodes);

                    // Once I have the path from edgeHead to target, I can build the path from currentNode to target by simply adding the edge 'e'
                    for(LinkedList<IEdge> nsp : nextStepPaths){
                        nsp.add(0, e);
                        innerPaths.add(nsp);
                    }
                }
                //Else, simply ignore the edge and move to the next one.
            }
        }

        return innerPaths;
    }
}
