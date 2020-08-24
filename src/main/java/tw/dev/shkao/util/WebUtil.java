/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.dev.shkao.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import static tw.dev.shkao.restful.BaseRestClient.isUrlConnectable;

/**
 *
 * @author kengao
 */
public class WebUtil {
    
    private static final Logger LOGGER = tw.dev.shkao.util.log.Logger.WEB.getLogger();
    
    protected static Client restClient;
    protected static Client restSslClient;

    static{
        
        SSLContext ctx = null;
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };

            ctx = SSLContext.getInstance("SSL");
            ctx.init(null, trustAllCerts, null);
        } catch (NoSuchAlgorithmException | KeyManagementException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        ClientBuilder clientBuilder = ClientBuilder.newBuilder()
                .hostnameVerifier(hostnameVerifier)
                .sslContext(ctx);

        restSslClient = clientBuilder.build();
        restSslClient.property("jersey.config.client.connectTimeout", 5000);

        restClient = ClientBuilder.newClient();
        restClient.property("jersey.config.client.connectTimeout", 5000);
    }
    
    public static WebTarget getWebTarget(URL baseUrl) {
        if (null == baseUrl.getProtocol()) {
            return restSslClient.target(baseUrl.toString());
        } else switch (baseUrl.getProtocol()) {
            case "http":
                return restClient.target(baseUrl.toString());
            case "https":
                return restSslClient.target(baseUrl.toString());
            default:
                return restSslClient.target(baseUrl.toString());
        }
    }
    
    
    public static URL getURL(String strUrl) {

        if (strUrl == null || strUrl.isEmpty()) {
            return null;
        }

        try {
            URL url;

            try {
                url = new URL(strUrl);
            } catch (MalformedURLException ex) {
                url = new URL("http", "localhost", strUrl);
            }

            if (!"localhost".equals(url.getHost()) && !"127.0.0.1".equals(url.getHost())) {
                return url;
            }

            if (isUrlConnectable(url)) {
                return url;
            }

            if (url.getPort() == 80 || url.getPort() == -1) {
                url = new URL("http", "localhost", 8080, url.getFile());
            } else if (url.getPort() == 8080) {
                url = new URL("http", "localhost", url.getFile());
            }

            return url;
        } catch (MalformedURLException ex) {
            // impossible...
            return null;
        }

    }
    
}
