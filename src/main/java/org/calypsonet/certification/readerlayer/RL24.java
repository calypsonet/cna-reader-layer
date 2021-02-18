package org.calypsonet.certification.readerlayer;

import org.calypsonet.certification.readerlayer.procedures.Console;
import org.calypsonet.certification.readerlayer.procedures.ContactlessProtocol;
import org.calypsonet.certification.readerlayer.procedures.RLProcedures;
import org.calypsonet.certification.readerlayer.procedures.ReaderType;
import org.calypsonet.certification.readerlayer.reader.IReaderModule;
import org.calypsonet.certification.readerlayer.reader.PcscReaderModule;

public class RL24 {

    private static void Test() {
        String Reader_For_Test = "OMNIKEY CardMan 5x21-CL 0";
        String AID_For_Test =  "315449432E494341";
        String FCIValue = "";
        String APDU = "";
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

            FCIValue = RLP.RL_P_UT_SmartCardSelection(AID_For_Test);
            Console.display("FCI = " + FCIValue);

            Console.display("Send a Select File APDU command");
            RLP.RL_P_UT_SendAPDU("00 A4 02 00 02 2001 19", true);
            RLP.RL_P_UT_CheckDataOutLen(25,"9000");

            Console.display("Send a Read Binary APDU command");
            RLP.RL_P_UT_SendAPDU("00 B0 80 00 01", false);
            RLP.RL_P_UT_CheckSW1SW2("6981");

            Console.display("Send an Update Binary APDU command");
            RLP.RL_P_UT_SendAPDU("00 D6 80 00 01 00", false);
            RLP.RL_P_UT_CheckSW1SW2("6981");

            Console.display("Send a Write Binary APDU command");
            RLP.RL_P_UT_SendAPDU("00 D0 80 00 01 00", false);
            RLP.RL_P_UT_CheckSW1SW2("6981");

            Console.display("Send a Read Record APDU command");
            RLP.RL_P_UT_SendAPDU("00 B2 01 3C 1D", false);
            RLP.RL_P_UT_CheckDataOutLen(29,"9000");

            Console.display("Send an Append Record APDU command");
            RLP.RL_P_UT_SendAPDU("00 E2 00 00 01 00", false);
            RLP.RL_P_UT_CheckSW1SW2("6981");

            Console.display("Send a Read Multiple Record APDU command");
            RLP.RL_P_UT_SendAPDU("00 B3 01 05 04 54 02 00 01 01", true);
            RLP.RL_P_UT_CheckSW1SW2("9000");

            Console.display("Send a Search Multiple Record APDU command");
            RLP.RL_P_UT_SendAPDU("00 A2 01 07 04 01 00 01 55 00", true);
            RLP.RL_P_UT_CheckSW1SW2("9000");

            Console.display("Send a Write Record APDU command");
            RLP.RL_P_UT_SendAPDU("00 D2 01 3C 01 00", false);
            RLP.RL_P_UT_CheckSW1SW2("9000");

            Console.display("Send an Increase APDU command");
            RLP.RL_P_UT_SendAPDU("00 32 01 98 03 01 02 03", false);
            RLP.RL_P_UT_CheckSW1SW2("6A82");

            Console.display("Send a Decrease APDU command");
            RLP.RL_P_UT_SendAPDU("00 30 01 98 03 01 02 03", false);
            RLP.RL_P_UT_CheckSW1SW2("6A82");

            Console.display("Send a Increase Multiple APDU command");
            RLP.RL_P_UT_SendAPDU("00 3A 00 00 04 01 01 02 03 01", false);
            RLP.RL_P_UT_CheckSW1SW2("6981");

            Console.display("Send a Decrease Multiple APDU command");
            RLP.RL_P_UT_SendAPDU("00 38 00 00 04 01 01 02 03 01", false);
            RLP.RL_P_UT_CheckSW1SW2("6981");

            Console.display("Send a Get Data APDU command");
            RLP.RL_P_UT_SendAPDU("00 CA 00 4F 00", false);
            RLP.RL_P_UT_CheckSW1SW2("9000");

            Console.display("Send a Put Data APDU command");
            RLP.RL_P_UT_SendAPDU("00 DA 00 4F 04 01020304", false);
            RLP.RL_P_UT_CheckSW1SW2("6700");

            Console.display("Send an Invalidate DF APDU command");
            RLP.RL_P_UT_SendAPDU("00 04 00 00 00", false);
            RLP.RL_P_UT_CheckSW1SW2("9000");

            Console.display("Send a Rehabilitate DF APDU command");
            RLP.RL_P_UT_SendAPDU("00 44 00 00 00", false);
            RLP.RL_P_UT_CheckSW1SW2("9000");

            Console.display("Send a Get Challenge APDU command");
            RLP.RL_P_UT_SendAPDU("00 84 00 00 08", false);
            RLP.RL_P_UT_CheckDataOutLen(8,"9000");

            Console.display("Send a Verify Pin APDU command");
            RLP.RL_P_UT_SendAPDU("00 20 00 00 04 31323334", false);
            RLP.RL_P_UT_CheckSW1SW2("9000");

            Console.display("Send a Change Pin APDU command");
            RLP.RL_P_UT_SendAPDU("00 D8 00 04 04 31323334", false);
            RLP.RL_P_UT_CheckSW1SW2("9000");

            Console.display("Send a Change Key APDU command");
            RLP.RL_P_UT_SendAPDU("00 D8 00 01 18 000000000000000000000000000000000000000000000000", false);
            RLP.RL_P_UT_CheckSW1SW2("6982");

            Console.display("Send an Open Secure Session APDU command");
            RLP.RL_P_UT_SendAPDU("00 8A 81 00 04 01 02 03 04", false);
            RLP.RL_P_UT_CheckSW1SW2("9000");

            Console.display("Send an Close Secure Session APDU command");
            RLP.RL_P_UT_SendAPDU("00 8E 80 00 04 01 02 03 04", false);
            RLP.RL_P_UT_CheckSW1SW2("6988");


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
