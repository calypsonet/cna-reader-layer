package org.calypsonet.certification.readerlayer;

import org.calypsonet.certification.readerlayer.procedures.*;
import org.calypsonet.certification.readerlayer.reader.IReaderModule;
import org.calypsonet.certification.readerlayer.reader.PcscReaderModule;

public class RL16 {

    private static void Test() {
        String Reader_For_Test = "OMNIKEY CardMan 5x21-CL 0";
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
            Console.display("Ensure that the Upper Layer can select the polling scenario");

            /////////////////////////////////////////////
            Console.display("PRE CONDITIONS");
            /////////////////////////////////////////////

            Console.display("Initialize smart card service / register the Plugin");
            RLP.RL_P_UT_SetReaderName(Reader_For_Test);
            RLP.RL_P_UT_Initialization();

            Console.display("Initialize reader as contactless non observable, ISO_14443_3B");
            RLP.RL_P_UT_ReaderConfiguration(ReaderType.CONTACTLESS, ContactlessProtocol.NFC_A_ISO_14443_3A, true);
            RLP.RL_P_UT_ReaderConfiguration(ReaderType.CONTACTLESS, ContactlessProtocol.NFC_A_ISO_14443_3B, true);

            // Wait until the user is ready.
            Console.waitEnter("Press enter when ready...");

            /////////////////////////////////////////////
            Console.display("PROCEDURE");
            /////////////////////////////////////////////
            Console.display("Initialize a polling in single shot mode");
            RLP.RL_P_UT_PollingConfiguration("SINGLESHOT");
            Console.waitEnter("Press enter when ready and present a card to the reader...");
            CardAvailable = RLP.RL_P_UT_CheckCardPresence();
            if (CardAvailable) {
                Console.display("Card is detected for polling in single shot mode");

                Console.display("Initialize a polling mode in repeating");
                RLP.RL_P_UT_PollingConfiguration("REPEATING");
                Console.waitEnter("Press enter when ready and present a card to the reader...");
                CardAvailable = RLP.RL_P_UT_CheckCardPresence();
                if (CardAvailable) {
                    Console.display("Card is detected for polling in repeating mode");
                }
                else
                    ErrorMessage = "Failed to detect card with polling in repeating mode";
            }
            else
                ErrorMessage = "Failed to detect card with polling in single shot mode";
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