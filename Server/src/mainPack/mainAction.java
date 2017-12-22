package mainPack;

import simpleServer.exception.IllegalPortException;
import simpleServer.exception.RunningServerException;

public class mainAction {

	public static void main(String[] args) {

		try {
			LedServer server = new LedServer("LedServer", 9999);
			server.start();
			System.out.println("Server started");
			System.out.println("Server stopped");

		} catch (NullPointerException | IllegalPortException e) {			
			System.out.println("Server could not be started!");
			e.printStackTrace();
		} catch (RunningServerException e) {
			System.out.println("Server already runnig!");
			e.printStackTrace();
		}

	}

}
