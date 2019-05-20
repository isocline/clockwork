package isocline.clockwork.dummy;

public class TestSplit {

    public static void main(String[] args) throws Exception {

        String txt="123&345,678";

        String[] lists = txt.split("&");

        for(String x:lists) {
            System.out.println(x);
        }
    }
}
