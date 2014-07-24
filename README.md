DistributedCurrency
===================
Implemented Totally Ordered multicast using Lamport's Logical Clocks. Replica consistency was maintained on 3 Virtual Machines using active replication protocol. Multithread and Socket Programming techniques were used for implementation. Completely in JAVA




EEL 6935 Spring 14 (Distributed Computing) Homework 4
Assigned: Saturday , Mar -01-2014
Due: Friday, Mar-14-2014 (at 5 pm)
Distributed control of currency exchange value
In this homework, you are to implement totally-ordered multicast using Lamport’s logical clocks (textbook
pages 244-248, in particular page 248). The goal is to keep the replicas on the 3 VMs consistent using
active replication protocol (page 311).
The idea is to have the processes on the VMs share a currency value as speci􀄮􀄞d by the pair of rates (sell
rate, buy rate). The processes can access the currency value simultaneously. As the same 􀆟me, concurrent
update operations are allowed on the value of currency. An operation that occurs at a process is
contained in a message that is delivered to other processes as well as the node itself.
Each process has two threads: a worker thread and dispatcher thread. The process generates random
rates changes (􀑐x, 􀑐y) at random intervals and sends them to all the processes including itself. The
dispatcher thread waits for updates (􀑐x, 􀑐y) from other replicas. Upon receiving a message, it inserts
the message in its local queue and multicasts an acknowledgement to the other processes. The local
queue containing messages is ordered according to the timestamps of the messages. That is, the earlier
the timestamp, the closer the message is to the head of the queue.
Requirements
Your program should be running on the VM instances given to you. As the underlying transport
protocol, TCP is to be used to communicate between processes. You will therefore need multithread
and socket programming skills. Below are useful links for this homework in Java. We will accept only
Java solu􀆟ons for this homework.
Socket Programming Tutorial: h􀆩p://java.sun.com/docs/books/tutorial/networking/sockets/index.html
Multi-threading Tutorial: h􀆩p://java.sun.com/docs/books/tutorial/essential/concurrency/index.html
For details on multithread servers, please refer to page 77 in the textbook.
Processes are assumed to be numbered (Pn) in the order of the last digit of the IP address of the virtual
machine that the process is residing in. The process numbers range from 0 to 2. Thus the processes are
named P0, P1, and P2.
For example,
P0: 192.168.0.2 (the lowest)
P1: 192.168.0.3
P2: 192.168.0.4 (the highest)
EEL6935 Homework 4 – Page 1
Each process is assumed to have an endpoint location consisting of the IP address and the port. For
example, the endpoint of the process P0 would be 192.168.0.2:9999 if the port number is 9999. Also,
each process has a list of the endpoints for all the processes (P0, P1, and P2) in the network including
itself. To do this, your program should read a file named “info.txt” that contains a list of addresses and
connect them one by one as described in the deployment phase 1 below. The file name should be
EXACTLY the same as “info.txt” in lower case. The lines of the file contain the IP address and port of a
process as: [ip address] [port]
The content of the file “info.txt” would be the following:
192.168.0.2 9999
192.168.0.3 9999
192.168.0.4 9999
Again, use the first 4 digits of your UFID as the port number.
The details of the implementation are described below.
Currency
The currency value is specified by a pair of rates (sell rate, buy rate). It is initially set to (100, 100). Rate
changes (i.e., the “deltas”) Δx and Δy are in the range [-80, 80]. A process can update the rate by delta (Δx,
Δy). Update operation: new pair of rates (x’, y’) = current pair of rates (x, y) + delta (Δx, Δy)
That is, (x’, y’) = (x + Δx, y + Δy) where x and y are integer type and the range of Δx and Δy is [-80, 80]
including -80 and 80. (For simplicity, we allow negative rates in this homework.)
Message
A message contains a type, process id, and payload as shown below. You may add more types if
necessary.
- Type: UPDATE, ACK
o UPDATE: indicates the rate changes (Δx, Δy)
o ACK: acknowledges the UPDATE message has been received
- Payload:
o If type is UPDATE then the payload contains the process id of the message sender and the
pair of rates (x, y) of the currency
o If type is ACK then the payload contains the process id of the message sender and the
timestamp of the message.
Queue
A process has its own local queue to store messages. A message in the queue is inserted in the order of
its timestamp. It is removed from the queue only when it is acknowledged by all the other processes.
EEL6935 Homework 4 – Page 2
Clock counter
Processes maintain a Lamport clock at a constant rate (ticks/sec). However, the clock rates are di􀄫erent
for each process. The clock increases by the clock rate every second. In addition, it increases by 1 for
every event (sending/receiving/delivering message) as in the textbook pages 246-247. For example, if P0,
P1, and P2 have clock counter unit of 2, 3, and 1, respectively, then the clocks tick as follows. Note that
the process id is attached to low-order end of time separated by a decimal point to break the tie.
P0’s clock: 2 ticks/sec
0.0
2.0
4.0
…
P1’s clock: 3 ticks/sec
0.1
3.1
6.1
…
P2’s clock: 1 tick/sec
0.2
1.2
2.2
…
Command line
The main program name is Curren cy. The program should accept the process id and the number of
update operations to be performed.
$java Currency [Process id] [Number of operations] [clock rate]
e.g., java 0 30 4
If you run 30 update operations for each process, you would end up with 90 (30 * 3) update operations
performed.
EEL6935 Homework 4 – Page 3
Currency
Deployment
Phase 1: Connection setup
All the processes on the VMs are started. You will need to have the processes wait until the other
processes are up. When a process starts, it establishes TCP connections to the processes with lower
process ID.
In this case,
P0 starts
P1 starts and connects to P0
P2 starts and connects to P0 and P1.
Phase 2: Running phase
Once all the processes are running and connected to each other, each process starts update the
currency value by randomly generating rate changes (Δx, Δy) of the value pair (x, y) at every interval
period. The interval is chosen also randomly between 0 to 1000 milliseconds (1 second) excluding 0.
A process sends the update (Δx, Δy) to all the processes including itself. It also accepts incoming
messages from other processes and performs the operations in the messages. Upon receiving an
UPDATE message, the process multicasts acknowledgement (ACK) messages to others. The process is
allowed to update the value of its currency ONLY after it receives acknowledgements from all the
processes. Note that the order of updates performed could be different from what is actually done in
real-time, but all the processes see the updates in the same order (total order). Also, since there are
local objects shared by multiple threads, you need to consider synchronization of accesses to the objects
within a process.
Phase 3: Termination phase
After the number of iterations given, all the processes are terminated gracefully. Note that a process
terminates only after all the messages are delivered and its local queue is empty. Also, the process is
allowed to terminate after it confirms that others have finished their job (no more messages to send
AND have delivered all the messages). In order to do this, they need to exchange finishing messages.
Termination conditions
1. All the messages are delivered.
2. Local queue is empty
3. Other processes finish
Logging
Your program is required to create a log file in which all the events regarding connections and messages
are written with the timestamps. The log files are named log0, log1, and log2 with the number being the
corresponding process id.
Process connection
When a process is connected to other process, write something like the following into the log file:
P0 is connected to P1 (192.168.0.3).
When a process is connected from other process, write something like the following into the log file:
P1 is connected from P0 (192.168.0.2).
EEL6935 Homework 4 – Page 4
When a process has 􀄮nished its job, write something like the following into the log file:
P2 finished.
P0 finished.
P1 finished.
When all the processes have finished, write something like the following into the log file:
All finished. P2 is terminating…
Message
When a message is delivered, the currency is set to a new value pair and this event is wri􀆩en into a line in
the log file. The line has three columns which represent the local time, process id of the message
sender, timestamp, and the current rates of currency separated by a white space as shown below.
column1 column2 column3
[ localtime ] [ operation number : local counter ] description of the event …
[ month/day hour:minute:second ] [ OPnum : Cclock ] description of the event …
where operation number and local counter start from 1.
The exact format is:
[ MM/DD hh:mm:ss ] [OPnum : Cclock ] Currency value is set to (x,y) by (􀑐x, 􀑐y).
An example of the log 􀄮le of the process P1 would be:
IMPORTANT: Your log file format MUST be the same as the above format. Otherwise, you will not get
credit.
Submission (March 14th 2014, before 5 pm):
You are required to provide the following in a single 􀄮le.
- readme.txt file that describes the program structure such as 􀄮les, classes, and signi􀄮cant methods
EEL6935 Homework 4 – Page 5
- report.txt file that shows what you learned and the difficulties you had in developing the program
and how you solved them
- makefile to compile the program. We will type only “make” to compile the program as done in
previous homework.
- program source code files. DO NOT include binary files (.class, .obj, .out, .exe...). That will lead to
grade penalty.
IMPORTANT: ALL the above files should be tarred into ONE FILE named as yourname-hw4.tar. If you
submit more than one file, you will be penalized.
Please use the following commands when you submit the files.
Create a directory yourname_hw4.
mkdir yourname_hw4
Copy all the required files into the directory yourname_hw4.
cp file1 file2 file3 … yourname_hw4
tar cf yourname_hw4.tar yourname_hw4/*
If you submit files with extension other than tar (such as tar.gz, rar, and zip), points will be deducted
from your grade.
Submission Policy:
􀁸 Do NOT include binary files. Use the file names as specified above. Incorrect submission formats
will lead to a grade reduction.
􀁸 You will be given access to a virtual machine. Your program will be tested on a virtual machine
with exactly the same configuration as your virtual machine. Make sure to test your program
before submission.
􀁸 All submissions are expected by the deadline specified in the homework assignment. Grade is
automatically reduced by 25% for every late day.
􀁸 Make sure you test your submitted code using the tar file you submitted on the virtual machine
used for course homework. If untar, make, compile or any other command needed to execute
your program do not work, your homework grade will be zero.
EEL6935 Homework 4 – Page 6
