import java.util.Date;

class PeerThread implements Runnable {
	
	P2P peer;
	
	PeerThread(P2P _peer) {
		this.peer = _peer;
	}
	
	public void run() {
			
		int timeA = 0;
		int sendA = 40;			// sending alive all 30 sec
		
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}

			/******************************/
			/*   CONNECTING WITH LEADER   */
			/******************************/
			
			while (!this.peer.connected) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
				System.out.println("try to connect to leader");
				try {
					this.peer.sendMSG(this.peer.leader, P2P.defaultPort, this.peer.getMSG(1, null));
				} catch (Exception e) {
					System.out.println("...");
				}
			}
			
			
			
			/*********************/
			/*   SENDING ALIVE   */
			/*********************/
		
			if (timeA >= sendA) {
				timeA = 0;
				try {
					this.peer.sendMSG(this.peer.leader, P2P.defaultPort, this.peer.getMSG(5, null));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				timeA++;
			}
			
			
			
			/********************/
			/*   TIME REFRESH   */
			/********************/
			
			
			this.peer.myTimeLong += 1000;
			this.peer.date = new Date(this.peer.myTimeLong);
			this.peer.infoTime.setText("   Time: " + this.peer.dateFormat.format(this.peer.date));
			
			
			
			/******************/
			/*   CHECK LIST   */
			/******************/
		
			/*if (timer > 33) {
				P2P.checkTimestamp();
				timer = 0;
			} else {
				timer++;
			}*/
		}
	}
}