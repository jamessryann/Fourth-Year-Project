package crowdsourceddj.clientConnection;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class ProxDetector {
	
	static Socket hostDevice;
	static PrintWriter pw;
	static Scanner sc;
	static Timer timer = new Timer();
	static ArrayList<String> devices = new ArrayList<String>();
	
	public static Socket getHostDevice() {
		return hostDevice;
	}


	public static void setHostDevice(Socket host){
		hostDevice=host;
		try {
			pw = new PrintWriter(hostDevice.getOutputStream(), true);
			sc = new Scanner(hostDevice.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				for(String s : devices){
					checkAddress(s);
				}
			}	
		}, 0, 300000);
	}
	
	
	//Sends message to a static android device to check a mac address strength
	public static synchronized boolean checkAddress(String macAddress){
		pw.println("check: "+macAddress);
		pw.flush();
		if(sc.hasNext()){
			String temp = sc.nextLine();
			if (temp.contains("pass")){
				System.out.println("device verified");
				if(!devices.contains(macAddress)){
					devices.add(macAddress);
				}
				return true;
			}
		}
		System.out.println("device not verified");
		return false;
	}

}
