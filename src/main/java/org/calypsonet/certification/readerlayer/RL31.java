package org.calypsonet.certification.readerlayer;

import org.calypsonet.certification.readerlayer.procedures.Console;
import org.calypsonet.certification.readerlayer.procedures.ContactProtocol;
import org.calypsonet.certification.readerlayer.procedures.ReaderType;
import org.calypsonet.certification.readerlayer.procedures.RLProcedures;

public class RL31 {

  private static void Test() {
    RLProcedures RLP = new RLProcedures();

    try {
      // Display test infos
      Console.displayTestName();
      Console.display("Ensure that the Reader Layer manage correctly the SW1SW2=6CXXh"
              + " status word when it receive a correct SW1SW2=6CXXh status word.");

      /////////////////////////////////////////////
      Console.display("PRE CONDITIONS");
      /////////////////////////////////////////////

      Console.display("Initialize smart card service / register the Plugin / SAM Reader");
      RLP.RL_P_UT_SetReaderName("Identive CLOUD 2700 R Smart Card Reader 0");
      RLP.RL_P_UT_Initialization();

      Console.display("Initialize reader as contact non observable, protocol T=0");
      RLP.RL_P_UT_ReaderConfiguration(ReaderType.CONTACT, ContactProtocol.ISO_7816_3_T0, false);

      // Wait until the user is ready.
      Console.waitEnter("Press enter when ready...");

      /////////////////////////////////////////////
      Console.display("PROCEDURE");
      /////////////////////////////////////////////

      Console.display("Send SAM Read parameters APDU command Case 2 to the SAM reader");
      Console.display("CLA: 80");
      Console.display("INS: BE");
      Console.display("P1: 00");
      Console.display("P2: A0");
      Console.display("Le: 00");
      RLP.RL_P_UT_SendAPDU("80 BE 00 A0 00", true);

      Console.display("Check the APDU response: expect at least one byte (2 digits) and SW1-SW2 = 9000");
      RLP.RL_P_UT_CheckSW1SW2("9000");

      /////////////////////////////////////////////
      Console.display("POST CONDITIONS");
      /////////////////////////////////////////////

      Console.display("Unregister the plugin");
      RLP.RL_P_UT_Unregister();

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
