package com.software.codetime.toolwindows.codetime;

import com.software.codetime.toolwindows.WebviewClosedConnection;
import com.software.codetime.toolwindows.WebviewOpenedConnection;
import com.software.codetime.toolwindows.WebviewResourceState;
import org.apache.commons.lang.StringUtils;
import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandler;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

import swdc.java.ops.manager.FileUtilManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.net.MalformedURLException;
import java.net.URL;

public class CodeTimeResourceHandler implements CefResourceHandler {

    private WebviewResourceState state = new WebviewClosedConnection();

    @Override
    public boolean processRequest(CefRequest cefRequest, CefCallback cefCallback) {
        String url = cefRequest.getURL();
        if (StringUtils.isNotBlank(url)) {

            String pathToResource = url.replace("http://myapp", "codetime");
            URL resourceUrl = getClass().getClassLoader().getResource(pathToResource);

            File f = new File(FileUtilManager.getCodeTimeViewHtmlFile());
            Writer writer = null;
            try {
                String html = buildHtml();

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
                state = new WebviewOpenedConnection(resourceUrl.openConnection());
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
        state = new WebviewClosedConnection();
    }

    private String buildHtml() {
        return "<!doctype html>\n" +
                "<html lang=\"en\">\n" +
                "  <head>\n" +
                "    <!-- Required meta tags -->\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n" +
                "\n" +
                "    <!-- Bootstrap CSS -->\n" +
                "    <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\" integrity=\"sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm\" crossorigin=\"anonymous\">\n" +
                "\n" +
                "    <title>Hello, world!</title>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "<nav class=\"navbar navbar-expand-lg navbar-light bg-light\">\n" +
                "  <button class=\"navbar-toggler\" type=\"button\" data-toggle=\"collapse\" data-target=\"#navbarTogglerDemo01\" aria-controls=\"navbarTogglerDemo01\" aria-expanded=\"false\" aria-label=\"Toggle navigation\">\n" +
                "    <span class=\"navbar-toggler-icon\"></span>\n" +
                "  </button>\n" +
                "  <div class=\"collapse navbar-collapse\" id=\"navbarTogglerDemo01\">\n" +
                "    <a class=\"navbar-brand\" href=\"#\">Hidden brand</a>\n" +
                "    <ul class=\"navbar-nav mr-auto mt-2 mt-lg-0\">\n" +
                "      <li class=\"nav-item active\">\n" +
                "        <a class=\"nav-link\" href=\"#\">Home <span class=\"sr-only\">(current)</span></a>\n" +
                "      </li>\n" +
                "      <li class=\"nav-item\">\n" +
                "        <a class=\"nav-link\" href=\"#\">Link</a>\n" +
                "      </li>\n" +
                "      <li class=\"nav-item\">\n" +
                "        <a class=\"nav-link disabled\" href=\"#\">Disabled</a>\n" +
                "      </li>\n" +
                "    </ul>\n" +
                "    <form class=\"form-inline my-2 my-lg-0\">\n" +
                "      <input class=\"form-control mr-sm-2\" type=\"search\" placeholder=\"Search\" aria-label=\"Search\">\n" +
                "      <button class=\"btn btn-outline-success my-2 my-sm-0\" type=\"submit\">Search</button>\n" +
                "    </form>\n" +
                "  </div>\n" +
                "</nav>\n" +
                "\n" +
                "    <!-- Optional JavaScript -->\n" +
                "    <!-- jQuery first, then Popper.js, then Bootstrap JS -->\n" +
                "    <script src=\"https://code.jquery.com/jquery-3.2.1.slim.min.js\" integrity=\"sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN\" crossorigin=\"anonymous\"></script>\n" +
                "    <script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js\" integrity=\"sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q\" crossorigin=\"anonymous\"></script>\n" +
                "    <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\" integrity=\"sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl\" crossorigin=\"anonymous\"></script>\n" +
                "  </body>\n" +
                "</html>";
    }

}
