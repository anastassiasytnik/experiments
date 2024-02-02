package experiments.photographyDirector;

import static experiments.photographyDirector.Constants.*;
import experiments.photographyDirector.Occupant;
import experiments.photographyDirector.MiddleElement;

/**
 * Class that traverses the provided input string and sorts the elements of the string
 * into 3 separate lists: list of photographers, list of actors and list of backdrops.
 * The lists of photographers and backdrops only stores the index of the element in the input string,
 * while the list of actors also stores additional computed information.
 * @see {@link MiddleElement}
 */
public class InputParser {
  
    /** The index of the input string that we stopped on */
    public int currentStrIdx = -1;
    /** The character of the input string that we stopped on */
    public char currentChar;
    /** The input string */
    public final String input;
    /** The length of the input string - used for multiple checks of whether we're finished or not */
    public final int inputLen;
    /** The minimum distance between the DIFFERENT elements to make the photograph artistic - provided in the input */
    public final int x;
    /** The maximum distance between the DIFFERENT elements to make the photograph artistic - provided in the input */
    public final int y;
    
    /** List (and 1st element) of photographer elements parsed from the input */
    public Occupant pListFirst = null;
    /**
     * List (and 1st element) of actor elements parsed from the input
     * with possibly additional computed info
     */
    public MiddleElement aListFirst = null;
    /** List (and 1st element) of backdrop elements parsed from the input */
    public Occupant bListFirst = null;
    /** Last element of photographers list */
    public Occupant pListLast = null;
    /** Last element of actors list */
    public MiddleElement aListLast = null;
    /** Last element of backdrop list */
    public Occupant bListLast = null;
    
    /**
     * Initializes the parser with artistic distances values and the actual input we need to parse.
     * @param input - the input to parse; a string containing characters '.', 'P', 'A' and 'B'
     * @param x - the minimum allowed distance between P and A and B and A for the photograph to be artistic
     * @param y - the maximum allowed distance between P and A and B and A for the photograph to be artistic
     */
    public InputParser(String input, int x, int y) {
      // we got arguments check in the task accepting. Probably should put it here also, but meh.
      this.input = input;
      this.inputLen = input.length();
      this.x = x;
      this.y = y;
    }
    
    /**
     * Tests whether we parsed the entire input.
     * @return
     */
    public boolean isFinished() {
      return inputLen <= currentStrIdx; 
    }
    
    /**
     * Examines the next character in the input string and adds it to one of the lists
     * if it's not a dot.
     */
    public void processChar() {
      currentStrIdx++;
      if (inputLen == currentStrIdx) {
        // we've reached the end
        return;
      }
      currentChar = input.charAt(currentStrIdx);
      if (P == currentChar) {
        if (null == pListFirst) {
          pListFirst = new Occupant(currentStrIdx);
          pListLast = pListFirst;
        } else {
          pListLast = pListLast.append(currentStrIdx);
        }
      } else if (B == currentChar) {
        if (null == bListFirst) {
          bListFirst = new Occupant(currentStrIdx);
          bListLast = bListFirst;
        } else {
          bListLast = bListLast.append(currentStrIdx);
        }
      } else if (A == currentChar) {
        if (null == aListFirst) {
          aListFirst = new MiddleElement(currentStrIdx, x, y);
          aListLast = aListFirst;
        } else {
          aListLast = aListLast.append(currentStrIdx, x, y);
        }
      }
    }

}
