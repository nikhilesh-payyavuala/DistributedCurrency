import java.net.*;
import java.io.*;
import java.util.*;

@SuppressWarnings("unused")
public class CurrencyThread extends Thread 
{
    static Random generator = new Random();
    CurrencyValue c = new CurrencyValue();
    private Socket socket1 = null;
    private Socket socket2 = null;
    private String type;
    private static int processId;
    private String buffer;
    private  int cTicks;
    private static double counter;
    private static Message msg_temp;
    private static int iterations;
    private static BufferedWriter writer;
    private static ArrayList<Message> queue = new ArrayList<Message>();
    private static ArrayList<String> ack_queue = new ArrayList<String>();
    private static ArrayList<Message> tQueue = new ArrayList<Message>();    
    static int count_update;
    static int count_remove;					// Counts the number of processed transactions(Final count of transactions)
    static int addCount;
    static int finishCount=0;
    private static int flag_ready_for_Remove = 0;
    private static int flag_acknowledgement_Receieved=0;
    private static int flag_update_found_in_Queue=0;
    
//  Constructor for dispatcher and worker threads
    public CurrencyThread(Socket socket1,Socket socket2,String type,int pId, int i) 
    {
	    super("CurrencyThread");
	    this.socket1 = socket1;
	    this.socket2 = socket2;
	    this.type=type;
	    processId=pId;
	    iterations=i;
    }

// synchronizer for finish count
    public static synchronized void counter(String type)
    {
    	if(type.equals("finishCount"))
    	{
    			finishCount++;
    			if(finishCount==2)
   				{
   		        System.out.println("All Processes are done with the transactions. Process P"+processId+" terminated gracefully");
				logging("All finished. p"+processId+" is terminating",2);
   				}
    	}
    	
    }
//	Queue operations     
    public static synchronized void queue_modify(Message message,String op)
    {
    	if(op.equals("ADD"))
    	{
	    // debugging code for the problem of acknowledgment received for a message before receiving an update message problem
    		if(ack_queue.indexOf(message.mSerial)!=-1)		
    		{
    			flag_acknowledgement_Receieved=1;
    			ack_queue.remove(ack_queue.indexOf(message.mSerial));
    		}

    		queue.add(message);   
		Collections.sort(queue, new SortQueue());
    	}
    	else if(op.equals("UPDATE"))
    	{
    		flag_update_found_in_Queue = 0;
    		flag_ready_for_Remove=0;
    		for (Message temp : queue)											
				{ 
					if(temp.mSerial.equals(message.mSerial))
					{
						flag_update_found_in_Queue=1;
						temp.ack++;
						if(temp.ack==2)		//If ack count is 2, process the transaction
						{
							CurrencyValue.update_currency(temp.dx,temp.dy);
							flag_ready_for_Remove=1;
							msg_temp=temp;
						}
					}
				}
    		
	//debugging code for "acknowledgment received for a message before receiving an update message" problem
    		if(flag_update_found_in_Queue==0)ack_queue.add(message.mSerial);
    	}
    	else
    	{
    				queue.remove(msg_temp);
    				count_remove++;
    				System.out.println("After Tansaction "+count_remove+": X:"+CurrencyValue.x+" Y:"+CurrencyValue.y);
				logging("[ OP"+count_remove+" : C"+(double)msg_temp.clock+" ] "+"CurrencyValue value is set to ("+CurrencyValue.x+","+CurrencyValue.y+") by ("+msg_temp.dx+","+msg_temp.dy+")",2);
    		flag_ready_for_Remove=0;
    	}
    }

 
    public void run() 
    {
    	if(type.equals("D"))   //dispatched thread
    	{	
    		
    		try 
    		{
    			PrintWriter out_1 = new PrintWriter(socket1.getOutputStream(), true);
    			PrintWriter out_2 = new PrintWriter(socket2.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
                
            	while((buffer = in.readLine())!=null)
           		{

           			String tokens[] = buffer.split(":");
           			Message message = new Message();

//           		Check if the received message is Update
           			if (tokens[0].equals("U"))
           			{
//           			Build the update Message to push into the queue
           				double tempCounter=++counter;
           				message.mtype = tokens[0];
           				message.pid = Integer.parseInt(tokens[1]);
           				message.mSerial = tokens[2];
           				message.dx = Integer.parseInt(tokens[3]);
           				message.dy = Integer.parseInt(tokens[4]);
           				message.clock = Double.parseDouble(tokens[5]);
           				message.ack = Integer.parseInt(tokens[6]); 
     //        			Adjust local clock counter
           				if(tempCounter<message.clock)
           				{

           					tempCounter = (int)Math.max(tempCounter, message.clock);
           					counter = tempCounter+processId*0.1+1;
           				}
           				flag_acknowledgement_Receieved=0;
           				queue_modify(message,"ADD");									// 	
              				if(flag_acknowledgement_Receieved==1)
           				{
           					queue_modify(message,"UPDATE");
           					if(flag_ready_for_Remove==1)
           						queue_modify(message,"REMOVE");
           				}
           				
           				
//           			Building an Acknowledge message and broadcast to other servers
           			counter++;
    	    			String temp = "A"+":"+processId+":"+message.mSerial+":"+counter;
    	    			out_1.println(temp);
        			out_2.println(temp);
        				
           			}
           			

           			else if(tokens[0].equals("A"))		// for ack messages
           			{
           				double tempCounter=++counter;
           				message.mtype = tokens[0];
           				message.pid = Integer.parseInt(tokens[1]);
           				message.mSerial = tokens[2];
           				message.clock = Double.parseDouble(tokens[3]);


           				if(tempCounter<message.clock)
           				{     					
           					tempCounter = (int)Math.max(tempCounter, message.clock); // counter adjustment
           					counter = tempCounter+processId*0.1+1;
           				}
           				
//           			Check the queue for the corresponding message and increment the ack variable by 1

           				flag_ready_for_Remove=0;
           				queue_modify(message,"UPDATE");
           				if(flag_ready_for_Remove==1)queue_modify(msg_temp,"REMOVE");			//	After processing remove the message from the queue
           				
           				if(count_remove==iterations*3)
           	    		{
           	    			String finish = "F"+":"+processId;
           	    			out_1.println(finish);
           					out_2.println(finish);
           	    		}
           			}
           			else									//	Code for processing Finish message
           			{
           				message.mtype = tokens[0];
           				message.pid = Integer.parseInt(tokens[1]);
           				System.out.println("Process p"+message.pid+" is done with all transactions ");
					logging("p"+message.pid+" finished.",2);
           				counter("finishCount");
           				
           				
           			}
           		}
	            out_1.close();
	            out_2.close();
	            in.close();
	   //         socket1.close();
	 //           socket2.close();
	     
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
        }
    	
//    	Worker Thread begins here
    	else if (type.equals("W"))
    	{
    		try
			{

	    		for(int i = 0; i < iterations ; i++)
	    		  {
	    			int interval = rand(1,1000,generator);
	    			try 
	    			{
	    				Thread.sleep(interval);
	    			} 
	    			catch(InterruptedException e) 
	    			{
	    				System.out.println("exception");
	    			}
	    			
	    			double tempCounter=++counter;
	    			String mssg_type = "U";
	    			int delx = rand(-80,80,generator);
	    			int dely = rand(-80,80,generator);
		Message message = new Message(mssg_type, "M"+processId+i ,processId, delx, dely, tempCounter, 0);		
	    			queue_modify(message,"ADD");				//Add the message to the local queue
	    			

	 String ack = message.mtype+":"+message.pid+":"+message.mSerial+":"+message.dx+":"+message.dy+":"+message.clock+":"+"1";
	    			
	    			PrintWriter out_1 = new PrintWriter(socket1.getOutputStream(), true);
    				PrintWriter out_2= new PrintWriter(socket2.getOutputStream(), true);
    				out_1.println(ack);
    				out_2.println(ack);
    			
	    		  }
			}catch (IOException e)
			{
				e.printStackTrace();
			}
    	  }
	}


//  Random number generator
    public static int rand(int start, int end, Random r)
    {
	    long range = (long)end - (long)start + 1;
	    long fraction = (long)(range * r.nextDouble());
	    int randomNumber =  (int)(fraction + start);    
	    return(randomNumber);

    }

	// method for logging
	public static synchronized void logging(String s1,int x){
		FileWriter fstream;
		Date date= new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		String timeStamp = "[ " + String.format("%02d",(cal.get(Calendar.MONTH) + 1)) + 
				"/" + String.format("%02d",cal.get(Calendar.DAY_OF_MONTH)) + 
				" "+String.format("%02d",cal.get(Calendar.HOUR_OF_DAY))+
				":"+String.format("%02d",cal.get(Calendar.MINUTE))+
				":"+String.format("%02d",cal.get(Calendar.SECOND))+
				" ]";
		
		try 
		{
			fstream = new FileWriter("log"+processId+".txt", true);
			writer = new BufferedWriter(fstream);
			if(x==2)
				writer.write(timeStamp+" "+s1+"\n");
			else
				writer.write(s1);
			writer.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
