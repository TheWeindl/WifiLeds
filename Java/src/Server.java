

public class Server {

	public Server() {
	}

	public static void main(String[] args) {
		Controler controler = new Controler();
		Thread thread = new Thread(controler);
		thread.start();
	}

}
