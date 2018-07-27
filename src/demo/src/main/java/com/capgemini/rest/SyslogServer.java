package com.capgemini.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;

public class SyslogServer {
	public static void main(String[] args) {
		int port = 8999;
		String protocol = "tcp";
		if (args.length > 0) {
			port = new Integer(args[0]);
		}
		if (args.length > 1) {
			protocol = args[1];
		}
		System.out.println(String.format("Listening %d", port));
		if ("tcp".equals(protocol)) {
			tcpServer(port);
		} else {
			udpServer(port);
		}
	}
	
	public static void tcpServer(int port) {
		ServerSocket s = null;
		try {
			s = new ServerSocket(port);
			while (true) {
				Socket connectionSocket = null;
				BufferedReader reader = null;
				try {
					connectionSocket = s.accept();
					reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream(), Charset.forName("UTF-8")));
					String str;
					while((str = reader.readLine()) != null) {
						System.out.println(str);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						connectionSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void udpServer(int port) {
		DatagramSocket serverSocket = null;
		
		try {
			serverSocket = new DatagramSocket(port);
			byte[] receiveData = new byte[1024];
	
			while(true)
			{
				try {
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					serverSocket.receive(receivePacket);
					String s = new String( receivePacket.getData());
					System.out.println(s);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} finally {
			try {
				serverSocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
