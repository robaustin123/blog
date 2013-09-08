import junit.framework.Assert;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class LazyConcurrentBlockingIntQueueVArrayBlockingQueueTest {

    /**
     * reader and add, reader and writers on different threads
     *
     * @throws Exception
     */

    public long lazyConcurrentBlockingIntQueueReadsAndWrites(final long times) throws Exception {

        final LazyConcurrentBlockingIntQueue queue = new LazyConcurrentBlockingIntQueue();

        final CountDownLatch countDown = new CountDownLatch(1);

        final AtomicBoolean dataOutOfSequence = new AtomicBoolean();


        final AtomicInteger writeCounter = new AtomicInteger(0);
        final AtomicInteger readerCounter = new AtomicInteger(0);


        final long l = times / 2;

        long start = System.nanoTime();


        new Thread(
                new Runnable() {

                    @Override
                    public void run() {

                        int value;
                        do {
                            value = writeCounter.getAndIncrement();
                            queue.add(value);
                        } while (value <= times);
                    }

                }
        ).start();


        new Thread(
                new Runnable() {

                    @Override
                    public void run() {

                        do {
                            final int valueRead = queue.take();
                            if (valueRead == times) {
                                countDown.countDown();
                                return;
                            }

                            if (valueRead != readerCounter.getAndIncrement()) {

                                dataOutOfSequence.set(true);
                                countDown.countDown();
                                return;
                            }
                        } while (true);
                    }

                }
        ).start();

        countDown.await();

        if (dataOutOfSequence.get())
            Assert.fail("data is out of sequence");

        return System.nanoTime() - start;
    }

    /**
     * reader and add, reader and writers on different threads
     *
     * @throws Exception
     */

    public long arrayBlockingQueueReadAndWrites(final long times) throws Exception {

        final ArrayBlockingQueue queue = new ArrayBlockingQueue(1024);

        final CountDownLatch countDown = new CountDownLatch(1);

        final AtomicBoolean error = new AtomicBoolean();

        final AtomicInteger writeCounter = new AtomicInteger(0);
        final AtomicInteger readerCounter = new AtomicInteger(0);


        final long l = times / 2;

        long start = System.nanoTime();


        new Thread(
                new Runnable() {

                    @Override
                    public void run() {

                        int value;
                        do {
                            value = writeCounter.incrementAndGet();
                            try {
                                queue.put(value);
                            } catch (InterruptedException e) {
                                error.set(true);
                                countDown.countDown();
                                return;
                            }
                        } while (value <= times);
                    }

                }
        ).start();


        new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        int value;
                        do {

                            int valueRead;
                            try {
                                valueRead = (Integer) queue.take();
                            } catch (InterruptedException e) {
                                error.set(true);
                                countDown.countDown();
                                return;
                            }

                            if (valueRead == times) {
                                countDown.countDown();
                                return;
                            }

                            if (valueRead != readerCounter.incrementAndGet()) {
                                error.set(true);
                                countDown.countDown();
                                return;
                            }

                        } while (true);

                    }
                }
        ).start();


        countDown.await();

        if (error.get())
            Assert.fail("data is out of sequence or an InterruptedException was thrown.");

        return System.nanoTime() - start;
    }

    @Test
    public void performacne() throws Exception {

        final LazySetLong that = new LazySetLong();

        for (int pwr = 2; pwr < 11; pwr++) {
            long i = (long) Math.pow(8, pwr);
            long time1 = this.arrayBlockingQueueReadAndWrites(i);
            long time2 = this.lazyConcurrentBlockingIntQueueReadsAndWrites(i);
            System.out.printf("Performing %,d loops, arrayBlockingQueueReadAndWrites() took %.3f us and using lazyConcurrentBlockingIntQueueReadsAndWrites took %.3f us on average, ratio=%.1f%n",
                    i, time1 / 1e3, time2 / 1e3, (double) time1 / time2);
        }

        System.out.println("\nJust printing work so that it is not optimized out, work=" + that.o);

    }
}
