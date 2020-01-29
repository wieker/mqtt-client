package org.fusesource.mqtt.client;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import static com.fazecast.jSerialComm.SerialPort.LISTENING_EVENT_DATA_AVAILABLE;

public class CheckConnectionTest {
    public static void main(String[] args) throws Exception {
        MQTT mqtt = new MQTT();
        mqtt.setHost("localhost", 1883);

        final BlockingConnection connection = mqtt.blockingConnection();
        connection.connect();

        SerialPort commPort = SerialPort.getCommPort("/dev/ttyACM0");
        commPort.setBaudRate(115200);
        System.out.println(commPort.openPort());
        if (!commPort.isOpen()) {
            System.out.println("fuckup");
            return;
        }

        commPort.addDataListener(new SerialPortDataListener() {
            public int getListeningEvents() {
                return LISTENING_EVENT_DATA_AVAILABLE;
            }

            public void serialEvent(SerialPortEvent serialPortEvent) {
                System.out.println(serialPortEvent.getReceivedData());
                SerialPort comPort = serialPortEvent.getSerialPort();
                System.out.println("Available: " + comPort.bytesAvailable() + " bytes.");
                byte[] newData = new byte[comPort.bytesAvailable()];
                int numRead = comPort.readBytes(newData, newData.length);
                System.out.println("Read " + numRead + " bytes.");
                try {
                    connection.publish("foo", newData, QoS.AT_LEAST_ONCE, false);
                } catch (Exception e) {
                    System.out.println("ec " + e);
                }
            }
        });


        /*Topic[] topics = {new Topic("foo", QoS.AT_LEAST_ONCE)};
        byte[] qoses = connection.subscribe(topics);

        Message message = connection.receive();
        System.out.println(message.getTopic());
        byte[] payload = message.getPayload();
        // process the message then:
        message.ack();

        connection.disconnect();*/
    }
}
