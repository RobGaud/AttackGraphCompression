package attackpaths;

import graphmodels.graph.*;
import graphmodels.graph.sccmodels.ISCCAttackEdge;
import graphmodels.graph.sccmodels.ISCCEdge;
import graphmodels.graph.sccmodels.ISCCNode;
import graphmodels.hypergraph.HyperEdge;
import graphmodels.hypergraph.sccmodels.ISCCHyperEdge;

import java.util.*;

/**
 * Created by Roberto Gaudenzi on 21/10/17.
 */
public class AttackPathExtractor {

    private static int attackPathGenerator;

    public static Set<IAttackPath> extractPaths(IGraph graph, IAttackPath compressedPath, int maxInnerPathLength){
        attackPathGenerator = 0;

        Set<IAttackPath> extractedPaths = new HashSet<>();
        Set<LinkedList<IEdge>> rawPaths = new HashSet<>();

        int currentRank = 0;
        while(currentRank < compressedPath.getLength()){
            LinkedList<IEdge> partialPath = new LinkedList<>();

            IEdge currentEdge = compressedPath.getEdge(currentRank);
            IHostNode currentHead = graph.getNode(currentEdge.getHeadID());
            if(ISCCNode.isSCCNode(currentHead)){
                /* Take the original tail of currentEdge, say 'h1'
                 * Take the next edge in the path, say 'e2'
                 * Take the original tail of 'e2', say 't2'
                 * Compute all the possible paths from h1 towards t2 made only of inner edges of currentHead (which is an SCCNode)
                 */

                ISCCEdge nextEdge = (ISCCEdge)compressedPath.getEdge(currentRank+1);
                String h1 = ((ISCCEdge)currentEdge).getInnerHead();
                String t2 = nextEdge.getInnerTail();
                Set<LinkedList<IEdge>> innerPaths = InnerPathComputation.execute((ISCCNode)currentHead, h1, t2, maxInnerPathLength);

                // Add the innerPaths to each extractedPath
                partialPath.addLast(convertSCCEdge(currentEdge));
                rawPaths = combinePaths(rawPaths, partialPath, innerPaths);
            }
            else{
                if(ISCCAttackEdge.isSCCAttackEdge(currentEdge) || ISCCHyperEdge.isSCCHyperEdge(currentEdge))
                    partialPath.addLast(convertSCCEdge(currentEdge));
                else
                    partialPath.addLast(currentEdge);
            }

            currentRank++;
        }

        for(LinkedList<IEdge> rawPath : rawPaths){
            extractedPaths.add(convertRawPath(rawPath));
        }

        return extractedPaths;
    }

    private static IEdge convertSCCEdge(IEdge sccEdge){
        if(ISCCAttackEdge.isSCCAttackEdge(sccEdge)){
            ISCCAttackEdge sccae = (ISCCAttackEdge)sccEdge;
            IAttackEdge resultEdge = new AttackEdge(sccae.getID(), sccae.getTailID(), sccae.getHeadID(), sccae.getData());
            for(String vulnID : sccae.getVulnerabilities()){
                resultEdge.addVulnerability(vulnID);
            }
            return resultEdge;
        }
        else{
            ISCCHyperEdge scche = (ISCCHyperEdge)sccEdge;
            return new HyperEdge(scche.getID(), scche.getTailID(), scche.getHeadID(), scche.getVulnNodeID(), scche.getData());
        }
    }

    private static Set<LinkedList<IEdge>> combinePaths(Set<LinkedList<IEdge>> rawPaths, LinkedList<IEdge> intermediatePath, Set<LinkedList<IEdge>> innerPaths){

        Set<LinkedList<IEdge>> combinedPaths = new HashSet<>();

        for (LinkedList<IEdge> rawPath : rawPaths) {
            for (LinkedList<IEdge> innerPath : innerPaths) {

                // Create a new path which initial part is 'rawPath'
                LinkedList<IEdge> combinedPath = new LinkedList<>();
                combinedPath.addAll(rawPath);

                // Add all the edges within intermediatePath
                for (IEdge ie : intermediatePath) {
                    combinedPath.addLast(ie);
                }

                // Add all the edges within the innerPath
                for (IEdge ipe : innerPath) {
                    combinedPath.addLast(ipe);
                }

                combinedPaths.add(combinedPath);
            }
        }

        return combinedPaths;
    }

    private static IAttackPath convertRawPath(LinkedList<IEdge> rawPath){
        IAttackPath attackPath = new AttackPath("EXT_PATH_"+attackPathGenerator);
        for(IEdge e : rawPath){
            attackPath.addEdge(e);
        }

        return attackPath;
    }
}
