package likelihood;

/**
 * Created by Roberto Gaudenzi on 15/10/17.
 */
public class ComputeGeneratorMatrix {

    public static double[][] execute(double[] exitRates, double[][] transProb){

        int n = exitRates.length;
        double[][] genMatrix = new double[n][n];

        Double[][] IminusP = new Double[n][n];
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                if(i == j)
                    IminusP[i][j] = 1 - transProb[i][j];
                else
                    IminusP[i][j] = -1 * transProb[i][j];
            }
        }

        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                genMatrix[i][j] = 0.0f;
                for(int k = 0; k < n; k++){
                    if(i == k) {
                        genMatrix[i][j] += -1 * exitRates[k] * IminusP[k][j];
                    }
                    // Else, exitRates[i][k] is zero and therefore the product is zero => NOTHING TO ADD
                }
            }
        }

        return genMatrix;
    }
}
