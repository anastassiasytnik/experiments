package experiments.photographyDirector;

import static experiments.photographyDirector.Constants.UNDEFINED;

import experiments.photographyDirector.Occupant;

/**
 * A class to handle "artistic" tail info/calculations etc.
 * It will also hold index for "A" (Actor) elements of the input string just like other occupants.
 */
public class MiddleElement extends Occupant {
    
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
     * Link to the first "valid" photographer-tail when we reach it.
     * (valid = index isn't too small, bigger than or equal to minTailIdx).
     * Might be the biggest index available, but still too small because we hadn't parsed far enough yet.
     * We can use it as first check for NEXT actor and ignore all previous photographers, because
     * if their indexes were too small for this actor, they definitely will be too small for actor with bigger index.
     */
    public Occupant qualifiedPTail = null;
    
    /**
     * Link to the first "valid" backdrop tail or closest thing we reached so far.
     * See {@link #qualifiedPTail} for details.
     */
    public Occupant qualifiedBTail = null;
    
    /**
     * this flag should be {@code true} if this actor wasn't yet used to find any backdrop artistic tails.
     */
    public boolean freshPabActor = true;
    
    /**
     * this flag should be {@code true} if this actor wasn't yet used to find any photographer tails.
     */
    public boolean freshBapActor = true;
    
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
