package com.software.codetime.dashboard;

import org.apache.commons.lang.StringUtils;
import org.cef.callback.CefCallback;
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefResourceHandler;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

import swdc.java.ops.manager.FileUtilManager;
import swdc.java.ops.http.OpsHttpClient;
import swdc.java.ops.http.ClientResponse;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DashboardResourceHandler implements CefResourceHandler {

    private DashboardResourceState state = new ClosedConnection();

    @Override
    public boolean processRequest(CefRequest cefRequest, CefCallback cefCallback) {
        String url = cefRequest.getURL();
        if (StringUtils.isNotBlank(url)) {

            String pathToResource = url.replace("http://myapp", "dashboard");
            URL resourceUrl = getClass().getClassLoader().getResource(pathToResource);

            File f = getDashboardFile();
            Writer writer = null;
            try {
                String jwt = FileUtilManager.getItem("jwt");
                ClientResponse resp = OpsHttpClient.softwareGet("/v1/plugin_dashboard", jwt);
                String html = resp.getJsonObj().get("html").getAsString();

                writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(f), StandardCharsets.UTF_8));
                writer.write(html);
            } catch (Exception e) {
                System.out.println("Dashboard write error: " + e.getMessage());
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (Exception e) {
                        System.out.println("Writer close error: " + e.getMessage());
                    }
                }
            }

            try {
                resourceUrl = f.toURI().toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                state = new OpenedConnection(resourceUrl.openConnection());
            } catch (Exception e) {
                //
            }
            cefCallback.Continue();
            return true;
        }
        return false;
    }

    private static File getDashboardFile() {
        String htmlFile = getSoftwareDir(true) + getOsSpecificName("dashboard.html");
        File f = new File(htmlFile);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return f;
    }

    private static String getOsSpecificName(String fileName) {
        return File.separator + fileName;
    }

    public static String getSoftwareDir(boolean autoCreate) {
        String softwareDataDir = System.getProperty("user.home") + getOsSpecificName(".software");

        File f = new File(softwareDataDir);
        if (autoCreate && !f.exists()) {
            // make the directory
            f.mkdirs();
        }

        return softwareDataDir;
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
