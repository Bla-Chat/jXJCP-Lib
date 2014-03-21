package net.michaelfuerst.xjcp;

/**
 * A parser used to convert a serverreply to a {@link Message}.
 * 
 * @author TheDwoon
 * @version 1.0
 * @since 21.03.2014
 *
 */
public interface MessageParser {
	/**
	 * Converts the given string to a {@link Message}.
	 * 
	 * @param msg The message.
	 * @return The representation as a {@link Message}.
	 */
	Message parseMessage(String msg);
}