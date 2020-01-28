package org.fusesource.mqtt.client;

public class CheckConnectionTestFuckJetBrains {
    public static void main(String[] args) throws Exception {
        MQTT mqtt = new MQTT();
        mqtt.setHost("localhost", 1883);

        BlockingConnection connection = mqtt.blockingConnection();
        connection.connect();

        connection.publish("foo", "Hello".getBytes(), QoS.AT_LEAST_ONCE, false);

        Topic[] topics = {new Topic("foo", QoS.AT_LEAST_ONCE)};
        byte[] qoses = connection.subscribe(topics);

        Message message = connection.receive();
        System.out.println(message.getTopic());
        byte[] payload = message.getPayload();
        // process the message then:
        message.ack();

        connection.disconnect();
    }
}
