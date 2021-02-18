package org.calypsonet.certification.readerlayer;

import org.calypsonet.certification.readerlayer.procedures.Console;
import org.calypsonet.certification.readerlayer.procedures.ContactProtocol;
import org.calypsonet.certification.readerlayer.procedures.RLProcedures;
import org.calypsonet.certification.readerlayer.procedures.ReaderType;
import org.calypsonet.certification.readerlayer.reader.IReaderModule;
import org.calypsonet.certification.readerlayer.reader.PcscReaderModule;

public class RL32 {

  private static void Test() {
    String Reader_For_Test = "Broadcom Corp Contacted SmartCard 0";
    String CardATR = "";
    String ErrorMessage = "";
    Boolean CardAvailable = false;

    /*
     * Select plugin to certify
     */
    IReaderModule pluginModuleToCertify = new PcscReaderModule();
//	    IReaderModule pluginModuleToCertify = new StubReaderModule();

    RLProcedures RLP = new RLProcedures(pluginModuleToCertify);

    try {
      // Display test infos
      Console.displayTestName();

      Console.display("Ensure that the Reader Layer manage correctly the SW1SW2=6CXXh"
              + " status word when it receive a correct SW1SW2=6CXXh status word");
      Console.display("The Reader Layer receives the status word 0x6CXX in response"
              + " to a Case 2 command. The Reader Layer repeats the command with Le = XXh.");

      /////////////////////////////////////////////
      Console.display("PRE CONDITIONS");
      /////////////////////////////////////////////
      Console.display("Initialize smart card service / register the Plugin / SAM contact reader");
      RLP.RL_P_UT_SetReaderName(Reader_For_Test);
      RLP.RL_P_UT_Initialization();

      Console.display("Initialize reader as contact non observable, protocol T=0");
      RLP.RL_P_UT_ReaderConfiguration(ReaderType.CONTACT, ContactProtocol.ISO_7816_3_T0, false);

      // Wait until the user is ready.
      Console.waitEnter("Press enter when ready...");

      /////////////////////////////////////////////
      Console.display("PROCEDURE");
      /////////////////////////////////////////////
      CardAvailable = RLP.RL_P_UT_CheckCardPresence();
      if (CardAvailable) {
        Console.display("Card is detected in the reader: " + RLP.RL_P_UT_GetReaderName());
        CardATR = RLP.RL_P_UT_GetATR();
        Console.display("Card ATR = " + CardATR);

        Console.display("Send SAM Read parameters APDU command Case 2 to the SAM reader");
        Console.display("CLA: 80");
        Console.display("INS: BE");
        Console.display("P1: 00");
        Console.display("P2: A0");
        Console.display("Le: 00");
        RLP.RL_P_UT_SendAPDU("80 BE 00 A0 00", false);

        Console.display("Check the APDU response: expect 48 bytes and SW1-SW2 = 9000");
        RLP.RL_P_UT_CheckDataOutLen(48, "9000");
      }
      else
        ErrorMessage = "The card is not detected for the reader " +  RLP.RL_P_UT_GetReaderName();

      /////////////////////////////////////////////
      Console.display("POST CONDITIONS");
      /////////////////////////////////////////////

      Console.display("Unregister the plugin");
      RLP.RL_P_UT_Unregister();

      if (ErrorMessage != "")
        Console.notifyFailure(ErrorMessage);
      else
        Console.notifySuccess();

    } catch (Exception ex) {

      Console.notifyFailure(ex.getMessage());

    }
  }

  /**
   * main program entry
   * @param args not used
   */
  public static void main(String[] args) {
    Test();
  }
}
