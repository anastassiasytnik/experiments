package experiments;

/**
 * Interview preparation task on meta
 * @author Anastassia Sytnik
 * @since 02/01/2024 (Feb)
 */
public class PhotographyDirector {
  
  /** A value for all undefined indexes, counts etc (otherwise non-negative values) */
  public static final int UNDEFINED = -1;
  
  /**
   * A class to string a list of primitive integers that represent indexes in a string
   * (might add some additional info later) 
   * TODO Probably could and should just use LinkedList, but we'll see.
   */
  private static class Occupant {
    /** index of a character in a string */
    public int idx = -1;
    /** next index of supposedly the same character */
    public Occupant next = null;
    
    /**
     * Creates a list member out of index. Substitutes all negative arguments with
     *  {@link PhotographyDirector#UNDEFINED} 
     */
    public Occupant(int idxValue) {
      if (UNDEFINED > idxValue) {
        this.idx = UNDEFINED;
      } else {
        this.idx = idxValue;
      }
    }
    
    /**
     * creates another element from the provided index and add a reference to it as next element.
     * WARNING: if another element was referenced in {@code this.next} the reference to it will 
     * be replaced with the new element and the former value might be lost forever.
     * It's unsafe, but atm I don't want extra overhead - just bare bones that I need.
     * 
     * @param idxValue - the value of the next element
     * @return the newly created element.
     */
    public Occupant append(int idxValue) {
      Occupant next = new Occupant(idxValue);
      this.next = next;
      return this.next;
    }
    
    /**
     * sets {@code null} to the {@code this.next} so this element is detached 
     * 
     * @return previous value of {@code this.next} field (former second, and now first element of this list)
     */
    public Occupant removeFirst() {
      Occupant result = this.next;
      this.next = null;
      return result;
    }
  }
  
  private static class MiddleElement {
    public int idx = 0;
  }

  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
