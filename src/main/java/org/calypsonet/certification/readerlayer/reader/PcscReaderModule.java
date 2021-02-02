package org.calypsonet.certification.readerlayer.reader;

import org.calypsonet.certification.readerlayer.procedures.ContactProtocol;
import org.calypsonet.certification.readerlayer.procedures.ContactlessProtocol;
import org.eclipse.keyple.core.service.Reader;
import org.eclipse.keyple.core.service.SmartCardService;
import org.eclipse.keyple.plugin.pcsc.PcscPluginFactory;
import org.eclipse.keyple.plugin.pcsc.PcscReader;
import org.eclipse.keyple.plugin.pcsc.PcscSupportedContactProtocols;
import org.eclipse.keyple.plugin.pcsc.PcscSupportedContactlessProtocols;

/**
 * created on 2021-02-02
 *
 * @author youssefamrani
 */

public class PcscReaderModule extends IReaderModule
{
	private static final String DEFAULT_CARD_READER_NAME = "Identive CLOUD 2700 R Smart Card Reader 0";

	private PcscPluginFactory mPcscPluginFactory;
	private PcscReader mReader;

	public PcscReaderModule()
	{
		readerName = DEFAULT_CARD_READER_NAME;
	}

	@Override
	public void setReaderName(String readerName)
	{
		this.readerName = readerName;
	}

	@Override
	public String getReaderName()
	{
		return readerName;
	}

	@Override
	public void initPlugin()
	{
		if (mPcscPluginFactory == null)
		{
			mPcscPluginFactory = new PcscPluginFactory(null, null);
		}

		plugin = SmartCardService.getInstance().registerPlugin(mPcscPluginFactory);

	}

	@Override
	public void configureReader(String readerType)
	{
		if (isReaderTypeContactless(readerType))
		{
			// Get and configure a contactless reader
			mReader = (PcscReader) plugin.getReader(readerName);
			mReader.setContactless(true);
			mReader.setIsoProtocol(PcscReader.IsoProtocol.T1);
		}
		else
		{
			// Get and configure a contactless reader
			mReader = (PcscReader) plugin.getReader(readerName);
			mReader.setContactless(true);
			mReader.setIsoProtocol(PcscReader.IsoProtocol.T0);
		}
	}

	@Override
	public void activateProtocol(String cardProtocol)
	{
		// Activate protocols
		if (ContactlessProtocol.NFC_A_ISO_14443_3A.equals(cardProtocol))
		{
			mReader.activateProtocol(PcscSupportedContactlessProtocols.ISO_14443_4.name(), cardProtocol);
		}
		else if (ContactlessProtocol.NFC_A_ISO_14443_3B.equals(cardProtocol))
		{
			mReader.activateProtocol(PcscSupportedContactlessProtocols.ISO_14443_4.name(), cardProtocol);
		}
		else if (ContactProtocol.ISO_7816_3_T0.equals(cardProtocol))
		{
			mReader.activateProtocol(PcscSupportedContactProtocols.ISO_7816_3_T0.name(), cardProtocol);
		}
		else if (ContactProtocol.ISO_7816_3_T1.equals(cardProtocol))
		{
			mReader.activateProtocol(PcscSupportedContactProtocols.ISO_7816_3_T1.name(), cardProtocol);
		}
		else
		{
			throw new IllegalArgumentException("Protocol not supported by this PC/SC reader: " + cardProtocol);
		}
	}

	@Override
	public Reader getReader()
	{
		return mReader;
	}

	@Override
	public String getPluginName()
	{
		return plugin.getName();
	}
}
