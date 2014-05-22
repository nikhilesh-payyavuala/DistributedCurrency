import java.util.Comparator;

public class SortQueue implements Comparator<Message>{
	
	//sorting according to time stamps

	 
	 public int compare(Message m1,Message m2) {
	      String temp1=m1.clock+"";
	      String temp2=m2.clock+"";
	      int compared_value=temp1.compareToIgnoreCase(temp2);
	        
	        if (compared_value>0)
	            return 1;
	        else if (compared_value<0)
	            return -1;
	        else
	            return 0;
	    }
}
