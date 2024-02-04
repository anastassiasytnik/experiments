package experiments.photographyDirector;

import static experiments.photographyDirector.Constants.*;

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
    
    InputParser parser = new InputParser(C, X, Y);
    SearchProcess pab = new SearchProcess(X, Y, PHOTOGRAPHER_FIRST, null);
    SearchProcess bap = new SearchProcess(X, Y, !PHOTOGRAPHER_FIRST, null);
    
    for (int i = 0; i < N; i++) {
      parser.processChar();
      pab.proceed(parser);
      bap.proceed(parser);
      releaseProcessedObjects(parser, pab, bap);
    }
    // our search processes could have been waiting on a tail, but it didn't happen, 
    // this doesn't mean that with a different start we don't have more uncollected artistic photos, 
    // so make sure to collect the rest
    pab.proceed(parser);
    bap.proceed(parser);
    return pab.currentCount + bap.currentCount;
  }
  
  /**
   * unlinks the objects that were already used in search/count of the combinations and no longer needed
   * @param parser - the current parser
   * @param pab - search process that collects combinations where "P" is first
   * @param bap - search process that collects combinations where "B" is first.
   */
  public static void releaseProcessedObjects(InputParser parser, SearchProcess pab, SearchProcess bap) {
    // not doing argument check for brevity sake
    // also we could sort which process is pab and bap here to not rely on correct parameter passing, but 
    // same reason (brevity) not doing that

    // clean all processed and no longer needed photographers
    if (null != pab.lastFreedPb1 && null != bap.lastFreedPb3) {
      int firstFreedPhotographerIdx = Math.min(pab.lastFreedPb1.idx, bap.lastFreedPb3.idx);
      while (parser.pListFirst.idx <= firstFreedPhotographerIdx) {
        parser.pListFirst = parser.pListFirst.removeFirst();
      }
      if (firstFreedPhotographerIdx == pab.lastFreedPb1.idx) {
        pab.lastFreedPb1 = null;
      }
      if (firstFreedPhotographerIdx == bap.lastFreedPb3.idx) {
        bap.lastFreedPb3 = null;
      }
    }
    
    // clean all processed and no longer needed backdrops
    if (null != pab.lastFreedPb3 && null != bap.lastFreedPb1) {
      int firstFreedBackdropIdx = Math.min(pab.lastFreedPb3.idx, bap.lastFreedPb1.idx);
      while (parser.bListFirst.idx <= firstFreedBackdropIdx) {
        parser.bListFirst = parser.bListFirst.next;
      }
      if (firstFreedBackdropIdx == pab.lastFreedPb3.idx) {
        pab.lastFreedPb3 = null;
      }
      if (firstFreedBackdropIdx == bap.lastFreedPb1.idx) {
        bap.lastFreedPb1 = null;
      }
    }

    // clean all processed and no longer needed actors
    if (null != pab.lastFreedA && null != bap.lastFreedA) {
      int firstFreedActorIdx = Math.min(pab.lastFreedA.idx, bap.lastFreedA.idx);
      while (parser.aListFirst.idx <= firstFreedActorIdx) {
        MiddleElement freed = parser.aListFirst;
        parser.aListFirst = freed.removeFirst();
        freed.qualifiedBTail = null;
        freed.qualifiedPTail = null;
      }
      if (firstFreedActorIdx == pab.lastFreedA.idx) {
        pab.lastFreedA = null;
      }
      if (firstFreedActorIdx == bap.lastFreedA.idx) {
        bap.lastFreedA = null;
      }
    }
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
