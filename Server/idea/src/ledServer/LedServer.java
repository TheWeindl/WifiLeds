package ledServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
		OutputStreamWriter writer = null;
		
		try {
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			writer = new OutputStreamWriter(sock.getOutputStream());	
			//writer = new BufferedOutputStream(sock.getOutputStream());
		
			String content = reader.readLine();
			System.out.println("Input: " + content);
			JSONObject jsonObject = new JSONObject(content);
			
			//Check for the selector of what to do with the message
			if(jsonObject.getInt("change") == 1)
			{
				//The message was sent by the website and is a change request
				int id = jsonObject.getInt("id");
				
				if(id < strips.getLength() && id >= 0)
				{
					strips.setStripColor(jsonObject.getJSONArray("color").getInt(0), jsonObject.getJSONArray("color").getInt(1), jsonObject.getJSONArray("color").getInt(2), id);
					strips.setStatus(jsonObject.getInt("status"), id);
				}
				else
				{
					System.out.println("Wrong index ... no strip avialiable");
				}
			}
			else
			{
				//The message was sent by a led strip and is an update request
				int id = jsonObject.getInt("id");
				int color[] = {strips.getRed(id), strips.getGreen(id), strips.getBlue(id)};
						
				JSONObject jsonResponse = new JSONObject();
				jsonResponse.put("id", id);
				jsonResponse.put("color", color);
				jsonResponse.put("status", strips.getStatus(id));
				jsonResponse.put("change", true);
				
				
				writer.write(jsonResponse.toString());
				System.out.println("Response: " + jsonResponse.toString());
				writer.flush();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
			try {
				sock.shutdownOutput();
                reader.close();
                writer.close();
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
