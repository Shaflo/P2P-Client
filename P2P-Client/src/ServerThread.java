import java.io.*;
import java.net.*;

class ServerThread implements Runnable {
	
	P2P peer;
	
	ServerThread(P2P _peer) {
		this.peer = _peer;
	}
	
	public void run() {
		try {
			@SuppressWarnings("resource")
			ServerSocket welcomeSocket = new ServerSocket(this.peer.port);
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
