package com.example.murdo_000.myapplication;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Spinner;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by murdo_000 on 2015-10-05.
 */
public class MessageHandler extends AsyncTask<Integer, Void, Void>{

    private String message;
    private MainActivity act;

    public MessageHandler(MainActivity a, Spinner spin, String str) throws IOException{
        message = spin.getSelectedItem().toString()+";"+str;
        act = a;
    }

    @Override
    protected Void doInBackground(Integer... params) {
        try{
            Socket host = new Socket("192.168.1.130", 4545);
            PrintWriter pw =  new PrintWriter(host.getOutputStream(), true);
            pw.println(message);
            Scanner sc = new Scanner(host.getInputStream());
            while(sc.hasNextLine()){
                for(int i =0;i<act.mArrayAdapter.getCount();i++) {
                    String s = sc.nextLine();
                    if(act.mArrayAdapter.getItem(i).contains(s)){
                        pw.println("device found");
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }



}
