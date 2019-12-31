import java.awt.Color;
import java.io.IOException;

public class ElectionThread implements Runnable {
	
	P2P peer;
	
	ElectionThread(P2P _peer) {
		this.peer = _peer;
	}

	public void run() {
		this.peer.electStat.setBackground(Color.GREEN);
		
		int askID = P2P.twoToInt(this.peer.idA);
		
		while (!this.peer.doLeaderElection && askID < P2P.lastIndexID) {
			askID++;
			this.peer.electStat.setText("search IDs: " + askID);
			try {
				this.peer.send(askID, new byte[] {(byte) 9, (byte) 1, this.peer.ipA[0], this.peer.ipA[1],
						this.peer.ipA[2], this.peer.ipA[3], this.peer.portA[0], this.peer.portA[1],
						this.peer.idA[0], this.peer.idA[1]});
			} catch (IOException e) {
				System.out.println("ELECTION could not send 9 to " + askID);
				e.printStackTrace();
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		
		if (!this.peer.doLeaderElection && askID >= P2P.lastIndexID) {
			this.peer.doLeaderElection = false;
			this.peer.isLeader = true;
			this.peer.leaderStatus.setBackground(Color.GREEN);
			
			int sendToID = P2P.firstIndexID;
			while (sendToID <= P2P.lastIndexID) {
				
				this.peer.search(sendToID);
				

					try {
						this.peer.send(sendToID, new byte[] {(byte) 10, (byte) 1, this.peer.ipA[0], this.peer.ipA[1],
								this.peer.ipA[2], this.peer.ipA[3], this.peer.portA[0], this.peer.portA[1],
								this.peer.idA[0], this.peer.idA[1]});
					} catch (IOException e) {
						System.out.println("could not send IAmLeader to " + sendToID);
						e.printStackTrace();
					}

				
				
				sendToID++;
			}
		}
		
		this.peer.doLeaderElection = false;
		this.peer.electStat.setBackground(Color.GRAY);
		this.peer.electStat.setText("no voting");
	}
}
