
class CurrencyValue {

	public static int x = 100;
	public static int y = 100;
	
	CurrencyValue(){
	}
	
	void Currency_init(){
		x = 100;
		y = 100;
	}
	
	CurrencyValue(int x1, int y1) {
		x = x1;
		y = y1;
	}
	
	public static void update_currency(int delx, int dely) {
		x = x + delx;
		y = y + dely;
	}
	
	public void print_currency() {
		System.out.println("sell rate =  " + x);
		System.out.println("buy rate =  " + y);
	}


}

