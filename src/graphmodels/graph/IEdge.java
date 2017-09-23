package graphmodels.graph;

/**
 * Created by Roberto Gaudenzi on 10/09/17.
 */
public interface IEdge {

    String getTailID();
    String getHeadID();
    String getData();

    int hashCode();
    boolean equals(Object o);
}
