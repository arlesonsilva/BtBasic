package br.com.prot.btbasic;

import android.app.Notification;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by fredericom on 23/03/2018.
 */

public class ConnectionThread extends Thread {

    BluetoothAdapter btAdapter;
    BluetoothDevice btDevice;
    BluetoothSocket btSocket = null;
    BluetoothServerSocket btServerSocket = null;
    InputStream input = null;
    OutputStream output = null;
    String macDisp = null;
    String dispUIID = "229c9794-2ea2-11e8-b467-0ed5f89f718b";
    boolean server;
    boolean running = false;

    // Construtor p/ atuar como Servidor...
    public ConnectionThread() {
        this.server = true;
    }

    // Construtor p/ atuar como Cliente...
    public ConnectionThread(String dispositivoMac){
        this.server = false;
        this.macDisp = dispositivoMac;
    }

    public void run(){

        this.running = true;
        this.btAdapter = BluetoothAdapter.getDefaultAdapter();

        if(this.server){
            // C/ Servidor...
            try{
                btServerSocket = btAdapter.listenUsingRfcommWithServiceRecord("BtBasic", UUID.fromString(dispUIID));
                btSocket = btServerSocket.accept();

                if(btSocket != null){
                    btServerSocket.close();
                }
            }catch (IOException e){
                e.printStackTrace();
                toMainActivity("---N".getBytes());
            }
        }else{
            // C/ Cliente...
            try{
                btDevice = btAdapter.getRemoteDevice(macDisp);
                btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString(dispUIID));

                if (btSocket != null)
                    btSocket.connect();
            }catch (IOException e){
                e.printStackTrace();
                toMainActivity("---N".getBytes());
            }
        }
    }

    private void toMainActivity(byte[] data){
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putByteArray("data", data);
        message.setData(bundle);
        MainActivity.handler.sendMessage(message);
    }

    public void cancel(){

        try{
            running = false;
            btServerSocket.close();
            btSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        running = false;
    }
}
