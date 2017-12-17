import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class Controler implements Runnable {

	public final static String ADDRESS = "192.168.1.4";
	public final static int PORT = 5045;
	public final static long TIMEOUT = 10000;
	public final static int READWAIT = 2000;

	private List<LedStrip> mStrips = new ArrayList<LedStrip>();
	private ServerSocketChannel serverChannel;
	private Selector selector;
	private Map<SocketChannel, byte[]> dataTracking = new HashMap<SocketChannel, byte[]>();

	public Controler() {
		init();
		InitLedStrips(3);
	}

	public void InitLedStrips(int num) {
		for (int i = 0; i < num; i++) {
			LedStrip strip = new LedStrip();
			strip.setStripColor(120, 110, 100);
			mStrips.add(strip);
		}
	}

	private void init() {
		System.out.println("Initializing Server");
		if (selector != null)
			return;
		if (serverChannel != null)
			return;

		try {
			selector = Selector.open();
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverChannel.socket().bind(new InetSocketAddress(ADDRESS, PORT));
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		System.out.println("Now accepting connections...");
		try {
			while (!Thread.currentThread().isInterrupted()) {
				selector.select(TIMEOUT);
				Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

				while (keys.hasNext()) {
					SelectionKey key = keys.next();
					keys.remove();

					if (key.isValid()) {
						// Accept a new connection
						if (key.isAcceptable()) {
							System.out.println("Accepting connection");
							accept(key);
						}
						// Write to the connection
						if (key.isWritable()) {
							System.out.println("Writing");
							write(key);
						}
						// Read from the connection
						if (key.isReadable()) {
							System.out.println("Reading");
							read(key);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}

	}

	private void accept(SelectionKey key) throws IOException {
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		SocketChannel socketChannel = serverSocketChannel.accept();
		socketChannel.configureBlocking(false);

		socketChannel.register(selector, SelectionKey.OP_READ);
		// byte[] hello = new String("LED Server").getBytes();
		// dataTracking.put(socketChannel, hello);
	}

	private void write(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		byte[] data = dataTracking.get(channel);
		dataTracking.remove(channel);

		System.out.println("	Sending: " + new String(data));

		channel.write(ByteBuffer.wrap(data));
		key.interestOps(SelectionKey.OP_READ);

	}

	private void read(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer readBuffer = ByteBuffer.allocate(1024);
		readBuffer.clear();
		int read;
		try {
			// Needed because the microcontroller can not send data that fast
			sleep(READWAIT);
			read = channel.read(readBuffer);
		} catch (IOException e) {
			System.out.println("Reading problem, closing connection");
			key.cancel();
			channel.close();
			return;
		}
		if (read == -1) {
			System.out.println("Nothing to read -> closing connection");
			channel.close();
			key.cancel();
			return;
		}
		readBuffer.flip();
		byte[] data = new byte[1000];
		readBuffer.get(data, 0, read);
		System.out.println("	Received: " + new String(data));
		decodeMessage(key, data);
	}

	private void decodeMessage(SelectionKey key, byte[] data) throws IOException {
		String input = new String(data);
		String response = "empty";
		SocketChannel channel = (SocketChannel) key.channel();
		JSONObject jsonObject = new JSONObject(input);
		
		//Check for the selector of what to do with the message
		if(jsonObject.getInt("change") == 1)
		{
			//The message was sent by the website and is a change request
			int id = jsonObject.getInt("id");
			
			if(id < mStrips.size() && id >= 0)
			{
				mStrips.get(id).setStripColor(jsonObject.getJSONArray("color").getInt(0), jsonObject.getJSONArray("color").getInt(1), jsonObject.getJSONArray("color").getInt(2));
				mStrips.get(id).setStatus(jsonObject.getBoolean("status"));
			}
			key.interestOps(SelectionKey.OP_ACCEPT);
		}
		else
		{
			//The message was sent by a led strip and is an update request
			int id = jsonObject.getInt("id");
			int color[] = {mStrips.get(id).getRed(), mStrips.get(id).getGreen(), mStrips.get(id).getBlue()};
					
			JSONObject jsonResponse = new JSONObject();
			jsonResponse.put("id", id);
			jsonResponse.put("color", color);
			jsonResponse.put("status", mStrips.get(id).getStatus());
			jsonResponse.put("change", 1);
			
			dataTracking.put(channel, jsonResponse.toString().getBytes());
			key.interestOps(SelectionKey.OP_WRITE);
		}		
	}

	private void closeConnection() {
		System.out.println("Closing server down");
		if (selector != null) {
			try {
				selector.close();
				serverChannel.socket().close();
				serverChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void sleep(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}
}