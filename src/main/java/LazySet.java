import java.lang.reflect.Field;

public class LazySet {


    private static final long valueOffset;
    static long offset;
    static sun.misc.Unsafe unsafe;
    volatile Object o;

    static {
        try {
            Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (sun.misc.Unsafe) field.get(null);
            valueOffset = unsafe.objectFieldOffset
                    (LazySet.class.getDeclaredField("o"));
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    public static void main(String... args) throws NoSuchFieldException {

        final LazySet that = new LazySet();

        for (int pwr = 2; pwr < 11; pwr++) {
            long i = (long) Math.pow(10, pwr);
            long time1 = that.callPutOrderedObject(i);
            long time2 = that.setTheVolatileDirectly(i);
            System.out.printf("Performing %,d loops, callPutOrderedObject() took %.3f us and setting the volatile directly took %.3f us on average, ratio=%.1f%n",
                    i, time1 / 1e3, time2 / 1e3, (double) time1 / time2);
        }

        System.out.println("\nJust printing work so that it is not optimized out, work=" + that.o);

    }

    /**
     * @param o It is only really useful where the field is volatile, and is thus expected to change unexpectedly.
     * @throws NoSuchFieldException
     */
    public void lazySet(Object o) throws NoSuchFieldException {
        unsafe.putOrderedObject(this, valueOffset, o);
    }

    public long callPutOrderedObject(long times) throws NoSuchFieldException {


        final long l = times / 2;

        long start = System.nanoTime();

        for (long i = 0; i < l; i++) {
            lazySet("do work");
        }

        return System.nanoTime() - start;

    }

    public long setTheVolatileDirectly(long times) {


        final long l = times / 2;

        long start = System.nanoTime();

        for (long i = 0; i < l; i++) {
            o = "do work";
        }

        return System.nanoTime() - start;

    }

}
