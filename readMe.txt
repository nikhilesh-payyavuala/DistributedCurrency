Main Class: Currency.java

run p0 on dc01.acis.ufl.edu
    p1 on dc02.acis.ufl.edu
    p2 on dc03.acis.ufl.edu

Other Classes:

1.CurrencyThread.java  // has the functionality of worker, dispatcher threads
2.Message.java		// message components and a constructor for initializing the message
3.CurrencyValue.java		// takes care of updating of currency
4.SortQueue.java		// uses comparator interface to sort the messages in queue
5.Clock.java		// for running clocks on all processes, each process starts a separate thread for clock

significant methods:

queue_modify()        // for adding, removing   update and acknowledge messages in queue
rand()		     // for generating random numbers for delx, dely and interval terms
run() of CurrencyThread  // all of the worker and dispatcher thread functionality including debugging code for the problem of 					acknowledgements reaching processes earlier than updates		  


