package org.calypsonet.certification.readerlayer.reader;

import org.calypsonet.certification.readerlayer.procedures.ContactProtocol;
import org.calypsonet.certification.readerlayer.procedures.ContactlessProtocol;
import org.eclipse.keyple.core.service.Reader;
import org.eclipse.keyple.core.service.SmartCardService;
import org.eclipse.keyple.core.service.event.PluginObservationExceptionHandler;
import org.eclipse.keyple.core.service.event.ReaderObservationExceptionHandler;
import org.eclipse.keyple.plugin.pcsc.PcscSupportedContactProtocols;
import org.eclipse.keyple.plugin.pcsc.PcscSupportedContactlessProtocols;
import org.eclipse.keyple.plugin.stub.StubPluginFactory;
import org.eclipse.keyple.plugin.stub.StubReader;

/**
 * created on 2021-02-02
 *
 * @author youssefamrani
 */

public class StubReaderModule extends IReaderModule
{
	private static final String DEFAULT_CARD_READER_NAME = "Stub Reader";

	private StubPluginFactory mStubPluginFactory;
	private StubReader mReader;

	protected ObservationExceptionHandler observationExceptionHandler =
			new ObservationExceptionHandler();

	public StubReaderModule()
	{
		readerName = DEFAULT_CARD_READER_NAME;
	}

	@Override
	public String getReaderName()
	{
		return readerName;
	}

	@Override
	public void setReaderName(String readerName)
	{
		this.readerName = readerName;
	}

	@Override
	public void initPlugin()
	{
		if (mStubPluginFactory == null)
		{
			mStubPluginFactory = new StubPluginFactory(readerName, observationExceptionHandler, observationExceptionHandler);
		}

		plugin = SmartCardService.getInstance().registerPlugin(mStubPluginFactory);

	}

	@Override
	public void configureReader(String readerType)
	{
		if (isReaderTypeContactless(readerType))
		{
			// Get and configure a contactless reader
			mReader = (StubReader) plugin.getReader(readerName);
//			mReader.setContactless(true);
//			mReader.setIsoProtocol(PcscReader.IsoProtocol.T1);
		}
		else
		{
			// Get and configure a contactless reader
			mReader = (StubReader) plugin.getReader(readerName);
//			mReader.setContactless(true);
//			mReader.setIsoProtocol(PcscReader.IsoProtocol.T0);
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
			throw new IllegalArgumentException("Protocol not supported by this reader: " + cardProtocol);
		}
	}

	@Override
	public void deactivateProtocol(String cardProtocol) {
		// Deactivate protocols
		if ((ContactlessProtocol.NFC_A_ISO_14443_3A.equals(cardProtocol)) || (ContactlessProtocol.NFC_A_ISO_14443_3B.equals(cardProtocol)))
		{
			mReader.deactivateProtocol(PcscSupportedContactlessProtocols.ISO_14443_4.name());
		}
		else if (ContactProtocol.ISO_7816_3_T0.equals(cardProtocol))
		{
			mReader.deactivateProtocol(PcscSupportedContactProtocols.ISO_7816_3_T0.name());
		}
		else if (ContactProtocol.ISO_7816_3_T1.equals(cardProtocol))
		{
			mReader.deactivateProtocol(PcscSupportedContactProtocols.ISO_7816_3_T1.name());
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

	@Override
	public boolean checkcardpresence()
	{
		return mReader.isCardPresent();
	}


	public class ObservationExceptionHandler
			implements PluginObservationExceptionHandler, ReaderObservationExceptionHandler
	{
		@Override
		public void onPluginObservationError(String pluginName, Throwable e) {}

		@Override
		public void onReaderObservationError(String pluginName, String readerName, Throwable e) {}
	}
}
