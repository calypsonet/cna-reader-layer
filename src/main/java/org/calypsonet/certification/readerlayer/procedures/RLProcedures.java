package org.calypsonet.certification.readerlayer.procedures;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.keyple.core.card.message.*;
import org.eclipse.keyple.core.card.selection.*;
import org.eclipse.keyple.core.service.Plugin;
import org.eclipse.keyple.core.service.Reader;
import org.eclipse.keyple.core.service.SmartCardService;
import org.eclipse.keyple.core.util.Assert;
import org.eclipse.keyple.core.util.ByteArrayUtil;
import org.eclipse.keyple.plugin.pcsc.PcscPluginFactory;
import org.eclipse.keyple.plugin.pcsc.PcscReader;
import org.eclipse.keyple.plugin.pcsc.PcscSupportedContactProtocols;
import org.eclipse.keyple.plugin.pcsc.PcscSupportedContactlessProtocols;

/**
 * Define the Reader Layer test procedures.<p>
 * All public procedures raise a {@link RuntimeException} in case of an error.
 */
public class RLProcedures {
  private static final String DEFAULT_CARD_READER_NAME =
      "Identive CLOUD 2700 R Smart Card Reader 0";
  private String readerName;
  private SmartCardService smartCardService;
  private Plugin plugin;
  private Reader reader;
  private CardSelectionsResult cardSelectionsResult;
  private CardSelectionsService cardSelectionsService;
  private CardResponse cardResponse;

  /** Create a new class extending AbstractCardSelection */
  public final class GenericCardSelection extends AbstractCardSelection {
    public GenericCardSelection(CardSelector cardSelector) {
      super(cardSelector);
    }

    @Override
    protected AbstractSmartCard parse(CardSelectionResponse cardSelectionResponse) {
      class GenericSmartCard extends AbstractSmartCard {
        public GenericSmartCard(CardSelectionResponse cardSelectionResponse) {
          super(cardSelectionResponse);
        }

        public String toJson() {
          return "{}";
        }
      }
      return new GenericSmartCard(cardSelectionResponse);
    }
  }

  /**
   * (private)
   */
  public RLProcedures() {
    readerName = DEFAULT_CARD_READER_NAME;
  }

  /**
   * Helper method to check the reader type provided as a case insensitive String.
   *
   * @param readerType A not null String containing the reader type "contactless" or "contact"
   * @return true is the type is contactless
   * @throws IllegalArgumentException if the argument is wrong
   */
  private boolean isReaderTypeContactless(String readerType) {
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

  /*
  Description: Sets the reader name
  Note: overwrites the default value.
  */
  public void RL_P_UT_SetReaderName(String readerName) {
    this.readerName = readerName;
  }

  /*
  Description: Register the plugin
  Create:
  - SmartCardService smartCardService
  - Plugin plugin
  */
  public void RL_P_UT_Initialization() {
    smartCardService = SmartCardService.getInstance();

    // Register the Plugin with SmartCardService, get the corresponding generic Plugin in return
    plugin = SmartCardService.getInstance().registerPlugin(new PcscPluginFactory(null, null));
  }

  /*
  Description: Get and configure the reader.
  Use the protocols names declared in the ICS for the activation of the protocols supported
  Create:
  - Reader reader
  Parameters:
  - readerType: String "contactless" or "contact" to define the reader type
  - Protocol: ISO protocol
  - Observable: true if Reader Observer available, false if not
  Call:
  - RL_P_UT_Observable()
  */
  public void RL_P_UT_ReaderConfiguration(String readerType, String protocol, boolean observable) {
    if (isReaderTypeContactless(readerType)) {
      // Get and configure a contactless reader
      reader = plugin.getReader(readerName);
      ((PcscReader) reader).setContactless(true);
      ((PcscReader) reader).setIsoProtocol(PcscReader.IsoProtocol.T1);
    } else {
      // Get and configure a contactless reader
      reader = plugin.getReader(readerName);
      ((PcscReader) reader).setContactless(true);
      ((PcscReader) reader).setIsoProtocol(PcscReader.IsoProtocol.T0);
    }

    RL_P_UT_ActivateProtocol(protocol);

    // Observer Reader
    if (observable) {
      RL_P_UT_Observable();
    }
  }

  /*
  Description: Activate a protocol
  Use:
  - Reader reader
  Parameters:
  - ReaderProtocol: String ISO Protocol
  */
  void RL_P_UT_ActivateProtocol(String cardProtocol) {
    // Activate protocols
    if (ContactlessProtocol.NFC_A_ISO_14443_3A.equals(cardProtocol)) {
      reader.activateProtocol(PcscSupportedContactlessProtocols.ISO_14443_4.name(), cardProtocol);
    } else if (ContactlessProtocol.NFC_A_ISO_14443_3B.equals(cardProtocol)) {
      reader.activateProtocol(PcscSupportedContactlessProtocols.ISO_14443_4.name(), cardProtocol);
    } else if (ContactProtocol.ISO_7816_3_T0.equals(cardProtocol)) {
      reader.activateProtocol(PcscSupportedContactProtocols.ISO_7816_3_T0.name(), cardProtocol);
    } else if (ContactProtocol.ISO_7816_3_T1.equals(cardProtocol)) {
      reader.activateProtocol(PcscSupportedContactProtocols.ISO_7816_3_T1.name(), cardProtocol);
    } else {
      throw new IllegalArgumentException(
          "Protocol not supported by this PC/SC reader: " + cardProtocol);
    }
  }

  /*
  Description: Add an observer reader
  Use:
  - Reader reader
  Configuration:
  - Polling: REPEATING
  */
  void RL_P_UT_Observable() {
    // Add an observer
    //	((ObservableReader) reader).addObserver(new CardReaderObserver());
    //	((ObservableReader) reader).startCardDetection(ObservableReader.PollingMode.REPEATING);
  }

  /*
  Description: Select a DF with an AID. The logical channel is open after the selection.
  Create:
  - CardSelectionsService cardSelectionsService
  - CardSelectionsResult cardSelectionsResult
  Parameters:
  - smartCardAID: String AID, AID to select
  */
  public void RL_P_UT_SmartCardSelection(String smartCardAID)
  {
    // Prepare the card selection
    cardSelectionsService = new CardSelectionsService();

    // first selection case targeting cards with AID1
    GenericCardSelection cardSelection =
            new GenericCardSelection(
                    CardSelector.builder()
                            .aidSelector(CardSelector.AidSelector.builder().aidToSelect(smartCardAID).build())
                            .build());

    // Add the selection case to the current selection
    cardSelectionsService.prepareSelection(cardSelection);

    // Actual card communication: operate through a single request the card selection
    CardSelectionsResult cardSelectionsResult =
            cardSelectionsService.processExplicitSelections(reader);
  }

  /*
  Description: Sends an APDU with specified the case4 flag
  Create:
  - CardResponse cardResponse
  Use:
  - Reader reader
  */
  public void RL_P_UT_SendAPDU(String apdu, boolean case4) {
    List<ApduRequest> apduRequestList = new ArrayList<ApduRequest>();
    apduRequestList.add(new ApduRequest(ByteArrayUtil.fromHex(apdu), case4));
    CardRequest cardRequest = new CardRequest(apduRequestList);
    System.out.println(cardRequest.getApduRequests().toString());
    cardResponse =
        ((ProxyReader) reader)
            .transmitCardRequest(cardRequest, ChannelControl.CLOSE_AFTER);
  }

  /*
  Description: Checks the last SW1-SW2 received.
  Use:
  - CardResponse cardResponse
  */
  public void RL_P_UT_CheckDataOutLen(int expectedDataOutLen, String expectedSW1SW2) {
    int expectedNumberLen = expectedDataOutLen*2;
    String regularExpression = "^[0-9a-fA-F]{"+ String.valueOf(expectedNumberLen) + "}" + expectedSW1SW2 + "$";
    System.out.println("Regular Expression:" + regularExpression);
    Pattern pattern = Pattern.compile(regularExpression);
    System.out.println(cardResponse.getApduResponses().toString());
    Matcher expressionMatcher =
            pattern.matcher(ByteArrayUtil.toHex(cardResponse.getApduResponses().get(0).getBytes()));
    Assert.getInstance().isTrue(expressionMatcher.matches(), "expressionMatcher");
  }

  /*
  Description: Checks the last SW1-SW2 received.
  Use:
  - CardResponse cardResponse
  */
  public void RL_P_UT_CheckSW1SW2(String expectedSW1SW2) {
    String regularExpression = "[0-9a-fA-F]{0}.*" + expectedSW1SW2 + "$";
    Pattern pattern = Pattern.compile(regularExpression);
    System.out.println("Regular Expression:" + regularExpression);
    System.out.println(cardResponse.getApduResponses().toString());
    Matcher expressionMatcher =
            pattern.matcher(ByteArrayUtil.toHex(cardResponse.getApduResponses().get(0).getBytes()));
    Assert.getInstance().isTrue(expressionMatcher.matches(), "expressionMatcher");
  }

  /*
  Description: Unregister the plugin
  Use:
  - SmartCardService smartCardService
  */
  public void RL_P_UT_Unregister() {
    // unregister plugin
    smartCardService.unregisterPlugin(plugin.getName());
  }
}

