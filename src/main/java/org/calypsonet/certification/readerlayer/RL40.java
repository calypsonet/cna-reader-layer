package org.calypsonet.certification.readerlayer;

import org.calypsonet.certification.readerlayer.procedures.Console;
import org.calypsonet.certification.readerlayer.procedures.ContactlessProtocol;
import org.calypsonet.certification.readerlayer.procedures.RLProcedures;
import org.calypsonet.certification.readerlayer.procedures.ReaderType;
import org.calypsonet.certification.readerlayer.reader.IReaderModule;
import org.calypsonet.certification.readerlayer.reader.PcscReaderModule;

public class RL40 {

    private static void Test() {
        String Reader_For_Test = "OMNIKEY CardMan 5x21-CL 0";
        String AID_For_Test =  "315449432E494341";
        String FCIValue = "";
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
            Console.display("Ensure that the Reader Layer send P2=02h & Lc between 05h and 10h for Select command");

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
                Console.display("Card is detected for the reader: " + RLP.RL_P_UT_GetReaderName());

                Console.display("Process a card selection using AID with length between 05h to 10h"
                        + "and P2 = 02h (Next Occurence) ");
                Console.display("AID: " + AID_For_Test);
                FCIValue = RLP.RL_P_UT_SmartCardSelection_With_Occurence(AID_For_Test, "NEXT");
                Console.display("FCI = " + FCIValue);
            }
            else
                ErrorMessage = "Card not detected for the reader "+ RLP.RL_P_UT_GetReaderName();

            /////////////////////////////////////////////
            Console.display("POST CONDITIONS");
            /////////////////////////////////////////////

            Console.display("Unregister the plugin");
            RLP.RL_P_UT_Unregister();

            if (ErrorMessage != "")
                Console.notifyFailure(ErrorMessage);
            else
                Console.notifySuccess();;

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
