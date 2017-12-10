import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Controler {
	
	static List<LedStrip> mStrips = new ArrayList<LedStrip>();
	
	public static void InitLedStrips(int num) {
		for(int i = 0; i < num; i++) {
			mStrips.add(new LedStrip());
		}
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Server init");
		String input = "";

		ServerSocket server = new ServerSocket(5045);
		
		InitLedStrips(1);
		
		System.out.println("Init done");

		while (input.compareTo("stop") != 0) {
			Socket sock = server.accept();
			BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));

			System.out.println("Client connected");
			String s;
			try {
				while ((s = reader.readLine()) != null) {
					System.out.println("> " + s);
					
					// Check if update was requested
					if(s.contains("Request update ID")) {
						
						String res[] = s.split("ID");
						int id = Integer.parseInt(res[res.length - 1]);
						
						if(id <= mStrips.size() && id >= 0) {
							System.out.println("> lookup RGB values");
							String reply = "R" + mStrips.get(id-1).getRed() + "G" + mStrips.get(id-1).getGreen() + "B" + mStrips.get(id-1).getBlue();
							writer.write(reply);
						}	
						else {
							writer.write("Illegal ID");
						}
					}
					// Illegal request received
					else {
						System.out.println("Unknown command: " + s);
						writer.write("Not known command");
					}
					writer.newLine();
					writer.flush();
				}

			} catch (Exception e) {
			}
			System.out.println("Client disconnected");
			reader.close();
			sock.close();
		}
		server.close();

	}

}