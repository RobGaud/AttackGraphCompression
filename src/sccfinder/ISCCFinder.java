package sccfinder;

import graphmodels.graph.IGraph;
import graphmodels.graph.IHostNode;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Roberto Gaudenzi on 10/09/17.
 *
 * This interface must be implemented by all classes that aim to find Strongly Connected Components (SCCs)
 * in a given graph.IGraph instance.
 */
public interface ISCCFinder {
    Map<String, Collection<IHostNode>> findSCCs(IHostNode startPoint);
    IGraph getGraph();
}
