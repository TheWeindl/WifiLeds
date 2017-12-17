package servers;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import json.JSONObject;

public class WorkerRunnable implements Runnable {

	protected Socket clientSocket = null;
	protected Strips strips = null;

	public WorkerRunnable(Socket clientSocket, Strips mStrips) {
		this.clientSocket = clientSocket;
		this.strips = mStrips;
	}

	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			OutputStreamWriter writer = new OutputStreamWriter(clientSocket.getOutputStream());
			
			String content = reader.readLine();			
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
				jsonResponse.put("change", 1);
			}		
			
			
			reader.close();
			writer.close();
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}