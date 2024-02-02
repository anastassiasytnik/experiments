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
  
  /**
   * A class to handle "artistic" tail info/calculations etc.
   * It will also hold index for "A" (Actor) elements of the input string just like other occupants.
   */
  private static class MiddleElement extends Occupant{
    
    /** index of "A" character in the input string. */
    public int idx = 0;
    
    /**
     * The minimum valid value for the index of the 3rd element in the composition
     * for the photo to be artistic.
     * Calculated as the index of THIS element + X value from the task's input.
     */
    public final int minTailIdx;
    
    /**
     * The maximum valid value for the index of the 3rd element in the composition
     * for the photo to be artistic.
     * Calculated as the index of THIS element + Y value from the task's input.
     */
    public final int maxTailIdx;
    
    /**
     * Same Actor will always have same number of "valid" artistic tails, but 
     * same actor can be valid for multiple 1st elements,
     * therefore once the number of "valid tails" is calculated - it makes sense to keep and reuse it,
     * rather than perform calculations every time. 
     * This variable will hold count of "valid tails" of photographers.
     */
    public int pTailCount = UNDEFINED;
    
    /**
     * See {@link #pTailCount} for details.
     * This variable will hold count of "valid tails" of backdrops.
     */
    public int bTailCount = UNDEFINED;
    
    /**
     * Link to the first "valid" photographer-tail. (valid = index isn't too small, bigger than minTailIdx)
     * We can use it as first check for NEXT actor and ignore all previous photographers, because
     * if their indexes were too small for this actor, they definitely will be too small for actor with bigger index.
     */
    Occupant qualifiedPTail = null;
    /** Link to the first "valid" backdrop tail. See {@link #qualifiedPTail} for details. */
    Occupant qualifiedBTail = null;
    
    /**
     * Constructor required by inheritance. Should not be used by actual actor-holding element, 
     * because it will make calculations of valid tail impossible.
     * This assigns {@link PhotographyDirector#UNDEFINED} value to {@link #minTailIdx} and {@link #maxTailIdx}
     * @param idxValue - the index value to store.
     */
    public MiddleElement(int idxValue) {
      super(idxValue);
      this.minTailIdx = UNDEFINED;
      this.maxTailIdx = UNDEFINED;
    }

    /**
     * Min info constructor - stores the provided index and calculates boundaries for valid tails.
     * @param idxValue - index to store
     * @param x - the X from the task's input 
     *             - minimal distance between actor and other elements for the photo to be artistic
     * @param y - the Y from the task's input 
     *             - maximum distance between actor and other elements for the photo to be artistic
     */
    public MiddleElement(int idxValue, int x, int y) {
      super(idxValue);
      if (UNDEFINED == this.idx || UNDEFINED >= x || UNDEFINED >= y ) {
        this.minTailIdx = UNDEFINED;
        this.maxTailIdx = UNDEFINED;
      } else {
        this.minTailIdx = idx + x;
        this.maxTailIdx = idx + y;
      }
    }
    
    /**
     * @throws IllegalArgumentException - always because append(int, int, int) should be used instead.
     */
    public Occupant append(int idxValue) throws IllegalArgumentException {
      throw new IllegalArgumentException("Need X and Y information to add an actor for tail calculations.");
    }
    
    /**
     * Creates an element with the provided parameters, provides it with current element's 
     * tail references if they exists and adds newly created element as {@link Occupant#next} value 
     * for {@code this} element.
     * @param idxValue - the index value of the next element.
     * @param x - the minimum distance between occupants for artistic photo
     * @param y - the maximum distance between occupants for artistic photo
     * @return newly created element.
     */
    public MiddleElement append(int idxValue, int x, int y) {
      MiddleElement result = new MiddleElement(idxValue, x, y);
      result.qualifiedBTail = this.qualifiedBTail;
      result.qualifiedPTail = this.qualifiedPTail;
      this.next = result;
      return result;
    }

    /**
     * Replaces all the reference fields with {@code null}s to simplify garbage collection.
     * 
     * @return the element that was stored as {@link Occupant#next} field
     *         (supposedly formerly second and now first element of the list)
     */
    public MiddleElement removeFirst() {
      MiddleElement result = null;
      if (null != this.next && !(this.next instanceof MiddleElement)) {
        throw new IllegalStateException("Non-actor in the actor list!");
      }
      result = (MiddleElement) this.next;
      // untie element we're deleting from everything, so garbage collector won't have any weird problems.
      this.next = null;
      this.qualifiedBTail = null;
      this.qualifiedPTail = null;
      return result;
    }
  }

  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
