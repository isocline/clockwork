package isocline.clockwork.learning;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Loader extends Application{


    public List<float[]> load(String filepath) throws IOException {

        List<float[]> resultArray = new ArrayList<>();

        FileReader fr = new FileReader(new File(filepath));
        BufferedReader br = new BufferedReader(fr);
        while(br.ready()) {
            String line = br.readLine();

            String[] items = line.split(",");
            //System.err.println(items.length);

            boolean printLog = false;

            if(items[0].equals("4")) {
                printLog = true;
            }

            List<String[]> list = new ArrayList<>();


            int mode =0;

            if(mode==0) {
                //x axis


                for (int i = 0; i < 28; i++) {
                    String[] newItem = new String[28];
                    System.arraycopy(items, 1 + 28 * (i), newItem, 0, 28);
                    //System.err.println("--");
                    list.add(newItem);

                    //
                }

            }else {


                for (int x = 0; x < 28; x++) {
                    String[] newItem = new String[28];
                    for (int y = 0; y < 28; y++) {
                        newItem[y] = items[x + (y * 28) + 1];
                    }

                    list.add(newItem);


                }
            }



            float[] resultItem = new float[28];
            for(int y=0;y<28;y++) {

                String[] item = list.get(y);

                if(printLog) {
                    //process(item);
                }

                int preVal2 = 1;
                int preVal = 2;
                for(int x=0;x<28;x++) {
                    String val = item[x];

                    float f = Float.parseFloat(val);
                    f= f/256 ;

                    if(f<0.3) {
                        f=0;
                    }else if(f<0.6){
                        f=0.5F;
                    }else {
                        f=0.9F;
                    }

                    int nextVal = preVal+preVal2;

                    preVal2 = preVal;
                    preVal = nextVal;





                    if(f>0) {
                        f = nextVal;
                    }

                    if(y>0) {
                        f = f+resultItem[x];
                    }
                    if(printLog) {
                        //System.err.println(x +" = "+f);
                    }

                    resultItem[x]=f;


                }


            }



            if(printLog) {
                //System.err.println(">> "+line);
                process(resultItem);
                resultArray.add(resultItem);
                //System.exit(-1);
            }




        }

        return resultArray;


    }


    public void start(Stage stage) {
        stage.setTitle("Test");

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("x");

        final LineChart<Number,Number> lineChart = new LineChart<Number, Number>(xAxis,yAxis);
        Scene scene = new Scene(lineChart,800,600);

        String path = "/Users/sungkwonkim/Downloads/mnist_test.csv";

        Loader loader = new Loader();
        try {

            List<float[]> result = loader.load(path);

            XYChart.Series series = new XYChart.Series();
            int count = 0;
            for(float[] items: result) {
                count++;
                if(count>1) break;
                for(int i=0;i<items.length;i++) {
                    series.getData().add(new XYChart.Data(i, items[i]));
                }
            }

            float[] avg = new float[28];

            int seq=0;

            for(float[] items: result) {

                seq++;

                for(int i=0;i<items.length;i++) {
                    if(i==27 &&seq<5) {
                        System.err.println(seq+":"+i+"--> "+avg[i] + " "+items[i]);
                    }

                    if(seq>1) {
                        avg[i] = avg[i ] + items[i];

                    }else {
                        avg[i] = items[i];
                    }

                    if(i==27 && seq<5) {
                        System.err.println(seq+"::"+i+"--> "+avg[i] + " "+items[i]);
                    }

                }
            }
            lineChart.getData().add(series);

            int cnt = result.size();
            System.err.println("====== "+cnt);

            for(int x=0;x<avg.length;x++) {

                 avg[x] = avg[x]/cnt;

            }

            XYChart.Series series2 = new XYChart.Series();


            for(int x=0;x<avg.length;x++) {
                series2.getData().add(new XYChart.Data(x, avg[x]));
                System.err.println(x+" > "+avg[x]);



            }
            lineChart.getData().add(series2);


            //series.getData().add(new XYChart.Data(2, 23));


        }catch (IOException ioe) {
            ioe.printStackTrace();
        }





        stage.setScene(scene);
        stage.show();



    }

    private void process(float[] items) {
        for(Object item:items) {
            System.err.print(item +" ");
        }
        System.err.println();
    }

    private void process(String[] items) {
        for(Object item:items) {
            System.err.print(item +" ");
        }
        System.err.println();
    }

    public static void main(String[] args) throws Exception{
        /*
        String path = "/Users/sungkwonkim/Downloads/mnist_test.csv";

        Loader loader = new Loader();
        loader.load(path);

*/
        launch(args);
    }

}