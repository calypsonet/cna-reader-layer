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

import javax.swing.text.html.HTMLDocument;

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

 /*
  Description: Sets the reader name
  Note: overwrites the default value.
  */
  public void RL_P_UT_SetReaderName(String readerName) {
    mReaderModule.setReaderName(readerName);
  }

  /*
  Description: Get the current reader name
  */
  public String RL_P_UT_GetReaderName ()
  {
    String ReaderName = "";
     ReaderName = String.valueOf(mReaderModule.getReaderName());
     return ReaderName;
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
        //((ObservableReader) mReaderModule).vaddObserver(new ReaderObserver());
    }
  }

  /*
  Description: Activate a protocol
  Use:
  - Reader reader
  Parameters:
  - ReaderProtocol: String ISO Protocol
  */
  public void RL_P_UT_ActivateProtocol(String cardProtocol) {
	  // Activate protocols
	  mReaderModule.activateProtocol(cardProtocol);
  }

  /*
  Description: Deactivate a protocol
  Use:
  - Reader reader
  Parameters:
  - ReaderProtocol: String ISO Protocol
  */
 public void RL_P_UT_DeactivateProtocol(String cardProtocol) {
    // Deactivate protocols
     mReaderModule.deactivateProtocol(cardProtocol);
 }

  /*
  Description: Polling Configuration
  Use:
  - Reader reader
    Configuration:
  - For observable readers

  */
 public void RL_P_UT_PollingConfiguration(String PollingMode)
  {
    //((ObservableReader) mReaderModule).startCardDetection(ObservableReader.PollingMode.valueOf(PollingMode));
  }

    /*
  Description: Finalize the card processing
  Use:
  - Reader reader
  Configuration:
  - For observable readers
  */
  public void RL_P_UT_FinalizeCardProcessing()
  {
      //((ObservableReader) mReaderModule).finalizeCardProcessing();
  }

  /*
Description: Verify a card is present. Return true if a card is present, false otherwise.
Use:
- reader
*/
  public boolean RL_P_UT_CheckCardPresence()
  {
   return mReaderModule.checkcardpresence();
  }


    public void RL_P_UT_CheckCardDetected() {
        System.out.println("Check card presence for the reader " + RL_P_UT_GetReaderName());
        Assert.getInstance().isTrue(mReaderModule.checkcardpresence(), "The card is detected for the reader"+ RL_P_UT_GetReaderName());
        System.out.println("Confirm that the card is detected for the reader " + RL_P_UT_GetReaderName());
    }

    public void RL_P_UT_CheckCardNotDetected() {
        boolean ExpectedNotDetected = false;
        System.out.println("Check card presence for the reader " + RL_P_UT_GetReaderName());
        Pattern pattern = Pattern.compile(String.valueOf(ExpectedNotDetected));
        Matcher expressionMatcher =
                pattern.matcher(String.valueOf(mReaderModule.checkcardpresence()));
        Assert.getInstance().isTrue(expressionMatcher.matches(), "The card is not detected for the reader " + RL_P_UT_GetReaderName());
        System.out.println("Confirm that the card is not detected for the reader" + RL_P_UT_GetReaderName());
    }

  /*
Description: Return the ATR of the card
  Create:
  - CardSelectionsService cardSelectionsService
  - CardSelectionsResult cardSelectionsResult
  Parameters:
  - Return card ATR value
*/
  public String RL_P_UT_GetATR()
   {
     String ATR ="";
     cardSelectionsService = new CardSelectionsService();

     // Card selection case without AID to avoid a select of the application
     GenericCardSelection cardSelection =
             new GenericCardSelection(
                     CardSelector.builder()
                             .build());

     // Add the selection case to the current selection
     cardSelectionsService.prepareSelection(cardSelection);

     // Actual card communication: operate through a single request the card selection
     CardSelectionsResult cardSelectionsResult =
             cardSelectionsService.processExplicitSelections(mReaderModule.getReader());
     AbstractSmartCard smartCard = cardSelectionsResult.getActiveSmartCard();
     ATR = ByteArrayUtil.toHex(smartCard.getAtrBytes());
     return ATR;
   }

  /*
  Description: Select a DF with an AID. The logical channel is open after the selection.
  Create:
  - CardSelectionsService cardSelectionsService
  - CardSelectionsResult cardSelectionsResult
  Parameters:
  - smartCardAID: String AID, AID to select
  - Return FCI value
  */
  public String RL_P_UT_SmartCardSelection(String smartCardAID)
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
      AbstractSmartCard smartCard = cardSelectionsResult.getActiveSmartCard();
      String FCIResult = ByteArrayUtil.toHex(smartCard.getFciBytes());
      return FCIResult;
  }

    /*
  Description: Select a DF with an AID. The logical channel is open after the selection.
  Create:
  - CardSelectionsService cardSelectionsService
  - CardSelectionsResult cardSelectionsResult
  Parameters:
  - smartCardAID: String AID, AID to select
  - Return FCI value
  */
    public String RL_P_UT_SmartCardSelection_With_Occurence(String smartCardAID, String OccValue)
    {
        // Prepare the card selection
        cardSelectionsService = new CardSelectionsService();

        // first selection case targeting cards with AID1
        GenericCardSelection cardSelection =
                new GenericCardSelection(
                        CardSelector.builder()
                                .aidSelector(
                                        CardSelector.AidSelector.builder()
                                                .aidToSelect(smartCardAID)
                                                .fileOccurrence(CardSelector.AidSelector.FileOccurrence.valueOf(OccValue))
                                                .build())
                                .build());

        // Add the selection case to the current selection
        cardSelectionsService.prepareSelection(cardSelection);

        // Actual card communication: operate through a single request the card selection
        CardSelectionsResult cardSelectionsResult =
                cardSelectionsService.processExplicitSelections(mReaderModule.getReader());
        AbstractSmartCard smartCard = cardSelectionsResult.getActiveSmartCard();

        String FCIResult = ByteArrayUtil.toHex(smartCard.getFciBytes());
        return FCIResult;
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

