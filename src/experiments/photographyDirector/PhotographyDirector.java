package experiments.photographyDirector;

/**
 * Interview preparation task on meta
 * @author Anastassia Sytnik
 * @since 02/01/2024 (Feb)
 */
public class PhotographyDirector {
  
  /**
   * Given a string C consisting of letters P, A, B and a dot ('.') 
   * finds the number of 3-letter positions combinations where each combination of letters
   * consist of one occurence of letters P, A and B and their indexes (positions) in the input string C
   * differ from each other by less or equal Y and by more or equal X, and letter A is "in the middle"
   * TODO should put full description in the package.info
   * @param N - the length of input string, supposedly between 1 and 100000
   * @param C - the input string
   * @param X - the minimum allowed index difference between selected letters
   * @param Y - the maximum allowed index difference between selected letters
   * @return - the number of specified combinations that can be found within the provided input string.
   */
  public static int getArtisticPhotographCount(int N, String C, int X, int Y) {
    if (null == C || N != C.length() || 0 == N || 0 >= X || 0 >= Y || X > Y || Y > N) {
      throw new IllegalArgumentException("Input parameters do not satisfy task criteria");
    }
    //string C isn't checked that it doesn't contain other characters (than specified)
    // TODO implement
    return 0;
  }
  
  public static void main(String[] args) {
    String[] test = {
        "APABA",
        ".PBAAP.B",
        "PPPPPPPPPP",
        ".......",
        "PPPPBABABAPABBAAPPBBPAB.A.BPABBAPABBAPPAB"
    };
    int[] x = { 1, 1, 2, 2, 3 }; 
    int[] y = { 2, 3, 5, 4, 6 };
    System.out.println(test[4].length());
    for (int i = 0; i < 5; i++) {
      int result = getArtisticPhotographCount(test[i].length(), test[i], x[i], y[i]);
      System.out.println("TEST" + i + ": " + result);
    }
    
  }

}
