package org.calypsonet.certification.readerlayer.reader;

import org.eclipse.keyple.core.service.Plugin;
import org.eclipse.keyple.core.service.Reader;
import org.eclipse.keyple.core.util.Assert;

/**
 * created on 2021-02-02
 *
 * @author youssefamrani
 */

public abstract class IReaderModule
{
	Plugin plugin;
	String readerName;

	public abstract void initPlugin();
	public abstract void configureReader(String readerType);
	public abstract void activateProtocol(String cardProtocol);
	public abstract void deactivateProtocol(String cardProtocol);
	public abstract boolean checkcardpresence();
	public abstract Reader getReader();
	public abstract String getReaderName();
	public abstract void setReaderName(String readerName);
	public abstract String getPluginName();


	/**
	 * Helper method to check the reader type provided as a case insensitive String.
	 *
	 * @param readerType A not null String containing the reader type "contactless" or "contact"
	 * @return true is the type is contactless
	 * @throws IllegalArgumentException if the argument is wrong
	 */
	boolean isReaderTypeContactless(String readerType) {
		boolean isContactless;
		Assert.getInstance().notNull(readerType, "readerType");
		if (readerType.equalsIgnoreCase("contactless")) {
			isContactless = true;
		} else if (readerType.equalsIgnoreCase("contact")) {
			isContactless = false;
		} else {
			throw new IllegalArgumentException("Unknown reader type: " + readerType);
		}
		return isContactless;
	}
}
