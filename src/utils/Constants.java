package utils;

/**
 * Created by Roberto Gaudenzi on 01/10/17.
 */
public class Constants {

    public static final String CVSS_DEFAULT_VALUE = "5.0";
    public static final String COMPLEXITY_DEFAULT_VALUE = "MEDIUM";

    private static final String LINUX_IDEA_HOME = "/home/roberto/ProgrammingProjects/IdeaProjects/";
    private static final String WINDOWS_IDEA_HOME = "";

    public static final String PROJECT_HOME = "AttackGraphCompression/";
    public static final String DATA_HOME = "data/";
    public static final String CVE_DATA_HOME = "cvedata/";

    // Constants for Graph Compression
    public static final int MIN_SCC_SIZE = 4;
    public static final int[] MAX_SCC_SIZES = {5, 7, 9, 15};
    public static final String SCC_NODE_ID_PREFIX = "SCC_NODE_";
    public static final String SCC_EDGE_ID_PREFIX = "SCC_EDGE_";

    // Constants for JacksonEdgeUtils
    public static final String ATTACK_EDGE_TYPE = "attack_edge";
    public static final String HYPER_EDGE_TYPE = "hyper_edge";
    public static final String SCC_ATTACK_EDGE_TYPE = "scc_attack_edge";
    public static final String SCC_HYPER_EDGE_TYPE = "scc_hyper_edge";

    // Constants for JacksonNodeUtils
    public static final String HOST_NODE_TYPE = "host_node";
    public static final String SCC_NODE_TYPE = "scc_node";

    public static String getIdeaHome(){
        String osname = System.getProperty("os.name");

        if(osname.contains("win"))
            return WINDOWS_IDEA_HOME;
        else
            return LINUX_IDEA_HOME;
    }

    public static String getDataHome(){
        return getIdeaHome() + PROJECT_HOME + DATA_HOME;
    }

    public static String getCveDataHome(){
        return getDataHome() + CVE_DATA_HOME;
    }

    public static float getAccessComplexityScore(String ac){
        float score;
        switch (ac){
            case "LOW":
                score = 0.60f;
                break;
            case "MEDIUM":
                score = 0.40f;
                break;
            case "HIGH":
                score = 0.20f;
                break;
            default:
                System.err.println("ERROR: UNEXPECTED ACCESS COMPLEXITY VALUE = " + ac);
                score = 0.40f;
        }
        return score;
    }


    public static final float EPSILON = 0.50f;

    public static final int[] MAX_PATH_LENGTHS = {3, 4, 5, 6, 7, 8};

    public static final int[] MAX_INNER_PATH_LENGTHS = {2, 3, 4};

    public static final int MAX_PATHS_PER_FILE = 500000;

    public static final int OBSERVED_EXIT_RATE = 1000;

    public static String getPathsFileName(String graphName, int pathsLength){
        return graphName + "_paths_" + pathsLength + ".json";
    }

    public static String getPathsBigSetFileName(String graphName, int pathsLength, int fileNumber){
        return graphName + "_paths_" + pathsLength + "_" + fileNumber + ".json";
    }
}
