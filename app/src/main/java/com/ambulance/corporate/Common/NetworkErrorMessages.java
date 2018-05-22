/*
 * Copyright (c) 2018. Sumit Ranjan
 */

package com.ambulance.corporate.Common;

/**
 * Created by sumit on 06-Apr-18.
 */

public class NetworkErrorMessages {

    public static String networkErrorMsg(int statusCode){

        switch (statusCode){

            case 100:
                return "Only a part of the request has been received by the server, but as long as it has not been rejected, the app will continue with the request.";

            case 101:
                return "The server switches protocol.";

            case 200:
                return "The request is Ok.";

            case 201:
                return "The request is complete, and a new resource is created.";

            case 202:
                return "The request is accepted for processing, but the processing is not complete.";

            case 203:
                return "The information in the entity header is from a local or third-party copy, not from the original server.";

            case 204:
                return "A status code and a header are given in the response, but there is no entity-body in the reply.";

            case 205:
                return "The app should clear the form used for this transaction for additional input.";

            case 206:
                return "The server is returning partial data of the size requested.";

            case 300:
                return "A link list. The user can select a link and go to that location. Maximum five addresses.";

            case 301:
                return "The requested page has moved to a new url.";

            case 302:
                return "The requested page has moved temporarily to a new url.";

            case 303:
                return "The requested page can be found under a different url.";

            case 305:
                return "The requested URL must be accessed through the proxy mentioned in the Location header.";

            case 306:
                return "This code was used in a previous version. It is no longer used, but the code is reserved.";

            case 307:
                return "The requested page has moved temporarily to a new url.";

            case 400:
                return "The server did not understand the request.";

            case 401:
                return "The requested page needs a username and a password.";

            case 403:
                return "Access is forbidden to the requested page.";

            case 404:
                return "The server can not find the requested page.";

            case 405:
                return "The method specified in the request is not allowed.";

            case 406:
                return "The server can only generate a response that is not accepted by the app.";

            case 407:
                return "You must authenticate with a proxy server before this request can be served.";

            case 408:
                return "The request took longer than the server was prepared to wait.";

            case 409:
                return "The request could not be completed because of a conflict.";

            case 410:
                return "The requested page is no longer available.";

            case 411:
                return "The 'Content-Length' is not defined. The server will not accept the request without it.";

            case 412:
                return "The pre condition given in the request evaluated to false by the server.";

            case 413:
                return "The server will not accept the request, because the request entity is too large.";

            case 414:
                return "The server will not accept the request, because the url is too long.";

            case 415:
                return "The server will not accept the request, because the mediatype is not supported.";

            case 416:
                return "The requested byte range is not available and is out of bounds.";

            case 417:
                return "The expectation given in an Expect request-header field could not be met by this server.";

            case 500:
                return "The request was not completed. The server met an unexpected condition.";

            case 501:
                return "The request was not completed. The server did not support the functionality required.";

            case 502:
                return "The request was not completed. The server received an invalid response from the upstream server.";

            case 503:
                return "The request was not completed. The server is temporarily overloading or down.";

            case 504:
                return "The gateway has timed out.";

            case 505:
                return "The server does not support the 'http protocol' version.";

            default:
                return "Something not right with the server.";

        }

    }

}
