package com.klochkov.app.queing;

/**
 * Created by georgyklochkov on 16/10/16.
 */
public class PublishSubscribeReceiverDemo {

    public static void execute() {
        try {
            final PublishSubscriberReceiver receiver1 = new PublishSubscriberReceiver();
            receiver1.initialize();
            final PublishSubscriberReceiver receiver2 = new PublishSubscriberReceiver();
            receiver2.initialize();
            Thread t1 = new Thread(new Runnable() {
                public void run() {
                    receiver1.receive("pubsub_queue1");
                }
            });
            Thread t2 = new Thread(new Runnable() {
                public void run() {
                    receiver2.receive("pubsub_queue2");
                }
            });
            t1.start();
            t2.start();
            t1.join();
            t2.join();

            receiver1.destroy();
            receiver2.destroy();
        }catch(InterruptedException ex){
            ex.printStackTrace();
        }
    }
}
