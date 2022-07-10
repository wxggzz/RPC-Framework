/**
 * @author wxg
 * @version 1.0
 * @date 2022/6/25 10:09
 */
public class test {
    public    static   int pre []=new int[]{1, 2, 3, 4, 5, 6};
    public    static   int mid[]=new int[]{3, 2, 4, 1, 6, 5};
    public static   void post(int root, int start, int end){
        if(start>end)
            return;
        int i=start;
        //定位根在中序的位置
        while (i < end && mid[i]!= pre[root]) i++;
        //递归处理左子树
        post(root+1,start, i-1);
        //递归处理右子树
        post(root+1+i-start,i+1,end);
        System.out.print(mid[i] + " ");
    }

    public static void main(String[] args) {
        post(0,0,pre.length-1);

    }
}


