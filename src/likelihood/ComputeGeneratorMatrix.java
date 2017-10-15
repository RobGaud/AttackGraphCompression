package likelihood;

/**
 * Created by Roberto Gaudenzi on 15/10/17.
 */
public class ComputeGeneratorMatrix {

    public static Float[][] execute(Float[] exitRates, Float[][] transProb){
        int n = exitRates.length;
        Float[][] genMatrix = new Float[n][n];

        Float[][] IminusP = new Float[n][n];
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
                for(int k = 0; k < n; k++){
                    if(i == k)
                        genMatrix[i][j] += -1 * exitRates[i] * IminusP[k][j];
                    // Else, exitRates[i][k] is zero and therefore the product is zero => NOTHING TO ADD
                }
            }
        }

        return genMatrix;
    }
}
