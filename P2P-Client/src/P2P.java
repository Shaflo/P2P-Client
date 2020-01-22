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
	static String defaultLeader = "10.10.10.1";
	static boolean startAsLeader = true;
	static int defaultPort = 3333;
	static int peerAnz = 3;
	static int maxKnownPeers = 4;
	static int firstIndexID = 1;
	static int lastIndexID = 7;

	/* Peer */
	static int idCounter = firstIndexID;
	boolean connected;
	String leader;
	boolean isLeader;
	boolean doLeaderElection;
	String ip;
	byte[] ipA;
	int port;
	byte[] portA;
	int id;
	byte[] idA;
	byte[][] list;
	boolean doSearch;
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
	JPanel chatP;					// Chat Panel
	JPanel chatInP;
	JTextArea chatInTA;
	JPanel chatOut;
	JButton chatInClear;
	JLabel chatIdL;
	JTextField chatID;
	JLabel chatMsgL;
	JTextField chatMSG;
	JButton chatSend;

	private P2P(int _port, String _leader) {

		/* GUI */
		this.frame = new JFrame("P2P-Client");
		this.frame.setSize(500, 400);
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
		this.chatP = new JPanel();																					// Chat Panel
		this.chatP.setLayout(new BoxLayout(this.chatP, BoxLayout.Y_AXIS));
		//this.chatP.setBackground(Color.BLUE);
		this.chatInP = new JPanel();
		this.chatInP.setLayout(new BoxLayout(this.chatInP, BoxLayout.X_AXIS));
		//this.chatInP.setBackground(Color.BLUE);
		this.chatInTA = new JTextArea();
		//this.chatInTA.setSize(this.frame.getWidth(), 8);
		//this.chatInTA.setMaximumSize(this.chatInTA.getPreferredSize());
		this.chatInP.add(this.chatInTA);
		this.chatOut = new JPanel();
		this.chatOut.setLayout(new BoxLayout(this.chatOut, BoxLayout.X_AXIS));
		//this.chatOut.setBackground(Color.BLUE);
		this.chatInClear = new JButton("clear InBox");
		this.chatInClear.addActionListener(this);
		this.chatOut.add(this.chatInClear);
		this.chatIdL = new JLabel("ID:");
		this.chatOut.add(this.chatIdL);
		this.chatID = new JTextField();
		this.chatID.setColumns(5);
		this.chatID.setMaximumSize(this.chatID.getPreferredSize());
		this.chatOut.add(this.chatID);
		this.chatMsgL = new JLabel("MSG:");
		this.chatOut.add(this.chatMsgL);
		this.chatMSG = new JTextField();
		this.chatMSG.setColumns(20);
		this.chatMSG.setMaximumSize(this.chatMSG.getPreferredSize());
		this.chatOut.add(this.chatMSG);
		this.chatSend = new JButton("send");
		this.chatSend.addActionListener(this);
		this.chatOut.add(this.chatSend);
		this.chatP.add(this.chatInP);
		this.chatP.add(this.chatOut);
		this.contentPane.add(this.chatP);
		this.frame.setContentPane(this.contentPane);
		this.frame.setVisible(true);

		/* INIT */
		this.connected = false;										// Connection Status
		this.leader = _leader;										// Leader
		if (this.leader.equals("localhost")) {						// Leader Status
			this.isLeader = true;
			this.leaderStatus.setText("          leader");
			this.leaderStatus.setBackground(Color.GREEN);
		} else {
			this.isLeader = false;
			this.leaderStatus.setText("          leader:" + this.leader + ":" + defaultPort);
			this.leaderStatus.setBackground(Color.RED);
		}
		this.doLeaderElection = false;								// Leader Election Status
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
		this.doSearch = false;										// Search Status
		this.searchID = 1000;										// searchIDs
		this.searches = new LinkedList<byte[]>();					// search List

		/*   Update Dashboard   */
		this.infoIP.setText("   IP: " + this.ip);
		this.infoPort.setText("   Port: " + Integer.toString(this.port));
	}



			/*****************/
			/*   GUI EVENT   */
			/*****************/

	@Override
	public void actionPerformed(ActionEvent ae) {

		if (ae.getSource() == this.startSearch) {									// START SEARCH
			this.searchStat.setBackground(Color.RED);
			if (!(this.searchTF.getText().equals(""))) {
				this.search(Integer.parseInt(this.searchTF.getText()));
			} else {
				System.out.println("!!!ERROR: keine geultige ID eingetragen!");
			}
		} else if (ae.getSource() == this.electButton) {							// START LEADER ELECTION
			this.startLeaderElection();
		} else if (ae.getSource() == this.chatInClear) {							// Clear InBox Chat
			this.chatInTA.setText("");
		} else if (ae.getSource() == this.chatSend) {								// SEND MSG
			try {
				System.out.println(Integer.parseInt(this.chatID.getText()));
				this.send(Integer.parseInt(this.chatID.getText()), this.getMSG(8, new byte[] {}));
			} catch (NumberFormatException | IOException e) {
				e.printStackTrace();
			}
		}
	}



			/************/
			/*   MAIN   */
			/************/

	public static void main(String[] args) throws Exception {

		P2P[] peer = new P2P[peerAnz];

		for (int i = 0; i < peerAnz; i++) {

			if (i < 1 && startAsLeader) {
				peer[i] = new P2P(defaultPort, "localhost");
			} else if (i < 1) {
				peer[i] = new P2P(defaultPort, defaultLeader);
			} else {
				peer[i] = new P2P(defaultPort+i, defaultLeader);
			}

			peer[i].startPeer();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}



			/******************/
			/*   START PEER   */
			/******************/

	private void startPeer() {

		/* SERVER THREAD */
		ServerThread server = new ServerThread(this);
		new Thread(server).start();

		/* PEER THREAD */
		PeerThread peer = new PeerThread(this);
		new Thread(peer).start();

		/* SHOW PEER LIST */
		this.showList();
	}



			/********************/
			/*   SEND MESSAGE   */
			/********************/

	void sendMSG(String hostname, int port, byte[] msg) throws UnknownHostException, IOException {			// old require hostname + port
		Socket sock;
		OutputStream out;
		InputStream in;
		sock = new Socket(hostname, port);
		out = new DataOutputStream(sock.getOutputStream());
		in = new DataInputStream(sock.getInputStream());
		// senden
		out.write(msg);
		System.out.println("PEER"+ this.id + ": ---> " + Arrays.toString(msg) + " TO -> " + hostname + ":" + port);
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

	boolean send(int _id, byte[] msg) throws UnknownHostException, IOException {								// new just need id
		Socket sock;
		OutputStream out;
		InputStream in;
		byte[] IDhelper = twoToByte(_id);
		int tries = 0;
		while (tries < 3) {
			for (int i = 0; i < this.list.length; i++) {
				if (this.list[i][6] == IDhelper[0] && this.list[i][7] == IDhelper[1]) {
					// gefunden (senden)
					String hostname = "" + (this.list[i][0]&0xFF) + "."
							+ (this.list[i][1]&0xFF) + "." + (this.list[i][2]&0xFF) + "."
							+ (this.list[i][3]&0xFF);
					int tempP = twoToInt(new byte[] {this.list[i][4], this.list[i][5]});

					sock = new Socket(hostname, tempP);
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
					System.out.println("Nachricht gesendet an " + _id);
					return true;
				}

			}
			this.search(_id);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
			tries++;
		}
		System.out.println("Nachricht konnte nicht gesendet werden (ID not found)" + _id);
		return false;
	}



			/**********************/
			/*   HANDLE MESSAGE   */
			/**********************/

	byte[] handleMSG(byte[] rec) {

		if (rec[0] == 1 && this.isLeader) {																	// R 1
			if (this.isLeader) {
				System.out.println("SERVER: R 1 <--- " + Arrays.toString(rec));
				byte[] newId = twoToByte(idCounter);
				this.addList(new byte[] {rec[2], rec[3], rec[4], rec[5], rec[6], rec[7], newId[0], newId[1], 1});
				byte[] newM = this.getMSG(2, rec);
				idCounter++;
				return newM;
			} else {
				System.out.println("<--- R 1 BUT IM NOT LEADER!!!!!!!!!!!!!!--------------------------------------------------------------------!!!!!!!");
			}
		}

		else if (rec[0] == 2) {																				// R 2
			System.out.println("CLIENT: R 2 <--- " + Arrays.toString(rec));

			byte[] bla = {rec[2], rec[3]};
			this.id = twoToInt(bla);
			this.infoID.setText("   ID: " + this.id);

			this.connected = true;
			this.connectStatus.setBackground(Color.green);
			this.idA[0] = rec[2];
			this.idA[1] = rec[3];
			byte[] newP1 = {rec[4], rec[5], rec[6], rec[7], rec[8], rec[9], rec[10], rec[11], 1};
			byte[] newP2 = {rec[12], rec[13], rec[14], rec[15], rec[16], rec[17], rec[18], rec[19], 1};
			byte[] newP3 = {rec[20], rec[21], rec[22], rec[23], rec[24], rec[25], rec[26], rec[27], 1};
			byte[] newP4 = {rec[28], rec[29], rec[30], rec[31], rec[32], rec[33], rec[34], rec[35], 1};
			if (rec[4] != 0)
				this.addList(newP1);
			if (rec[12] != 0)
				this.addList(newP2);
			if (rec[20] != 0)
				this.addList(newP3);
			if (rec[28] != 0)
				this.addList(newP4);
			return null;
		}

		else if (rec[0] == 3) {																				// R 3
			System.out.println("PEER: R 3 <--- " + Arrays.toString(rec));
			byte[] newP = {rec[2], rec[3], rec[4], rec[5], rec[6], rec[7], rec[8], rec[9], 1};
			this.addList(newP);
			return this.getMSG(4, rec);
		}

		else if (rec[0] == 4) {																				// R 4
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

		else if (rec[0] == 5) {																				// R 5
			System.out.println("SERVER: R 5 <--- " + Arrays.toString(rec));
			if (this.doLeaderElection && twoToInt(new byte[] {rec[8], rec[9]}) > twoToInt(new byte[] {this.idA[0], this.idA[1]})) {
				this.doLeaderElection = false;
				this.leaderStatus.setBackground(Color.GRAY);
			} else if (!this.isLeader) {
				System.out.println("------------------------------------------------------------------------------------------------------------------------!!!!!!!!!!!!!!");
			}
			return null;
		}

		else if (rec[0] == 6) {																				// R 6
			System.out.println("PEER" + this.id + ": R 6 <--- " + Arrays.toString(rec));

			if(rec[12] == this.idA[0] && rec[13] == this.idA[1]) {

				return this.getMSG(7, rec);				// im found

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
							this.sendMSG(node1, twoToInt(new byte[] {this.list[0][4], this.list[0][5]}), rec);
						if (node2.length() > 3)
							this.sendMSG(node2, twoToInt(new byte[] {this.list[1][4], this.list[1][5]}), rec);
						if (node3.length() > 3)
							this.sendMSG(node3, twoToInt(new byte[] {this.list[2][4], this.list[2][5]}), rec);
						if (node4.length() > 3)
							this.sendMSG(node4, twoToInt(new byte[] {this.list[3][4], this.list[3][5]}), rec);
					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

				} else {

					System.out.println("PEER " + this.id + ": MSG bereits gecarried " + Arrays.toString(rec));
				}
			}
			return null;
		}

		else if (rec[0] == 7) {																				// R 7
			System.out.println("PEER" + this.id + ": R 7 <--- " + Arrays.toString(rec));
			this.addList(new byte[] {rec[2], rec[3], rec[4], rec[5], rec[6], rec[7], rec[8], rec[9], 1});
			return null;
		}

		else if (rec[0] == 8) {																				// R 8
			System.out.println("PEER: R 8 <--- " + Arrays.toString(rec));
			byte[] txt = new byte[twoToInt(new byte[] {rec[10], rec[11]})];
			for (int i = 0; i < txt.length; i++) {
				txt[i] = rec[i+12];
			}
			this.chatInTA.append(twoToInt(new byte[] {rec[8], rec[9]}) + ": " + byteToString(txt) + "\n");
			return null;
		}

		else if (rec[0] == 9) {																				// R 9
			System.out.println("[HANDLE] R 9");
			this.startLeaderElection();
			return this.getMSG(5, rec);
		}

		else if (rec[0] == 10) {																			// R 10
			System.out.println("[HANDLE] R 10");
			//this.connected = false;
			this.leader = "" + (rec[2]&0xFF) + "." + (rec[3]&0xFF) + "." + (rec[4]&0xFF) + "." + (rec[5]&0xFF);
			this.leaderStatus.setBackground(Color.RED);
			defaultPort = twoToInt(new byte[] {rec[6], rec[7]});
			this.leaderStatus.setText("          leader:" + this.leader + ":" + defaultPort);
		}

		System.out.println("!!! FEHLER: handleMSG() rec=" + Arrays.toString(rec));							// ERROR
		return null;
	}



			/*******************/
			/*   GET MESSAGE   */
			/*******************/

	byte[] getMSG(int tag, byte[] rec) {

		if (tag == 1) {																						// S 1
			byte[] msg = {1, 0,
				this.ipA[0], this.ipA[1] , this.ipA[2], this.ipA[3],
				this.portA[0], this.portA[1]};
			return msg;
		}

		else if (tag == 2) {																				// S 2
			byte[] msg = new byte[36];
			msg[0] = (byte) 2;
			msg[1] = (byte) 1;

			for (int i = 0; i < this.list.length; i++) {

				if (this.list[i][0] == rec[2] && this.list[i][1] == rec[3] &&
						this.list[i][2] == rec[4] && this.list[i][3] == rec[5] &&
						this.list[i][4] == rec[6] && this.list[i][5] == rec[7]) {							// schon in Liste vorhanden
					msg[2] = this.list[i][6];
					msg[3] = this.list[i][7];
					for (int j = 0; j < 8; j++) {
						msg[j+4] = this.list[0][j];
					}
					for (int j = 0; j < 8; j++) {
						msg[j+12] = this.list[1][j];
					}
					for (int j = 0; j < 8; j++) {
						msg[j+20] = this.list[2][j];
					}
					for (int j = 0; j < 8; j++) {
						msg[j+28] = this.list[3][j];
					}
					return msg;
				}
			}
			byte[] newID = twoToByte(idCounter);
			msg[2] = newID[0];
			msg[3] = newID[1];
			for (int j = 0; j < 8; j++) {
				msg[j+4] = this.list[0][j];
			}
			for (int j = 0; j < 8; j++) {
				msg[j+12] = this.list[1][j];
			}
			for (int j = 0; j < 8; j++) {
				msg[j+20] = this.list[2][j];
			}
			for (int j = 0; j < 8; j++) {
				msg[j+28] = this.list[3][j];
			}

			return msg;
		}

		else if (tag == 3) {																				// S 3
			byte[] msg = {3, 1,
					this.ipA[0], this.ipA[1], this.ipA[2], this.ipA[3],
					this.portA[0], this.portA[1],
					this.idA[0], this.idA[1]};
			return msg;
		}

		else if (tag == 4) {																				// S 4
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

		else if (tag == 5) {																				// S 5
			byte[] msg = {5, 1,
					this.ipA[0], this.ipA[1] , this.ipA[2], this.ipA[3],
					this.portA[0], this.portA[1],
					this.idA[0], this.idA[1]};
			return msg;
		}

		else if (tag == 6) {																				// S 6
			byte[] msg = {6, 1,
					rec[2], rec[3], rec[4], rec[5], rec[6], rec[7], rec[8], rec[9],
					rec[10], rec[11], rec[12], rec[13]};
			return msg;
		}

		else if (tag == 7) {																				// S 7
			byte[] msg = {7, 1,
					this.ipA[0], this.ipA[1], this.ipA[2], this.ipA[3],
					this.portA[0], this.portA[1], this.idA[0], this.idA[1],
					rec[10], rec[11]};
			this.addList(new byte[] {rec[2], rec[3], rec[4], rec[5], rec[6], rec[7], rec[8], rec[9], 1});
			return msg;
		}

		else if (tag == 8) {																				// S 8
			String txt = this.chatMSG.getText();
			byte[] msgcache = stringToByte(txt);
			byte[] msg = new byte[12+msgcache.length];
			msg[0] = 8;
			msg[1] = 1;
			for (int i = 0; i < this.ipA.length; i++)
				msg[i+2] = this.ipA[i];
			msg[6] = this.portA[0];
			msg[7] = this.portA[1];
			msg[8] = this.idA[0];
			msg[9] = this.idA[1];
			byte[] lengthA = twoToByte(msgcache.length);
			msg[10] = lengthA[0];
			msg[11] = lengthA[1];
			for (int i = 0; i < msgcache.length; i++) {
				msg[i+12] = msgcache[i];
			}
			return msg;
		}

		else if (tag == 9) {																				// S 9
			System.out.println("[GET] G 9");
			byte[] msg = new byte[10];
			msg[0] = (byte) 9;
			msg[1] = (byte) 1;
			msg[2] = this.ipA[0];
			msg[3] = this.ipA[1];
			msg[4] = this.ipA[2];
			msg[5] = this.ipA[3];
			msg[6] = this.portA[0];
			msg[7] = this.portA[1];
			msg[8] = this.idA[0];
			msg[9] = this.idA[1];
			return msg;
		}

		else if (tag == 10) {																				// S 10
			System.out.println("[GET] G 10");
			byte[] msg = new byte[10];
			msg[0] = (byte) 10;
			msg[1] = (byte) 1;
			msg[2] = this.ipA[0];
			msg[3] = this.ipA[1];
			msg[4] = this.ipA[2];
			msg[5] = this.ipA[3];
			msg[6] = this.portA[0];
			msg[7] = this.portA[1];
			msg[8] = this.idA[0];
			msg[9] = this.idA[1];
			return msg;
		}

		System.out.println("!!!!!!!!!!!!!!!!!!!ERROR: getMSG TAG: " + tag);									// ERROR
		return null;
	}



			/********************/
			/*   START SEARCH   */
			/********************/

	void search(int whoIs) {

		SearchThread searcher = new SearchThread(this, whoIs);
		new Thread(searcher).start();
	}



			/*****************************/
			/*   START LEADER ELECTION   */
			/*****************************/

	void startLeaderElection() {
		ElectionThread leaderElect = new ElectionThread(this);
		new Thread(leaderElect).start();
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
		if (newPeer[0] == this.ipA[0] && newPeer[1] == this.ipA[1] && newPeer[2] == this.ipA[2] &&
				newPeer[3] == this.ipA[3] && newPeer[4] == this.portA[0] &&
				newPeer[5] == this.portA[1] && newPeer[6] == this.idA[0] &&
				newPeer[7] == this.idA[1]) {																	// mich selbst nicht adden
			System.out.println("IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII bin selbst schon drin");
			return;
		}
		if (newPeer[0] != 0) {
			for (int i = 0; i < this.list.length; i++) {

				if (this.list[i][0] == newPeer[0] && this.list[i][1] == newPeer[1] &&
						this.list[i][2] == newPeer[2] && this.list[i][3] == newPeer[3] &&
						this.list[i][4] == newPeer[4] && this.list[i][5] == newPeer[5]) {						// schon in Liste vorhanden
					System.out.println("LIST: ALREADY KNOW " + Arrays.toString(newPeer));
					this.showList();
					return;

				} else if (this.list[i][0] == 0) {																// freier slot gefunden und hinzuf�gen

					for (int j = 0; j < this.list[i].length; j++) {
						this.list[i][j] = newPeer[j];
					}
					this.showList();
					return;

				}
			}

			int rand = (int) (Math.random() * this.list.length);												// ersetzt einen random Plantz in der Liste
			for (int k = 0; k < this.list[0].length; k++) {
				this.list[rand][k] = newPeer[k];
			}
		} else {
			System.out.println("LIST: NOT ADDED (wrong syntax)!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + Arrays.toString(newPeer));
		}

		this.showList();
	}

	/* GET PEER FROM LIST */
	byte[] getList(byte[] _id) {
		byte[] res = new byte[6];
		for (int i = 0; i < res.length; i++) {
			res[i] = 0;
		}
		for (int i = 0; i < this.list.length; i++) {
			if (this.list[i][6] == _id[0] && this.list[i][7] == _id[1]) {
				for (int j = 0; j < res.length; j++) {
					res[j] = this.list[i][j];
				}
			}
		}
		return res;
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

	static String byteToString(byte[] arr) {
		String res = new String(arr);
		return res;
	}

	static byte[] stringToByte(String str) {
		byte[] res = str.getBytes();
		return res;
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
