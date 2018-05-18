package client;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Client2 {

    public static void main(String[] args) throws IOException {

//        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
//        System.setProperty("javax.net.ssl.trustStore", System.getProperty("user.dir") + File.separator + "keyStore" + File.separator + "truststore");
//        System.setProperty("javax.net.ssl.trustStorePassword","123456");
//
//        HttpClient httpclient = HttpClients.createDefault();
//        HttpPost httppost = new HttpPost("http://www.a-domain.com/foo/");
//
//// Request parameters and other properties.
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("param-1", "12345"));
//        params.add(new BasicNameValuePair("param-2", "Hello!"));
//        try {
//            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
//        }catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//
////Execute and get the response.
//        HttpResponse response = null;
//        try {
//            response = httpclient.execute(httppost);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        HttpEntity entity = response.getEntity();
//
//        if (entity != null) {
//            InputStream instream = null;
//            try {
//                instream = entity.getContent();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                // do something useful
//            } finally {
//                try {
//                    instream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        SSLSocketFactory sslSocketFactory =
//                (SSLSocketFactory)SSLSocketFactory.getDefault();
//        try {
//            Socket socket = sslSocketFactory.createSocket("localhost", port);
//            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//            try (BufferedReader bufferedReader =
//                         new BufferedReader(
//                                 new InputStreamReader(socket.getInputStream()))) {
//                Scanner scanner = new Scanner(System.in);
//                while(true){
//                    System.out.println("Enter something:");
//                    String inputLine = scanner.nextLine();
//                    if(inputLine.equals("q")){
//                        break;
//                    }
//
//                    out.println(inputLine);
//                    System.out.println(bufferedReader.readLine());
//                }
//            }
//
//        } catch (IOException ex) {
//            Logger.getLogger(Client.class.getName())
//                    .log(Level.SEVERE, null, ex);
//        }
//

//        String url = "http://www.google.com/search?q=httpClient";
//
//        HttpClient client = HttpClientBuilder.create().build();
//        HttpGet request = new HttpGet(url);
//
//        // add request header
//        request.addHeader("User-Agent", "Mozilla/5.0");
//        HttpResponse response = null;
//        try {
//            response = client.execute(request);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("Response Code : "
//                + response.getStatusLine().getStatusCode());
//
//        BufferedReader rd = new BufferedReader(
//                new InputStreamReader(response.getEntity().getContent()));
//
//        StringBuffer result = new StringBuffer();
//        String line = "";
//        while ((line = rd.readLine()) != null) {
//            result.append(line);
//        }

        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("http://www.a-domain.com/foo/");

// Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("param-1", "12345"));
        params.add(new BasicNameValuePair("param-2", "Hello!"));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

//Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                // do something useful
            } finally {
                instream.close();
            }
        }

    }
}
