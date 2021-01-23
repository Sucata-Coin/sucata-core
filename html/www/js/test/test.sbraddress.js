/******************************************************************************
 * Copyright © 2013-2016 The Nxt Core Developers.                             *
 * Copyright © 2016-2019 Jelurida IP B.V.                                     *
 *                                                                            *
 * See the LICENSE.txt file at the top-level directory of this distribution   *
 * for licensing information.                                                 *
 *                                                                            *
 * Unless otherwise agreed in a custom licensing agreement with Jelurida B.V.,*
 * no part of the Sbr software, including this file, may be copied, modified, *
 * propagated, or distributed except according to the terms contained in the  *
 * LICENSE.txt file.                                                          *
 *                                                                            *
 * Removal or modification of this copyright notice is prohibited.            *
 *                                                                            *
 ******************************************************************************/

QUnit.module("sbr.address");

QUnit.test("sbrAddress", function (assert) {
    var address = new SbrAddress();
    assert.equal(address.set("SBR-XK4R-7VJU-6EQG-7R335"), true, "valid address");
    assert.equal(address.toString(), "SBR-XK4R-7VJU-6EQG-7R335", "address");
    assert.equal(address.set("SBR-XK4R-7VJU-6EQG-7R336"), false, "invalid address");
});
