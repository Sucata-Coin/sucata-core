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

import sbr.db.BasicDb;
import sbr.db.TransactionalDb;

public final class Db {

    public static final String PREFIX = Constants.isTestnet ? "sbr.testDb" : "sbr.db";
    public static final TransactionalDb db = new TransactionalDb(new BasicDb.DbProperties()
            .maxCacheSize(Sbr.getIntProperty("sbr.dbCacheKB"))
            .dbUrl(Sbr.getStringProperty(PREFIX + "Url"))
            .dbType(Sbr.getStringProperty(PREFIX + "Type"))
            .dbDir(Sbr.getStringProperty(PREFIX + "Dir"))
            .dbParams(Sbr.getStringProperty(PREFIX + "Params"))
            .dbUsername(Sbr.getStringProperty(PREFIX + "Username"))
            .dbPassword(Sbr.getStringProperty(PREFIX + "Password", null, true))
            .maxConnections(Sbr.getIntProperty("sbr.maxDbConnections"))
            .loginTimeout(Sbr.getIntProperty("sbr.dbLoginTimeout"))
            .defaultLockTimeout(Sbr.getIntProperty("sbr.dbDefaultLockTimeout") * 1000)
            .maxMemoryRows(Sbr.getIntProperty("sbr.dbMaxMemoryRows"))
    );

    public static void init() {
        db.init(new SbrDbVersion());
    }

    static void shutdown() {
        db.shutdown();
    }

    private Db() {} // never

}
