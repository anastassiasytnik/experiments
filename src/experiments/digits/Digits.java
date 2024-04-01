package experiments.digits;


public class Digits {

  
  /**
   * given an array of digits (from 0 to 1) representing positive number,
   * perform 0 or 1 swap between 2 digits to achieve biggest number.
   * Example: given {1, 9, 5, 3, 2, 4, 5} return {9, 1, 5, 3, 2, 4, 5}
   * @param input - array of digits
   * @return array of digits that differs by at most 1 swap between 2 digits 
   * that represent biggest number possible to achieve by that single swapping
   */
  public static int[] swapToBiggest(int[] input) {
    if (null == input) {
      return null; // or should we throw exception?
    }
    
    if (1 == input.length) {
      return input; // nothing to swap
    }
    
    // so the new approach considering the following test:
    // [9,9,9,9,7,7,3,3,3,1,1,1,3,2,2,2,3,3,4,1,1,1,4,3,3,2,2,1,1]
    // the answer we want to get would be [9,9,9,9,7,7,4,3,3,1,1,1,3,2,2,2,3,3,4,1,1,1,3,3,3,2,2,1,1]

    // so the new approach is to remember all descending points before flip (maximum of 10) on the front, 
    // while looking for max at the end after the descend piece.
    // and then flip the first lesser digit from the descending points with the found tail maximum.
    // descending points in our case would be d[9] = 0; d[8] = UNDEFINED; d[7] = 4; d[6] = d[5]= d[4] = UNDEFINED; d[3] = 6;
    // d[2] = UNDEFINED; d[1] = 9; d[0] = UNDEFINED;
    // tailMax = 4 @ index 19
    
    final int UNDEFINED = -1;
    final int NUM_OF_DIGITS = 10;
    int[] descendIdx = new int[NUM_OF_DIGITS];
    for (int i = 0; i < NUM_OF_DIGITS; i++) {
      descendIdx[i] = UNDEFINED;
    }
    //[9,9,9,9,7,7,3,3,3,1,1,1,3,2,2,2,3,3,4,1,1,1,4,3,3,2,2,1,1]
    // 0 1 2 3 4 5 6 7 8 9 
    //[  9   6       4   0] <- descendIdx for the array above. empty spaces are UNDEFINED value
    int stopIdx = UNDEFINED;
    
    // Find "flip point" - point in the array where next digit is bigger than current.
    // also populate descendIdx with FIRST occurence indexes of encountered digits
    for (int i = 0; i < input.length - 1; i++) {
      int current = input[i];
      if (UNDEFINED == descendIdx[current]) {
        descendIdx[current] = i;
      }
      if (current < input[i + 1]) {
        stopIdx = i;
        break;
      }
    }
    
    // Now if we didn't find stopIdx - then there's nothing to flip and we can return input as-is (or rather it's copy)
    int[] result = new int[input.length];
    System.arraycopy(input, 0, result, 0, input.length);

    if (UNDEFINED == stopIdx) {
      return result;
    }
    
    // here we did find the stopIdx (flip point), so we need to know maximum BEFORE this point at the latest position
    // and that maximum will be the value with flip with
    int tailMaxIdx = UNDEFINED;
    int tailMax = UNDEFINED;
    
    int lastIdx = input.length - 1;
    
    // traverse from the end and find first occurence of the max number among the tail.
    for (int i = lastIdx; i > stopIdx; i--) {
      int current = input[i];
      if (current > tailMax) {
        tailMax = current;
        tailMaxIdx = i;
      }
      if (9 == tailMax) {
        break;
      }
    }
    
    // now we have tail max AND stop point. Find first digit from the 1st descend that is less than tailMax,
    // and we will swap it with tailMax.
    int swapDigit = UNDEFINED;
    for (int i = tailMax - 1; i >= 0; i--) {
      if (UNDEFINED != descendIdx[i]) {
        swapDigit = i;
        break;
      }
    }
    // now we have idx of biggest digit smaller than tailMax - we can swap it with tailMax
    result[tailMaxIdx] = swapDigit;
    result[descendIdx[swapDigit]] = tailMax;
    return result;
  }
    
  
  private static String printArray(int[] input) {
    StringBuilder result = new StringBuilder("[");
    for (int num : input) {
      result.append(num).append(", ");
    }
    result.setLength(result.length() - 2);
    result.append("]");
    return result.toString();
  }
  
  public static void main(String[] args) {
    int[][] tests = {{1, 2, 3, 4, 5, 6, 7, 8, 9, 0},
                     {9, 8, 7, 6, 5, 4, 3, 2, 1, 0},
                     {0, 9, 1, 2, 3, 4, 5, 6, 7, 8},
                     {9, 9, 9, 9, 9, 9, 9, 9},
                     {9, 1, 9, 5, 3, 7, 6, 4, 2, 9},
                     {9, 8, 9, 5, 3, 7, 6, 4, 2, 9},
                     {9, 8, 1, 9, 8, 1, 9, 8, 1, 9},
                     {9,9,9,9,7,7,3,3,3,1,1,1,3,2,2,2,3,3,4,1,1,1,4,3,3,2,2,1,1}};
    for (int[] test : tests) {
      System.out.println(">>" + printArray(test) + "<< : >> " + printArray(swapToBiggest(test)));
    }

  }

}
