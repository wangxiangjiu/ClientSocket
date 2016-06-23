package networking.dji.clientsocket;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    TextView serverMessage;
    Socket clientSocket;
    TextView serverReceptor;

    InputStream in = null;
    OutputStream os = null;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            messageDisplay(msg.obj.toString());
        }
    };

    public void messageDisplay(String servermessage) {
        serverReceptor.setText("" + servermessage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serverMessage = (TextView) findViewById(R.id.textView);
        serverReceptor = (TextView) findViewById(R.id.server_receptor);
    }

    public void start(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    clientSocket = new Socket("192.168.1.102", 2001);
                    in = clientSocket.getInputStream();
                    os = clientSocket.getOutputStream();
                    int len;
                    try {
                        byte[] buffer = new byte[1024 * 8];
                        while (true) {
                            len = in.read(buffer);
                            Message serverReceptor = Message.obtain();
                            //serverReceptor.obj = "DJI" + len;
                            if(len == 9)
                            {
                                serverReceptor.obj = "UP" + len;
                            }
                            if(len == 11)
                            {
                                serverReceptor.obj = "DOWN" + len;
                            }
                            mHandler.sendMessage(serverReceptor);
                            Thread.sleep(100);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
