package crowdsourceddj.clientConnection;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import crowdsourceddj.genreradio.DynamicPlaylister;

public class VotingServerConnector {
	
	public VotingServerConnector(int portNum) throws IOException{
		ServerSocket host = new ServerSocket(portNum);
		boolean awaitingConnection=true;
		while(awaitingConnection){
			new VotingServerThread(host.accept()).start();
		}
		host.close();
	}
	
	public static void main(String[] args){
		try {
			VotingServerConnector vs = new VotingServerConnector(4545);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
