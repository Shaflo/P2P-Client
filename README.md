# P2P-Client

- Protocol: TCP
- Port: 3333
- Type: Unstructed Network
- Join network over Lead Peer

---

## Definition

Client→Server:  
EntryMsg: (byte array)  
Tag: 1 (1 byte)  
Version: 0 (1byte)  
IPv4 in byte-Form (4byte)Port (2byte)  

Server→Client  
EntryResponseMsg: (byte array)  
Tag: 2 (1 byte)  
Version: 1 (1byte)  
ID: (2byte)  
4 * IPv4+Port (je 6 byte)+IDs  

Client→Peer  
P2PNodeRequestMsg  
Tag: 3 (1byte)  
Version: 1 (1byte)  
IPv4+Port (6 byte)  
ID: (2byte)  

Peer→Client  
P2PNodeResponseMsg  
Tag: 4 (1byte)  
Version: 1 (1byte)  
4 * IPv4+Port (je 6 byte)+IDs  

Peer→Server:(min. 1 pro  Minute)  
IAmAliveMsg: (byte array)  
Tag: 5 (1 byte)  
Version: 1 (1byte)  
IPv4+Port (6 byte)ID (2byte)  

Peer → Client  
P2PNodeSearchMsg  
Tag: 6 (1byte)  
Version: 1 (1byte)  
IPv4+Port (6 byte)  
SourceID (2byte)  
SearchID (2byte) → laufende Nummer pro Peer  
DestinationID (2byte)  

Client → Peer  
P2P”IAMFOUND”Msg  
Tag: 7 (1byte)  
Version: 1 (1byte)  
IPv4+Port (6 byte)  
SourceID (2byte)  
SearchID (2byte) → laufende Nummer pro Peer  

Peer → Peer  
P2PMsgMsg  
Tag: 8 (1byte)  
Version: 1 (1byte)  
IPv4+Port (6 byte)  
SourceID (2byte)  
LengthMsg (2byte) (Anzahl der xbytes)  
Msg (xbyte)  

Peer → Peer:  
P2PAreYouAliveMsg  
Tag: 9 (1byte)  
Version: 1 (1byte)  
IPv4+Port (6 byte)  
ID (2byte)  

Peer → Peer:  
P2PIamLeaderMsg  
Tag: 10 (1byte)  
Version: 1 (1byte)  
IPv4+Port (6 byte)  
ID (2byte)  

Ablauf Suche:  
- Peer fragt die Nachbarn  
- Fall 1: Nachbar hat SearchID bekommen  
- Fall 2: Nachbar hat SearchID nicht bekommen◦bin ich der Knoten? → Antwort an Suchenden◦Bin ich nicht der Knoten → Nachricht an alle meine Nachbarn, außer SuchendenListe

IDs:- von 1 – 25  

Ablauf Leader Election:  
- Peer fragt nacheinander alle Peers an, die eine höhere ID haben, bis einer antwortet* schickt Tag 9 und lässt die Verbindung offen  
- angefragter Peer antwortet an Peer mit Tag 5 und sendet Anfrage an alle Peers mit höherer ID, bei Antwort durch Peer mit höherer ID → fragender Peer wird nicht Leader- wenn keine Antwort von höherer ID oder höhere existiert nicht, Nachricht “I am leader”an alle

Hinweise:  
SourceID: ID des suchenden Knoten  
DestinationID: ID des zu suchenden Knoten  
SearchID: eindeutige ID der jeweiligen Suche  
