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
    final int UNDEFINED = -1;
    int flipIdx = UNDEFINED;
    int flip = UNDEFINED;
    int maxIdx = UNDEFINED;
    int max = UNDEFINED;
    for (int i = 0; i < input.length; i++) {
      // check for flip. Once flipIdx initialized for real - we don't worry about it anymore
      if (flip == UNDEFINED) {
        flip = input[i];
      } else if (flipIdx == UNDEFINED && flip < input[i]) {
        flipIdx = i - 1;
      } else if (flipIdx == UNDEFINED) {
        flip = input[i];
      }
      // check for max. We are interested in biggest max index. 
      // Since we have DIGITS - once we found "9" - game's over.
      if (max == UNDEFINED) {
        max = input[input.length - 1]; 
        maxIdx = input.length - 1;
      } else if (max < input[input.length - i - 1]) {
        // found bigger one
        max = input[input.length - i - 1];
        maxIdx = input.length - i - 1;
      }
      // check if we still searching, or if we are done and don't need to traverse anymore
      if (UNDEFINED != flipIdx && 9 == max) {
        break;
      }
    }
    int[] result = new int[input.length];
    System.arraycopy(input, 0, result, 0, input.length);
    // if we didn't find flip - the biggest number is the one we have
    if (UNDEFINED == flipIdx) {
      return result;
    }
    // if we found flip - is max bigger than first member?
    if (max > input[0]) {
      // then flipping max with 1st character (is that right?)
      flip = input[0];
      flipIdx = 0;
    }
    // flip to make a bigger number
    result[flipIdx] = max;
    result[maxIdx] = flip;
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
  
  public static class Node {
    public int value;
    public Node left;
    public Node right;
  }
  
  public static void main(String[] args) {
    int[][] tests = {{1, 2, 3, 4, 5, 6, 7, 8, 9, 0},
                     {9, 8, 7, 6, 5, 4, 3, 2, 1, 0},
                     {0, 9, 1, 2, 3, 4, 5, 6, 7, 8},
                     {9, 9, 9, 9, 9, 9, 9, 9},
                     {9, 1, 9, 5, 3, 7, 6, 4, 2, 9},
                     {9, 8, 9, 5, 3, 7, 6, 4, 2, 9},
                     {9, 8, 1, 9, 8, 1, 9, 8, 1, 9}};
    for (int[] test : tests) {
      System.out.println(">>" + printArray(test) + "<< : >> " + printArray(swapToBiggest(test)));
    }

  }

}
