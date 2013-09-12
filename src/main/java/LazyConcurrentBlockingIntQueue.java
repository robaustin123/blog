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


    // about 128 kb to fit in a L1 cache ( the 4 is from the size of a int, 4 bytes )
    private final int size = 1024 * (128 / 4);


    // intentionally not volatile, as we are carefully ensuring that the memory barriers are controlled below by other objects
    private final int[] data = new int[size];

    // we set volatiles here, for the writes we use putOrderedInt ( as this is quicker ),
    // but for the read the is no performance benefit un using getOrderedInt.
    private volatile int readLocation = 0;
    private volatile int writeLocation = 0;

    /**
     * the writes must always occur on the same thread,
     *
     * @param value
     */
    public void add(int value) {

        // we want to minimize the number of volatile reads, so we read the writeLocation just once.
        final int writeLocation = this.writeLocation;

        // sets the nextWriteLocation my moving it on by 1, this may cause it it wrap back to the start.
        final int nextWriteLocation = (writeLocation + 1 == size) ? 0 : writeLocation + 1;

        if (nextWriteLocation == size - 1)

            while (readLocation == 0)
                // // this condition handles the case where writer has caught up with the read,
                // we will wait for a read, ( which will cause a change on the read location )
                blockAtAdd();

        else


            while (nextWriteLocation + 1 == readLocation)
                // this condition handles the case general case where the read is at the start of the backing array and we are at the end,
                // blocks as our backing array is full, we will wait for a read, ( which will cause a change on the read location )
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

        // we want to minimize the number of volatile reads, so we read the readLocation just once.
        final int readLocation = this.readLocation;

        // sets the nextReadLocation my moving it on by 1, this may cause it it wrap back to the start.
        final int nextReadLocation = (readLocation + 1 == size) ? 0 : readLocation + 1;

        // in the for loop below, we are blocked reading unit another item is written, this is because we are empty ( aka size()=0)
        // inside the for loop, getting the 'writeLocation', this will serve as our read memory barrier.
        while (writeLocation == readLocation)
            blockAtTake();

        // purposely not volatile as the read memory barrier occurred above when we read 'writeLocation'
        final int value = data[readLocation];

        // the write memory barrier will occur here, as we are storing the nextReadLocation
        unsafe.putOrderedInt(this, READ_LOCATION_OFFSET, nextReadLocation);

        return value;

    }

    /**
     * currently implement as a spin lock
     */
    private void blockAtTake() {
    }

    /**
     * currently implement as a spin lock
     */
    private void blockAtAdd() {
    }
}
