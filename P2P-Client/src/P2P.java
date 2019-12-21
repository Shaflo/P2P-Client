import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



class P2P implements ActionListener {
	
	
	
			/************/
			/*   INIT   */
			/************/
	
	/* Settings */
	static int defaultPort = 3333;
	static int maxKnownPeers = 4;
	static int peerIDs = 1000;			// delete
	static int firstIndexID = 1000;
	static int lastIndexID = 1025;
	
	/* Peer */
	boolean connected;
	String leader;
	boolean isLeader;
	String ip;
	byte[] ipA;
	int port;
	byte[] portA;
	int id;
	byte[] idA;
	byte[][] list;
	int searchID;
	LinkedList<byte[]> searches;
	
	/* GUI */
	JFrame frame;
	JPanel contentPane;
	JPanel statusPanel;				// Status Panel
	JTextField connectStatus;
	JTextField leaderStatus;
	JPanel midPanel;				// MID Panel
	JPanel infoPanel;				// Peer Panel
	JLabel infoHeader;
	JLabel infoID;
	JLabel infoIP;
	JLabel infoPort;
	JPanel listP;					// List Panel
	JLabel listL;
	JTextArea listTA;
	JPanel searchP;					// Search Panel
	JTextField searchStat;
	JTextField searchTF;
	JButton startSearch;
	JPanel electPanel;				// Election Panel
	JLabel electLabel;
	JTextField electStat;
	JButton electButton;
	
	private P2P(int _port, String _leader) {
		
		/* GUI */
		this.frame = new JFrame("P2P-Client");
		this.frame.setSize(500, 300);
		this.contentPane = new JPanel();
		this.contentPane.setLayout(new BoxLayout(this.contentPane, BoxLayout.Y_AXIS));
		this.connectStatus = new JTextField("         connected");													// Status Panel
		this.connectStatus.setBackground(Color.RED);
		this.leaderStatus = new JTextField("          leader");
		this.statusPanel = new JPanel();
		this.statusPanel.setLayout(new BoxLayout(this.statusPanel, BoxLayout.X_AXIS));
		this.statusPanel.add(this.connectStatus);
		this.statusPanel.add(this.leaderStatus);
		this.contentPane.add(this.statusPanel);
		this.infoPanel = new JPanel();																				// Peer Panel
		this.infoPanel.setLayout(new BoxLayout(this.infoPanel, BoxLayout.Y_AXIS));
		this.infoHeader = new JLabel("              PEER                                                    ");
		this.infoPanel.add(this.infoHeader);
		this.infoID = new JLabel("   ID: ");
		this.infoPanel.add(this.infoID);
		this.infoIP = new JLabel("   IP: ");
		this.infoPanel.add(this.infoIP);
		this.infoPort = new JLabel("   Port: ");
		this.infoPanel.add(this.infoPort);
		this.midPanel = new JPanel();
		this.midPanel.setLayout(new BoxLayout(this.midPanel, BoxLayout.X_AXIS));
		this.midPanel.add(this.infoPanel);
		this.listP = new JPanel();																					// List Panel
		this.listP.setLayout(new BoxLayout(this.listP, BoxLayout.Y_AXIS));
		this.listL = new JLabel("Node-List");
		this.listP.add(this.listL);
		this.listTA = new JTextArea();
		this.listP.add(this.listTA);
		this.midPanel.add(this.listP);
		this.contentPane.add(this.midPanel);
		this.searchP = new JPanel();																				// Search Panel
		this.searchP.setLayout(new BoxLayout(this.searchP, BoxLayout.X_AXIS));
		this.searchStat = new JTextField();
		this.searchStat.setColumns(2);
		this.searchStat.setMaximumSize(this.searchStat.getPreferredSize());
		this.searchStat.setBackground(Color.GRAY);
		this.searchP.add(this.searchStat);
		this.searchTF = new JTextField();
		this.searchTF.setColumns(3);
		this.searchTF.setMaximumSize(this.searchTF.getPreferredSize());
		this.searchP.add(this.searchTF);
		this.startSearch = new JButton("search");
		this.startSearch.addActionListener(this);
		this.searchP.add(this.startSearch);
		this.contentPane.add(this.searchP);
		this.electPanel = new JPanel();																				// Election Panel
		this.electPanel.setLayout(new BoxLayout(this.electPanel, BoxLayout.X_AXIS));
		this.electLabel = new JLabel("Leader Election: ");
		this.electPanel.add(this.electLabel);
		this.electStat = new JTextField("no voting");
		this.electStat.setColumns(10);
		this.electStat.setMaximumSize(this.electStat.getPreferredSize());
		this.electStat.setBackground(Color.GRAY);
		this.electPanel.add(this.electStat);
		this.electButton = new JButton("start Voting");
		this.electButton.addActionListener(this);
		this.electPanel.add(this.electButton);
		this.contentPane.add(this.electPanel);
		this.frame.setContentPane(this.contentPane);
		this.frame.setVisible(true);
		
		/* INIT */
		this.connected = false;										// Connection Status
		this.leader = _leader;										// Leader
		if (this.leader.equals("localhost")) {						// Leader Status
			this.isLeader = true;
			this.leaderStatus.setBackground(Color.GREEN);
		} else {
			this.isLeader = false;
			this.leaderStatus.setBackground(Color.RED);
		}
		this.ip = this.getLocalIP();								// IP String
		this.ipA = new byte[4];										// IP Byte Array
		String[] tempSA = this.ip.split("\\.");
		for (int i = 0; i < 4; i++) {
			this.ipA[i] = (byte) Integer.parseInt(tempSA[i]);
		}
		this.port = _port;											// Port Integer
		this.portA = new byte[2];									// Port Byte Array
		byte[] tempB = twoToByte(this.port);
		this.portA[0] = tempB[0];
		this.portA[1] = tempB[1];
		this.id = 0;												// ID Integer
		this.idA = new byte[2];										// ID Byte Array
		this.list = new byte[maxKnownPeers][9];						// Peer list
		for (int i = 0; i < this.list.length; i++) {
			for (int j = 0; j < this.list.length; j++) {
				this.list[i][j] = 0;
			}
		}
		this.searchID = firstIndexID;								// searchIDs
		this.searches = new LinkedList<byte[]>();					// search List
		
		/*   Update Dashboard   */
		this.infoIP.setText("   IP: " + this.ip);
		this.infoPort.setText("   Port: " + Integer.toString(port));
	}
	
	
	
			/*****************/
			/*   GUI EVENT   */
			/*****************/
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		/* START SEARCH */
		if (ae.getSource() == this.startSearch) {
			this.searchStat.setBackground(Color.RED);
			if (!(this.searchTF.getText().equals(""))) {
				int i = Integer.parseInt(this.searchTF.getText());
				byte[] rec = new byte[14];
				byte[] help = twoToByte(i);
				rec[12] = help[0];
				rec[13] = help[1];
				rec[2] = this.ipA[0];
				rec[3] = this.ipA[1];
				rec[4] = this.ipA[2];
				rec[5] = this.ipA[3];
				rec[6] = this.portA[0];
				rec[7] = this.portA[1];
				rec[8] = this.idA[0];
				rec[9] = this.idA[1];
				String node1 = "";
				String node2 = "";
				String node3 = "";
				String node4 = "";
				rec = this.getMSG(6, rec);
				if (this.list[0][0] != 0) {						
					node1 = "" + (this.list[0][0]&0xFF) + "."
							+ (this.list[0][1]&0xFF) + "." + (this.list[0][2]&0xFF) + "."
							+ (this.list[0][3]&0xFF);
				}
				if (this.list[1][0] != 0) {
					node2 = "" + (this.list[1][0]&0xFF) + "."
							+ (this.list[1][1]&0xFF) + "." + (this.list[1][2]&0xFF) + "."
							+ (this.list[1][3]&0xFF);
				}
				if (this.list[2][0] != 0) {
					node3 = "" + (this.list[2][0]&0xFF) + "."
							+ (this.list[2][1]&0xFF) + "." + (this.list[2][2]&0xFF) + "."
							+ (this.list[2][3]&0xFF);
				}
				if (this.list[3][0] != 0) {
					node4 = "" + (this.list[3][0]&0xFF) + "."
							+ (this.list[3][1]&0xFF) + "." + (this.list[3][2]&0xFF) + "."
							+ (this.list[3][3]&0xFF);
				}
				this.searches.add(new byte[] {rec[8], rec[9], rec[10], rec[11]});
				try {
					if (node1.length() > 3)
						this.sendMSG(node1, this.port, rec);
					if (node2.length() > 3)
						this.sendMSG(node2, this.port, rec);
					if (node3.length() > 3)
						this.sendMSG(node3, this.port, rec);
					if (node4.length() > 3)
						this.sendMSG(node4, this.port, rec);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("!!!ERROR: keine geultige ID eingetragen!");
			}
		}
		
		/* START LEADER ELECTION*/
		if (ae.getSource() == this.electButton) {
			this.electStat.setBackground(Color.GREEN);
			this.electStat.setText("voting...");
		}
	}
	
	
	
			/************/
			/*   MAIN   */
			/************/
	
	public static void main(String[] args) throws Exception {
		
		P2P peer = new P2P(3333, "localhost");
		peer.startPeer();
	}
	
	
	
			/******************/
			/*   START PEER   */
			/******************/
	
	private void startPeer() {
		
		/* SERVER THREAD */
		ServerThread server = new ServerThread(this);
		new Thread(server).start();
		
		/* PEER THREAD*/
		PeerThread peer = new PeerThread(this);
		new Thread(peer).start();
		
		/* SHOW PEER LIST*/
		this.showList();
	}
	
	
	
			/********************/
			/*   SEND MESSAGE   */
			/********************/
	
	void sendMSG(String hostname, int port, byte[] msg) throws UnknownHostException, IOException {
		Socket sock;
		OutputStream out;
		InputStream in;
		sock = new Socket(hostname, port);
		out = new DataOutputStream(sock.getOutputStream());
		in = new DataInputStream(sock.getInputStream());
		// senden
		out.write(msg);
		System.out.println("PEER: ---> " + Arrays.toString(msg));
		// empfangen
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		for(int s; (s=in.read(buffer)) != -1; )
		{
		  baos.write(buffer, 0, s);
		}
		byte[] res = baos.toByteArray();
		if (res.length > 3) {
			byte[] ans = this.handleMSG(res);
			if (ans != null) {
				out.write(ans);
			}
		}
		in.close();
		out.close();
		sock.close();
	}
	
	
	
			/**********************/
			/*   HANDLE MESSAGE   */
			/**********************/
	
	byte[] handleMSG(byte[] rec) {
		
		if (rec[0] == 1 && this.isLeader) {																		// R 1
			if (this.isLeader) {
				System.out.println("SERVER: R 1 <--- " + Arrays.toString(rec));
				return this.getMSG(2, rec); // getMSG 2 added in list
			} else {
				System.out.println("<--- R 1 BUT IM NOT LEADER!!!!!!!!!!!!!!");
			}
		}
		
		else if (rec[0] == 2) {																					// R 2
			System.out.println("CLIENT: R 2 <--- " + Arrays.toString(rec));
			
			byte[] bla = {rec[2], rec[3]};
			this.infoID.setText("   ID: " + Integer.toString(twoToInt(bla)));
			
			this.connected = true;
			this.connectStatus.setBackground(Color.green);
			this.idA[0] = rec[2];
			this.idA[1] = rec[3];
			byte[] newP1 = {rec[4], rec[5], rec[6], rec[7], rec[8], rec[9], rec[10], rec[11], 1};
			byte[] newP2 = {rec[12], rec[13], rec[14], rec[15], rec[16], rec[17], rec[18], rec[19], 1};
			byte[] newP3 = {rec[20], rec[21], rec[22], rec[23], rec[24], rec[25], rec[26], rec[27], 1};
			byte[] newP4 = {rec[28], rec[29], rec[30], rec[31], rec[32], rec[33], rec[34], rec[35], 1};
			this.addList(newP1);
			this.addList(newP2);
			this.addList(newP3);
			this.addList(newP4);
			return null;
		}
		
		else if (rec[0] == 3) {																					// R 3
			System.out.println("PEER: R 3 <--- " + Arrays.toString(rec));
			byte[] newP = {rec[2], rec[3], rec[4], rec[5], rec[6], rec[7], rec[8], rec[9], 1};
			this.addList(newP);
			return this.getMSG(4, rec);
		}
		
		else if (rec[0] == 4) {																					// R 4
			System.out.println("PEER: R 4 <--- " + Arrays.toString(rec));
			byte[] newP1 = {rec[2], rec[3], rec[4], rec[5], rec[6], rec[7], rec[8], rec[9], 1};
			byte[] newP2 = {rec[10], rec[11], rec[12], rec[13], rec[14], rec[15], rec[16], rec[17], 1};
			byte[] newP3 = {rec[18], rec[19], rec[20], rec[21], rec[22], rec[23], rec[24], rec[25], 1};
			byte[] newP4 = {rec[26], rec[27], rec[28], rec[29], rec[30], rec[31], rec[32], rec[33], 1};
			this.addList(newP1);
			this.addList(newP2);
			this.addList(newP3);
			this.addList(newP4);
			return null;
		}
		
		else if (rec[0] == 5) {																					// R 5
			System.out.println("SERVER: R 5 <--- " + Arrays.toString(rec));
			return null;
		}
		
		else if (rec[0] == 6) {																					// R 6
			System.out.println("PEER: R 6 <--- " + Arrays.toString(rec));
			if(rec[12] == this.idA[0] && rec[13] == this.idA[1]) { // im found
				return this.getMSG(7, rec);
			} else {									// carry
				boolean newMSG = true;
				Iterator<byte[]> itr = this.searches.iterator();
				while (itr.hasNext()) {
					byte[] x = itr.next();
					if (rec[8] == x[0] && rec[9] == x[1] && rec[10] == x[2] && rec[11] == x[3]) {
						newMSG = false;
					}
				}
				if (newMSG) {
					String node1 = "";
					String node2 = "";
					String node3 = "";
					String node4 = "";
					if (this.list[0][0] != 0) {						
						node1 = "" + (this.list[0][0]&0xFF) + "."
								+ (this.list[0][1]&0xFF) + "." + (this.list[0][2]&0xFF) + "."
								+ (this.list[0][3]&0xFF);
					}
					if (this.list[1][0] != 0) {
						node2 = "" + (this.list[1][0]&0xFF) + "."
								+ (this.list[1][1]&0xFF) + "." + (this.list[1][2]&0xFF) + "."
								+ (this.list[1][3]&0xFF);
					}
					if (this.list[2][0] != 0) {
						node3 = "" + (this.list[2][0]&0xFF) + "."
								+ (this.list[2][1]&0xFF) + "." + (this.list[2][2]&0xFF) + "."
								+ (this.list[2][3]&0xFF);
					}
					if (this.list[3][0] != 0) {
						node4 = "" + (this.list[3][0]&0xFF) + "."
								+ (this.list[3][1]&0xFF) + "." + (this.list[3][2]&0xFF) + "."
								+ (this.list[3][3]&0xFF);
					}
					this.searches.add(new byte[] {rec[8], rec[9], rec[10], rec[11]});
					try {
						if (node1.length() > 3)
							this.sendMSG(node1, port, rec);
						if (node2.length() > 3)
							this.sendMSG(node2, port, rec);
						if (node3.length() > 3)
							this.sendMSG(node3, port, rec);
						if (node4.length() > 3)
							this.sendMSG(node4, port, rec);
					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("PEER: MSG bereits gecarried " + Arrays.toString(rec));
				}
			}
			return null;
		}
		
		else if (rec[0] == 7) {																						// R 7
			System.out.println("PEER: R 7 <--- " + Arrays.toString(rec));
			// TODO answer search
			return null;
		}
		
		else if (rec[0] == 8) {																						// R 8
			System.out.println("PEER: R 8 <--- " + Arrays.toString(rec));
			// TOTO change to string and print
			return null;
		}
		
		else if (rec[0] == 9) {																						// R 9
			System.out.println("[DEBUG] R 9");
		}
		
		else if (rec[0] == 10) {																					// R 10
			System.out.println("[DEBUG] R 10");
		}
		
		System.out.println("!!! FEHLER: handleMSG() rec=" + Arrays.toString(rec));									// ERROR
		return null;
	}
	
	
	
			/*******************/
			/*   GET MESSAGE   */
			/*******************/
	
	byte[] getMSG(int tag, byte[] rec) {
		
		if (tag == 1) {																								// S 1
			byte[] msg = {1, 0,
				this.ipA[0], this.ipA[1] , this.ipA[2], this.ipA[3],
				this.portA[0], this.portA[1]};
			return msg;
		}
		
		else if (tag == 2) {																						// S 2
			byte[] msg = new byte[36];
			msg[0] = (byte) 2;
			msg[1] = (byte) 1;
			byte[] newID = twoToByte(peerIDs);
			peerIDs++;
			msg[2] = newID[0];
			msg[3] = newID[1];
			this.addList(new byte[] {rec[2], rec[3], rec[4], rec[5], rec[6], rec[7], newID[0], newID[1], 1});
			for (int i = 0; i < 8; i++) {
				msg[i+4] = this.list[0][i];
			}
			for (int i = 0; i < 8; i++) {
				msg[i+12] = this.list[1][i];
			}
			for (int i = 0; i < 8; i++) {
				msg[i+20] = this.list[2][i];
			}
			for (int i = 0; i < 8; i++) {
				msg[i+28] = this.list[3][i];
			}
			return msg;
		}
		
		else if (tag == 3) {																						// S 3
			byte[] msg = {3, 1,
					this.ipA[0], this.ipA[1], this.ipA[2], this.ipA[3],
					this.portA[0], this.portA[1],
					this.idA[0], this.idA[1]};
			return msg;
		}
		
		else if (tag == 4) {																						// S 4
			byte[] msg = new byte[34];
			msg[0] = 4;
			msg[1] = 1;
			for (int i = 0; i < 8; i++) {
				msg[i+2] = this.list[0][i];
			}
			for (int i = 0; i < 8; i++) {
				msg[i+10] = this.list[1][i];
			}
			for (int i = 0; i < 8; i++) {
				msg[i+18] = this.list[2][i];
			}
			for (int i = 0; i < 8; i++) {
				msg[i+26] = this.list[3][i];
			}
			return msg;
		}
		
		else if (tag == 5) {																						// S 5
			byte[] msg = {5, 1,
					this.ipA[0], this.ipA[1] , this.ipA[2], this.ipA[3],
					this.portA[0], this.portA[1],
					this.idA[0], this.idA[1]};
			return msg;
		}
		
		else if (tag == 6) {																						// S 6
			byte[] sID = twoToByte(searchID);
			byte[] msg = {6, 1,
					rec[2], rec[3], rec[4], rec[5], rec[6], rec[7], rec[8], rec[9],
					sID[0], sID[1], rec[12], rec[13]};
			return msg;
		}
		
		else if (tag == 7) {																						// S 7
			byte[] msg = {7, 1,
					this.ipA[0], this.ipA[1], this.ipA[2], this.ipA[3],
					this.portA[0], this.portA[1], this.idA[0], this.idA[1],
					rec[10], rec[11]};
			return msg;
		}
		
		else if (tag == 8) {																						// S 8
			byte[] msg = new byte[12+rec.length];
			msg[0] = 8;
			msg[1] = 1;
			for (int i = 0; i < this.ipA.length; i++)
				msg[i+1] = this.ipA[i];
			msg[6] = this.portA[0];
			msg[7] = this.portA[1];
			msg[8] = this.idA[0];
			msg[9] = this.idA[1];
			byte[] lengthA = twoToByte(rec.length);
			msg[10] = lengthA[0];
			msg[11] = lengthA[1];
			for (int i = 0; i < rec.length; i++) {
				msg[i+11] = rec[i];
			}
			return msg;
		}
		
		else if (tag == 9) {																						// S 9
			System.out.println("[DEBUG] G 9");
		}
		
		else if (tag == 10) {																						// S 10
			System.out.println("[DEBUG] G 10");
		}
		
		System.out.println("!!!!!!!!!!!!!!!!!!!ERROR: getMSG TAG: " + tag);											// ERROR
		return null;
	}
	
	
	
	/***********************/
	/*   LEADER ELECTION   */
	/***********************/
	
	void leaderElection() {
		
	}
	
			/*****************/
			/*   PEER LIST   */
			/*****************/
	
	/* SHOW LIST */
	void showList() {
		this.listTA.setText("");
		for (int i = 0; i < this.list.length; i++) {
			if (this.list[i][0] != 0) {
				byte[] tempIdA = {this.list[i][6], this.list[i][7]};
				int tempId = twoToInt(tempIdA);
				byte[] tempPortA = {this.list[i][4], this.list[i][5]};
				int tempPort = twoToInt(tempPortA);
				this.listTA.append("" + tempId + " ---> " + (this.list[i][0]&0xFF)
						+ "." + (this.list[i][1]&0xFF) + "." + (this.list[i][2]&0xFF) + "." + (this.list[i][3]&0xFF)
								+ " : " + tempPort + "   #" + (this.list[i][8]&0xFF) + "\n");
			} else {
				this.listTA.append("empty slot\n");
			}
		}
	}
	
	/* ADD PEER TO LIST */
	void addList(byte[] newPeer) {
		if (newPeer[0] != 0) {
			for (int i = 0; i < this.list.length; i++) {
				if (this.list[i][0] == newPeer[0] && this.list[i][1] == newPeer[1] &&
						this.list[i][2] == newPeer[2] && this.list[i][3] == newPeer[3] &&
						this.list[i][4] == newPeer[4] && this.list[i][5] == newPeer[5] &&
						this.list[i][6] == newPeer[6] && this.list[i][7] == newPeer[7]) {
					System.out.println("LIST: ALREADY KNOW " + Arrays.toString(newPeer));
					return;
				}
				if (this.list[i][0] == 0) {
					for (int j = 0; j < this.list[i].length; j++) {
						this.list[i][j] = newPeer[j];
					}
					System.out.println("LIST: ADDED " + Arrays.toString(newPeer));
					this.showList();
					return;
				} else {
					if (!(i < this.list.length)) {
					System.out.println("LIST : NO EMPTY slot for " + Arrays.toString(newPeer));
					}
				}
			}
		} else {
			System.out.println("LIST: NOT ADDED " + Arrays.toString(newPeer));
		}
		this.showList();
	}
	
	
	
	/***************/
	/*   HELPERS   */
	/***************/
	
	String getLocalIP() {
		String lIP = "";
		
		//InetAddress ip = InetAddress.getLocalHost();					// Version 1 works with OpenSuse
		//String lIP = ip.getHostAddress();
		
		try {															// Version 2 works with Win10, Linux Mint
			Socket s = new Socket("www.google.de", 80);
			lIP = s.getLocalAddress().getHostAddress();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lIP;
	}
	
	static int twoToInt(byte[] i) {
		int res = ((i[0]&0xFF) << 8) | (i[1]&0xFF);
		return res;
	}
	
	static byte[] twoToByte(int value) {
		byte[] res = {(byte)(value >>> 8), (byte)(value)};
		return res;
	}
}