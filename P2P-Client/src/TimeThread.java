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
		 */																						// EDIT
		/*byte[] askTimeA = new byte[10];
		askTimeA[0] = (byte)11;
		askTimeA[1] = (byte)1;
		askTimeA[2] = this.peer.ipA[0];
		askTimeA[3] = this.peer.ipA[1];
		askTimeA[4] = this.peer.ipA[2];
		askTimeA[5] = this.peer.ipA[3];
		askTimeA[6] = this.peer.portA[0];
		askTimeA[7] = this.peer.portA[1];
		askTimeA[8] = this.peer.idA[0];
		askTimeA[9] = this.peer.idA[1];*/
		
		byte[] askTimeA = this.peer.getMSG(11, null);
		
		/*
		 * Send tag10 to all peers with id < leader's id												// to all IDs
		 */
		int askID = P2P.firstIndexID; //start from smallest ID
		while (askID < this.peer.id) { //until reach leader's id
			//System.out.println("askID " + askID); 
				try {
					this.peer.send(askID, askTimeA); //send tag11 to peer
				} catch (IOException e) {
					System.out.println("Could not ask Time of " + askID);
					e.printStackTrace();
				}
				askID++; //increase id for next peer
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
		
		byte[] sendTimeA = new byte[18];															// edit from 10 to 18
		sendTimeA[0] = (byte)13;
		sendTimeA[1] = (byte)1;
		sendTimeA[2] = this.peer.ipA[0];
		sendTimeA[3] = this.peer.ipA[1];
		sendTimeA[4] = this.peer.ipA[2];
		sendTimeA[5] = this.peer.ipA[3];
		sendTimeA[6] = this.peer.portA[0];
		sendTimeA[7] = this.peer.portA[1];
		sendTimeA[8] = this.peer.idA[0];
		sendTimeA[9] = this.peer.idA[1];
		//insert final time array in the message
		for (int i = 0; i < 8; i++) {
			sendTimeA[10 + i] = finalTime[i];							// edit [9+i] to [10+i]		// Exception
		}
		
		/*
		 * Send tag13 to all peers with id < leader's id											// to all IDs
		 */
		int anounceID = P2P.firstIndexID;
		while (anounceID < this.peer.id) {
			System.out.println("anounceID " + anounceID);
				try {
					this.peer.send(anounceID, sendTimeA);
				} catch (IOException e) {
					System.out.println("Could not send Time to " + askID);
					e.printStackTrace();
				}
				anounceID++;
		}
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
