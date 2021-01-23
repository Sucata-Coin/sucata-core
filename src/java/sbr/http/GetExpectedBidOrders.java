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

import sbr.Attachment;
import sbr.Sbr;
import sbr.SbrException;
import sbr.Transaction;
import sbr.TransactionType;
import sbr.util.Filter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;

public final class GetExpectedBidOrders extends APIServlet.APIRequestHandler {

    static final GetExpectedBidOrders instance = new GetExpectedBidOrders();

    private GetExpectedBidOrders() {
        super(new APITag[] {APITag.AE}, "asset", "sortByPrice");
    }

    private final Comparator<Transaction> priceComparator = (o1, o2) -> {
        Attachment.ColoredCoinsOrderPlacement a1 = (Attachment.ColoredCoinsOrderPlacement)o1.getAttachment();
        Attachment.ColoredCoinsOrderPlacement a2 = (Attachment.ColoredCoinsOrderPlacement)o2.getAttachment();
        return Long.compare(a2.getPriceNQT(), a1.getPriceNQT());
    };

    @Override
    protected JSONStreamAware processRequest(HttpServletRequest req) throws SbrException {

        long assetId = ParameterParser.getUnsignedLong(req, "asset", false);
        boolean sortByPrice = "true".equalsIgnoreCase(req.getParameter("sortByPrice"));
        Filter<Transaction> filter = transaction -> {
            if (transaction.getType() != TransactionType.ColoredCoins.BID_ORDER_PLACEMENT) {
                return false;
            }
            Attachment.ColoredCoinsOrderPlacement attachment = (Attachment.ColoredCoinsOrderPlacement)transaction.getAttachment();
            return assetId == 0 || attachment.getAssetId() == assetId;
        };

        List<? extends Transaction> transactions = Sbr.getBlockchain().getExpectedTransactions(filter);
        if (sortByPrice) {
            transactions.sort(priceComparator);
        }
        JSONArray orders = new JSONArray();
        transactions.forEach(transaction -> orders.add(JSONData.expectedBidOrder(transaction)));
        JSONObject response = new JSONObject();
        response.put("bidOrders", orders);
        return response;

    }

}
