package sccfinder;

import graphmodels.graph.IHostNode;

import java.util.List;
import java.util.Map;

/**
 * Created by Roberto Gaudenzi on 10/09/17.
 * This class implements the interface scc.ISCCFinder and therefore aims to find SCC component within a given graph.
 * This is done by performing two different DFS: a classical one, and a DFS using reversed edges.
 * (Edges are not actually reversed during the second DFS, but it moves between nodes using outEdges instead of InEdges.)
 */
public class KosarajuSCCFinder implements ISCCFinder {

    @Override
    public Map<String, List<IHostNode>> findSCCs(IHostNode startPoint) {
        // TODO
        return null;
    }
}
