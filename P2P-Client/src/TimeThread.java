import java.io.IOException;
import java.nio.ByteBuffer;

public class TimeThread implements Runnable {

	P2P peer;
	byte[]  finalTime = new byte[8];

	TimeThread(P2P _peer) {
		this.peer = _peer;
	}

	public void run() {
		/*
		 * Create message tag 11
		 */
		
		System.out.println("#########################################################################################");
		System.out.println("##################################################################################################################################################");
		System.out.println("################################################################################################");
		
		byte[] askTimeA = this.peer.getMSG(11, null);

		/*
		 * Send tag10 to all peers
		 */
		int askID = P2P.firstIndexID; //start from smallest ID
		while (askID <= this.peer.id) {
			//System.out.println("askID " + askID);

				try {
					this.peer.send(askID, askTimeA); //send tag11 to peer
				} catch (IOException e) {
					System.out.println("Could not ask Time of " + askID);
					e.printStackTrace();
				}
				askID++; //increase id for next peer
		}



		try {
			Thread.sleep(5000);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}

		
		/*
		 * Calculating standard time
		 * "Time" array is list of time (byte arr) collected from peer, "time"'s length = leader's id - 1, each element is an array of length 8
		 * Convert "time" to an array of long number
		 * Calculate the average of these long numbers
		 * Convert the average back to an array (length = 8)
		 */

		long newSum = 0l;
		int newCount = 0;
		for (int i = 0; i < this.peer.timelist.length; i++) {
			if (this.peer.timelist[i][0] != 0) {
				newCount++;
				newSum += convertByteArrayToLong(new byte[] {this.peer.timelist[i][16], this.peer.timelist[i][17],
						this.peer.timelist[i][18], this.peer.timelist[i][19], this.peer.timelist[i][20],
						this.peer.timelist[i][21], this.peer.timelist[i][22], this.peer.timelist[i][23]});
			}
		}
		long newAverage = newSum / newCount;


		/*
		 * Send message tag 13
		 */
		
		int i = 0;
		byte[] sendTimeA = new byte[8];
		
		while(this.peer.timelist[i][0] != 0) {
			long cach = convertByteArrayToLong(new byte[] {this.peer.timelist[i][8], this.peer.timelist[i][9], 
					this.peer.timelist[i][10], this.peer.timelist[i][11], this.peer.timelist[i][12], 
					this.peer.timelist[i][13], this.peer.timelist[i][14], this.peer.timelist[i][15]});
			cach += newAverage;
			byte[] newTime = longtoBytes(cach);
			sendTimeA = this.peer.getMSG(13, newTime);
			try {
				this.peer.sendMSG(("" + (this.peer.timelist[i][0]&0xFF) + "."
						+ (this.peer.timelist[i][1]&0xFF) + "." + (this.peer.timelist[i][2]&0xFF) + "."
						+ (this.peer.timelist[i][3]&0xFF)),
						P2P.twoToInt(new byte[] {this.peer.timelist[i][4], this.peer.timelist[i][5]}),
						sendTimeA);
			} catch (IOException e) {
				System.out.println("Could not send Time!!!");
				e.printStackTrace();
			}
			i++;
		}
		
		

		
		
		//Clean TimeList
		for (int m = 0; m < this.peer.timelist.length; m++) {
			for (int j = 0; j < this.peer.timelist[m].length; j++) {
				this.peer.timelist[m][j] = 0;
			}
		}
	}

	/*
	 * Converting functions
	 */
	long convertByteArrayToLong(byte[] data){
	    return ByteBuffer.wrap(data).getLong();
	}

	byte[] longtoBytes(long data) {
		return new byte[]{
		 (byte) ((data >> 56) & 0xff),
		 (byte) ((data >> 48) & 0xff),
		 (byte) ((data >> 40) & 0xff),
		 (byte) ((data >> 32) & 0xff),
		 (byte) ((data >> 24) & 0xff),
		 (byte) ((data >> 16) & 0xff),
		 (byte) ((data >> 8) & 0xff),
		 (byte) ((data >> 0) & 0xff),
		 };
	}

}
