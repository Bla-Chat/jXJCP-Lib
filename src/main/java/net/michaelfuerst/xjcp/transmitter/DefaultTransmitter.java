package net.michaelfuerst.xjcp.transmitter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.michaelfuerst.xjcp.Handler;
import net.michaelfuerst.xjcp.Message;
import net.michaelfuerst.xjcp.MessageParser;
import net.michaelfuerst.xjcp.connection.Connection;
import net.michaelfuerst.xjcp.device.Device;

/**
 * This class transmits messages whenever it has access to the interntet.
 * 
 * @author TheDwoon
 * @version 1.0
 * @since 23.03.2014
 *
 */
public final class DefaultTransmitter implements Transmitter {
	private static final int SLEEP = 1000;
	
	private final MessageParser parser;
	private final Connection connection;
	private final List<Entry> queue;
	private final Worker worker;
	
	/**
	 * Creates a new DefaultTransmitter.
	 * 
	 * @param connection
	 * @param parser
	 */
	public DefaultTransmitter(final Connection connection, final MessageParser parser) {
		this.parser = parser;
		this.connection = connection;
		this.queue = Collections.synchronizedList(new LinkedList<Entry>());
		
		this.worker = new Worker(connection.getDevice());
	}
	
	@Override
	public void submit(String message, Handler handler) {
		queue.add(new Entry(message, handler));
	}

	@Override
	public void start() {
		worker.start();
	}

	@Override
	public void stop() {
		worker.interrupt();
	}

	private class Worker extends Thread {
		private final Device device;
		
		private Worker(Device device) {
			this.device = device;
		}
		
		@Override
		public void run() {			
			while (!isInterrupted()) {
				//Wait until we have WLAN or LAN and something to send.
				while (!device.hasInternet() || queue.isEmpty()) {
					try {
						Thread.sleep(SLEEP);
					} catch (InterruptedException e) {						
						return;
					}
				}
				
				Entry entry = queue.remove(0);
				String response = connection.send(entry.getMessage());
				List<Message> messages = parser.parseMessage(response);
				entry.invokeHandler(messages);
			}
		}
	}
}