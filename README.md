# P2P-Client

- Protocol: TCP
- Port: 3333
- Type: Unstructed Network
- Join network over Lead Peer
- Peer list with max 4 Peers
- sending Messages in Byte Array
- Heartbeat (min. 1 per minute to leader)
- unique IDs for every Peer (1-25)

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
#### Time Synchronization
```
Leader → Peer (TellMeYourTimeMSG):  
Tag: 11 (1 byte)  
Version: 1 (1 byte)  
IPv4+Port+ID (8 byte)  
```
```
Peer → Leader (HereIsMyTimeMSG):  
Tag: 12 (1 byte)  
Version: 1 (1 byte)  
IPv4+Port+ID (8 byte)
Time (8 byte)  
```
```
Leader → Peer (HereIsYourNewTimeMSG):  
Tag: 13 (1 byte)  
Version: 1 (1 byte)  
IPv4+Port+ID (8 byte)
Time (8 byte)
```

## Search Algorithm

- Peer fragt die Nachbarn  
- Fall 1: Nachbar hat SearchID bekommen  
- Fall 2: Nachbar hat SearchID nicht bekommen  
2.1 bin ich der Knoten?  
→ Antwort an Suchenden  
2.2 Bin ich nicht der Knoten  
→ Nachricht an alle meine Nachbarn, außer SuchendenListe  

## Leader Election (Bully Algorithm)

2 ways of starting leader election:  
- we want (manually)  
- we get Tag 9 message and answer with Tag 5  

1. The Peer asks all higher IDs with Tag 9  
2. If a higher ID answer with Tag 5, this peer will take the leader election. we are finish  
3. We are leader, when we cannot find a peer with higher ID. We send Tag10 (IAmLeaderMessage) to all Peers  

## Time Synchronization

1. Leader asks every Peer for the time
2. Peers answer the Leader
3. Leader calculate the Time and tell tell the all Peers

## Dictionary
SourceID: ID des suchenden Knoten  
DestinationID: ID des zu suchenden Knoten  
SearchID: eindeutige ID der jeweiligen Suche
