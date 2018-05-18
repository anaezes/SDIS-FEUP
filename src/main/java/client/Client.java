package client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class Client {

    public static void main(String[] args) {
        try {

            HttpClient httpClient = HttpClients.createDefault();
            HttpPost postRequest = new HttpPost(
                    "http://localhost:8000/test");

//            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
//            urlParameters.add(new BasicNameValuePair("user", "admfactory"));
//            urlParameters.add(new BasicNameValuePair("password", "supersecret"));
//            urlParameters.add(new BasicNameValuePair("email", "admin@admfactory.com"));

            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter something:");
                    String inputLine = scanner.nextLine();

            StringEntity input = new StringEntity(inputLine);
            postRequest.setEntity(input);

//            postRequest.setEntity(new UrlEncodedFormEntity(urlParameters));

            HttpResponse response = httpClient.execute(postRequest);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

//            httpClient.get.shutdown();

        } catch (ClientProtocolException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter something:");
        String inputLine = scanner.nextLine();
    }

}