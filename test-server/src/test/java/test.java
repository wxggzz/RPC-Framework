/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/05/28/12:35
 * @Description:
 */
public class test {
    public static void main(String[] args) {
        String a = "192";
        System.out.println(Long.parseLong(a));
        System.out.println(Long.parseLong(a)<<24);

        long d = Long.parseLong("167773121");
        String out = String.format("%d.%d.%d.%d", (d >> 24) & 0xff, (d >> 16) & 0xff, (d >> 8) & 0xff, d & 0xff);
        System.out.println(d >> 24 );
        System.out.println(out);
    }
}
