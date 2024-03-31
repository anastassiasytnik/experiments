package experiments.treedepth;

import java.util.ArrayList;

public class TreeDepth {
  public enum TraversingState {NEUTRAL, LEFT, RIGHT};
  public static class TreeNode {
    public TreeNode leftChild = null;
    public TreeNode rightChild = null;
    public TraversingState state = TraversingState.NEUTRAL;
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
    public String toString() {
      return "" + this.value + "(" + 
          (null == this.leftChild? null : this.leftChild.value) + 
          ", " + (null == this.rightChild? null : this.rightChild.value) + ")," + 
          this.state;
    }
  }

  /**
   * you need to add all the numbers that the digits create from root to each leaf and return the sum
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
    // TODO implement
    ArrayList<TreeNode> remains = new ArrayList<>();
    TreeNode current = root;
    int curNumber = 0;
    remains.add(current);
    while (remains.size() > 0) {
      curNumber = curNumber * 10 + current.value;
      // if it's a leaf - get the number and add to the result.
      if (null == current.leftChild && null == current.rightChild) {
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
          current = remains.get(remains.size() - 1);
        }
      } else if (TraversingState.NEUTRAL == current.state 
            && null != current.leftChild) {
        // this isn't a leaf.
        // only go to the left if we haven't been there yet
        current.state = TraversingState.LEFT;
        // if we have left child - move to the left child
        current = current.leftChild;
        remains.add(current);
      } else if (TraversingState.RIGHT != current.state 
          && null != current.rightChild) { 
        // this isn't a leaf, 
        // and we checked left child, but didn't check the right yet
        current.state = TraversingState.RIGHT;
        current = current.rightChild;
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
    // TODO create sample trees and test
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
  }
}
