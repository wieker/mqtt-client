package org.fusesource.mqtt.client;

public class CheckConnectionTestFuckJetBrains {
    public static void main(String[] args) throws Exception {
        MQTT mqtt = new MQTT();
        mqtt.setHost("localhost", 1883);

        BlockingConnection connection = mqtt.blockingConnection();
        connection.connect();

        Topic[] topics = {new Topic("foo", QoS.AT_LEAST_ONCE)};
        byte[] qoses = connection.subscribe(topics);

        for (;;) {
            Message message = connection.receive();
            System.out.println(message.getTopic());
            byte[] payload = message.getPayload();
            // process the message then:
            System.out.println(new String(payload));
            message.ack();
        }

        //connection.disconnect();
    }
}
