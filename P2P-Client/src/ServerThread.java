import java.io.*;
import java.net.*;

class ServerThread implements Runnable {
	
	int port;
	P2P peer;
	
	ServerThread(int _port, P2P _peer) {
		this.port = _port;
		this.peer = _peer;
	}
	
	public void run() {
		try {
			@SuppressWarnings("resource")
			ServerSocket welcomeSocket = new ServerSocket(this.port);
			while (true) {
				Socket connectionSocket = welcomeSocket.accept();
				System.out.println("SERVER: " + connectionSocket.getInetAddress() + ":" + connectionSocket.getPort() + " connected!");
				new Thread(new ConnectionThread(connectionSocket, this.peer)).start();
			}
		} catch (IOException ioe) {
			System.out.println("!ServerThread IOException!");
			ioe.printStackTrace();
		}
	}
}
