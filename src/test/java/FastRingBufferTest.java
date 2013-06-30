import junit.framework.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * Created with IntelliJ IDEA.
 * User: robaustin
 * Date: 30/06/2013
 * Time: 23:02
 * To change this template use File | Settings | File Templates.
 */
public class FastRingBufferTest {
    @Test
    public void testWrite() throws Exception {

    }

    @Test
    public void testRead() throws Exception {
        final FastRingBuffer fastRingBuffer = new FastRingBuffer();
        fastRingBuffer.write(10);
        final int value = fastRingBuffer.read();
        Assert.assertEquals(10, value);
    }

    @Test
    public void testRead2() throws Exception {
        final FastRingBuffer fastRingBuffer = new FastRingBuffer();
        fastRingBuffer.write(10);
        fastRingBuffer.write(11);
        final int value = fastRingBuffer.read();
        Assert.assertEquals(10, value);
        final int value1 = fastRingBuffer.read();
        Assert.assertEquals(11, value1);
    }

    @Test
    public void testReadLoop() throws Exception {
        final FastRingBuffer fastRingBuffer = new FastRingBuffer();

        for (int i = 1; i < 50; i++) {
            fastRingBuffer.write(i);
            final int value = fastRingBuffer.read();
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

        final FastRingBuffer fastRingBuffer = new FastRingBuffer();
        final int max = 100;
        final CountDownLatch countDown = new CountDownLatch(1);

        new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        for (int i = 1; i < max; i++) {
                            fastRingBuffer.write(i);
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

                            final int value = fastRingBuffer.read();
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

        final FastRingBuffer fastRingBuffer = new FastRingBuffer();
        final int max = 200;
        final CountDownLatch countDown = new CountDownLatch(1);

        new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        for (int i = 1; i < max; i++) {
                            fastRingBuffer.write(i);
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

                            final int value = fastRingBuffer.read();
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

        final FastRingBuffer fastRingBuffer = new FastRingBuffer();
        final int max = 101024;
        final CountDownLatch countDown = new CountDownLatch(1);

        new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        for (int i = 1; i < max; i++) {
                            fastRingBuffer.write(i);

                        }

                    }
                }).start();


        new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        for (int i = 1; i < max; i++) {

                            final int value = fastRingBuffer.read();
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

