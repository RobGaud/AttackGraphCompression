package main;

import attackpaths.AttackPathExtractor;
import attackpaths.IAttackPath;
import graphmodels.hypergraph.IHyperGraph;
import test.PrecisionRecall;
import utils.Constants;
import utils.JacksonHAGUtils;
import utils.JacksonPathUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Roberto Gaudenzi on 09/11/17.
 */
public class PrecisionRecallMain {
    public static void main(String[] args){
        String dataFolderPath = Constants.getDataHome();

        String attackGraphName = "HAG_attack_graph";
        String compressedGraphName = "C-"+attackGraphName;

        int compressedPathsLength = Constants.MAX_PATH_LENGTHS[0];
        int normalPathsLength = Constants.MAX_PATH_LENGTHS[3];

        String compressedGraphFilename = compressedGraphName+".json";
        String attackPathsFilenamePrefix = attackGraphName + "_paths_" + normalPathsLength + "_";
        String compressedPathsFilename = compressedGraphName + "_paths_" + compressedPathsLength + "_0.json";

        double minCompressedSL = Constants.MIN_COMPRESSED_SL[0];
        double minNormalSL = Constants.MIN_PATHS_SL[0];

        System.out.println("PrecisionRecallMain: loading compressed graph.");
        IHyperGraph compressedGraph = JacksonHAGUtils.loadHAG(dataFolderPath, compressedGraphFilename);

        System.out.println("PrecisionRecallMain: loading paths to be extracted.");
        Map<String, IAttackPath> compressedPathsMap = JacksonPathUtils.loadPaths(dataFolderPath, compressedPathsFilename);

        System.out.println("PrecisionRecallMain: extracting relevant paths.");
        LinkedList<IAttackPath> relevantExtractedPaths = new LinkedList<>();
        for(String pathID : compressedPathsMap.keySet()){
            IAttackPath cPath = compressedPathsMap.get(pathID);
            double pathSL = cPath.getLikelihood();
            if(pathSL >= minCompressedSL && pathSL < 1.0){
                Collection<IAttackPath> extractedPaths = AttackPathExtractor.extractPaths(compressedGraph, cPath, Constants.MAX_INNER_PATH_LENGTHS[0]);
                for(IAttackPath ePath : extractedPaths){
                    if(ePath.getLikelihood() > minNormalSL && ePath.getLikelihood() < 1.0)
                        relevantExtractedPaths.add(ePath);
                }
            }
        }

        LinkedList<IAttackPath> relevantAttackPaths = new LinkedList<>();
        for(int i = 0; i < 9; i++){
            //TODO remove
            int tooHighCount = 0;

            String attackPathsFilename = attackPathsFilenamePrefix + i + ".json";
            System.out.println("PrecisionRecallMain: loading original paths from file " + attackPathsFilename + ".");

            Map<String, IAttackPath> attackPathsMap = JacksonPathUtils.loadPaths(dataFolderPath, attackPathsFilename);

            for(String attackPathID : attackPathsMap.keySet()){
                IAttackPath attackPath = attackPathsMap.get(attackPathID);
                double normalPathSL = attackPath.getLikelihood();

                if(normalPathSL > minNormalSL && normalPathSL < 1.0)
                    relevantAttackPaths.add(attackPath);
                //TODO remove
                if(normalPathSL > 1.0)
                    tooHighCount++;
            }
            //TODO remove
            System.out.println("    " + i +") TooHighCount = " + tooHighCount);
        }
        //TODO remove
        System.out.println("PrecisionRecallMain: relevantAttackPaths.size() = " + relevantAttackPaths.size());

        System.out.println("PrecisionRecallMain: computing the precision value.");
        double precision = PrecisionRecall.computePrecision(relevantAttackPaths, relevantExtractedPaths);
        System.out.println("PrecisionRecallMain: precision = " + precision + ".");

        System.out.println("PrecisionRecallMain: computing the recall value.");
        double recall = PrecisionRecall.computeRecall(relevantAttackPaths, relevantExtractedPaths);
        System.out.println("PrecisionRecallMain: recall = " + recall + ".");
    }
}
