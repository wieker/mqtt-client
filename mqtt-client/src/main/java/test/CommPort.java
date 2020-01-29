package test;

import com.fazecast.jSerialComm.*;

import java.io.InputStream;
import java.util.Scanner;

/**
 * This class provides a test case for the jSerialComm library.
 *
 * @author Will Hedgecock &lt;will.hedgecock@gmail.com&gt;
 * @version 2.5.4
 * @see java.io.InputStream
 * @see java.io.OutputStream
 */
public class CommPort
{
    private static final class PacketListener implements SerialPortPacketListener
    {
        public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }
        public void serialEvent(SerialPortEvent event)
        {
            byte[] newData = event.getReceivedData();
            System.out.println("Received data of size: " + newData.length);
            for (int i = 0; i < newData.length; ++i)
                System.out.print((char)newData[i]);
            System.out.println("\n");
        }
        public int getPacketSize() { return 100; }
    }

    private static final class MessageListener implements SerialPortMessageListener
    {
        public String byteToHex(byte num)
        {
            char[] hexDigits = new char[2];
            hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
            hexDigits[1] = Character.forDigit((num & 0xF), 16);
            return new String(hexDigits);
        }
        public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }
        public void serialEvent(SerialPortEvent event)
        {
            byte[] byteArray = event.getReceivedData();
            StringBuffer hexStringBuffer = new StringBuffer();
            for (int i = 0; i < byteArray.length; i++)
                hexStringBuffer.append(byteToHex(byteArray[i]));
            System.out.println("Received the following message: " + hexStringBuffer.toString());
        }
        public byte[] getMessageDelimiter() { return new byte[]{ (byte)0xB5, (byte)0x62 }; }
        public boolean delimiterIndicatesEndOfMessage() { return false; }
    }

    static public void main(String[] args)
    {
        System.out.println("\nUsing Library Version v" + SerialPort.getVersion());
        SerialPort[] ports = SerialPort.getCommPorts();
        System.out.println("\nAvailable Ports:\n");
        for (int i = 0; i < ports.length; ++i)
            System.out.println("   [" + i + "] " + ports[i].getSystemPortName() + ": " + ports[i].getDescriptivePortName() + " - " + ports[i].getPortDescription());
        SerialPort ubxPort;
        System.out.print("\nChoose your desired serial port or enter -1 to specify a port directly: ");
        int serialPortChoice = 0;
        try {
            Scanner inputScanner = new Scanner(System.in);
            serialPortChoice = inputScanner.nextInt();
            inputScanner.close();
        } catch (Exception e) {}
        if (serialPortChoice == -1)
        {
            String serialPortDescriptor = "";
            System.out.print("\nSpecify your desired serial port descriptor: ");
            try {
                Scanner inputScanner = new Scanner(System.in);
                serialPortDescriptor = inputScanner.nextLine();
                inputScanner.close();
            } catch (Exception e) {}
            ubxPort = SerialPort.getCommPort(serialPortDescriptor);
        }
        else
            ubxPort = ports[serialPortChoice];
        byte[] readBuffer = new byte[2048];

        System.out.println("\nPre-setting RTS: " + (ubxPort.setRTS() ? "Success" : "Failure"));
        boolean openedSuccessfully = ubxPort.openPort(0);
        System.out.println("\nOpening " + ubxPort.getSystemPortName() + ": " + ubxPort.getDescriptivePortName() + " - " + ubxPort.getPortDescription() + ": " + openedSuccessfully);
        if (!openedSuccessfully)
            return;
        System.out.println("Setting read timeout mode to non-blocking");
        ubxPort.setBaudRate(115200);
        ubxPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 1000, 0);
        try
        {
            for (int i = 0; i < 3; ++i)
            {
                System.out.println("\nReading #" + i);
                System.out.println("Available: " + ubxPort.bytesAvailable());
                int numRead = ubxPort.readBytes(readBuffer, readBuffer.length);
                System.out.println("Read " + numRead + " bytes.");
            }
        } catch (Exception e) { e.printStackTrace(); }
        System.out.println("\nSetting read timeout mode to semi-blocking with a timeout of 200ms");
        ubxPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 200, 0);
        try
        {
            for (int i = 0; i < 3; ++i)
            {
                System.out.println("\nReading #" + i);
                System.out.println("Available: " + ubxPort.bytesAvailable());
                int numRead = ubxPort.readBytes(readBuffer, readBuffer.length);
                System.out.println("Read " + numRead + " bytes.");
            }
        } catch (Exception e) { e.printStackTrace(); }
        System.out.println("\nSetting read timeout mode to semi-blocking with no timeout");
        ubxPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        try
        {
            for (int i = 0; i < 3; ++i)
            {
                System.out.println("\nReading #" + i);
                System.out.println("Available: " + ubxPort.bytesAvailable());
                int numRead = ubxPort.readBytes(readBuffer, readBuffer.length);
                System.out.println("Read " + numRead + " bytes.");
            }
        } catch (Exception e) { e.printStackTrace(); }
        System.out.println("\nSetting read timeout mode to blocking with a timeout of 100ms");
        ubxPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 100, 0);
        try
        {
            for (int i = 0; i < 3; ++i)
            {
                System.out.println("\nReading #" + i);
                System.out.println("Available: " + ubxPort.bytesAvailable());
                int numRead = ubxPort.readBytes(readBuffer, readBuffer.length);
                System.out.println("Read " + numRead + " bytes.");
            }
        } catch (Exception e) { e.printStackTrace(); }
        System.out.println("\nSetting read timeout mode to blocking with no timeout");
        ubxPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
        try
        {
            for (int i = 0; i < 3; ++i)
            {
                System.out.println("\nReading #" + i);
                System.out.println("Available: " + ubxPort.bytesAvailable());
                int numRead = ubxPort.readBytes(readBuffer, readBuffer.length);
                System.out.println("Read " + numRead + " bytes.");
            }
        } catch (Exception e) { e.printStackTrace(); }
        System.out.println("\nSwitching over to event-based reading");
        System.out.println("\nListening for any amount of data available\n");
        ubxPort.addDataListener(new SerialPortDataListener() {
            public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; }
            public void serialEvent(SerialPortEvent event)
            {
                SerialPort comPort = event.getSerialPort();
                System.out.println("Available: " + comPort.bytesAvailable() + " bytes.");
                byte[] newData = new byte[comPort.bytesAvailable()];
                int numRead = comPort.readBytes(newData, newData.length);
                System.out.println("Read " + numRead + " bytes.");
            }
        });
        try { Thread.sleep(5000); } catch (Exception e) {}
        ubxPort.removeDataListener();
        System.out.println("\nNow listening for full 100-byte data packets\n");
        PacketListener listener = new PacketListener();
        ubxPort.addDataListener(listener);
        try { Thread.sleep(5000); } catch (Exception e) {}
        ubxPort.removeDataListener();
        System.out.println("\nNow listening for byte-delimited binary messages\n");
        MessageListener messageListener = new MessageListener();
        ubxPort.addDataListener(messageListener);
        try { Thread.sleep(5000); } catch (Exception e) {}
        ubxPort.removeDataListener();
        System.out.println("\n\nClosing " + ubxPort.getDescriptivePortName() + ": " + ubxPort.closePort());
        try { Thread.sleep(1000); } catch (InterruptedException e1) { e1.printStackTrace(); }
        System.out.println("Reopening " + ubxPort.getDescriptivePortName() + ": " + ubxPort.openPort() + "\n");
        ubxPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);
        InputStream in = ubxPort.getInputStream();
        try
        {
            for (int j = 0; j < 1000; ++j)
                System.out.print((char)in.read());
            in.close();
        } catch (Exception e) { e.printStackTrace(); }
        System.out.println("\n\nAttempting to read from two serial ports simultaneously\n");
        System.out.println("\nAvailable Ports:\n");
        for (int i = 0; i < ports.length; ++i)
            System.out.println("   [" + i + "] " + ports[i].getSystemPortName() + ": " + ports[i].getDescriptivePortName() + " - " + ports[i].getPortDescription());
        SerialPort ubxPort2;
        System.out.print("\nChoose your second desired serial port, or enter -1 to skip this test: ");
        serialPortChoice = 0;
        try {
            Scanner inputScanner = new Scanner(System.in);
            serialPortChoice = inputScanner.nextInt();
            inputScanner.close();
        } catch (Exception e) {}
        if (serialPortChoice != -1)
        {
            ubxPort2 = ports[serialPortChoice];
            ubxPort2.openPort();
            try
            {
                System.out.print("\nReading from first serial port...\n\n");
                in = ubxPort.getInputStream();
                InputStream in2 = ubxPort2.getInputStream();
                for (int j = 0; j < 1000; ++j)
                    System.out.print((char)in.read());
                System.out.print("\nReading from second serial port...\n\n");
                for (int j = 0; j < 100; ++j)
                    System.out.print((char)in2.read());
                System.out.print("\nReading from first serial port again...\n\n");
                for (int j = 0; j < 1000; ++j)
                    System.out.print((char)in.read());
                in.close();
                in2.close();
            }
            catch (SerialPortIOException e) { e.printStackTrace(); }
            catch (Exception e) { e.printStackTrace(); }
        }
        System.out.println("\n\nEntering Java-based InputStream in Scanner mode and reading 200 lines\n");
        ubxPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        Scanner scanner = new Scanner(ubxPort.getInputStream());
        for (int i = 1; i < 201; ++i)
            if (scanner.hasNextLine())
                System.out.println("Full Line #" + i + ": " + scanner.nextLine());
        scanner.close();
        System.out.println("\n\nClosing " + ubxPort.getDescriptivePortName() + ": " + ubxPort.closePort());
    }
}
