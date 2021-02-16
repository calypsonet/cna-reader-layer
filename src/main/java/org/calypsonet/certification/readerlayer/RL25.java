package org.calypsonet.certification.readerlayer;

import org.calypsonet.certification.readerlayer.procedures.*;
import org.calypsonet.certification.readerlayer.reader.IReaderModule;
import org.calypsonet.certification.readerlayer.reader.PcscReaderModule;

public class RL25 {

    private static void Test() {
        String Reader_For_Test = "OMNIKEY CardMan 5x21-CL 0";
        String AID_For_Test =  "315449432E494341";
        String FCIValue ="";
        String ErrorMessage = "";
        String CardATR = "";
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
            Console.display("Ensure that any CLA for all APDU commands provided by the Upper layer is accepted");

            /////////////////////////////////////////////
            Console.display("PRE CONDITIONS");
            /////////////////////////////////////////////
            Console.display("Initialize smart card service / register the Plugin / Contactless reader name");
            RLP.RL_P_UT_SetReaderName(Reader_For_Test);
            RLP.RL_P_UT_Initialization();

            Console.display("Initialize reader as contactless non observable, Contactless protocol");
            RLP.RL_P_UT_ReaderConfiguration(ReaderType.CONTACTLESS, ContactlessProtocol.NFC_A_ISO_14443_3B, false);

            // Wait until the user is ready.
            Console.waitEnter("Press enter when ready...");

            /////////////////////////////////////////////
            Console.display("PROCEDURE");
            /////////////////////////////////////////////

            CardAvailable = RLP.RL_P_UT_CheckCardPresence();
            if (CardAvailable) {
                Console.display("Card is detected for the reader: " + RLP.RL_P_UT_GetReaderName());
                FCIValue = RLP.RL_P_UT_SmartCardSelection(AID_For_Test);
                Console.display("FCI = " + FCIValue);
                for (int int_CLA = 1; int_CLA<=255; int_CLA++) {
                    if (int_CLA != 148)  {    // All CLA between 01 and FF except 00 and 94
                        String CLA = String.format("%X", int_CLA);
                        if (CLA.length() == 1)
                        {
                            CLA = "0" + CLA;
                        }
                        String APDU = CLA + " A4 02 00 02 2001 19";
                        Console.display("Send APDU " + APDU);
                        RLP.RL_P_UT_SendAPDU(APDU, true);
                        RLP.RL_P_UT_CheckSW1SW2("6E00");
                    }
                }
            }
            else
                ErrorMessage = "Card not found for the reader "+ RLP.RL_P_UT_GetReaderName();

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
