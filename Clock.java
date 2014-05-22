
public class Clock extends Thread {

	public static double counter;
	public int pid;
	public int cticks;
	public int iterations;
	
	Clock(int pid, int cticks, int iterations)
	{
		this.pid = pid;
		this.cticks = cticks;
		this.iterations = iterations;
		counter = 0 + this.pid*0.1;
	}
		public void run() {
	
			while(true) 
			{
				counter = counter+cticks;

				try 
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e) 
				{
					System.out.println("failed to sleep");
				}
			}
		}
		
		public double get_clock() {
			return (counter);
	} 
	
 }

