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

package sbr.http;

import sbr.Account;
import sbr.Appendix;
import sbr.Attachment;
import sbr.Constants;
import sbr.SbrException;
import sbr.util.Convert;
import sbr.util.JSON;
import sbr.util.Search;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import javax.servlet.http.HttpServletRequest;

import static sbr.http.JSONResponses.INCORRECT_DGS_LISTING_DESCRIPTION;
import static sbr.http.JSONResponses.INCORRECT_DGS_LISTING_NAME;
import static sbr.http.JSONResponses.INCORRECT_DGS_LISTING_TAGS;
import static sbr.http.JSONResponses.MISSING_NAME;

public final class DGSListing extends CreateTransaction {

    static final DGSListing instance = new DGSListing();

    private DGSListing() {
        super("messageFile", new APITag[] {APITag.DGS, APITag.CREATE_TRANSACTION},
                "name", "description", "tags", "quantity", "priceNQT");
    }

    @Override
    protected JSONStreamAware processRequest(HttpServletRequest req) throws SbrException {

        String name = Convert.emptyToNull(req.getParameter("name"));
        String description = Convert.nullToEmpty(req.getParameter("description"));
        String tags = Convert.nullToEmpty(req.getParameter("tags"));
        long priceNQT = ParameterParser.getPriceNQT(req);
        int quantity = ParameterParser.getGoodsQuantity(req);

        if (name == null) {
            return MISSING_NAME;
        }
        name = name.trim();
        if (name.length() > Constants.MAX_DGS_LISTING_NAME_LENGTH) {
            return INCORRECT_DGS_LISTING_NAME;
        }

        if (description.length() > Constants.MAX_DGS_LISTING_DESCRIPTION_LENGTH) {
            return INCORRECT_DGS_LISTING_DESCRIPTION;
        }

        if (tags.length() > Constants.MAX_DGS_LISTING_TAGS_LENGTH) {
            return INCORRECT_DGS_LISTING_TAGS;
        }

        Appendix.PrunablePlainMessage prunablePlainMessage = (Appendix.PrunablePlainMessage)ParameterParser.getPlainMessage(req, true);
        if (prunablePlainMessage != null) {
            if (prunablePlainMessage.isText()) {
                return MESSAGE_NOT_BINARY;
            }
            byte[] image = prunablePlainMessage.getMessage();
            String mediaType = Search.detectMimeType(image);
            if (mediaType == null || !mediaType.startsWith("image/")) {
                return MESSAGE_NOT_IMAGE;
            }
        }

        Account account = ParameterParser.getSenderAccount(req);
        Attachment attachment = new Attachment.DigitalGoodsListing(name, description, tags, quantity, priceNQT);
        return createTransaction(req, account, attachment);

    }

    private static final JSONStreamAware MESSAGE_NOT_BINARY;
    static {
        JSONObject response = new JSONObject();
        response.put("errorCode", 8);
        response.put("errorDescription", "Only binary message attachments accepted as DGS listing images");
        MESSAGE_NOT_BINARY = JSON.prepare(response);
    }

    private static final JSONStreamAware MESSAGE_NOT_IMAGE;
    static {
        JSONObject response = new JSONObject();
        response.put("errorCode", 9);
        response.put("errorDescription", "Message attachment is not an image");
        MESSAGE_NOT_IMAGE = JSON.prepare(response);
    }

}
