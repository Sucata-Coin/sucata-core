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

import sbr.Token;
import org.json.simple.JSONStreamAware;

import javax.servlet.http.HttpServletRequest;

import static sbr.http.JSONResponses.INCORRECT_WEBSITE;
import static sbr.http.JSONResponses.MISSING_TOKEN;
import static sbr.http.JSONResponses.MISSING_WEBSITE;

public final class DecodeToken extends APIServlet.APIRequestHandler {

    static final DecodeToken instance = new DecodeToken();

    private DecodeToken() {
        super(new APITag[] {APITag.TOKENS}, "website", "token");
    }

    @Override
    public JSONStreamAware processRequest(HttpServletRequest req) {

        String website = req.getParameter("website");
        String tokenString = req.getParameter("token");
        if (website == null) {
            return MISSING_WEBSITE;
        } else if (tokenString == null) {
            return MISSING_TOKEN;
        }

        try {

            Token token = Token.parseToken(tokenString, website.trim());

            return JSONData.token(token);

        } catch (RuntimeException e) {
            return INCORRECT_WEBSITE;
        }
    }

    @Override
    protected boolean allowRequiredBlockParameters() {
        return false;
    }

}