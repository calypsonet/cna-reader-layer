package org.calypsonet.certification.readerlayer;

        import org.calypsonet.certification.readerlayer.procedures.*;
        import org.calypsonet.certification.readerlayer.reader.IReaderModule;
        import org.calypsonet.certification.readerlayer.reader.PcscReaderModule;

public class RL15 {

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
            Console.display("Ensure that the Upper Layer can activate and deactivate the contactless"
                            + "layer ISO/IEC 14443 type B");

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

            Console.display("Initialize reader as contactless non observable, Contactless protocol Type B");
            RLP.RL_P_UT_ReaderConfiguration(ReaderType.CONTACTLESS, ContactlessProtocol.NFC_A_ISO_14443_3B, false);

            Console.waitEnter("Present a contactless card Type B in the field and press enter ...");
            CardAvailable = RLP.RL_P_UT_CheckCardPresence();
            if (CardAvailable) {
                Console.display("Card is detected for the reader: " + RLP.RL_P_UT_GetReaderName());
                CardATR = RLP.RL_P_UT_GetATR();
                Console.display("Card ATR = " + CardATR);
                Console.display("Deactivate Contactless protocol Type B for the reader: " + RLP.RL_P_UT_GetReaderName());
                RLP.RL_P_UT_DeactivateProtocol(ContactlessProtocol.NFC_A_ISO_14443_3B);
                CardAvailable = RLP.RL_P_UT_CheckCardPresence();
                if (!CardAvailable) {
                    Console.display("The protocol deactivation is successful and the card is not detected");
                    Console.display("Reactivate Contactless protocol Type B for the reader: " + RLP.RL_P_UT_GetReaderName());
                    RLP.RL_P_UT_ActivateProtocol(ContactlessProtocol.NFC_A_ISO_14443_3B);
                    CardAvailable = RLP.RL_P_UT_CheckCardPresence();
                    if (CardAvailable) {
                        Console.display("Card is detected for the reader: " + RLP.RL_P_UT_GetReaderName());
                        CardATR = "";
                        CardATR = RLP.RL_P_UT_GetATR();
                        Console.display("Card ATR = " + CardATR);
                        Console.display("Reactivation is successful");
                        }
                    else ErrorMessage = ("Reactivation failed for the reader "+ RLP.RL_P_UT_GetReaderName());
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
