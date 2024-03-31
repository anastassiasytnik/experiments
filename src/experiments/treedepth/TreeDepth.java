package experiments.treedepth;

import java.util.ArrayList;

/**
 * Class that provides a solution to a problem 
 * where you need to add all the numbers that the digits create from root to each leaf 
 * and return the sum. See {@link #sumBranches(TreeNode)} for examples and details.
 * @since March, 2024
 */
public class TreeDepth {
  /** State for each node in non-recursive tree traversal */
  public enum TraversingState {NEUTRAL, LEFT, RIGHT};
  
  /**
   * Tree as defined in the problem description
   *  (have to have separate fields for left and right child and a value).
   *  For the purpose of brevity the children are public without getter and setter.
   *  The method {@code equals()} and {@code hashcode()} aren't overridden
   *  because this task doesn't require it, but they probably should be
   *  if the tree was to be used someplace else.
   */
  public static class TreeNode {
    
    public TreeNode leftChild = null;
    public TreeNode rightChild = null;
    /** supposed to be single digit. Could have used char and convert */
    public final int value;
    
    public TreeNode(int value) {
      if (0 > value || 9 < value) {
        throw new IllegalArgumentException("Node value can be only single digit (positive)");
      }
      this.value = value;
    }
    public TreeNode addRightChild(TreeNode child) {
      this.rightChild = child;
      return this;
    }
    public TreeNode addLeftChild(TreeNode child) {
      this.leftChild = child;
      return this;
    }
    public TreeNode addChildren(TreeNode left, TreeNode right) {
      this.rightChild = right;
      this.leftChild = left;
      return this;
    }
    
    @Override
    public String toString() {
      return "" + this.value + "(" + 
          (null == this.leftChild? null : this.leftChild.value) + 
          ", " + (null == this.rightChild? null : this.rightChild.value) + ")"; 
    }
  }

  /**
   * Basically a stack member for keeping traversing state to avoid recursion.
   * It's a struct-like thing, so again for brevity no getters and setters and public access.
   */
  public static class TraverseInfo {
    public final TreeNode node;
    public TraversingState state = TraversingState.NEUTRAL;
    public TraverseInfo(TreeNode current) {
      this.node = current;
    }
  }
  
  /**
   * Add all the numbers that the digits create from root to each leaf and return the sum
   * Empty tree results in 0 being returned.
   * 
   * Example:
   *       7
   *      / \
   *     3   5
   *    / \
   *   6   1
   *    \
   *     8
   *    / \
   *   2   4
   *   
   *   should result in 73682 + 73684 + 731 + 75
   * 
   * @param root - the tree to traverse
   * @return the number that consists of digits (values of the tree nodes)
   */
  public static long sumBranches(TreeNode root) {
    long result = 0;
    if (null == root) {
      return 0;
    }
    
    // stack that allows to avoid recursion.
    ArrayList<TraverseInfo> remains = new ArrayList<>();
    TraverseInfo current = new TraverseInfo(root);
    // number currently accumulated from all already-traversed ancestors
    int curNumber = 0;
    remains.add(current);
    while (remains.size() > 0) {
      curNumber = curNumber * 10 + current.node.value;
      // if it's a leaf - get the number and add to the result.
      if (null == current.node.leftChild && null == current.node.rightChild) {
        result += curNumber;
        System.out.println("New leaf number is: " + curNumber);
        System.out.println(">>Result: " + result);
        // now the parent's digit is already added as was the parent - 
        // so move onto the parent's right child (what if it's null?)
        // and remove last leaf digit of the left child from current number
        // the rounding just drops any decimal part
        curNumber /= 100;
        remains.remove(remains.size() - 1);
        if (0 < remains.size()) {
          current = (remains.get(remains.size() - 1));
        }
      } else if (TraversingState.NEUTRAL == current.state 
            && null != current.node.leftChild) {
        // this isn't a leaf.
        // only go to the left if we haven't been there yet
        current.state = TraversingState.LEFT;
        // if we have left child - move to the left child
        current = new TraverseInfo(current.node.leftChild);
        remains.add(current);
      } else if (TraversingState.RIGHT != current.state 
          && null != current.node.rightChild) { 
        // this isn't a leaf, 
        // and we checked left child, but didn't check the right yet
        current.state = TraversingState.RIGHT;
        current = new TraverseInfo(current.node.rightChild);
        remains.add(current);
      } else {
        // this isn't a leaf 
        // and we checked both left and right children already
        // so just ascend if not the root
        current.state = TraversingState.NEUTRAL;
        remains.remove(remains.size() - 1);
        curNumber /= 100;
        if (0 < remains.size()) {
          current = remains.get(remains.size() - 1);
        }
      }
    }
    return result;
  }
  
  public static void main(String[] args) {
    TreeNode test1 = new TreeNode(3);
    test1.leftChild = new TreeNode(4);
    test1.leftChild.leftChild = new TreeNode(1);
    test1.leftChild.rightChild = new TreeNode(7);
    test1.rightChild = new TreeNode(5);
    System.out.println(sumBranches(test1));
    
    TreeNode test2 = new TreeNode(7);
    test2.addChildren(
        (new TreeNode(3)).addChildren(
            (new TreeNode(6)).addRightChild(
                new TreeNode(8).addChildren(
                    new TreeNode(2), new TreeNode(4)
                )
            ), 
            new TreeNode(1)
        ), 
        new TreeNode(5)
    );
    System.out.println(sumBranches(test2));
    
    TreeNode test3 = new TreeNode(1);
    System.out.println(sumBranches(test3));
    System.out.println(sumBranches(null));
  }
}
