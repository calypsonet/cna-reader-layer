package org.calypsonet.certification.readerlayer;

import org.calypsonet.certification.readerlayer.procedures.Console;
import org.calypsonet.certification.readerlayer.procedures.ContactProtocol;
import org.calypsonet.certification.readerlayer.procedures.RLProcedures;
import org.calypsonet.certification.readerlayer.procedures.ReaderType;
import org.calypsonet.certification.readerlayer.reader.IReaderModule;
import org.calypsonet.certification.readerlayer.reader.PcscReaderModule;

public class RL20 {

    private static void Test() {
        String Reader_For_Test = "Broadcom Corp Contacted SmartCard 0";
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
            Console.display("Ensure that the Reader Layer is able to share the ATR bytes with the upper layer");

            /////////////////////////////////////////////
            Console.display("PRE CONDITIONS");
            /////////////////////////////////////////////
            Console.display("Insert a SAM into a SAM slot or connect a simulator");
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
            }
            else
                ErrorMessage = "Card not found in the reader "+ RLP.RL_P_UT_GetReaderName();

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
