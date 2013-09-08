import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class LazyConcurrentBlockingIntQueue {

    private static final long READ_LOCATION_OFFSET;
    private static final long WRITE_LOCATION_OFFSET;
    private static final int shift;
    private static final Unsafe unsafe;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            READ_LOCATION_OFFSET = unsafe.objectFieldOffset
                    (LazyConcurrentBlockingIntQueue.class.getDeclaredField("readLocation"));
            WRITE_LOCATION_OFFSET = unsafe.objectFieldOffset
                    (LazyConcurrentBlockingIntQueue.class.getDeclaredField("writeLocation"));
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    private static final int base = unsafe.arrayBaseOffset(int[].class);

    static {
        int scale = unsafe.arrayIndexScale(int[].class);
        if ((scale & (scale - 1)) != 0)
            throw new Error("data type scale not a power of two");
        shift = 31 - Integer.numberOfLeadingZeros(scale);
    }

    private final int size = 1024;
    private final int[] data = new int[size];

    // can only be updated from the reader thread
    private volatile int readLocation = 0;

    // can only be updated from the writer thread
    private volatile int writeLocation = 0;

    /**
     * the writes must always occur on the same thread,
     *
     * @param value
     */
    public void add(int value) {


        int nextWriteLocation = writeLocation + 1;

        if (nextWriteLocation == size)
            nextWriteLocation = 0;

        if (nextWriteLocation == size - 1)
            while (readLocation == 0) {
            }
        else
            while (nextWriteLocation == readLocation - 1) {
            }

        unsafe.putOrderedInt(data, ((long) writeLocation << shift) + base, value);

        // write back the next write location
        unsafe.putOrderedInt(this, WRITE_LOCATION_OFFSET, nextWriteLocation);

        // writeLocation =  nextWriteLocation;
    }

    /**
     * the reads must always occur on the same thread
     *
     * @return
     */

    public int take() {

        int nextReadLocation = readLocation + 1;

        if (nextReadLocation == size)
            nextReadLocation = 0;


        // we are blocked reading waiting for another add
        while (writeLocation == readLocation) {
            // spin lock
        }

        final int value = unsafe.getIntVolatile(data, ((long) readLocation << shift) + base);
        unsafe.putOrderedInt(this, READ_LOCATION_OFFSET, nextReadLocation);

        return value;

    }
}
