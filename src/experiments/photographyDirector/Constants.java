package experiments.photographyDirector;

/** Some common constants */
public class Constants {
  
  /** A value for all undefined indexes, counts etc (otherwise non-negative values) */
  public static final int UNDEFINED = -1;

  /** The character representing a photographer in the input string */
  public static final char P = 'P';
  
  /** The character representing an actor in the input string */
  public static final char A = 'A';
  
  /** The character representing a backdrop in the input string */
  public static final char B = 'B';
  
  /** The character representing an empty space in the input string */
  public static final char DOT = '.';
  
  /**
   * A boolean value for the flag that indicates that in the current search for artistic photographs
   * we are looking for photographs where.
   * a photographer will have smaller index in the input string than actor or backdrop.
   */
  public static final boolean PHOTOGRAPHER_FIRST = true;

  /**
   * Represents search state - whether we need to parse input until we find element of certain type
   * or whether we can move ahead counting artistic photographs using already parsed elements.
   */
  public enum Waiting {ON_PHOTOGRAPHER, ON_ACTOR, ON_BACKDROP, NOT_WAITING}
}
