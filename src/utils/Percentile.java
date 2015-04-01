package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by myl on 2014/12/14.
 */
public class Percentile {
    private List<Integer> array;
    private double p;
    private int integerPart;
    private double decimalPart;

    public Percentile(List<Integer> array, double p) {
        this.array = array;
        this.p = p;
    }

    public double getResult() {
        int size = array.size();

        if (size == 0) {
            return 0;
        }

        if (size > 1) {
            splitTwoPart(array.size(), p);
            return (1 - decimalPart) * array.get(integerPart) + decimalPart * array.get(integerPart + 1);
        } else {
            return array.get(0) * p;
        }
    }

    private void splitTwoPart(int size, double p) {
        double total = (size - 1) * p;
        integerPart = (int) Math.floor(total);
        decimalPart = total - integerPart;
    }

    public static void main(String[] args) {
        String s="sssss";
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
//        list.add(2);
//        list.add(3);
//        list.add(4);

        double p = 0.95;

        Percentile percentile = new Percentile(list, p);
        System.out.println(percentile.getResult());
    }
}
