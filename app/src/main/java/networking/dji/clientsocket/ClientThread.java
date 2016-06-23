package networking.dji.clientsocket;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by wangxiangjiu on 6/22/16.
 */
public class ClientThread implements Runnable {


    private Socket s;

    //定义向UI线程发送消息的Handler 对象
    private Handler handler;

    //定义接受UI线程发送消息的Handler 对象
    public Handler revHandler;

    // 该线程所处理的Socket所对应的输入流
    BufferedReader br = null;
    OutputStream os = null;

    public ClientThread(Handler handler) {
        this.handler = handler;
    }

    public void run() {
        try {
            s = new Socket("192.168.1.88", 2001);
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            os = s.getOutputStream();
            //启动一条子线程来读取服务器响应的数据
            new Thread() {
                @Override
                public void run() {
                    String content = null;
                    try {
                        while ((content = br.readLine()) != null) {
                            Message msg = new Message();
                            msg.what = 0x123;
                            msg.obj = content;
                            handler.sendMessage(msg);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            Looper.prepare();
            revHandler = new Handler() {
               @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 0x345) {
                        try {
                            os.write((msg.obj.toString() + "\r\n").getBytes("utf-8"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
               }
            };
            Looper.loop();
        } catch (SocketTimeoutException e1) {
            System.out.println("网络连接照时！！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
