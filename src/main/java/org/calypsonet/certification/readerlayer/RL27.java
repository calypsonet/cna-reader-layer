package org.calypsonet.certification.readerlayer;

import org.calypsonet.certification.readerlayer.procedures.*;
import org.calypsonet.certification.readerlayer.reader.IReaderModule;
import org.calypsonet.certification.readerlayer.reader.PcscReaderModule;

public class RL27 {

    private static void Test() {
        String Reader_For_Test = "OMNIKEY CardMan 5x21-CL 0";
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
            Console.display("Ensure that the success Status Word is not analysed by the Reader Layer.");

            /////////////////////////////////////////////
            Console.display("PRE CONDITIONS");
            /////////////////////////////////////////////

            Console.display("Initialize smart card service / register the Plugin");
            RLP.RL_P_UT_SetReaderName(Reader_For_Test);
            RLP.RL_P_UT_Initialization();

            Console.display("Initialize reader as contactless non observable, ISO_14443_3B");
            RLP.RL_P_UT_ReaderConfiguration(ReaderType.CONTACTLESS, ContactlessProtocol.NFC_A_ISO_14443_3B, false);

            // Wait until the user is ready.
            Console.waitEnter("Press enter when ready...");

            /////////////////////////////////////////////
            Console.display("PROCEDURE");
            /////////////////////////////////////////////

            CardAvailable = RLP.RL_P_UT_CheckCardPresence();
            if (CardAvailable) {
                Console.display("Card is detected in the reader: " + RLP.RL_P_UT_GetReaderName());

                Console.display("Process a card selection using the AID for the test");
                RLP.RL_P_UT_SmartCardSelection(AID_For_Test);
                FCIValue = RLP.RL_P_UT_SmartCardSelection(AID_For_Test);
                Console.display("FCI = " + FCIValue);

                Console.display("Send a Read Records command to the reader with parameters indicated an available"
                        + "record - Environment file SFI 07 record 1 from the reference profile");
                Console.display("CLA: 00");
                Console.display("INS: B2");
                Console.display("P1: 01");
                Console.display("P2: 3C (SFI*8+4)");
                Console.display("Le: 1D");
                RLP.RL_P_UT_SendAPDU("00 B2 01 3C 1D", false);

                Console.display("Check the APDU response: data out size = 29 bytes and SW1-SW2 = 9000");
                RLP.RL_P_UT_CheckDataOutLen(29, "9000");

                Console.display("Send an Open Secure Session command in compatibility mode");
                Console.display("CLA: 00");
                Console.display("INS: 8A");
                Console.display("P1: 81");
                Console.display("P2: 00");
                Console.display("Lc: 04");
                Console.display("Data In Fields: 01020304");
                Console.display("Le: 05");
                RLP.RL_P_UT_SendAPDU("00 8A 81 00 04 01020304 05", true);

                Console.display("Check the APDU response: data out size = 5 bytes and SW1-SW2 = 90000");
                RLP.RL_P_UT_CheckDataOutLen(5, "9000");
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
