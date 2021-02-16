package org.calypsonet.certification.readerlayer;

import org.calypsonet.certification.readerlayer.procedures.Console;
import org.calypsonet.certification.readerlayer.procedures.ContactlessProtocol;
import org.calypsonet.certification.readerlayer.procedures.RLProcedures;
import org.calypsonet.certification.readerlayer.procedures.ReaderType;
import org.calypsonet.certification.readerlayer.reader.IReaderModule;
import org.calypsonet.certification.readerlayer.reader.PcscReaderModule;

public class RL48 {

    private static void Test() {
        String Reader_For_Test = "OMNIKEY CardMan 5x21-CL 0";
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
            Console.display("Ensure that the Upper Layer can activate and deactivate a protocol"
                            + "without impacting the other protocols supported");

            /////////////////////////////////////////////
            Console.display("PRE CONDITIONS");
            /////////////////////////////////////////////
            Console.display("Initialize smart card service / register the Plugin / Contactless reader name");
            RLP.RL_P_UT_SetReaderName(Reader_For_Test);
            RLP.RL_P_UT_Initialization();

            Console.display("Initialize reader as contactless non observable and Contactless protocol Type A & B");
            RLP.RL_P_UT_ReaderConfiguration(ReaderType.CONTACTLESS, ContactlessProtocol.NFC_A_ISO_14443_3A, false);
            RLP.RL_P_UT_ReaderConfiguration(ReaderType.CONTACTLESS, ContactlessProtocol.NFC_A_ISO_14443_3B, false);

            Console.waitEnter("Present a Card type A in the field and press enter when ready...");
            CardAvailable = RLP.RL_P_UT_CheckCardPresence();
            if (CardAvailable)
                Console.display("Card Type A detected by the reader");
            else
                Console.notifyFailure("Card Detection in Type A is failed");

            Console.waitEnter("Remove card and present now a Card type B in the field and press enter when ready...");
            CardAvailable = RLP.RL_P_UT_CheckCardPresence();
            if (CardAvailable)
                Console.display("Card Type B detected by the reader");
            else
                Console.notifyFailure("Card Detection in Type B is failed");

            // Wait until the user is ready.
            Console.waitEnter("Press enter to start the test procedure...");

            /////////////////////////////////////////////
            Console.display("PROCEDURE");
            /////////////////////////////////////////////

            Console.display("Deactivation Contactless protocol Type A for the reader: " + RLP.RL_P_UT_GetReaderName());
            RLP.RL_P_UT_DeactivateProtocol(ContactlessProtocol.NFC_A_ISO_14443_3A);

            Console.waitEnter("Present a contactless card Type A in the field and press enter to continue ...");
            CardAvailable = RLP.RL_P_UT_CheckCardPresence();
            if (!CardAvailable) {
                Console.display("The protocol Type A deactivation is successful and the card is not detected");

                Console.waitEnter("Present now a contactless card in Type B and press enter ...");
                CardAvailable = RLP.RL_P_UT_CheckCardPresence();
                if (CardAvailable) {
                    CardATR = RLP.RL_P_UT_GetATR();
                    Console.display("Card ATR = " + CardATR);
                    Console.display("Protocol Type B is still active for the reader " + RLP.RL_P_UT_GetReaderName());

                    Console.waitEnter("Press Enter to continue ...");
                    CardATR = "";
                    Console.display("Protocol Type A reactivation for the reader " + RLP.RL_P_UT_GetReaderName());
                    RLP.RL_P_UT_ActivateProtocol(ContactlessProtocol.NFC_A_ISO_14443_3A);
                    Console.waitEnter("Present now a card in Type A and press enter ...");
                    CardAvailable = RLP.RL_P_UT_CheckCardPresence();
                    if (CardAvailable) {
                        CardATR = RLP.RL_P_UT_GetATR();
                        Console.display("Card ATR = " + CardATR);
                        Console.display("Protocol Type A is active again for the reader " + RLP.RL_P_UT_GetReaderName());

                        Console.waitEnter("Press Enter to continue ...");
                        Console.display("Deactivation Contactless protocol Type B for the reader: " + RLP.RL_P_UT_GetReaderName());
                        RLP.RL_P_UT_DeactivateProtocol(ContactlessProtocol.NFC_A_ISO_14443_3B);

                        Console.waitEnter("Present a contactless card Type B in the field and press enter to continue ...");
                        CardAvailable = RLP.RL_P_UT_CheckCardPresence();
                        if (!CardAvailable) {
                            Console.display("The protocol Type B deactivation is successful and the card is not detected");

                            Console.waitEnter("Present now a contactless card in Type A and press enter ...");
                            CardATR = "";
                            CardAvailable = RLP.RL_P_UT_CheckCardPresence();
                            if (CardAvailable) {
                                CardATR = RLP.RL_P_UT_GetATR();
                                Console.display("Card ATR = " + CardATR);
                                Console.display("Protocol Type A is still active for the reader " + RLP.RL_P_UT_GetReaderName());
                            } else
                                ErrorMessage = "Failed to detect a card in type A after protocol Type B Deactivation";
                        }
                        else
                            ErrorMessage = "Card Type B detected - Deactivation protocol type B failed";
                    }
                    else
                        ErrorMessage = "Protocol Type A reactivation is failed";
                }
                else
                        ErrorMessage = "Failed to detect a card in type B after protocol Type A deactivation";
            }
            else
               ErrorMessage = "Card Type A detected - Deactivation protocol type A is failed";

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
