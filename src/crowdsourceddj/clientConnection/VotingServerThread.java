package crowdsourceddj.clientConnection;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import crowdsourceddj.genreradio.DynamicPlaylister;
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
			while(!quit && !clientSocket.isClosed()){
				if(sc.hasNextLine()){
					message = sc.next();
					System.out.println("Got message from device: "+message);
					if(message.contains(";") && ProxDetector.checkAddress(message.split(";")[1])){
						GenreUtils.addGenre(message.split(";")[0]);
					}
					quit = true;
				}
			}
			sc.close();
			out.close();
			clientSocket.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}

}
