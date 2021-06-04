package com.software.codetime.dashboard;

import org.apache.commons.lang.StringUtils;
import org.cef.callback.CefCallback;
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefResourceHandler;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class DashboardResourceHandler implements CefResourceHandler {

    private DashboardResourceState state = new ClosedConnection();

    @Override
    public boolean processRequest(CefRequest cefRequest, CefCallback cefCallback) {
        String url = cefRequest.getURL();
        if (StringUtils.isNotBlank(url)) {

//            String jwt = FileUtilManager.getItem("jwt");
//            String api = "/sessions/summary";
//            ClientResponse resp = OpsHttpClient.softwareGet(api, jwt);

            String pathToResource = url.replace("http://myapp", "dashboard/");
            URL newUrl = getClass().getClassLoader().getResource(pathToResource);
            try {
                state = new OpenedConnection(newUrl.openConnection());
            } catch (Exception e) {
                //
            }
            cefCallback.Continue();
            return true;
        }
        return false;
    }

    @Override
    public void getResponseHeaders(CefResponse cefResponse, IntRef responseLength, StringRef redirectUrl) {
        state.getResponseHeaders(cefResponse, responseLength, redirectUrl);
    }

    @Override
    public boolean readResponse(byte[] dataOut, int designedBytesToRead, IntRef bytesRead, CefCallback callback) {
        return state.readResponse(dataOut, designedBytesToRead, bytesRead, callback);
    }

    @Override
    public void cancel() {
        state.close();
        state = new ClosedConnection();
    }

    protected static class OpenedConnection implements DashboardResourceState {

        private URLConnection connection;
        private InputStream inputStream;

        public OpenedConnection(URLConnection connection) {
            this.connection = connection;
            try {
                this.inputStream = connection.getInputStream();
            } catch (Exception e) {
                //
            }
        }

        @Override
        public void getResponseHeaders(CefResponse cefResponse, IntRef responseLength, StringRef redirectUrl) {
            try {
                String url = connection.getURL().toString();
                if (url.contains(".css")) {
                    cefResponse.setMimeType("text/css");
                } else if (url.contains(".js")) {
                    cefResponse.setMimeType("text/javascript");
                } else if (url.contains(".html")) {
                    cefResponse.setMimeType("text/html");
                } else {
                    cefResponse.setMimeType(connection.getContentType());
                }
                try {
                    responseLength.set(inputStream.available());
                    cefResponse.setStatus(200);
                } catch (Exception e) {
                    cefResponse.setError(CefLoadHandler.ErrorCode.ERR_FILE_NOT_FOUND);
                    cefResponse.setStatusText(e.getLocalizedMessage());
                    cefResponse.setStatus(404);
                }
            } catch (Exception e) {
                cefResponse.setError(CefLoadHandler.ErrorCode.ERR_FILE_NOT_FOUND);
                cefResponse.setStatusText(e.getLocalizedMessage());
                cefResponse.setStatus(404);
            }
        }

        @Override
        public boolean readResponse(byte[] dataOut, int designedBytesToRead, IntRef bytesRead, CefCallback callback) {
            try {
                int availableSize = inputStream.available();
                if (availableSize > 0) {
                    int maxBytesToRead = Math.min(availableSize, designedBytesToRead);
                    int realNumberOfReadBytes = inputStream.read(dataOut, 0, maxBytesToRead);
                    bytesRead.set(realNumberOfReadBytes);
                    return true;
                }
            } catch (Exception e) {
                //
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                    //
                }
            }
            return false;
        }

        @Override
        public void close() {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                //
            }
        }
    }

    protected static class ClosedConnection implements DashboardResourceState {

        @Override
        public void getResponseHeaders(CefResponse cefResponse, IntRef responseLength, StringRef redirectUrl) {
            cefResponse.setStatus(404);
        }

        @Override
        public boolean readResponse(byte[] dataOut, int designedBytesToRead, IntRef bytesRead, CefCallback callback) {
            return false;
        }

        @Override
        public void close() {

        }
    }
}
