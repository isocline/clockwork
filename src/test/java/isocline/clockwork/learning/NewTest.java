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

public class NewTest extends Application {


    private int math(int x, int y) {
        //return Math.abs(x - y);

        int g = Math.abs(x - y);
        if(g>50) return 10;

        return (x+y)/2;



    }


    public int[][] process(int[][] inputModel, int mode, int depth) {


        int width = inputModel[0].length;
        int height = inputModel.length;

        int[][] returnModel = null;

        if (mode == 0) {
            returnModel = new int[height][width - 1];

            for (int y = 0; y < height; y++) {


                for (int x = 1; x < width - 1; x++) {

                    int newVal = math(inputModel[y][x - 1], inputModel[y][x]);

                    returnModel[y][x - 1] = newVal;

                }

            }
            mode = 1;

        } else {
            returnModel = new int[height - 1][width];

            for (int x = 0; x < width; x++) {
                for (int y = 1; y < height - 1; y++) {

                    int newVal = math(inputModel[y - 1][x], inputModel[y][x]);

                    returnModel[y - 1][x] = newVal;

                }
            }
            mode = 0;
        }

        depth--;

        if (depth < 1) {
            return returnModel;
        } else {
            return process(returnModel, mode, depth);
        }


    }


    public List<int[]> load(String filepath) throws IOException {

        List<int[]> resultArray = new ArrayList<>();

        FileReader fr = new FileReader(new File(filepath));
        BufferedReader br = new BufferedReader(fr);
        while (br.ready()) {
            String line = br.readLine();

            String[] items = line.split(",");

            if(!items[0].equals("3")) {
                continue;
            }


            int[][] model = new int[28][28];

            int seq = -1;

            for (int y = 0; y < 28; y++) {

                for (int x = 0; x < 28; x++) {
                    seq++;
                    int val = Integer.parseInt(items[seq + 1]);

                    model[y][x] = val;



                }
            }


            int[][] newModel = process(model, 0, 30);

            int h = newModel.length;
            int w = newModel[h - 1].length;


            int sz = h*w;

            System.out.println(h+" X "+ w +" <<<<<<<<"+sz);


            int[] stream = new int[sz];

            seq = -1;
            for (int y = 0; y < h; y++) {

                for (int x = 0; x < w; x++) {
                    seq++;
                    stream[seq] = newModel[y][x];

                }

            }

            resultArray.add(stream);
            //System.exit(-1);


        }

        return resultArray;


    }


    public void start(Stage stage) {
        stage.setTitle("Test");

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("x");

        final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
        Scene scene = new Scene(lineChart, 800, 600);

        String path = "/Users/sungkwonkim/Downloads/mnist_test.csv";

        NewTest loader = new NewTest();
        try {

            List<int[]> result = loader.load(path);

            XYChart.Series series = new XYChart.Series();
            int count = 0;
            for (int[] items : result) {
                count++;
                if (count > 1) break;
                for (int i = 0; i < items.length; i++) {
                    series.getData().add(new XYChart.Data(i, items[i]));
                }
            }

            float[] avg = new float[result.get(0).length];

            int seq = 0;

            for (int[] items : result) {

                seq++;

                for (int i = 0; i < items.length; i++) {


                    if (seq > 1) {
                        avg[i] = avg[i] + items[i];

                    } else {
                        avg[i] = items[i];
                    }

                    if (i == 27 && seq < 5) {
                        System.err.println(seq + "::" + i + "--> " + avg[i] + " " + items[i]);
                    }

                }
            }
            lineChart.getData().add(series);

            int cnt = result.size();
            System.err.println("====== " + cnt);

            for (int x = 0; x < avg.length; x++) {

                avg[x] = avg[x] / cnt;

            }

            XYChart.Series series2 = new XYChart.Series();


            for (int x = 0; x < avg.length; x++) {
                series2.getData().add(new XYChart.Data(x, avg[x]));
                System.err.println(x + " > " + avg[x]);


            }
            lineChart.getData().add(series2);


            //series.getData().add(new XYChart.Data(2, 23));


        } catch (IOException ioe) {
            ioe.printStackTrace();
        }


        stage.setScene(scene);
        stage.show();


    }



    public static void main(String[] args) throws Exception {
        /*
        String path = "/Users/sungkwonkim/Downloads/mnist_test.csv";

        Loader loader = new Loader();
        loader.load(path);

*/
        launch(args);
    }

}