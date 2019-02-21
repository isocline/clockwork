package isocline.clockwork;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

;

public class Test3 {

    private String z;
    public Test3(String z) {
        this.z = z;
    }

    public void print(String x) {
        System.err.println(z+ " "+x);
    }
    public static void main(String[] args) throws Exception{
        long t1=System.currentTimeMillis();
        URL url = new URL("https://www.cyber-i.com");
        long t2=System.currentTimeMillis();

        URLConnection uc = url.openConnection();
        long t3=System.currentTimeMillis();
        InputStream is = uc.getInputStream();
        long t4=System.currentTimeMillis();

        BufferedReader reader = new BufferedReader( new InputStreamReader( is )  );
        long t5=System.currentTimeMillis();

        String line = null;
        while( ( line = reader.readLine() ) != null )  {
            System.out.println(line);
        }
        long t6=System.currentTimeMillis();
        reader.close();



        System.out.println(  t2-t1 );
        System.out.println(  t3-t2 );
        System.out.println(  t4-t3 );
        System.out.println(  t5-t4 );
        System.out.println(  t6-t5 );

        String [] strings = new String [] {
                "6", "5", "4", "3", "2", "1"
        };

        String str = "hello";
        Predicate<String> p = str::equals;
        p.test("world");




        List<String> list = Arrays.asList(strings);

        Test3 tt = new Test3("ㅎ");

        list.forEach(tt::print);

    }
}
