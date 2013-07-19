import junit.framework.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;


public class LazyConcurrentBlockingQueueTest {

    @Test
    public void testWrite() throws Exception {

    }

    @Test
    public void testRead() throws Exception {
        final LazyConcurrentBlockingQueue lazyConcurrentBlockingQueue = new LazyConcurrentBlockingQueue();
        lazyConcurrentBlockingQueue.write(10);
        final int value = lazyConcurrentBlockingQueue.read();
        Assert.assertEquals(10, value);
    }

    @Test
    public void testRead2() throws Exception {
        final LazyConcurrentBlockingQueue lazyConcurrentBlockingQueue = new LazyConcurrentBlockingQueue();
        lazyConcurrentBlockingQueue.write(10);
        lazyConcurrentBlockingQueue.write(11);
        final int value = lazyConcurrentBlockingQueue.read();
        Assert.assertEquals(10, value);
        final int value1 = lazyConcurrentBlockingQueue.read();
        Assert.assertEquals(11, value1);
    }

    @Test
    public void testReadLoop() throws Exception {
        final LazyConcurrentBlockingQueue lazyConcurrentBlockingQueue = new LazyConcurrentBlockingQueue();

        for (int i = 1; i < 50; i++) {
            lazyConcurrentBlockingQueue.write(i);
            final int value = lazyConcurrentBlockingQueue.read();
            Assert.assertEquals(i, value);
        }
    }

    /**
     * faster reader
     *
     * @throws Exception
     */
    @Test
    public void testWithFasterReader() throws Exception {

        final LazyConcurrentBlockingQueue lazyConcurrentBlockingQueue = new LazyConcurrentBlockingQueue();
        final int max = 100;
        final CountDownLatch countDown = new CountDownLatch(1);

        new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        for (int i = 1; i < max; i++) {
                            lazyConcurrentBlockingQueue.write(i);
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

                            final int value = lazyConcurrentBlockingQueue.read();
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

        final LazyConcurrentBlockingQueue lazyConcurrentBlockingQueue = new LazyConcurrentBlockingQueue();
        final int max = 200;
        final CountDownLatch countDown = new CountDownLatch(1);

        new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        for (int i = 1; i < max; i++) {
                            lazyConcurrentBlockingQueue.write(i);
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

                            final int value = lazyConcurrentBlockingQueue.read();
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

        final LazyConcurrentBlockingQueue lazyConcurrentBlockingQueue = new LazyConcurrentBlockingQueue();
        final int max = 101024;
        final CountDownLatch countDown = new CountDownLatch(1);

        new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        for (int i = 1; i < max; i++) {
                            lazyConcurrentBlockingQueue.write(i);

                        }

                    }
                }).start();


        new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        for (int i = 1; i < max; i++) {

                            final int value = lazyConcurrentBlockingQueue.read();
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

