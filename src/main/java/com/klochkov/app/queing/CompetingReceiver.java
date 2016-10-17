package com.klochkov.app.queing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by georgyklochkov on 16/10/16.
 */
public class CompetingReceiver {
    private static String QUEUE_NAME = "event_queue";
    private final static Logger LOGGER = LoggerFactory.getLogger(Sender.class);
    private Connection connection = null;
    private Channel channel = null;

    public CompetingReceiver(){

    }

    public CompetingReceiver(String queueName){
        QUEUE_NAME = queueName;
    }

    public void initialize() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            connection = factory.newConnection();
            channel = connection.createChannel();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public List<String> receive() {
        if (channel == null) {
            initialize();
        }
        String message = null;
        ArrayList<String> result = new ArrayList<String>();
        try {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
/*            QueueingConsumer consumer = new QueueingConsumer(channel);
            //TODO somewhere here is a problem with getting too much messages
            channel.basicConsume(QUEUE_NAME, true, consumer);

            QueueingConsumer.Delivery delivery = consumer.nextDelivery(10000);
            if(delivery == null)return result;
            System.out.println("Delivery is " + delivery);*/
            GetResponse response = channel.basicGet(QUEUE_NAME,false);
            if(response == null){
                LOGGER.info("No message received from " + QUEUE_NAME);
            }else{
                message = new String(response.getBody());
            }
//            message = new String(delivery.getBody());
            result.add(message);
            LOGGER.info("Message received: " + message);
            return result;

        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (ShutdownSignalException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (ConsumerCancelledException e) {
            LOGGER.error(e.getMessage(), e);
        }/* catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }*/
        return result;
    }

    public void destroy() {
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
    }
}
