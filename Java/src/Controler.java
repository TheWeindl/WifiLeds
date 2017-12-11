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

public class Controler implements Runnable {

	public final static String ADDRESS = "192.168.1.4";
	public final static int PORT = 5045;
	public final static long TIMEOUT = 10000;

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
			mStrips.add(new LedStrip());
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
			sleep(100);
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

		if (input.contains("Request update ID")) {

			String res[] = input.split("ID");
			int id = Integer.valueOf(res[res.length - 1].trim());

			if (id <= mStrips.size() && id >= 0) {
				response = "#" + mStrips.get(id - 1).getRed() + '#' + mStrips.get(id - 1).getGreen() + '#' + mStrips.get(id - 1).getBlue();
			}		

			dataTracking.put(channel, response.getBytes());
			key.interestOps(SelectionKey.OP_WRITE);
			
		//TODO: Add Registering 
			
		//TODO: Add Color setting for user client
			
		} else {
			System.out.println("Unknown Request");
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