package org.calypsonet.certification.readerlayer.procedures;

import org.calypsonet.certification.readerlayer.reader.IReaderModule;
import org.eclipse.keyple.core.card.message.ApduRequest;
import org.eclipse.keyple.core.card.message.CardRequest;
import org.eclipse.keyple.core.card.message.CardResponse;
import org.eclipse.keyple.core.card.message.CardSelectionResponse;
import org.eclipse.keyple.core.card.message.ChannelControl;
import org.eclipse.keyple.core.card.message.ProxyReader;
import org.eclipse.keyple.core.card.selection.AbstractCardSelection;
import org.eclipse.keyple.core.card.selection.AbstractSmartCard;
import org.eclipse.keyple.core.card.selection.CardSelectionsResult;
import org.eclipse.keyple.core.card.selection.CardSelectionsService;
import org.eclipse.keyple.core.card.selection.CardSelector;
import org.eclipse.keyple.core.service.SmartCardService;
import org.eclipse.keyple.core.util.Assert;
import org.eclipse.keyple.core.util.ByteArrayUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Define the Reader Layer test procedures.<p>
 * All public procedures raise a {@link RuntimeException} in case of an error.
 */
public class RLProcedures {
  private SmartCardService smartCardService;
  private CardSelectionsResult cardSelectionsResult;
  private CardSelectionsService cardSelectionsService;
  private CardResponse cardResponse;

  private IReaderModule mReaderModule;

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
  public RLProcedures(IReaderModule readerModule) {
	  mReaderModule = readerModule;
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
    mReaderModule.setReaderName(readerName);
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
    mReaderModule.initPlugin();
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
	mReaderModule.configureReader(readerType);
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
	  mReaderModule.activateProtocol(cardProtocol);
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
            cardSelectionsService.processExplicitSelections(mReaderModule.getReader());
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
        ((ProxyReader) mReaderModule.getReader())
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
    smartCardService.unregisterPlugin(mReaderModule.getPluginName());
  }
}

