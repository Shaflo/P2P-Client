import java.awt.Color;

public class ElectionThread implements Runnable {
	
	P2P peer;
	
	ElectionThread(P2P _peer) {
		this.peer = _peer;
	}

	public void run() {
		// TODO find higher id who adopt the election
		// TODO Update Status in GUI and terminate after found higher id or sending tag 10
		this.peer.electStat.setText("search IDs: " + (this.peer.id+1));
		this.peer.electStat.setBackground(Color.GREEN);
		
		int askID = this.peer.id;
		
		while (!this.peer.doLeaderElection && askID < P2P.lastIndexID) {
			askID++;
			// TODO search higher ID and ask Tag 9 then sleep 5 seconds
		}
		
		if (!this.peer.doLeaderElection && askID >= P2P.lastIndexID) {
			this.peer.doLeaderElection = false;
			this.peer.isLeader = true;
			this.peer.leaderStatus.setBackground(Color.GREEN);
			// TODO sending Tag 10 to everyone
		}
	}
}
