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
	static String leader = "localhost";					// localhost = this peer is leader
	static int port = 3333;
	static int maxKnownPeers = 4;
	
	boolean isLeader;
	boolean connected;
	String localIPS;
	byte[] localIPA;
	byte[] localPortA;
	byte[] localIDA;
	byte[][] peerList;
	static int peerIDs = 1000;
	static int searchID = 1000;
	LinkedList<byte[]> searches;
	
	
	
			/***********/
			/*   GUI   */
			/***********/
	
	JPanel infoPane = new JPanel();
	JPanel sysP = new JPanel();						// SysLog
	JLabel sysL = new JLabel("SysLog");
	JTextArea sysTA = new JTextArea();
	
	
	JPanel chatP = new JPanel();					// MsgBox
	JLabel chatL = new JLabel("incoming message");
	JTextArea chatTA = new JTextArea();
	JPanel msgP = new JPanel();						// Chat
	JLabel idL = new JLabel("ID:");
	JTextField idTF = new JTextField();
	JLabel msgL = new JLabel("Message");
	JButton sendB = new JButton("send");
	JTextField msgTF = new JTextField();
	
	// JFrame+contentpane bleibt
	JFrame frame;
	JPanel contentPane;
	JPanel headerPane; 				// HEADER
	JLabel headerTF;
	JPanel statusPanel;				// STAUS
	JTextField connectStatus;
	JTextField leaderStatus;
	JPanel midPanel;				// MID Panel
	JPanel infoPanel;		// infoPane
	JLabel infoHeader;
	JLabel infoID;
	JLabel infoIP;
	JLabel infoPort;
	JPanel listP;					// Peer-List
	JLabel listL;
	JTextArea listTA;
	
	private P2P() {
		/* GUI */
		// new GUI
		this.frame = new JFrame();
		this.frame.setSize(800, 500);
		this.contentPane = new JPanel();
		this.contentPane.setLayout(new BoxLayout(this.contentPane, BoxLayout.Y_AXIS));
		this.headerTF = new JLabel("P2P-Client");
		this.headerPane = new JPanel();
		this.headerPane.add(this.headerTF);
		this.contentPane.add(this.headerPane);
		this.connectStatus = new JTextField("         connected");								// Status Panel
		this.connectStatus.setBackground(Color.RED);
		//this.connectStatus.setColumns(10);
		//this.connectStatus.setMaximumSize(this.connectStatus.getPreferredSize()); // setzt die groesse
		this.leaderStatus = new JTextField("          leader");
		this.statusPanel = new JPanel();
		this.statusPanel.setLayout(new BoxLayout(this.statusPanel, BoxLayout.X_AXIS));
		this.statusPanel.add(this.connectStatus);
		this.statusPanel.add(this.leaderStatus);
		this.contentPane.add(this.statusPanel);
		
		this.infoPane = new JPanel();
		this.infoPane.setLayout(new BoxLayout(this.infoPane, BoxLayout.Y_AXIS));
		this.infoHeader = new JLabel("              PEER                                                                                                                  ");
		this.infoPane.add(this.infoHeader);

		this.infoID = new JLabel("   ID: ");
		this.infoPane.add(this.infoID);
		this.infoIP = new JLabel("   IP: ");
		this.infoPane.add(this.infoIP);
		this.infoPort = new JLabel("   Port: ");
		this.infoPane.add(this.infoPort);
		this.midPanel = new JPanel();
		this.midPanel.setLayout(new BoxLayout(this.midPanel, BoxLayout.X_AXIS));
		this.midPanel.add(this.infoPane);
		this.contentPane.add(this.midPanel);
		
		this.listP = new JPanel();
		this.listP.setLayout(new BoxLayout(this.listP, BoxLayout.Y_AXIS));
		this.listL = new JLabel("Node-List");
		this.listP.add(this.listL);
		this.listTA = new JTextArea();
		this.listP.add(this.listTA);
		this.midPanel.add(this.listP);
		
		
		//old GUI
		
		this.chatP.add(this.chatL);												// Message
		this.chatP.add(this.chatTA);
		this.msgP.add(this.idL);												// Chat
		this.idTF.setColumns(3);
		this.msgP.add(this.idTF);
		this.msgP.add(this.msgL);
		this.msgTF.setColumns(30);
		this.msgP.add(this.msgTF);
		this.sendB.addActionListener(this);
		this.msgP.add(this.sendB);
		this.contentPane.add(this.msgP);
		
		
		this.frame.setContentPane(this.contentPane);
		this.frame.setVisible(true);
		
		/* INIT */
		if (leader.equals("localhost")) {
			this.isLeader = true;
			this.leaderStatus.setBackground(Color.GREEN);
		} else {
			this.isLeader = false;
			this.leaderStatus.setBackground(Color.RED);
		}
		this.connected = false;
		this.localIPS = this.getLocalIPS();
		this.infoIP.setText("   IP: " + this.localIPS);
		this.infoPort.setText("   Port: " + Integer.toString(port));
		this.localIPA = new byte[4];
		this.setLocalIPA();
		this.localPortA = new byte[2];
		this.setLocalPortA();
		this.localIDA = new byte[2];
		this.peerList = new byte[maxKnownPeers][9];
		for (int i = 0; i < peerList.length; i++) {
			for (int j = 0; j < peerList.length; j++) {
				this.peerList[i][j] = 0;
			}
		}
		this.searches = new LinkedList<byte[]>();
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == this.sendB) {
			this.msgTF.setBackground(Color.RED);
			if (!(this.idTF.getText().equals(""))) {
				int i = Integer.parseInt(this.idTF.getText());
				byte[] rec = new byte[14];
				byte[] help = twoToByte(i);
				rec[12] = help[0];
				rec[13] = help[1];
				rec[2] = this.localIPA[0];
				rec[3] = this.localIPA[1];
				rec[4] = this.localIPA[2];
				rec[5] = this.localIPA[3];
				rec[6] = this.localPortA[0];
				rec[7] = this.localPortA[1];
				rec[8] = this.localIDA[0];
				rec[9] = this.localIDA[1];
				String node1 = "";
				String node2 = "";
				String node3 = "";
				String node4 = "";
				rec = this.getMSG(6, rec);
				if (this.peerList[0][0] != 0) {						
					node1 = "" + (this.peerList[0][0]&0xFF) + "."
							+ (this.peerList[0][1]&0xFF) + "." + (this.peerList[0][2]&0xFF) + "."
							+ (this.peerList[0][3]&0xFF);
				}
				if (this.peerList[1][0] != 0) {
					node2 = "" + (this.peerList[1][0]&0xFF) + "."
							+ (this.peerList[1][1]&0xFF) + "." + (this.peerList[1][2]&0xFF) + "."
							+ (this.peerList[1][3]&0xFF);
				}
				if (this.peerList[2][0] != 0) {
					node3 = "" + (this.peerList[2][0]&0xFF) + "."
							+ (this.peerList[2][1]&0xFF) + "." + (this.peerList[2][2]&0xFF) + "."
							+ (this.peerList[2][3]&0xFF);
				}
				if (this.peerList[3][0] != 0) {
					node4 = "" + (this.peerList[3][0]&0xFF) + "."
							+ (this.peerList[3][1]&0xFF) + "." + (this.peerList[3][2]&0xFF) + "."
							+ (this.peerList[3][3]&0xFF);
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.out.println("!!!ERROR: keine geultige ID eingetragen!");
			}
		}
	}
	
	
	
			/*****************/
			/*   Dashboard   */
			/*****************/
	
	public void newLog(String log, String msg) {
		if (log.equals("list")) {
			this.listTA.append(msg);
			this.listTA.setCaretPosition(this.listTA.getDocument().getLength());
		}
	}
	
	
	
			/************/
			/*   MAIN   */
			/************/
	
	public static void main(String[] args) throws Exception {
		
		P2P peer = new P2P();
		peer.startPeer();
	}
	
	
	
			/******************/
			/*   START PEER   */
			/******************/
	
	private void startPeer() {
		/* PRINT LocalIP */
		this.newLog("sys", "Peer IP: " + this.localIPS + "\n");
		
		/* STARTING SERVER THREAD */
		ServerThread server = new ServerThread(port, this);
		new Thread(server).start();
		
		/* CONNECTING TO LEADER */
		while (!this.connected) {
			if (!this.connected) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException ie) {
					System.out.println("!P2PClient1 InterruptedException!");
					ie.printStackTrace();
				}
			}
			this.newLog("sys", "try to connect to leader...\n");
			try {
				this.sendMSG(leader, port, this.getMSG(1, null));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/* SHOW LIST */
		this.showList();

		/*
		 * 			INIT PEERTHREAD
		 */
		
		PeerThread peer = new PeerThread(this);	// Thread: Peer
		new Thread(peer).start();				// Thread: Peer start
		
		/*
		 * 			SENDING CHAT-MESSAGES
		 */
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException ie) {
			System.out.println("!P2PClient1 InterruptedException!");
			ie.printStackTrace();
		}
		
		
		
		/*
		while (true) {
			try {
				InputStreamReader isr = new InputStreamReader(System.in);
			    BufferedReader br = new BufferedReader(isr);
			    //System.out.println("Empfaenger IP eingeben:");
			    String destinationIP = br.readLine();
			    System.out.println("Nachricht fuer " + destinationIP + " eingeben:");
			    String message = br.readLine();
			    System.out.println("will send " + message + " to " + destinationIP);
			} catch (IOException e) {
				this.newLog("sys", "fail");
				e.printStackTrace();
				return;
			}
		}*/
	}
	
	
	
			/***************/
			/*   MESSAGE   */
			/***************/
	
	/*
	 * 		SENDING AND RECEIVING MSG
	 */
	
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
	
	/*
	 * 		HANDLE MSG
	 */
	
	byte[] handleMSG(byte[] rec) {
		
		/*   TAG 1   */
		if (rec[0] == 1 && this.isLeader) {
			System.out.println("SERVER: R 1 <--- " + Arrays.toString(rec));
			return this.getMSG(2, rec); // getMSG 2 added in list
		}
		
		/*   TAG 2   */
		else if (rec[0] == 2) {
			System.out.println("CLIENT: R 2 <--- " + Arrays.toString(rec));
			
			byte[] bla = {rec[2], rec[3]};
			this.infoID.setText("   ID: " + Integer.toString(twoToInt(bla)));
			
			this.connected = true;
			this.connectStatus.setBackground(Color.green);
			this.newLog("sys", "connected!");
			this.localIDA[0] = rec[2];
			this.localIDA[1] = rec[3];
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
		
		/*   TAG 3   */
		else if (rec[0] == 3) {
			System.out.println("PEER: R 3 <--- " + Arrays.toString(rec));
			byte[] newP = {rec[2], rec[3], rec[4], rec[5], rec[6], rec[7], rec[8], rec[9], 1};
			this.addList(newP);
			return this.getMSG(4, rec);
		}
		
		/*   TAG 4   */
		else if (rec[0] == 4) {
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
		
		/*   TAG 5   */
		else if (rec[0] == 5) {
			System.out.println("SERVER: R 5 <--- " + Arrays.toString(rec));
			// TODO aktualisiere Listpoints
			return null;
		}
		
		/*   TAG 6   */
		else if (rec[0] == 6) {
			System.out.println("PEER: R 6 <--- " + Arrays.toString(rec));
			if(rec[12] == this.localIDA[0] && rec[13] == this.localIDA[1]) { // im found
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
					if (this.peerList[0][0] != 0) {						
						node1 = "" + (this.peerList[0][0]&0xFF) + "."
								+ (this.peerList[0][1]&0xFF) + "." + (this.peerList[0][2]&0xFF) + "."
								+ (this.peerList[0][3]&0xFF);
					}
					if (this.peerList[1][0] != 0) {
						node2 = "" + (this.peerList[1][0]&0xFF) + "."
								+ (this.peerList[1][1]&0xFF) + "." + (this.peerList[1][2]&0xFF) + "."
								+ (this.peerList[1][3]&0xFF);
					}
					if (this.peerList[2][0] != 0) {
						node3 = "" + (this.peerList[2][0]&0xFF) + "."
								+ (this.peerList[2][1]&0xFF) + "." + (this.peerList[2][2]&0xFF) + "."
								+ (this.peerList[2][3]&0xFF);
					}
					if (this.peerList[3][0] != 0) {
						node4 = "" + (this.peerList[3][0]&0xFF) + "."
								+ (this.peerList[3][1]&0xFF) + "." + (this.peerList[3][2]&0xFF) + "."
								+ (this.peerList[3][3]&0xFF);
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					System.out.println("PEER: MSG bereits gecarried " + Arrays.toString(rec));
				}
			}
			return null;
		}
		
		/*   TAG 7   */
		else if (rec[0] == 7) {
			System.out.println("PEER: R 7 <--- " + Arrays.toString(rec));
			// TODO answer search
			return null;
		}
		
		/*   TAG 8   */
		else if (rec[0] == 8) {
			System.out.println("PEER: R 8 <--- " + Arrays.toString(rec));
			// TOTO change to string and print
			return null;
		} else {
			System.out.println("!!! FEHLER: handleMSG() rec=" + Arrays.toString(rec));
		}
		return null;
	}
	
	/*
	 * 		GET MSG
	 */
	
	byte[] getMSG(int tag, byte[] rec) {
		
		/*   TAG 1   */
		if (tag == 1) {
			byte[] msg = {1, 0,
				this.localIPA[0], this.localIPA[1] , this.localIPA[2], this.localIPA[3],
				this.localPortA[0], this.localPortA[1]};
			return msg;
		}
		
		/*   TAG 2   */
		else if (tag == 2) {
			byte[] msg = new byte[36];
			msg[0] = (byte) 2;
			msg[1] = (byte) 1;
			byte[] newID = twoToByte(peerIDs);
			peerIDs++;
			msg[2] = newID[0];
			msg[3] = newID[1];
			this.addList(new byte[] {rec[2], rec[3], rec[4], rec[5], rec[6], rec[7], newID[0], newID[1], 1});
			for (int i = 0; i < 8; i++) {
				msg[i+4] = this.peerList[0][i];
			}
			for (int i = 0; i < 8; i++) {
				msg[i+12] = this.peerList[1][i];
			}
			for (int i = 0; i < 8; i++) {
				msg[i+20] = this.peerList[2][i];
			}
			for (int i = 0; i < 8; i++) {
				msg[i+28] = this.peerList[3][i];
			}
			return msg;
		}
		
		/*   TAG 3   */
		else if (tag == 3) {
			byte[] msg = {3, 1,
					this.localIPA[0], this.localIPA[1], this.localIPA[2], this.localIPA[3],
					this.localPortA[0], this.localPortA[1],
					this.localIDA[0], this.localIDA[1]};
			return msg;
		}
		
		/*   TAG 4   */
		else if (tag == 4) {
			byte[] msg = new byte[34];
			msg[0] = 4;
			msg[1] = 1;
			for (int i = 0; i < 8; i++) {
				msg[i+2] = this.peerList[0][i];
			}
			for (int i = 0; i < 8; i++) {
				msg[i+10] = this.peerList[1][i];
			}
			for (int i = 0; i < 8; i++) {
				msg[i+18] = this.peerList[2][i];
			}
			for (int i = 0; i < 8; i++) {
				msg[i+26] = this.peerList[3][i];
			}
			return msg;
		}
		
		/*   TAG 5   */
		else if (tag == 5) {
			byte[] msg = {5, 1,
					this.localIPA[0], this.localIPA[1] , this.localIPA[2], this.localIPA[3],
					this.localPortA[0], this.localPortA[1],
					this.localIDA[0], this.localIDA[1]};
			return msg;
		}
		
		/*   TAG 6   */
		else if (tag == 6) {
			byte[] sID = twoToByte(searchID);
			byte[] msg = {6, 1,
					rec[2], rec[3], rec[4], rec[5], rec[6], rec[7], rec[8], rec[9],
					sID[0], sID[1], rec[12], rec[13]};
			return msg;
		}
		
		/*   TAG 7   */
		else if (tag == 7) {
			byte[] msg = {7, 1,
					this.localIPA[0], this.localIPA[1], this.localIPA[2], this.localIPA[3],
					this.localPortA[0], this.localPortA[1], this.localIDA[0], this.localIDA[1],
					rec[10], rec[11]};
			return msg;
		}
		
		/*   TAG 8   */
		else if (tag == 8) {
			byte[] msg = new byte[12+rec.length];
			msg[0] = 8;
			msg[1] = 1;
			for (int i = 0; i < this.localIPA.length; i++)
				msg[i+1] = this.localIPA[i];
			msg[6] = this.localPortA[0];
			msg[7] = this.localPortA[1];
			msg[8] = this.localIDA[0];
			msg[9] = this.localIDA[1];
			byte[] lengthA = twoToByte(rec.length);
			msg[10] = lengthA[0];
			msg[11] = lengthA[1];
			for (int i = 0; i < rec.length; i++) {
				msg[i+11] = rec[i];
			}
			return msg;
		}
		
		System.out.println("!!!ERROR: getMSG TAG: " + tag);
		return null;
	}

	
	
			/*****************/
			/*   PEER LIST   */
			/*****************/
	
	/* SHOW LIST */
	void showList() {
		this.listTA.setText("");
		for (int i = 0; i < this.peerList.length; i++) {
			if (this.peerList[i][0] != 0) {
				byte[] tempIdA = {this.peerList[i][6], this.peerList[i][7]};
				int tempId = twoToInt(tempIdA);
				byte[] tempPortA = {this.peerList[i][4], this.peerList[i][5]};
				int tempPort = twoToInt(tempPortA);
				this.newLog("list", "" + tempId + " ---> " + (this.peerList[i][0]&0xFF)
						+ "." + (this.peerList[i][1]&0xFF) + "." + (this.peerList[i][2]&0xFF) + "." + (this.peerList[i][3]&0xFF)
								+ " : " + tempPort + "   #" + (this.peerList[i][8]&0xFF) + "\n");
			} else {
				this.newLog("list", "empty slot\n");
			}
		}
	}
	
	/* ADD PEER TO LIST */
	void addList(byte[] newPeer) {
		if (newPeer[0] != 0) {
			for (int i = 0; i < this.peerList.length; i++) {
				if (this.peerList[i][0] == newPeer[0] && this.peerList[i][1] == newPeer[1] &&
						this.peerList[i][2] == newPeer[2] && this.peerList[i][3] == newPeer[3] &&
						this.peerList[i][4] == newPeer[4] && this.peerList[i][5] == newPeer[5] &&
						this.peerList[i][6] == newPeer[6] && this.peerList[i][7] == newPeer[7]) {
					System.out.println("LIST: ALREADY KNOW " + Arrays.toString(newPeer));
					return;
				}
				if (this.peerList[i][0] == 0) {
					for (int j = 0; j < this.peerList[i].length; j++) {
						this.peerList[i][j] = newPeer[j];
					}
					System.out.println("LIST: ADDED " + Arrays.toString(newPeer));
					this.showList();
					return;
				} else {
					if (!(i < this.peerList.length)) {
					System.out.println("LIST : NO EMPTY slot for " + Arrays.toString(newPeer));
					}
				}
			}
		} else {
			System.out.println("LIST: NOT ADDED " + Arrays.toString(newPeer));
		}
	}
	

	
	
	
	
	
	
	
	
	
	
	
	String getLocalIPS() {
		
		String lIP = "";
		
		/* Version 1
		InetAddress ip = InetAddress.getLocalHost();
		String lIP = ip.getHostAddress();
		*/
		
		// Version 2
		try {
			Socket s = new Socket("www.google.de", 80);
			lIP = s.getLocalAddress().getHostAddress();
			s.close();
		} catch (IOException e) {
			this.newLog("sys", "init local address failed");
			e.printStackTrace();
		}
		
		return lIP;
	}
	
	void setLocalIPA() {
		
		String[] stringArr = this.localIPS.split("\\.");
		
		
		for (int i = 0; i < 4; i++) {
			this.localIPA[i] = (byte) Integer.parseInt(stringArr[i]);
		}
		
	}
	static int twoToInt(byte[] i) {
		int res = ((i[0]&0xFF) << 8) | (i[1]&0xFF);
		return res;
	}
	
	static byte[] twoToByte(int value) {
		byte[] res = {(byte)(value >>> 8), (byte)(value)};
		return res;
	}
	
	void setLocalPortA() {
		byte[] res = {(byte)(port >>> 8), (byte)(port)};
		this.localPortA[0] = res[0];
		this.localPortA[1] = res[1];
	}
}
