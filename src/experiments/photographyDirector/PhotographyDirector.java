package experiments.photographyDirector;

import java.util.Hashtable;

/**
 * Interview preparation task on meta
 * @author Anastassia Sytnik
 * @since 02/01/2024 (Feb)
 */
public class PhotographyDirector {
  
  /** A struct that contains all the task input */
  public static class Input {
    public final int inputLen;
    public final String inputStr;
    public final int minDiff;
    public final int maxDiff;
    
    public Input (int n, String c, int x, int y) {
      this.inputLen = n;
      this.inputStr = c;
      this.minDiff = x;
      this.maxDiff = y;
    }
  }
  
  /** A value for all undefined indexes, counts etc (otherwise non-negative values) */
  public static final int UNDEFINED = -1;

  /** The character representing a photographer in the input string */
  public static final char P = 'P';
  
  /** The character representing an actor in the input string */
  public static final char A = 'A';
  
  /** The character representing a backdrop in the input string */
  public static final char B = 'B';
  
  public static class ArtisticCounter implements Runnable {
    private final Input given;
    private final char FIRST_ELEM;
    private final char LAST_ELEM;
    private Hashtable<Integer, Integer> actorsTails = new Hashtable<>();
    private int e1Limit;
    private int e2Limit;
    private int artisticCount = 0;
    private int e2Reset = UNDEFINED;
    private int e1 = UNDEFINED;
    private int e2 = UNDEFINED;
    // TODO do we need this one? or could it be local?
    private int e3 = UNDEFINED;
    
    public ArtisticCounter(Input input, char firstElem ) {
      //TODO check arguments?
      this.FIRST_ELEM = firstElem;
      if (P == firstElem) {
        LAST_ELEM = B;
      } else {
        LAST_ELEM = P;
      }
      given = input;
      e1Limit = given.inputLen - given.minDiff * 2;
      e2Limit = given.inputLen - given.minDiff;      
    }
    
    /**
     * {@inheritDoc}
     * 
     * This method only supposed to run once. It doesn't reset values to cleanly count again.
     */
    public void run() {
      System.out.println("*********STARTING with " + FIRST_ELEM + "************");
      System.out.println(given.inputStr);
      e1 = given.inputStr.indexOf(FIRST_ELEM);
      while (e1 < e1Limit && UNDEFINED != e1) {
        processActors();
        // Move to next one
        e1 = given.inputStr.indexOf(FIRST_ELEM, e1 + 1);
      }
    }
    
    private void processActors() {
      int e2Max = Math.min(e1 + given.maxDiff, e2Limit - 1);
      int e2Min = e1 + given.minDiff;
      boolean firstActor = true;
      // loop through all valid actors and count valid tails
      do {
        // if it's a first actor maybe we can use e2Reset
        if (firstActor) {
          if (UNDEFINED != e2Reset && e2Reset >= e2Min) {
            // sure we can.
            e2 = e2Reset;
          } else {
            // we can't - just find a new one
            e2 = given.inputStr.indexOf(A, e2Min);
          }
        } else {
          // not a first actor - make sure to search PAST 1st
          e2 = given.inputStr.indexOf(A, e2 + 1);
        }
        // now we got e2 (hopefully) or it's -1
        if (UNDEFINED == e2) {
          // there aren't any more actors for this 1st element - quit
          return;
        } 
        
        // this actor either good or too far - in any case, if it's first - mark them as reset
        if (firstActor) {
          //TODO you can optimize space by deleting records from actorsTails with indexes smaller than e2Reset
          e2Reset = e2; 
          firstActor = false;
        }
        
        if (e2 > e2Max) {
          // this actor is too far - we're done with actors for this 1st element - quit
          return;
        }
        
        // at this point we got the actor with "artistic" index 
        // check if tails already calculated
        Integer aCount = actorsTails.get(e2);
        if (null == aCount) {
          // no luck - go and count valid tails
          countTails();
        } else {
          // it's already counted - use it
          this.artisticCount += aCount;
          if (0 < aCount) {
            System.out.println("Found artistics: " + e1 + ", " + e2 + " and " + aCount + " tails");
          }
        }
      } while (e2 < e2Limit && e2 != UNDEFINED);
    }
    
    private void countTails() {
      int e3Min = e2 + given.minDiff;
      int e3Max = Math.min(given.inputLen - 1, e2 + given.maxDiff);
      int tailCount = 0;
      boolean freshActor = true;
      // TODO if we want to avoid extra .indexOf we would need to implement 2 points of reset for tails:
      // shorter reset for next actor and longer reset for new 1st element
      // - see multifile branch for this kind of optimization
      do {
        if (UNDEFINED == e3 || freshActor) {
          e3 = given.inputStr.indexOf(LAST_ELEM, e3Min);
          freshActor = false;
        } else {
          e3 = given.inputStr.indexOf(LAST_ELEM, e3 + 1);
        }
        // only count good tails
        if (UNDEFINED != e3 && e3 <= e3Max) {
          System.out.println("FOUND ARTISTIC: " + e1 + ", " + e2 + ", " + e3);
          tailCount++;
          this.artisticCount++;
        }
      } while (e3 <= e3Max && e3 != UNDEFINED);
      // finished with tails - save result
      this.actorsTails.put(e2, tailCount);
    }
    
    public void decompose() {
      this.actorsTails.clear();
      this.actorsTails = null;
    }
    
    public int getCount() {
      return this.artisticCount;
    }
  }

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
    Input given = new Input(N, C, X, Y);
    if (simpleNegativeChecks(given)) {
      return 0;
    }
    
    // there's a chance for artistic photos - search
    ArtisticCounter countProcessPAB = new ArtisticCounter(given, P);
    countProcessPAB.run();
    int result = countProcessPAB.getCount();
    countProcessPAB.decompose();
    countProcessPAB = null;
    ArtisticCounter countProcessBAP = new ArtisticCounter(given, B);
    countProcessBAP.run();
    result += countProcessBAP.getCount();
    countProcessBAP.decompose();
    return result;
    /* Now if the computer has enough memory we could run PAB and BAP in parallel like this:
    Thread runner1 = new Thread(countProcessPAB);
    runner1.start();
    countProcessBAP.run();
    int interruptionCount = 0;
    boolean success = false;
    // 10 is a "magic number" that should probably be in a constant and included in the error message
    while (runner1.isAlive() && interruptionCount < 10) {
      try {
        runner1.join();
      } catch (InterruptedException ex) {
        System.out.println("Oops, something went wrong");
        ex.printStackTrace();
      }
      success = true;
    }
    if (success || !runner1.isAlive()) { 
      return countProcessPAB.getCount() + countProcessBAP.getCount();
    } else {
      System.out.println("10 interruption while waiting on PAB to complete wasn't enough." +
                  " You can increase waiting limit and try again");
      throw new RuntimeException("waiting on PAB counting process to complete was interrupted 10 times.");
    }
     */
  }

  /**
   * Performs fast checks that would mean there are no artistic photographs possible.
   * The cases for which we check: 
   * <ul><li>X is bigger than half-length</li>
   *     <li>One of the letters (P, A, B) is not present in the string</li>
   *     
   * @param given
   * @return
   */
  public static boolean simpleNegativeChecks(Input given) {
    // if minimum distance is bigger than half length - no way we can get artistic photographs
    if (given.minDiff * 2 > given.inputStr.length()) {
      return true;
    }
    
    // if one of the letters missing - no way we can get artistic photograph.
    if ( -1 == given.inputStr.indexOf(P) || -1 == given.inputStr.indexOf(A) || -1 == given.inputStr.indexOf(B)) {
      return true;
    }
    // if there's no "A in the middle" - then there's no artistic photographs.
    int pIdx1 = given.inputStr.indexOf(P);
    int aIdx1 = given.inputStr.indexOf(A, pIdx1 + given.minDiff);
    int bIdx1 = -1;
    if (-1 != aIdx1 && aIdx1 < (given.inputLen - given.minDiff)) {
      bIdx1 = given.inputStr.indexOf(B, aIdx1 + given.minDiff);
    }
    int bIdx2 = given.inputStr.indexOf(B);
    int aIdx2 = given.inputStr.indexOf(A, bIdx2);
    int pIdx2 = -1; 
    if (-1 != aIdx2 && aIdx2 < (given.inputLen - given.minDiff)) {
      pIdx2 = given.inputStr.indexOf(P, aIdx2);
    }
    if (-1 == bIdx1 && -1 == pIdx2) {
      // there's no "A" in the middle
      return true;
    }
    return false;
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
