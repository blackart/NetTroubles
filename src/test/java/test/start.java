package test;

public class start {
    public static void main(String[] args) {
        Double width = 0.8;
        int length = 384467 * 100 * 10;

        Double target_width = 0.0;

        int i = 0;
        while (target_width < length) {
            target_width = width * Math.pow(2, i);
            i++;
            System.out.println(i + " - " + target_width);
        }

        System.out.println(target_width.intValue());
        System.out.println(length);
        System.out.println(i);

    }
}
