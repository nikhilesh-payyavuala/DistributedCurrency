import java.net.*;
import java.io.*;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Scanner;
import java.util.Date;

public class Currency {
	

	private static Formatter log;
	public void logFile(int pid)
	{
		try {
 
			log = new Formatter ("log"+pid+".txt");
		}
		catch(Exception e){
			System.out.print("Error in file creation");
		}
		
	}
	
	public void write_to_logFile(String s1,int x){
		if(x==1)
			log.format("%s\n", s1);
		else
		{
			Date date= new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			
			String lamport_time = "[ " + String.format("%02d",(cal.get(Calendar.MONTH) + 1)) + 
					"/" + String.format("%02d",cal.get(Calendar.DAY_OF_MONTH)) + 
					" "+String.format("%02d",cal.get(Calendar.HOUR_OF_DAY))+
					":"+String.format("%02d",cal.get(Calendar.MINUTE))+
					":"+String.format("%02d",cal.get(Calendar.SECOND))+
					" ]";
			log.format("%s %s\n", lamport_time,s1);
		}
	}
	public void closeOutputFile(){
		log.close();
	}
	

	
    public static void main(String[] args) throws IOException {
    	int processId = Integer.parseInt(args[0]);
    	int iterations = Integer.parseInt(args[1]);
    	int cTicks = Integer.parseInt(args[2]);
		
    	Scanner file;
    	String p0_ip_address=null,p1_ip_address=null,p2_ip_address=null;
		int p0_port=0,p1_port=0,p2_port=0;
		
    	ServerSocket serverSocket = null;			
        Socket socket01 = null;					//    	Connection between Server P0 and server P1
        Socket socket12 = null;					//    	Connection between Server P3 and server P2
        Socket socket02 = null;					//    	Connection between Server P1 and server P3
        
//      Open the info.txt file
        try{
			file= new Scanner (new File("info.txt"));
			p0_ip_address=file.next();
			p0_port=Integer.parseInt(file.next());
			
			p1_ip_address=file.next();
			p1_port=Integer.parseInt(file.next());
			
			p2_ip_address=file.next();
			p2_port=Integer.parseInt(file.next());
			file.close();
		}
		catch(Exception e){
			System.out.println(" input file not found");
		}
        

        Currency l1 = new Currency();
        
//      Check for server's process ID and do the operations accordingly
        try {
        		
        			
        		new Clock(processId,cTicks,iterations).start();
				
        		l1.logFile(processId);														//	Create log file
    		   	String log_print = "[p0] "+p0_ip_address+":"+p0_port;
    		   	l1.write_to_logFile(log_print,1);
    		   	log_print = "[p1] "+p1_ip_address+":"+p1_port;
    		   	l1.write_to_logFile(log_print,1);
    		   	log_print = "[p2] "+p2_ip_address+":"+p2_port;
    		   	l1.write_to_logFile(log_print,1);
        	   
    		   
    		   	
        	   if(processId==0)
               {
        		   
        		   	System.out.println("Process P0 started");
        		   	serverSocket = new ServerSocket(p0_port);										
        		   	log_print = "p0 ("+p0_ip_address+") is listening on port "+p0_port;
	        		l1.write_to_logFile(log_print,1);
        		   	
        		   	System.out.println("Wating for all to connect");
        		   	socket01=serverSocket.accept();												//	Wait for P1 to connect
        		   	log_print = "p0 is connected from p1 ("+p1_ip_address+")";
	        		l1.write_to_logFile(log_print,2);
	        		log_print = "Waiting for all to be connected";
	        		l1.write_to_logFile(log_print,1);
        		   	
        		   	System.out.println("Connection established with P1");
        		   	System.out.println("Wating for P2 to connect");
        		   	socket02=serverSocket.accept();												//	Wait for P2 to connect
        		   	log_print = "p0 is connected from p2 ("+p2_ip_address+")";
	        		l1.write_to_logFile(log_print,2);
        		   	
	        		log_print = "All Connected";
	        		l1.write_to_logFile(log_print,1);
        		   	
        		   	System.out.println("Connection established with P2");
        		   	l1.closeOutputFile();
        		   	
               		new CurrencyThread(socket01,socket02,"D",processId, iterations).start();				//	Dispatch thread for P1
               		new CurrencyThread(socket02,socket01,"D",processId, iterations).start();				//	Dispatch thread for P2
            	   	new CurrencyThread(socket01,socket02,"W",processId, iterations).start();				//	Worker Thread of P0
            	   	serverSocket.close();
               }
               else if (processId==1)
               {
            	   	System.out.println("Process P1 started");
            	   	serverSocket = new ServerSocket(p1_port);										// 	Open port 1454 for connecting with P2
	            	   	log_print = "p1 ("+p1_ip_address+") is listening on port "+p1_port;
	        		   	l1.write_to_logFile(log_print,1);
        		   	
            	   	socket01 = new Socket(p0_ip_address, p0_port);									//	Connect with P0
            		
	            	   	log_print = "p1 is connected to p0 ("+p0_ip_address+")";
	        		   	l1.write_to_logFile(log_print,2);
	        		   	log_print = "Waiting for all to be connected";
	        		   	l1.write_to_logFile(log_print,1);
        		   	
            	   	System.out.println("Connection established with P0");
            	   	System.out.println("Wating for P2 to connect");
            	   	socket12=serverSocket.accept();												//	Wait for P2 to connect
	            		log_print = "p1 is connected from p2 ("+p1_ip_address+")";
	        		   	l1.write_to_logFile(log_print,2);
	        		   	log_print = "All Connected";
	        		   	l1.write_to_logFile(log_print,1);
	        		   	
            	   	System.out.println("Connection established with P2");
            	   	
            	   	l1.closeOutputFile();
            	   	
            	   	new CurrencyThread(socket12,socket01,"D",processId, iterations).start();				//	Dispatch Thread for P2
            	   	new CurrencyThread(socket01,socket12,"D",processId, iterations).start();				//	Dispatch Thread for P0
            	   	new CurrencyThread(socket01,socket12,"W",processId, iterations).start();				//	Worker Thread of P1

               }
               else
               {
            	   	System.out.println("Process P2 started");

            	   	socket02 = new Socket(p0_ip_address, p0_port);									//	Connect with P0
	            	System.out.println("Connection established with P0");
	            		log_print = "p2 is connected to p0 ("+p0_ip_address+")";
	        		   	l1.write_to_logFile(log_print,2);
	        		   	
	            	socket12 = new Socket(p1_ip_address, p1_port);									//	Connect with P1
	            	System.out.println("Connection established with P1");
		            	log_print = "p2 is connected to p1 ("+p1_ip_address+")";
	        		   	l1.write_to_logFile(log_print,2);
	        		   	log_print = "All Connected";
	        		   	l1.write_to_logFile(log_print,1);
	            	
	            	l1.closeOutputFile();
	            	
                    new CurrencyThread(socket02,socket12,"D",processId, iterations).start();				//	Dispatch Thread for P0
                    new CurrencyThread(socket12,socket02,"D",processId, iterations).start();				//	Dispatch Thread for P1
                    new CurrencyThread(socket12,socket02,"W",processId, iterations).start();				//	Worker Thread of P2
                	
	            	
               }
        	   
        } catch (IOException e) {
            System.err.println("Could not listen on port: "+p0_port);
            System.exit(-1); 
        }
     
    }
}