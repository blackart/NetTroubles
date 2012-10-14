package ru.blackart.dsi.infopanel.utils.crm;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public class SendToCrm {
    public static synchronized void send(ByteArrayOutputStream baos) throws JMSException, UnsupportedEncodingException {
        String user = "crm";
        String password = "cAD79snu";
        String url = "tcp://192.168.1.201:61716";

        //todo раскомментить ддя отправки в CRM
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
        Connection connection = connectionFactory.createConnection();
        connection.setClientID("MonitoringService");

        connection.start();

        javax.jms.Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination orderQueue = session.createQueue("MSCRM.entities");

        MessageProducer mProducer = session.createProducer(orderQueue);
        mProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
        String mess = baos.toString("UTF-8");
        TextMessage message = session.createTextMessage(mess);

        mProducer.send(message);

        connection.close();
    }
}
