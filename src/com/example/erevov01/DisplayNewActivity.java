package com.example.erevov01;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

@SuppressLint("HandlerLeak")
public class DisplayNewActivity extends Activity {
	
	private Intent intentback;
    private boolean connected = false;
    private String serverIpAddress = "";
    private String serverPort = "";
    private Button buttonDISCONN;
	private SeekBar speedbar, dirbar;
	private Socket socket = null;
	Thread cThread;
	private boolean ready2send;
	private char commandSent;
	private char instruction;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.mainnewact);
	    
	    speedbar = (SeekBar) findViewById(R.id.dirbar);
	    dirbar = (SeekBar) findViewById(R.id.speedbar);
	    
	    speedbar.setProgress(0);
	    dirbar.setProgress(50);
	    
	    Intent intent = getIntent();
	    serverIpAddress = intent.getStringExtra(MainActivity.EXTRA_IP);
	    serverPort = intent.getStringExtra(MainActivity.EXTRA_PORT);
	    
	    intentback = new Intent(this, MainActivity.class);
	    
	    buttonDISCONN = (Button) findViewById(R.id.disconnectNEW);
	    
        buttonDISCONN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	connected = false;
            }
        });	
	    
        speedbar.setOnSeekBarChangeListener( new OnSeekBarChangeListener() {
        	public void onProgressChanged(SeekBar seekBar, int speedval, boolean fromUser) {
        		// TODO Auto-generated method stub
        	}
            public void onStartTrackingTouch(SeekBar seekBar) {
            	// TODO Auto-generated method stub
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
            	// TODO Auto-generated method stub
            	int valF = speedbar.getProgress();
        	    commandSent = (char)valF;
        	    instruction = '1';
            	ready2send = true;
            }
        });
        
        dirbar.setOnSeekBarChangeListener( new OnSeekBarChangeListener() {
        	public void onProgressChanged(SeekBar seekBar, int speedval, boolean fromUser) {
        		// TODO Auto-generated method stub
        	}
            public void onStartTrackingTouch(SeekBar seekBar) {
            	// TODO Auto-generated method stub
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
            	// TODO Auto-generated method stub
            	int valF = dirbar.getProgress();
        	    commandSent = (char)valF;
        	    instruction = '2';
            	ready2send = true;
            }
        });
        
	    ready2send = false;
        cThread = new Thread(new ClientThread());
		cThread.start();
	}
	
	@SuppressLint("HandlerLeak")
	public class ClientThread implements Runnable {
		public void run() {
				try {
					socket = new Socket(serverIpAddress, Integer.parseInt(serverPort));
				    char[] bufferTX = new char[2];
					///////////////////////////
					connected = true;
					while (connected) {
						if (ready2send) {
							try {
								bufferTX[0] = instruction;
								bufferTX[1] = commandSent;
								Log.d("SocketConnectionv02Activity", "C: Sending command.");
								PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);// where you issue the commands
								out.println(bufferTX);
								Log.d("SocketConnectionv02Activity", "C: Sent.");
							} catch (Exception e) {
								Log.e("SocketConnectionv02Activity", "S: Error", e);
							}
							ready2send = false;
						}
					}
					PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);// where you issue the commands
					out.println("fx"); // "exit" may not work in beaglebone since problem with the "e" 
					socket.close();
					Log.d("SocketConnectionv02Activity", "C: Closed.");
					startActivity(intentback);
				} catch (Exception e) {
					Log.e("SocketConnectionv02Activity", "C: Error", e);
					connected = false;
					startActivity(intentback);
				}				
		}
	}
	
}