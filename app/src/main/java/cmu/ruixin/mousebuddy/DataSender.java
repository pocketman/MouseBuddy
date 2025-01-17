package cmu.ruixin.mousebuddy;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

/**
 * Created by JJ on 2/21/2015.
 */
public class DataSender implements Runnable {

    static final int DATA_PORT = 8888;
    InetAddress connectAddr;
    MouseActivity activity;

    public DataSender(InetAddress connectAddr, MouseActivity ma)
    {
        super();
        this.connectAddr = connectAddr;
        activity = ma;
    }

    @Override
    public void run()
    {
        try {
            Socket dataSocket = new Socket(connectAddr, DATA_PORT);
            DataInputStream keepAlive;
            DataOutputStream sendStream;

            keepAlive = new DataInputStream(dataSocket.getInputStream());
            sendStream = new DataOutputStream(dataSocket.getOutputStream());
            boolean alive = keepAlive.readBoolean();
            while (alive) {
                if (activity.isActive()) {
                    sendStream.writeBoolean(true);
                    // movements
                    sendStream.writeFloat(activity.deltaX);
                    sendStream.writeFloat(activity.deltaY);
                    activity.deltaX = 0;
                    activity.deltaY = 0;
                    // mouse button status
                    sendStream.writeBoolean(activity.leftMouseDown);
                    sendStream.writeBoolean(activity.rightMouseDown);

                } else {
                    sendStream.writeBoolean(false);
                }
                try {
                    Thread.sleep(50);
                }
                catch (Exception e)
                {
                    break;
                }
            }
            keepAlive.close();
            sendStream.close();
        }
        catch (IOException e) {
        }
    }
}
