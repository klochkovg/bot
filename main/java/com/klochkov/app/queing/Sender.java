package com.klochkov.app.queing;

import java.io.IOException;


import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by georgyklochkov on 16/10/16.
 * Actually it is a copy from RabbitMQ tutorial
 */
public class Sender {
    private final static Logger LOGGER = LoggerFactory.getLogger(Sender.class);
    private static final String DEFAULT_EXCHANGE = "";
    private Channel channel;
    private Connection connection;

    public void initialize(){
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            connection = factory.newConnection();
            channel = connection.createChannel();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
    //For point to point
    public void send(String queueName, String message) {
        try {
            LOGGER.info("Sending");
            channel.queueDeclare(queueName, false, false, false, null);
            channel.basicPublish(DEFAULT_EXCHANGE, queueName, null,message.getBytes());
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    //Point to point data sending
    public void send(String queueName, byte[] bytes){
        try {
            LOGGER.info("Sending");
            channel.queueDeclare(queueName, false, false, false, null);
            channel.basicPublish(DEFAULT_EXCHANGE, queueName, null,bytes);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }


    public int getQueueStatus(String queueName){
        int result = -1;
        try {
            AMQP.Queue.DeclareOk meta = channel.queueDeclare(queueName, false, false, false, null);
            return meta.getMessageCount();
        }catch(IOException e){
            LOGGER.error(e.getMessage(),e);
            //TODO May be I have to put some more adequate processing
        }
        return result;
    }

    //for publish subscribe
    public void send(String exchange, String type, String message) {
        try {
            channel.exchangeDeclare(exchange, type);
            channel.basicPublish(exchange, "", null,
                    message.getBytes());
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void destroy() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

}
