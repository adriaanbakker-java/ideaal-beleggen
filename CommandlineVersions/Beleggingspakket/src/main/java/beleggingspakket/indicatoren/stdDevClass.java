package beleggingspakket.indicatoren;



import java.util.*;
class stdevClass {
//    public static void main(String[] args){
////        int [] list = {1,-2,4,-4,9,-6,16,-8,25,-10};
////        double stdev_Result = stdev(list);
////        System.out.println(stdev(list));
////    }

    public static double stdev(double[] list){
        double sum = 0.0;
        double mean = 0.0;
        double num=0.0;
        double numi = 0.0;
        double deno = 0.0;

        for (double d : list) {
            sum+=d;
        }
        mean = sum/list.length;

        for (double d : list) {
            double ibr = d - mean;
            numi = Math.pow(ibr, 2);
            num+=numi;
        }

        return Math.sqrt(num/list.length);
    }
}

