package isocline.clockwork.dummy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TestAop {

    private String title;


    public void setTitle(String title) {
        this.title = title;
    }

    public static void main(String[] arg) throws Exception {

        TestAop test = new TestAop();


        TestAop proxyInstance2 = (TestAop) Proxy.newProxyInstance(
                TestAop.class.getClassLoader(),
                new Class[] { test.getClass() },
                (proxy, method, methodArgs) -> {
                    System.out.println("invoke method 2 ");
                    return method.invoke(test, methodArgs);
                });
        proxyInstance2.setTitle("hi 2");


        Method aMethod = null;



    }


}
