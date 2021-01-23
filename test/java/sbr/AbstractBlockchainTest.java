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

import sbr.crypto.Crypto;
import sbr.util.Listener;
import sbr.util.Logger;
import org.junit.Assert;

import java.util.Properties;

public abstract class AbstractBlockchainTest {

    protected static BlockchainProcessorImpl blockchainProcessor;
    protected static BlockchainImpl blockchain;
    private static final Object doneLock = new Object();
    private static boolean done = false;

    protected static Properties newTestProperties() {
        Properties testProperties = new Properties();
        testProperties.setProperty("sbr.shareMyAddress", "false");
        testProperties.setProperty("sbr.savePeers", "false");
        //testProperties.setProperty("sbr.enableAPIServer", "false");
        //testProperties.setProperty("sbr.enableUIServer", "false");
        testProperties.setProperty("sbr.disableGenerateBlocksThread", "true");
        //testProperties.setProperty("sbr.disableProcessTransactionsThread", "true");
        //testProperties.setProperty("sbr.disableRemoveUnconfirmedTransactionsThread", "true");
        //testProperties.setProperty("sbr.disableRebroadcastTransactionsThread", "true");
        //testProperties.setProperty("sbr.disablePeerUnBlacklistingThread", "true");
        //testProperties.setProperty("sbr.getMorePeers", "false");
        testProperties.setProperty("sbr.testUnconfirmedTransactions", "true");
        testProperties.setProperty("sbr.debugTraceAccounts", "");
        testProperties.setProperty("sbr.debugLogUnconfirmed", "false");
        testProperties.setProperty("sbr.debugTraceQuote", "\"");
        //testProperties.setProperty("sbr.numberOfForkConfirmations", "0");
        return testProperties;
    }

    protected static void init(Properties testProperties) {
        Sbr.init(testProperties);
        blockchain = BlockchainImpl.getInstance();
        blockchainProcessor = BlockchainProcessorImpl.getInstance();
        blockchainProcessor.setGetMoreBlocks(false);
        TransactionProcessorImpl.getInstance().clearUnconfirmedTransactions();
        Listener<Block> countingListener = block -> {
            if (block.getHeight() % 1000 == 0) {
                Logger.logMessage("downloaded block " + block.getHeight());
            }
        };
        blockchainProcessor.addListener(countingListener, BlockchainProcessor.Event.BLOCK_PUSHED);
    }

    protected static void shutdown() {
        TransactionProcessorImpl.getInstance().clearUnconfirmedTransactions();
    }

    protected static void downloadTo(final int endHeight) {
        if (blockchain.getHeight() == endHeight) {
            return;
        }
        Assert.assertTrue(blockchain.getHeight() < endHeight);
        Listener<Block> stopListener = block -> {
            if (blockchain.getHeight() == endHeight) {
                synchronized (doneLock) {
                    done = true;
                    blockchainProcessor.setGetMoreBlocks(false);
                    doneLock.notifyAll();
                    throw new SbrException.StopException("Reached height " + endHeight);
                }
            }
        };
        blockchainProcessor.addListener(stopListener, BlockchainProcessor.Event.BLOCK_PUSHED);
        synchronized (doneLock) {
            done = false;
            Logger.logMessage("Starting download from height " + blockchain.getHeight());
            blockchainProcessor.setGetMoreBlocks(true);
            while (! done) {
                try {
                    doneLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        Assert.assertEquals(endHeight, blockchain.getHeight());
        blockchainProcessor.removeListener(stopListener, BlockchainProcessor.Event.BLOCK_PUSHED);
    }

    protected static void forgeTo(final int endHeight, final String secretPhrase) {
        if (blockchain.getHeight() == endHeight) {
            return;
        }
        Assert.assertTrue(blockchain.getHeight() < endHeight);
        Listener<Block> stopListener = block -> {
            if (blockchain.getHeight() == endHeight) {
                synchronized (doneLock) {
                    done = true;
                    Generator.stopForging(secretPhrase);
                    doneLock.notifyAll();
                }
            }
        };
        blockchainProcessor.addListener(stopListener, BlockchainProcessor.Event.BLOCK_PUSHED);
        synchronized (doneLock) {
            done = false;
            Logger.logMessage("Starting forging from height " + blockchain.getHeight());
            Generator.startForging(secretPhrase);
            while (! done) {
                try {
                    doneLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        Assert.assertTrue(blockchain.getHeight() >= endHeight);
        Assert.assertArrayEquals(Crypto.getPublicKey(secretPhrase), blockchain.getLastBlock().getGeneratorPublicKey());
        blockchainProcessor.removeListener(stopListener, BlockchainProcessor.Event.BLOCK_PUSHED);
    }

    protected int getHeight() {
        return blockchain.getHeight();
    }
}
