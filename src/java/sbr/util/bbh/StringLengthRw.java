/*
 * Copyright © 2013-2016 The Nxt Core Developers.
 * Copyright © 2016-2019 Jelurida IP B.V.
 *
 * See the LICENSE.txt file at the top-level directory of this distribution
 * for licensing information.
 *
 * Unless otherwise agreed in a custom licensing agreement with Jelurida B.V.,
 * no part of this software, including this file, may be copied, modified,
 * propagated, or distributed except according to the terms contained in the
 * LICENSE.txt file.
 *
 * Removal or modification of this copyright notice is prohibited.
 *
 */

package sbr.util.bbh;

import sbr.SbrException;
import sbr.util.Convert;

import java.nio.ByteBuffer;

class StringLengthRw implements LengthRw {
    private final LengthRw lengthRw;
    private final int maxCharCount;

    StringLengthRw(LengthRw lengthRw, int maxCharCount) {
        this.lengthRw = lengthRw;
        this.maxCharCount = maxCharCount;
    }

    @Override
    public int getSize() {
        return lengthRw.getSize();
    }

    @Override
    public int readFromBuffer(ByteBuffer buffer) throws SbrException.NotValidException {
        int result = lengthRw.readFromBuffer(buffer);

        if (!validate(result)) {
            throw new SbrException.NotValidException("Read invalid length " + result);
        }

        return result;
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer, int length) {
        lengthRw.writeToBuffer(buffer, length);
    }

    @Override
    public boolean validate(int length) {
        return lengthRw.validate(length) && length <= Convert.getMaxStringSize(maxCharCount);
    }

    public int getMaxCharCount() {
        return maxCharCount;
    }
}
