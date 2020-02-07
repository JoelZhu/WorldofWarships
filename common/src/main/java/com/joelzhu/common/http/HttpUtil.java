package com.joelzhu.common.http;

import com.joelzhu.common.ArrayUtil;
import com.joelzhu.common.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

final class HttpUtil {
    private final static int TIME_OUT = 5000;

    /**
     * HTTP Get
     *
     * @param requestURL Request url.
     * @param parameters Parameters.
     */
    static void httpGet(String requestURL, Parameter... parameters) {
        // Is request url exists.
        if (StringUtil.isEmpty(requestURL)) {
            RequestManager.getInstance().onGetErrorOccurred(ErrorCode.NO_REQUEST_URL);
            return;
        }

        HttpURLConnection httpConn = null;
        BufferedReader bufferedReader = null;
        // Create string builder of response.
        StringBuilder responseSB = new StringBuilder();
        try {
            // Combine parameters into request url.
            requestURL = requestURL + "?" + convertKeyValue2String(parameters);

            RequestManager.getInstance().onPreGetRequest(requestURL);
            // Create Conn instance.
            httpConn = (HttpURLConnection) new URL(requestURL).openConnection();
            httpConn.setRequestMethod("GET");
            // Timeout.
            httpConn.setConnectTimeout(TIME_OUT);
            httpConn.setReadTimeout(TIME_OUT);

            // Read response from stream.
            bufferedReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            String readLine;
            while ((readLine = bufferedReader.readLine()) != null) {
                responseSB.append(readLine);
            }
        } catch (MalformedURLException e) {
            responseSB = null;
            RequestManager.getInstance().onGetErrorOccurred(ErrorCode.MALFORMED_URL);
        } catch (IOException e) {
            responseSB = null;
            RequestManager.getInstance().onGetErrorOccurred(ErrorCode.IO_EXCEPTION);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    responseSB = null;
                    RequestManager.getInstance().onGetErrorOccurred(ErrorCode.IO_EXCEPTION);
                }
            }
            if (httpConn != null) {
                httpConn.disconnect();
            }
        }

        if (responseSB != null) {
            final String responseString = responseSB.toString();
            RequestManager.getInstance().onPostGetRequest(responseString);
        }
    }

    /**
     * HTTP Post
     *
     * @param requestURL Request url.
     * @param parameters Parameters.
     */
    static void httpPost(String requestURL, Parameter... parameters) {
        // Is request url exists.
        if (StringUtil.isEmpty(requestURL)) {
            RequestManager.getInstance().onPostErrorOccurred(ErrorCode.NO_REQUEST_URL);
            return;
        }

        HttpURLConnection httpConn = null;
        BufferedReader bufferedReader = null;
        PrintWriter printWriter = null;
        // Create string builder of response.
        StringBuilder responseSB = new StringBuilder();

        try {
            final String paramString = convertKeyValue2String(parameters);
            RequestManager.getInstance().onPrePostRequest(requestURL + ", " + paramString);
            // Create Conn instance.
            httpConn = (HttpURLConnection) new URL(requestURL).openConnection();
            httpConn.setRequestMethod("POST");
            // Timeout.
            httpConn.setConnectTimeout(TIME_OUT);
            httpConn.setReadTimeout(TIME_OUT);
            // Necessary settings.
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);

            // Add parameters of post request.
            printWriter = new PrintWriter(httpConn.getOutputStream());
            printWriter.print(paramString);
            printWriter.flush();

            // Read response from stream.
            bufferedReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            String readLine;
            while ((readLine = bufferedReader.readLine()) != null) {
                responseSB.append(readLine);
            }
        } catch (MalformedURLException e) {
            responseSB = null;
            RequestManager.getInstance().onPostErrorOccurred(ErrorCode.MALFORMED_URL);
        } catch (IOException e) {
            responseSB = null;
            RequestManager.getInstance().onPostErrorOccurred(ErrorCode.IO_EXCEPTION);
        } finally {
            try {
                if (printWriter != null) {
                    printWriter.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (httpConn != null) {
                    httpConn.disconnect();
                }
            } catch (IOException exception) {
                responseSB = null;
                RequestManager.getInstance().onPostErrorOccurred(ErrorCode.IO_EXCEPTION);
            }
        }

        if (responseSB != null) {
            final String responseString = responseSB.toString();
            RequestManager.getInstance().onPostPostRequest(responseString);
        }
    }

    private static String convertKeyValue2String(Parameter... parameters) {
        return ArrayUtil.array2String(parameters, Parameter.SYMBOL_JOIN);
    }
}