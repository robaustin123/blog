import java.lang.reflect.Field;

public class LazySetLong {


    private static final long valueOffset;
    static long offset;
    static sun.misc.Unsafe unsafe;
    volatile long o;

    static {
        try {
            Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (sun.misc.Unsafe) field.get(null);
            valueOffset = unsafe.objectFieldOffset
                    (LazySetLong.class.getDeclaredField("o"));
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }


    /**
     * @param o It is only really useful where the field is volatile, and is thus expected to change unexpectedly.
     * @throws NoSuchFieldException
     */
    public void lazySet(long o) throws NoSuchFieldException {
        unsafe.putOrderedLong(this, valueOffset, o);
    }

    private long callPutOrderedLong(long times) throws NoSuchFieldException {


        final long l = times / 2;

        long start = System.nanoTime();

        for (long i = 0; i < l; i++) {

            lazySet(o + 1);
        }

        return System.nanoTime() - start;

    }

    private long setTheVolatileDirectly(long times) {


        final long l = times / 2;

        long start = System.nanoTime();

        for (long i = 0; i < l; i++) {
            o++;
        }

        return System.nanoTime() - start;

    }

    public static void main(String... args) throws NoSuchFieldException {

        final LazySetLong that = new LazySetLong();

        for (int pwr = 2; pwr < 11; pwr++) {
            long i = (long) Math.pow(9, pwr);
            long time1 = that.callPutOrderedLong(i);
            long time2 = that.setTheVolatileDirectly(i);
            System.out.printf("Performing %,d loops, callPutOrderedLong() took %.3f us and setting the volatile directly took %.3f us on average, ratio=%.1f%n",
                    i, time1 / 1e3, time2 / 1e3, (double) time1 / time2);
        }

        System.out.println("\nJust printing work so that it is not optimized out, work=" + that.o);

    }

}
