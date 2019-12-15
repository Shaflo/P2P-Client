class PeerThread implements Runnable {
	
	int port;
	String leader;
	P2P obj;
	
	PeerThread(P2P p2p) {
		this.leader = P2P.leader;
		this.port = P2P.port;
		this.obj = p2p;
	}
	
	public void run() {
			
		int timeA = 0;
		int sendA = 40;			// sending alive all 40sec
		int timer = 0;
		
		
		
			while (true) {
			
				/* WAITING */
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ie) {
					System.out.println("!P2PClient1 InterruptedException!");
					ie.printStackTrace();
				}
				
				//this.obj.newLog("sys", "spam!!!\n");
				
				/*********************/
				/*   SENDING ALIVE   */
				/*********************/
			
				if (timeA >= sendA) {									// sending alive
					timeA = 0;
					try {
						this.obj.sendMSG(this.leader, this.port, this.obj.getMSG(5, null));
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					timeA++;
				}
				
				/********************/
				/*   PEER MESSAGE   */
				/********************/
				
				if (timer > 20) {
					timer = 0;
					// TODO sending tag6
				} else {
					timer++;
				}
				
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