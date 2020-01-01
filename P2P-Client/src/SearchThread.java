import java.io.IOException;
import java.net.UnknownHostException;

public class SearchThread implements Runnable {
	
	P2P peer;
	int whois;
	
	SearchThread(P2P _peer, int _whois) {
		this.peer = _peer;
		this.whois = _whois;
	}
	
	
	public void run() {
		
		// create message
		byte[] rec = new byte[14];
		byte[] help = P2P.twoToByte(this.whois);
		rec[12] = help[0];
		rec[13] = help[1];
		rec[2] = this.peer.ipA[0];
		rec[3] = this.peer.ipA[1];
		rec[4] = this.peer.ipA[2];
		rec[5] = this.peer.ipA[3];
		rec[6] = this.peer.portA[0];
		rec[7] = this.peer.portA[1];
		rec[8] = this.peer.idA[0];
		rec[9] = this.peer.idA[1];
		this.peer.searchID++;
		help = P2P.twoToByte(this.peer.searchID);
		rec[10] = help[0];
		rec[11] = help[1];
		String node1 = "";
		String node2 = "";
		String node3 = "";
		String node4 = "";
		rec = this.peer.getMSG(6, rec);
		if (this.peer.list[0][0] != 0) {
			node1 = "" + (this.peer.list[0][0]&0xFF) + "."
					+ (this.peer.list[0][1]&0xFF) + "." + (this.peer.list[0][2]&0xFF) + "."
					+ (this.peer.list[0][3]&0xFF);
		}
		if (this.peer.list[1][0] != 0) {
			node2 = "" + (this.peer.list[1][0]&0xFF) + "."
					+ (this.peer.list[1][1]&0xFF) + "." + (this.peer.list[1][2]&0xFF) + "."
					+ (this.peer.list[1][3]&0xFF);
		}
		if (this.peer.list[2][0] != 0) {
			node3 = "" + (this.peer.list[2][0]&0xFF) + "."
					+ (this.peer.list[2][1]&0xFF) + "." + (this.peer.list[2][2]&0xFF) + "."
					+ (this.peer.list[2][3]&0xFF);
		}
		if (this.peer.list[3][0] != 0) {
			node4 = "" + (this.peer.list[3][0]&0xFF) + "."
					+ (this.peer.list[3][1]&0xFF) + "." + (this.peer.list[3][2]&0xFF) + "."
					+ (this.peer.list[3][3]&0xFF);
		}
		this.peer.searches.add(new byte[] {rec[8], rec[9], rec[10], rec[11]});
		// sending to nodes
		try {
			if (node1.length() > 3)
				this.peer.sendMSG(node1, this.peer.port, rec);
			if (node2.length() > 3)
				this.peer.sendMSG(node2, this.peer.port, rec);
			if (node3.length() > 3)
				this.peer.sendMSG(node3, this.peer.port, rec);
			if (node4.length() > 3)
				this.peer.sendMSG(node4, this.peer.port, rec);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int timer = 0;
		// wait for answer
		while (timer < 5) {
			timer++;
			byte[] helper = new byte[6];
			
			helper = this.peer.getList(new byte[] {rec[12], rec[13]});
			
			if (helper[0] != (byte) 0) {
				
				System.out.println("ID gefunden: " + P2P.twoToInt(new byte[] {rec[12], rec[13]}));
				return;
				
			} else {
				
				System.out.println("not found... wait 1 sec");
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			}
		}
	}
}