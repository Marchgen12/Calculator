package com.example.calculator;

//import androidx.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class  MainActivity extends Activity implements View.OnClickListener {
    EditText t1;
    EditText t2;

    ImageButton plus;
    ImageButton minus;
    ImageButton multiply;
    ImageButton divide;

    TextView displayResult;

    String oper = "";

    String serverData;

    private Socket socket;
    private static final int SERVERPORT = 5000;
    private static final String SERVER_IP = "192.168.254.14";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t1 = (EditText) findViewById(R.id.t1);
        t2 = (EditText) findViewById(R.id.t2);

        plus = (ImageButton) findViewById(R.id.plus);
        minus = (ImageButton) findViewById(R.id.minus);
        multiply = (ImageButton) findViewById(R.id.multiply);
        divide = (ImageButton) findViewById(R.id.divide);

        displayResult = (TextView) findViewById(R.id.displayResult);

        plus.setOnClickListener(this);
        minus.setOnClickListener(this);
        multiply.setOnClickListener(this);
        divide.setOnClickListener(this);

        new Thread(new ClientThread()).start();

        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);
    }

    @Override
    public void onClick(View v) {
        double num1 = 0;
        double num2 = 0;
        double result = 0;

        if (TextUtils.isEmpty(t1.getText().toString()) || TextUtils.isEmpty(t2.getText().toString())){
            return;
        }

        num1 = Float.parseFloat(t1.getText().toString());
        num2 = Float.parseFloat(t2.getText().toString());

        switch (v.getId()){
            case R.id.plus:
                oper = "+";
                result = num1 + num2;
                break;

            case R.id.minus:
                oper = "-";
                result = num1 - num2;
                break;

            case R.id.multiply:
                oper = "*";
                result = num1 * num2;
                break;

            case R.id.divide:
                oper = "/";
                result = num1 / num2;
                break;
        }

        serverData = num1 + " " + oper + " " + num2;

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())), true);
            out.println(serverData);
        } catch (UnknownHostException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

        try{
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            System.out.println(input);

            String inputLine = null;

            inputLine = input.readLine();
            displayResult.setText(inputLine);
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + SERVERPORT + " or listening for a connection");
            System.out.println(e.getMessage());
        }

        //displayResult.setText(num1 + " " + oper + " " + num2 + " = " + result);
    }

    class ClientThread implements Runnable {

        @Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                socket = new Socket(serverAddr, SERVERPORT);
            } catch (UnknownHostException el) {
                el.printStackTrace();
            } catch (IOException el) {
                el.printStackTrace();
            } catch (Exception el){
                el.printStackTrace();
            }
        }

    }
}
