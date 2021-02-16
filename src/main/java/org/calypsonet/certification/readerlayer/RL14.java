package org.calypsonet.certification.readerlayer;

import org.calypsonet.certification.readerlayer.procedures.Console;
import org.calypsonet.certification.readerlayer.procedures.ContactlessProtocol;
import org.calypsonet.certification.readerlayer.procedures.RLProcedures;
import org.calypsonet.certification.readerlayer.procedures.ReaderType;
import org.calypsonet.certification.readerlayer.reader.IReaderModule;
import org.calypsonet.certification.readerlayer.reader.PcscReaderModule;

public class RL14 {

    private static void Test() {
        String Reader_For_Test = "OMNIKEY CardMan 5x21-CL 0" ;
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
            Console.display("Ensure that the Upper Layer can activate and deactivate the contactless"
                            + "layer ISO/IEC 14443 type A");

            /////////////////////////////////////////////
            Console.display("PRE CONDITIONS");
            /////////////////////////////////////////////
            Console.display("Initialize smart card service / register the Plugin / Contactless reader name");
            RLP.RL_P_UT_SetReaderName(Reader_For_Test);
            RLP.RL_P_UT_Initialization();

            // Wait until the user is ready.
            Console.waitEnter("Press enter when ready...");

            /////////////////////////////////////////////
            Console.display("PROCEDURE");
            /////////////////////////////////////////////

            Console.display("Initialize reader as contactless non observable, Contactless protocol Type A");
            RLP.RL_P_UT_ReaderConfiguration(ReaderType.CONTACTLESS, ContactlessProtocol.NFC_A_ISO_14443_3A, false);

            Console.waitEnter("Present a contactless card Type A in the field and press enter ...");
            CardAvailable = RLP.RL_P_UT_CheckCardPresence();
            if (CardAvailable) {
                Console.display("Card is detected for the reader: " + RLP.RL_P_UT_GetReaderName());
                CardATR = RLP.RL_P_UT_GetATR();
                Console.display("Card ATR = " + CardATR);
                Console.display("Deactivate Contactless protocol Type A for the reader: " + RLP.RL_P_UT_GetReaderName());
                RLP.RL_P_UT_DeactivateProtocol(ContactlessProtocol.NFC_A_ISO_14443_3A);
                CardAvailable = RLP.RL_P_UT_CheckCardPresence();
                if (!CardAvailable) {
                    Console.display("The protocol deactivation is successful and the card is not detected");
                    Console.display("Reactivate Contactless protocol Type A for the reader: " + RLP.RL_P_UT_GetReaderName());
                    RLP.RL_P_UT_ActivateProtocol(ContactlessProtocol.NFC_A_ISO_14443_3A);
                    CardAvailable = RLP.RL_P_UT_CheckCardPresence();
                    if (CardAvailable) {
                        Console.display("Card is detected for the reader: " + RLP.RL_P_UT_GetReaderName());
                        CardATR = "";
                        CardATR = RLP.RL_P_UT_GetATR();
                        Console.display("Card ATR = " + CardATR);
                        Console.display("Reactivation is successful");
                    }
                    else
                        ErrorMessage = ("Reactivation failed for the reader "+ RLP.RL_P_UT_GetReaderName());
                }
                else
                    ErrorMessage = ("The protocol deactivation is failed for the reader "+ RLP.RL_P_UT_GetReaderName());
                }
                else
                    ErrorMessage = "Communication with card failed on the reader "+ RLP.RL_P_UT_GetReaderName();

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
