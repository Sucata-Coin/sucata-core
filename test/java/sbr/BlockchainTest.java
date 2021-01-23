/*
 * Copyright © 2013-2016 The Nxt Core Developers.
 * Copyright © 2016-2019 Jelurida IP B.V.
 *
 * See the LICENSE.txt file at the top-level directory of this distribution
 * for licensing information.
 *
 * Unless otherwise agreed in a custom licensing agreement with Jelurida B.V.,
 * no part of the Sbr software, including this file, may be copied, modified,
 * propagated, or distributed except according to the terms contained in the
 * LICENSE.txt file.
 *
 * Removal or modification of this copyright notice is prohibited.
 *
 */

package sbr;

import sbr.util.Logger;
import sbr.util.Time;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.util.Properties;

public abstract class BlockchainTest extends AbstractBlockchainTest {

    protected static Tester FORGY;
    protected static Tester ALICE;
    protected static Tester BOB;
    protected static Tester CHUCK;
    protected static Tester DAVE;

    protected static int baseHeight;

    protected static String forgerSecretPhrase = "aSykrgKGZNlSVOMDxkZZgbTvQqJPGtsBggb";
    protected static final String forgerAccountId = "SBR-9KZM-KNYY-QBXZ-5TD8V";

    public static final String aliceSecretPhrase = "hope peace happen touch easy pretend worthless talk them indeed wheel state";
    private static final String bobSecretPhrase2 = "rshw9abtpsa2";
    private static final String chuckSecretPhrase = "eOdBVLMgySFvyiTy8xMuRXDTr45oTzB7L5J";
    private static final String daveSecretPhrase = "t9G2ymCmDsQij7VtYinqrbGCOAtDDA3WiNr";

    protected static boolean isSbrInitialized = false;

    public static void initSbr() {
        if (!isSbrInitialized) {
            Properties properties = ManualForgingTest.newTestProperties();
            properties.setProperty("sbr.isTestnet", "true");
            properties.setProperty("sbr.isOffline", "true");
            properties.setProperty("sbr.enableFakeForging", "true");
            properties.setProperty("sbr.fakeForgingAccount", forgerAccountId);
            properties.setProperty("sbr.timeMultiplier", "1");
            properties.setProperty("sbr.testnetGuaranteedBalanceConfirmations", "1");
            properties.setProperty("sbr.testnetLeasingDelay", "1");
            properties.setProperty("sbr.disableProcessTransactionsThread", "true");
            properties.setProperty("sbr.deleteFinishedShufflings", "false");
            properties.setProperty("sbr.disableSecurityPolicy", "true");
            properties.setProperty("sbr.disableAdminPassword", "true");
            AbstractBlockchainTest.init(properties);
            isSbrInitialized = true;
        }
    }
    
    @BeforeClass
    public static void init() {
        initSbr();
        Sbr.setTime(new Time.CounterTime(Sbr.getEpochTime()));
        baseHeight = blockchain.getHeight();
        Logger.logMessage("baseHeight: " + baseHeight);
        FORGY = new Tester(forgerSecretPhrase);
        ALICE = new Tester(aliceSecretPhrase);
        BOB = new Tester(bobSecretPhrase2);
        CHUCK = new Tester(chuckSecretPhrase);
        DAVE = new Tester(daveSecretPhrase);
    }

    @After
    public void destroy() {
        TransactionProcessorImpl.getInstance().clearUnconfirmedTransactions();
        blockchainProcessor.popOffTo(baseHeight);
    }

    public static void generateBlock() {
        try {
            blockchainProcessor.generateBlock(forgerSecretPhrase, Sbr.getEpochTime());
        } catch (BlockchainProcessor.BlockNotAcceptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    public static void generateBlocks(int howMany) {
        for (int i = 0; i < howMany; i++) {
            generateBlock();
        }
    }
}
