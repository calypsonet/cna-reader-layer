package org.calypsonet.certification.readerlayer;

import org.calypsonet.certification.readerlayer.procedures.Console;
import org.calypsonet.certification.readerlayer.procedures.ContactlessProtocol;
import org.calypsonet.certification.readerlayer.procedures.RLProcedures;
import org.calypsonet.certification.readerlayer.procedures.ReaderType;
import org.calypsonet.certification.readerlayer.reader.IReaderModule;
import org.calypsonet.certification.readerlayer.reader.PcscReaderModule;

public class RL22 {

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
            Console.display("Ensure that the Reader Layer do not use the ISO/IEC 14443-4 PICC presence check method");

            /////////////////////////////////////////////
            Console.display("PRE CONDITIONS");
            /////////////////////////////////////////////


            /////////////////////////////////////////////
            Console.display("PROCEDURE");
            /////////////////////////////////////////////
            Console.display("Initialize smart card service / register the Plugin / SAM contact reader");
            RLP.RL_P_UT_SetReaderName(Reader_For_Test);
            RLP.RL_P_UT_Initialization();

            Console.display("Initialize reader as contactless non observable, Contactless protocol");
            RLP.RL_P_UT_ReaderConfiguration(ReaderType.CONTACTLESS, ContactlessProtocol.NFC_A_ISO_14443_3A, true);

            // Wait until the user is ready.
            Console.waitEnter("Connect a spy tool between the card and the reader and press enter when ready...");

            CardAvailable = RLP.RL_P_UT_CheckCardPresence();
            if (CardAvailable) {
                Console.display("Card is detected for the reader: " + RLP.RL_P_UT_GetReaderName());
                CardATR = RLP.RL_P_UT_GetATR();

                RLP.RL_P_UT_FinalizeCardProcessing();
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
