import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class LazyConcurrentBlockingIntQueue {

    private static final long READ_LOCATION_OFFSET;
    private static final long WRITE_LOCATION_OFFSET;
    private static final Unsafe unsafe;

    static {
        try {
            final Field field = Unsafe.class.getDeclaredField("theUnsafe");
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

    private final int size = 1024;
    private final int[] data = new int[size];
    private volatile int readLocation = 0;
    private volatile int writeLocation = 0;

    /**
     * the writes must always occur on the same thread,
     *
     * @param value
     */
    public void add(int value) {

        final int writeLocation = this.writeLocation;
        final int nextWriteLocation = (writeLocation + 1 == size) ? 0 : writeLocation + 1;

        if (nextWriteLocation == size - 1)

            while (readLocation == 0)
                takeAtBlock();

        else

            while (nextWriteLocation + 1 == readLocation)
                blockAtAdd();


        // purposely not volatile see the comment below
        data[writeLocation] = value;

        // the line below, is where the write memory barrier occurs,
        // we have just written back the data in the line above ( which is not require to have a memory barrier as we will be doing that in the line below

        // write back the next write location
        unsafe.putOrderedInt(this, WRITE_LOCATION_OFFSET, nextWriteLocation);
    }

    /**
     * the reads must always occur on the same thread
     *
     * @return
     */

    public int take() {

        final int readLocation = this.readLocation;
        int nextReadLocation = readLocation + 1;

        if (nextReadLocation == size)
            nextReadLocation = 0;

        // in the for loop below, we are blocked reading unit another item is written, this is because we are empty ( aka size()=0)
        // inside the for loop, getting the 'writeLocation', this will serve as our read memory barrier.
        while (writeLocation == readLocation)
            takeAtBlock();

        // purposely not volatile as the read memory barrier occurred above when we read 'writeLocation'
        final int value = data[readLocation];

        // the write memory barrier will occur here, as we are storing the nextReadLocation
        unsafe.putOrderedInt(this, READ_LOCATION_OFFSET, nextReadLocation);

        return value;

    }

    /**
     * currently implement as a spin lock
     */
    private void takeAtBlock() {
    }

    /**
     * currently implement as a spin lock
     */
    private void blockAtAdd() {
    }
}
