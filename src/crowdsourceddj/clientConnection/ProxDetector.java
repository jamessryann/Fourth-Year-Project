package crowdsourceddj.clientConnection;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ProxDetector {
	
	static Socket hostDevice;
	
	public void setHostDevice(Socket host){
		hostDevice=host;
	}
	
	
	//Sends message to a static android device to check a mac address strength
	public static boolean checkAddress(String macAddress){
		try {
			PrintWriter pw = new PrintWriter(hostDevice.getOutputStream());
			pw.println("check: "+macAddress);
			Scanner sc = new Scanner(hostDevice.getInputStream());
			if(sc.nextLine().contains("pass")){
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
