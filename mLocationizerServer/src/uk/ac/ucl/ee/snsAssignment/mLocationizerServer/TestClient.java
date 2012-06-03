package uk.ac.ucl.ee.snsAssignment.mLocationizerServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import uk.ac.ucl.ee.snsAssignment.mLocationizer.Message;

/**
 *
 * @author hasanein
 */
public class TestClient
{
    public static void main(String args[]) throws UnknownHostException, IOException, ClassNotFoundException
    {        
        InetAddress address = InetAddress.getByName("192.168.117.1");
        Socket connection = new Socket(address, 1234);
        Message messsage = new Message(Message.MSG_TYPE_ADD_FRIEND_REQUEST);
        messsage.putParameterKeyValue(Message.PAR_TYPE_MSISDN, "07746663954");        
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(connection.getOutputStream());
        objectOutputStream.writeObject(messsage);
        objectOutputStream.flush();
        ObjectInputStream objectInputStream = new ObjectInputStream(connection.getInputStream());
        Message x = (Message) objectInputStream.readObject();
        System.out.println(x.getMessageType());
    }
}
