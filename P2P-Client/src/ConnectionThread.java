import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Iterator;

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
					if (ans[0] == (byte) 7) {
						System.out.println("yeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
						
						boolean newMSG = true;
						Iterator<byte[]> itr = this.peer.searches.iterator();
						while (itr.hasNext()) {
							byte[] x = itr.next();
							if (result[8] == x[0] && result[9] == x[1] && result[10] == x[2] && result[11] == x[3]) {
								newMSG = false;
							}
						}
						if (newMSG) {
						String hostn = "" + (result[2]&0xFF) + "."
								+ (result[3]&0xFF) + "." + (result[4]&0xFF) + "."
								+ (result[5]&0xFF);
						this.peer.searches.add(new byte[] {result[8], result[9], result[10], result[11]});
						this.peer.sendMSG(hostn, P2P.twoToInt(new byte[] {result[6], result[7]}), ans);
						
						} else {
							System.out.println("PEER" + this.peer.id + ": R 6 bereits mit 7 geantwortet!");
						}
					} else {
						out.write(ans);
						System.out.println("PEER" + this.peer.id + ": S " + ans[0] + " ---> " + Arrays.toString(ans));
					}
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