
public class FastLoop {

    public static long work = 1;

    public static long unwoundLoop(long times) {


        final long l = times / 2;

        long start = System.nanoTime();

        for (long i = 0; i < l; i++) {
            doWork();
            doWork();

        }

        return System.nanoTime() - start;

    }

    public static long forLoop(long times) {

        long start = System.nanoTime();

        for (long i = 0; i < times; i++) {
            doWork();
        }

        return System.nanoTime() - start;

    }

    public static void main(String... args) {


        for (int pwr = 2; pwr < 11; pwr++) {
            long i = (long) Math.pow(10, pwr);
            long time1 = unwoundLoop(i);
            long time2 = forLoop(i);
            System.out.printf("Performing %,d loops, unwoundLoop() took %.3f us and forLoop() took %.3f us on average, ratio=%.1f%n",
                    i, time1 / 1e3, time2 / 1e3, (double) time1 / time2);
        }

        System.out.println("\nJust printing work so that it is not optimized out, work=" + work);

    }

    public static long doWork() {
        work += 1;
        return work;
    }
}
