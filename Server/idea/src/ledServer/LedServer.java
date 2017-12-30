package ledServer;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

import json.JSONObject;
import simpleServer.exception.IllegalPortException;
import simpleServer.server.InsecureServer;
import simpleServer.util.NetworkService;

public class LedServer extends InsecureServer{

	private Strips strips = new Strips();
	private NetworkService worker;

	public LedServer(String name, int port) throws NullPointerException, IllegalPortException {
		super(name, port);
	}

	@Override
	protected boolean start_Handler_service() {
		
		worker = new NetworkService(this.getSSocket(), this::handler);
		
		try{
			worker.start();
		}catch(IllegalThreadStateException ex){
			return false;
		}
			
		return true;
	}

	@Override
	protected boolean stop_Handler_service() {
		try{
			worker.interrupt();

			while(!worker.isInterrupted()){}
		}catch(SecurityException ex){
			return false;
		}
		return true;
	}

	public void handler(final Socket sock){
		Objects.requireNonNull(sock);
		
		BufferedReader reader = null;
		PrintWriter writer = null;
		String content = null;
		
		try {
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			writer = new PrintWriter(sock.getOutputStream());

			System.out.println("----------------------------------------------------------------------------");
			do {

				content = reader.readLine();
				if(content != null && !content.equals("END")) {

					System.out.println("Input: " + content);
					JSONObject jsonObject = new JSONObject(content);

					//Check for the selector of what to do with the message
					if (jsonObject.getInt("change") == 1) {
						//The message was sent by the website and is a change request
						System.out.println("From LED GUI");
						int id = jsonObject.getInt("id");

						if (id < strips.getLength() && id >= 0) {
							strips.setStripColor(jsonObject.getJSONArray("color").getInt(0), jsonObject.getJSONArray("color").getInt(1), jsonObject.getJSONArray("color").getInt(2), id);
							strips.setStatus(jsonObject.getInt("status"), id);
						} else {
							System.out.println("Wrong index ... no strip avialiable");
						}
					} else if(jsonObject.getInt("change") == 0){
						System.out.println("From LED Strip");
						//The message was sent by a led strip and is an update request
						int id = jsonObject.getInt("id");
						int color[] = {strips.getRed(id), strips.getGreen(id), strips.getBlue(id)};

						JSONObject jsonResponse = new JSONObject();

						jsonResponse.put("color", color);
						jsonResponse.put("id", id);
						jsonResponse.put("status", strips.getStatus(id));
						jsonResponse.put("change", true);

						writer.println(jsonResponse.toString());
						writer.flush();
						System.out.println("Response: " + jsonResponse.toString());
					}
					else {
						System.out.println("ERROR: command not recognized");
					}
				}
				Sleep(100);
			}
			while (!content.equals("END"));
			writer.flush();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
			try {
				//Start closing the socket
				System.out.println("Closing socket");
				sock.shutdownOutput();
				sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	private void Sleep(int milliseconds)
    {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
