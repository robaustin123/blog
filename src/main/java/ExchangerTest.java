import java.util.Arrays;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class ExchangerTest {

    public static void main(String[] args) {
        final Exchanger<Integer> e = new Exchanger<Integer>();


        final Thread producer = new Thread(new Runnable() {

            private final AtomicReference<Integer> last = new AtomicReference<Integer>(1);

            @Override
            public void run() {
                try {

                    long[] myArray = new long[1024];

                    Arrays.fill(myArray, 99);

                    while (true) {
                        try {
                            last.set(e.exchange(last.get(), 0, TimeUnit.NANOSECONDS));
                        } catch (TimeoutException e1) {
                            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                        System.out.println("Thread A has value: " + last.get());

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });
        producer.start();


        final Thread consumer = new Thread(new Runnable() {

            private final AtomicReference<Integer> last = new AtomicReference<Integer>(2);

            @Override
            public void run() {
                try {
                    while (true) {
                        last.set(e.exchange(last.get()));
                        System.out.println("Thread B has value: " + last.get());
                        Thread.sleep(2000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });
        consumer.start();
    }

}