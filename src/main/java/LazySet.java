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

    /**
     * @param o It is only really useful where the field is volatile, and is thus expected to change unexpectedly.
     * @throws NoSuchFieldException
     */
    public void lazySet(Object o) throws NoSuchFieldException {
        unsafe.putOrderedObject(this, valueOffset, o);

    }

}
