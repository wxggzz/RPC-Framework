import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wxg
 * @version 1.0
 * @date 2022/6/18 16:19
 */

class Solution {

    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode() {}
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }
    int mad = 0;
    List<Integer> ansPath = new ArrayList<>();
    /**
     1.计算直径和计算高度一样，分别算出两边的 再比较

     */
    public int diameterOfBinaryTree(TreeNode root) {
        if(root == null){
            return 0;
        }
        dfs(root);
        return mad;
    }
    public Pair dfs(TreeNode root){
        if(root == null){
            return new Pair(0,new ArrayList<>());
        }
        Pair leftPair = dfs(root.left);
        Pair rightPair = dfs(root.right);

        int res =0;
        List<Integer> resPath = new ArrayList<>();
        if(leftPair.sum > 0 && leftPair.sum > rightPair.sum){
            res = res + leftPair.sum;
            resPath.addAll(leftPair.path);
            resPath.add(root.val);
        }else if(rightPair.sum > 0 && rightPair.sum > leftPair.sum){
            res = root.val + rightPair.sum;
            resPath.addAll(leftPair.path);
            resPath.add(leftPair.sum);
        }else{
            resPath.add(root.val);
        }


        //更新最终结果
        if(leftPair.sum + rightPair.sum + root.val > mad){
            mad = leftPair.sum + rightPair.sum;
            ansPath.addAll(leftPair.path);
            ansPath.add(root.val);
            ansPath.addAll(rightPair.path);
        }
        return new Pair(res,resPath);
    }

    static class Pair{
        int sum;
        List<Integer> path;
        public  Pair(int sum, List<Integer> path){
            this.sum = sum;
            this.path = path;
        }
    }


}
