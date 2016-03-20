package crowdsourceddj.clientConnection;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import crowdsourceddj.utils.GenreUtils;

public class VotingServerThread extends Thread {
	
	private Socket clientSocket;
	
	public VotingServerThread(Socket client){
		super("VotingServerThread");
		clientSocket = client;
	}
	
	public void run(){
		try{
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			Scanner sc = new Scanner(clientSocket.getInputStream());
			String message;
			boolean quit = false;
			if(sc.hasNextLine()){
				message = sc.next();
				System.out.println("Got message from device: "+message);
				if(ProxDetector.getHostDevice()==null){
					ProxDetector.setHostDevice(clientSocket);
				}else{
					out.println("done: done");
					quit=true;
				}
				if(message.contains(";") && ProxDetector.checkAddress(message.split(";")[1])){
					GenreUtils.addGenre(message.split(";")[0].toLowerCase());
				}
			}
			if(quit){
				sc.close();
				out.close();
				clientSocket.close();
			}
			return;
		}catch (IOException e){
			e.printStackTrace();
		}
	}

}
