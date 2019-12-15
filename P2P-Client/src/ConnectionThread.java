import java.io.*;
import java.net.*;
import java.util.Arrays;

class ConnectionThread implements Runnable {
	
	Socket connectionSocket;
	P2P peer;

	ConnectionThread(Socket connectionSocket, P2P _peer) {
		this.connectionSocket = connectionSocket;
		this.peer = _peer;
	}

	public void run() {
		
		try {
			InputStream in = new DataInputStream(connectionSocket.getInputStream());
			OutputStream out = new DataOutputStream(connectionSocket.getOutputStream());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte buffer[] = new byte[1024];
			baos.write(buffer, 0, in.read(buffer));
			byte[] result = baos.toByteArray();
			
			if (result.length > 3) {
				byte[] ans = this.peer.handleMSG(result);
				if (ans != null) {
					out.write(ans);
					System.out.println("SERVER: S " + ans[0] + " ---> " + Arrays.toString(ans));
				}
			}
			connectionSocket.close();
			in.close();
			out.close();
			System.out.println("SERVER: " + connectionSocket.getInetAddress() + ":" + connectionSocket.getPort() + " disconnected!");
		} catch (IOException ioe) {
			System.out.println("!ConnectionThread IOException!");
			ioe.printStackTrace();
		}
	}
}