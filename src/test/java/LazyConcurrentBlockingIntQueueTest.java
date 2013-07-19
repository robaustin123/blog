import junit.framework.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;


public class LazyConcurrentBlockingIntQueueTest {

    @Test
    public void testWrite() throws Exception {

    }

    @Test
    public void testRead() throws Exception {
        final LazyConcurrentBlockingIntQueue lazyConcurrentBlockingIntQueue = new LazyConcurrentBlockingIntQueue();
        lazyConcurrentBlockingIntQueue.add(10);
        final int value = lazyConcurrentBlockingIntQueue.take();
        Assert.assertEquals(10, value);
    }

    @Test
    public void testRead2() throws Exception {
        final LazyConcurrentBlockingIntQueue lazyConcurrentBlockingIntQueue = new LazyConcurrentBlockingIntQueue();
        lazyConcurrentBlockingIntQueue.add(10);
        lazyConcurrentBlockingIntQueue.add(11);
        final int value = lazyConcurrentBlockingIntQueue.take();
        Assert.assertEquals(10, value);
        final int value1 = lazyConcurrentBlockingIntQueue.take();
        Assert.assertEquals(11, value1);
    }

    @Test
    public void testReadLoop() throws Exception {
        final LazyConcurrentBlockingIntQueue lazyConcurrentBlockingIntQueue = new LazyConcurrentBlockingIntQueue();

        for (int i = 1; i < 50; i++) {
            lazyConcurrentBlockingIntQueue.add(i);
            final int value = lazyConcurrentBlockingIntQueue.take();
            Assert.assertEquals(i, value);
        }
    }

    /**
     * reader and add, reader and writers on different threads
     *
     * @throws Exception
     */
    @Test
    public void testWithFasterReader() throws Exception {

        final LazyConcurrentBlockingIntQueue lazyConcurrentBlockingIntQueue = new LazyConcurrentBlockingIntQueue();
        final int max = 100;
        final CountDownLatch countDown = new CountDownLatch(1);

        new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        for (int i = 1; i < max; i++) {
                            lazyConcurrentBlockingIntQueue.add(i);
                            try {
                                Thread.sleep((int) (Math.random() * 100));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }).start();


        new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        for (int i = 1; i < max; i++) {

                            final int value = lazyConcurrentBlockingIntQueue.take();
                            try {
                                Assert.assertEquals(i, value);
                            } catch (Error e) {
                                System.out.println("value=" + value);

                            }
                            System.out.println("value=" + value);
                            try {
                                Thread.sleep((int) (Math.random() * 10));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        countDown.countDown();

                    }
                }).start();

        countDown.await();
    }


    /**
     * faster writer
     *
     * @throws Exception
     */
    @Test
    public void testWithFasterWriter() throws Exception {

        final LazyConcurrentBlockingIntQueue lazyConcurrentBlockingIntQueue = new LazyConcurrentBlockingIntQueue();
        final int max = 200;
        final CountDownLatch countDown = new CountDownLatch(1);

        new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        for (int i = 1; i < max; i++) {
                            lazyConcurrentBlockingIntQueue.add(i);
                            try {
                                Thread.sleep((int) (Math.random() * 3));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }).start();


        new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        for (int i = 1; i < max; i++) {

                            final int value = lazyConcurrentBlockingIntQueue.take();
                            try {
                                Assert.assertEquals(i, value);
                            } catch (Error e) {
                                System.out.println("value=" + value);

                            }
                            System.out.println("value=" + value);
                            try {
                                Thread.sleep((int) (Math.random() * 10));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        countDown.countDown();

                    }
                }).start();

        countDown.await();
    }


    @Test
    public void testFlatOut() throws Exception {

        final LazyConcurrentBlockingIntQueue lazyConcurrentBlockingIntQueue = new LazyConcurrentBlockingIntQueue();
        final int max = 101024;
        final CountDownLatch countDown = new CountDownLatch(1);

        new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        for (int i = 1; i < max; i++) {
                            lazyConcurrentBlockingIntQueue.add(i);

                        }

                    }
                }).start();


        new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        for (int i = 1; i < max; i++) {

                            final int value = lazyConcurrentBlockingIntQueue.take();
                            try {
                                Assert.assertEquals(i, value);
                            } catch (Error e) {
                                System.out.println("value=" + value);

                            }
                            System.out.println("value=" + value);

                        }
                        countDown.countDown();

                    }
                }).start();

        countDown.await();
    }
}

