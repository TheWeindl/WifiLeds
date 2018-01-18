package com.damnserver.hgb1.androidclient;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

public class ClientTask extends AsyncTask<String, Void, Void>{

    Socket sock;
    PrintWriter writer;
    String msg;
    Context context;
    Handler handler = new Handler();
    String ip;
    int port;

    ClientTask(Context context, String ip, int port){
        this.context = context;
        this.ip = ip;
        this.port = port;
    }

    @Override
    protected Void doInBackground(String... params) {

        msg = params[0];

        try {
            sock = new Socket(ip,port);
            writer = new PrintWriter(sock.getOutputStream());
            writer.write(msg + '\n');
            writer.flush();
            writer.write("END");
            writer.flush();

            writer.close();
            sock.close();

            Log.i(this.toString(), "Socket closed");
            Log.i(this.toString(), "Message written");

            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Colors sent to LED Strip", Toast.LENGTH_LONG).show();
                }
            });
        }
        catch (ConnectException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
