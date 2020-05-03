package com.joelzhu.base.presenter.network;

import com.joelzhu.base.presenter.ArrayUtil;
import com.joelzhu.base.presenter.StringUtil;

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
    static String httpGet(String requestURL, Parameter... parameters) throws RequestException {
        // Is request url exists.
        if (StringUtil.isEmpty(requestURL)) {
            throw new RequestException(ErrorCode.NO_REQUEST_URL);
        }
    
        int errorCode = ErrorCode.NO_ERROR;
        HttpURLConnection httpConn = null;
        BufferedReader bufferedReader = null;
        // Create string builder of response.
        StringBuilder responseSB = new StringBuilder();
        try {
            // Combine parameters into request url.
            requestURL = requestURL + "?" + convertKeyValue2String(parameters);
            
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
            errorCode = ErrorCode.MALFORMED_URL;
        } catch (IOException e) {
            responseSB = null;
            errorCode = ErrorCode.IO_EXCEPTION;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    responseSB = null;
                    errorCode = ErrorCode.IO_EXCEPTION;
                }
            }
            if (httpConn != null) {
                httpConn.disconnect();
            }
        }
    
        if (errorCode != ErrorCode.NO_ERROR) {
            throw new RequestException(errorCode);
        }
    
        return responseSB.toString();
    }
    
    /**
     * HTTP Post
     *
     * @param requestURL Request url.
     * @param parameters Parameters.
     * @return Request result.
     * @exception RequestException exception.
     */
    static String httpPost(String requestURL, Parameter... parameters) throws RequestException {
        // Is request url exists.
        if (StringUtil.isEmpty(requestURL)) {
            throw new RequestException(ErrorCode.NO_REQUEST_URL);
        }
        
        int errorCode = ErrorCode.NO_ERROR;
        HttpURLConnection httpConn = null;
        BufferedReader bufferedReader = null;
        PrintWriter printWriter = null;
        // Create string builder of response.
        StringBuilder responseSB = new StringBuilder();
        
        try {
            final String paramString = convertKeyValue2String(parameters);
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
            errorCode = ErrorCode.MALFORMED_URL;
        } catch (IOException e) {
            responseSB = null;
            errorCode = ErrorCode.IO_EXCEPTION;
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
                errorCode = ErrorCode.IO_EXCEPTION;
            }
        }
        
        if (errorCode != ErrorCode.NO_ERROR) {
            throw new RequestException(errorCode);
        }
        
        return responseSB.toString();
    }
    
    private static String convertKeyValue2String(Parameter... parameters) {
        return ArrayUtil.array2String(parameters, Parameter.SYMBOL_JOIN);
    }
}