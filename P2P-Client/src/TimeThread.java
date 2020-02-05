import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

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

		byte[] askTimeA = this.peer.getMSG(11, null);

		/*
		 * Send tag10 to all peers
		 */
		int askID = P2P.firstIndexID; //start from smallest ID
		while (askID < P2P.lastIndexID) {
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
		if (this.peer.time[0][0] != 0) {

			/*
			 * Convert list of time array to a long array
			 */
			long [] timeList = new long [this.peer.id - 1];
			for (int i = 0; i < this.peer.id - 1; i++) {
				timeList[i] = convertByteArrayToLong(this.peer.time[i]);
			}

			/*
			 * Calculate average time
			 */
			Date date = new Date();
			long myTime = date.getTime();
			long sum = myTime; // sum get the value of leader's time
			int counter = 1; //in case not all peer send back the time --> need to count number of times collected, start at 1 to count for leader
			for (int i = 0; i < this.peer.id; i++) {
				if(timeList[i] != 0) { //check if there is time
					sum = sum + timeList[i];
					counter++;
				}
			}
			long average = sum / counter;
			finalTime = longtoBytes(average);
		}

		/*
		 * Create message tag 13
		 */

		byte[] sendTimeA = this.peer.getMSG(13, finalTime);
		//insert final time array in the message

		/*
		 * Send tag13 to all peers with id < leader's id											// to all IDs
		 */
		int anounceID = P2P.firstIndexID;
		while (anounceID < P2P.lastIndexID) {														// edit from peerID to lastindex
			System.out.println("anounceID " + anounceID);
				try {
					this.peer.send(anounceID, sendTimeA);
				} catch (IOException e) {
					System.out.println("Could not send Time to " + anounceID);
					e.printStackTrace();
				}
				anounceID++;
		}
		
		//TODO clean this.timelist
	}


	/*
	 * Converting functions
	 */
	long convertByteArrayToLong(byte[] data){
	    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
	    byteBuffer.flip();
	    return byteBuffer.getLong();
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
