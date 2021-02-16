package org.calypsonet.certification.readerlayer;

import org.calypsonet.certification.readerlayer.procedures.Console;
import org.calypsonet.certification.readerlayer.procedures.ContactProtocol;
import org.calypsonet.certification.readerlayer.procedures.RLProcedures;
import org.calypsonet.certification.readerlayer.procedures.ReaderType;
import org.calypsonet.certification.readerlayer.reader.IReaderModule;
import org.calypsonet.certification.readerlayer.reader.PcscReaderModule;

public class RL34 {

  private static void Test() {
    String Reader_For_Test = "OMNIKEY AG Smart Card Reader USB 0";
    String AID_For_Test =  "315449432E494341";
    String FCIValue ="";
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

      Console.display("Ensure that the Reader Layer manages correctly the Get Response"
              + " in case the PO send a SW1SW2=6200h status word on a Case 4 command.");
      Console.display("The Upper Layer provides a Case 4 command to the Reader Layer"
              + " and the PO reponse is SW1SW2=6200h instead of SW1SW2=61XXh.");
      Console.display("Test only available for terminal with contact reader for the PO");

      /////////////////////////////////////////////
      Console.display("PRE CONDITIONS");
      /////////////////////////////////////////////

      Console.display("Initialize smart card service / register the Plugin / PO contact reader");
      RLP.RL_P_UT_SetReaderName(Reader_For_Test);
      RLP.RL_P_UT_Initialization();

      Console.display("Initialize reader as contact non observable, protocol T=0");
      RLP.RL_P_UT_ReaderConfiguration(ReaderType.CONTACT, ContactProtocol.ISO_7816_3_T0, false);

      // Wait until the user is ready.
      Console.waitEnter("Insert a PO into the contactless reader and Press enter when ready...");

      /////////////////////////////////////////////
      Console.display("PROCEDURE");
      /////////////////////////////////////////////

      CardAvailable = RLP.RL_P_UT_CheckCardPresence();
      if (CardAvailable) {
        Console.display("Card is detected for the reader: " + RLP.RL_P_UT_GetReaderName());
        FCIValue = RLP.RL_P_UT_SmartCardSelection(AID_For_Test);
        Console.display("FCI = " + FCIValue);

        Console.display("Send a Read multiple records APDU command to the P0 reader - File SFI 09 from the profile");
        Console.display("CLA: 00");
        Console.display("INS: B3");
        Console.display("P1: 01");
        Console.display("P2: 4D");
        Console.display("Lc: 04");
        Console.display("DataField: 5402001D");
        Console.display("Le: E8");
        RLP.RL_P_UT_SendAPDU("00 B3 01 4D 04 5402001D E8", true);

        Console.display("Check the APDU response: expect 232 bytes and SW1-SW2 = 6200");
        RLP.RL_P_UT_CheckDataOutLen(232, "6200");
      }
      else
        ErrorMessage = "The PO is not detected for the reader " +  RLP.RL_P_UT_GetReaderName();

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
