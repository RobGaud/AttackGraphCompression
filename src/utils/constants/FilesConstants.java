package utils.constants;

/**
 * Created by Roberto Gaudenzi on 17/12/17.
 */
public class FilesConstants {

    private static final String LINUX_IDEA_HOME = "/home/roberto/ProgrammingProjects/IdeaProjects/";

    public static final String PROJECT_HOME = "AttackGraphCompression/";
    public static final String DATA_HOME = "data/";
    public static final String CVE_DATA_HOME = "cvedata/";

    public static String getIdeaHome(){
        return LINUX_IDEA_HOME;
    }

    public static String getDataHome(){
        return getIdeaHome() + PROJECT_HOME + DATA_HOME;
    }

    public static String getCveDataHome(){
        return getDataHome() + CVE_DATA_HOME;
    }

    public static String getPathsFileName(String graphName, int pathsLength){
        return graphName + "_paths_" + pathsLength + ".json";
    }

    public static String getPathsBigSetFileName(String graphName, int pathsLength, int fileNumber){
        return graphName + "_paths_" + pathsLength + "_" + fileNumber + ".json";
    }
}
