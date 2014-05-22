import java.io.*;
@SuppressWarnings("serial")
public class Message implements Serializable
{
	public String mtype;
	public int pid;
	public String mSerial;
	public int dx;
	public int dy;
	public double clock;
	public int ack;
	
	Message(){}

  Message(String type, String Mid, int y, int dx, int dy, double clock, int ack) {
		this.mtype = type;
		this.mSerial = Mid;
		this.pid = y;
		this.dx = dx;
		this.dy = dy;
		this.clock = clock;
		this.ack = ack;
		//this.pupdate = p;
	}
	
}
