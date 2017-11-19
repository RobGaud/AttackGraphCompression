package test;

import attackpaths.IAttackPath;

import java.util.Collection;

/**
 * Created by Roberto Gaudenzi on 22/10/17.
 */
public class PrecisionRecall {

    public static float computeRecall(Collection<IAttackPath> relevantPaths, Collection<IAttackPath> retrievedPaths){
        int denominator = relevantPaths.size();
        float numerator = 0;
        for(IAttackPath rp : retrievedPaths){
            if(isRelevant(rp, relevantPaths))
                numerator++;
        }

        System.out.println("PrecisionRecall.computeRecall: denominator = " + denominator + ", numerator = " + numerator);
        return numerator / denominator;
    }

    public static float computePrecision(Collection<IAttackPath> relevantPaths, Collection<IAttackPath> retrievedPaths){
        int denominator = retrievedPaths.size();
        float numerator = 0;
        for(IAttackPath rp : retrievedPaths){
            if(isRelevant(rp, relevantPaths))
                numerator++;
        }

        System.out.println("PrecisionRecall.computePrecision: denominator = " + denominator + ", numerator = " + numerator);
        return numerator / denominator;
    }

    private static boolean isRelevant(IAttackPath path, Collection<IAttackPath> relevantPaths){
        for(IAttackPath rp : relevantPaths){
            if(rp.equals(path))
                return true;
        }
        // If we get here, then the path is not relevant
        return false;
    }
}
