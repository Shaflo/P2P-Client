# P2P-Client

- Protocol: TCP
- Port: 3333
- Type: Unstructed Network
- Join network over Lead Peer
- Peer list with max 4 Peers
- sending Messages in Byte Array
- Heartbeat (min. 1 per minute to leader)
- unique IDs for every Peer

## Messages

#### Join Network
```
new Client → Leader (EntryMSG):    
Tag: 1 (1 byte)  
Version: 0 (1 byte)  
IPv4+Port (6 byte)  
```
```
Leader → new Client (EntryResponseMsg):   
Tag: 2 (1 byte)  
Version: 1 (1 byte)  
ID (2 byte / Leader give IDs from 1-25)  
4 * IPv4+Port+ID (4 * 8 byte)  
```
#### Know other Peers
```
Peer1 → Peer2 (NodeRequestMsg):  
Tag: 3 (1 byte)  
Version: 1 (1 byte)  
IPv4+Port+ID (8 byte)  
```
```
Peer2 → Peer1 (NodeResponseMsg):  
Tag: 4 (1 byte)  
Version: 1 (1 byte)  
4 * IPv4+Port+ID (4 * 8 byte)  
```
#### Heartbeat
```
Peer → Server (IAmAliveMsg):  
Tag: 5 (1 byte)  
Version: 1 (1 byte)  
IPv4+Port+ID (8 byte)  
```
#### Search ID
```
Peer1 → Peer2 (NodeSearchMsg):  
Tag: 6 (1 byte)  
Version: 1 (1 byte)  
IPv4+Port (6 byte)  
SourceID (2 byte / Peer who start search)  
SearchID (2 byte / given by Peer who start search)  
DestinationID (2 byte / PeerID who is searched)  
```
```
DestinationPeer → AskingPeer (IAmFoundMsg):  
Tag: 7 (1 byte)  
Version: 1 (1 byte)  
IPv4+Port+ID (8 byte)    
SearchID (2 byte / copy from Tag 6 message)  
```
#### Message
```
Peer → Peer (TextMsg):   
Tag: 8 (1 byte)  
Version: 1 (1 byte)  
IPv4+Port+ID (8 byte)  
LengthMsg (2byte / length from xbytes)  
Msg (xbyte)  
```
#### Leader Election
```
Peer → Peer (AreYouAliveMsg):  
Tag: 9 (1 byte)  
Version: 1 (1 byte)  
IPv4+Port+ID (8 byte)  
```
```
Peer → Peer (IAmLeaderMsg):  
Tag: 10 (1 byte)  
Version: 1 (1 byte)  
IPv4+Port+ID (8 byte)  
```
## Search Algorithm 

- Peer fragt die Nachbarn  
- Fall 1: Nachbar hat SearchID bekommen  
- Fall 2: Nachbar hat SearchID nicht bekommen  
2.1 bin ich der Knoten?  
→ Antwort an Suchenden  
2.2 Bin ich nicht der Knoten  
→ Nachricht an alle meine Nachbarn, außer SuchendenListe  

IDs:- von 1 – 25  

## Leader Election (Bully Algorithm)

1. Unser Peer fragt nacheinander alle höheren IDs mit Tag 9 an.  
2. Sobald ein Peer mit Tag 5 antwortet, ist die Suche beendet (unser Peer wird kein Leader).  
3. Sobald ein Peer keine Antwort von höheren IDs bekommt wird er der Leader und muss alle Peers informieren. Dazu Peer IDs 1-25 IP+Port suchen und Tag 10 senden.  

Ablauf Leader Election:  
- Peer fragt nacheinander alle Peers an, die eine höhere ID haben, bis einer antwortet* schickt Tag 9 und lässt die Verbindung offen  
- angefragter Peer antwortet an Peer mit Tag 5 und sendet Anfrage an alle Peers mit höherer ID, bei Antwort durch Peer mit höherer ID → fragender Peer wird nicht Leader
- wenn keine Antwort von höherer ID oder höhere existiert nicht, Nachricht “I am leader”an alle


## Other  
SourceID: ID des suchenden Knoten  
DestinationID: ID des zu suchenden Knoten  
SearchID: eindeutige ID der jeweiligen Suche  

