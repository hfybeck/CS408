package com.android.aim.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

import com.android.aim.interfaces.IAppManager;
import com.android.aim.interfaces.ISocketOperator;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


public class SocketOperator implements ISocketOperator
{
	private static final String AUTHENTICATION_SERVER_ADDRESS = "lore.cs.purdue.edu"; //TODO change to your WebAPI Address
	
	private static final int AUTHENTICATION_SERVER_PORT = 8089; //TODO change to your WebAPI Address
	
	private int listeningPort = 0;
	
	private static final String HTTP_REQUEST_FAILED = new String();
	
	private HashMap<InetAddress, Socket> sockets = new HashMap<InetAddress, Socket>();
	
	private ServerSocket serverSocket = null;

	private boolean listening;

	private class ReceiveConnection extends Thread {
		Socket clientSocket = null;
		public ReceiveConnection(Socket socket) 
		{
			this.clientSocket = socket;
			SocketOperator.this.sockets.put(socket.getInetAddress(), socket);
		}
		
		@Override
		public void run() {
			 try {
				BufferedReader in = new BufferedReader(
						    new InputStreamReader(
						    		clientSocket.getInputStream()));
				String inputLine;
				
				 while ((inputLine = in.readLine()) != null) 
				 {
					 if (inputLine.equals("exit"))
					 {
						 clientSocket.shutdownInput();
						 clientSocket.shutdownOutput();
						 clientSocket.close();
						 SocketOperator.this.sockets.remove(clientSocket.getInetAddress());
					 }						 
				 }		
				
			} catch (IOException e) {
				Log.e("ReceiveConnection.run: when receiving connection ","");
			}			
		}	
	}

	public SocketOperator(IAppManager appManager) {	
	}
	
	
	public String sendHttpRequest(Context context, String params)
	{
		String result = new String();
		
		try{
			
			Log.i("LOGIN", "1");
			
			try {
		        Socket socket = new Socket(AUTHENTICATION_SERVER_ADDRESS, AUTHENTICATION_SERVER_PORT);
		        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		        out.println(params);
		        
		        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String inputLine;
	
				if ((inputLine = in.readLine()) != null) {
					result = result.concat(inputLine);
					Log.i("LOGIN", result);
				}
				in.close();
				socket.close();
		        
		    }catch(IOException e){
		        e.printStackTrace();
		    }
			
			if (result.length() == 0) {
				result = HTTP_REQUEST_FAILED;
			}
		
			Log.i("LOGIN", result);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	
	}

	public int startListening(int portNo) 
	{
		listening = true;
		
		try {
			serverSocket = new ServerSocket(portNo);
			this.listeningPort = portNo;
		} catch (IOException e) {
			this.listeningPort = 0;
			return 0;
		}

		while (listening) {
			try {
				new ReceiveConnection(serverSocket.accept()).start();
				
			} catch (IOException e) {			
				return 2;
			}
		}
		
		try {
			serverSocket.close();
		} catch (IOException e) {			
			Log.e("Exception server socket", "Exception when closing server socket");
			return 3;
		}
		
		
		return 1;
	}
	
	
	public void stopListening() 
	{
		this.listening = false;
	}
	
	public void exit() 
	{			
		for (Iterator<Socket> iterator = sockets.values().iterator(); iterator.hasNext();) 
		{
			Socket socket = (Socket) iterator.next();
			try {
				socket.shutdownInput();
				socket.shutdownOutput();
				socket.close();
			} catch (IOException e) 
			{				
			}		
		}
		
		sockets.clear();
		this.stopListening();
	}


	public int getListeningPort() {
		
		return this.listeningPort;
	}	

}
