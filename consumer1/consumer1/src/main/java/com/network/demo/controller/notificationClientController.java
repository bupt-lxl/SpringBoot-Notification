package com.network.demo.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

@RestController
public class notificationClientController {

    @RequestMapping("/notifications/001")
    public void receiveNotification(@RequestBody Map request, HttpServletResponse response)
            throws ServletException, IOException{
        System.out.println("Receive a new notification." );
        System.out.println("request:" + request);
        PrintWriter out = response.getWriter();
        String result = "Success to Subscribe a notification! ";
        out.write(result);
    }








    @RequestMapping("/notification")
    public void subscribeNotification(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        // 向81端口发起notifications请求，订阅一个通知
        String subscribeURL = "http://localhost:8080/notifications";
        HttpURLConnection subscribeConnection = null;
        StringBuffer responseBuffer = new StringBuffer();
        try{
            //urlConnect(subscribeConnection, subscribeURL);
            URL getsubscribeURL = new URL(subscribeURL);
            subscribeConnection = (HttpURLConnection) getsubscribeURL.openConnection();  // 建立连接
            subscribeConnection.setReadTimeout(15000);// 等待时间设置为15s
            subscribeConnection.setConnectTimeout(5000);
            subscribeConnection.setRequestMethod("POST");
            subscribeConnection.setRequestProperty("Accept-Charset", "utf-8");
            subscribeConnection.setRequestProperty("Content-Type", "application/json");
            subscribeConnection.setRequestProperty("Charset", "UTF-8");
        }catch (IOException e) {
        }
        if (subscribeConnection.getResponseCode() == 200) {    // 若响应码为200，则通知订阅成功
            System.out.println("Get 200 response code " );
            String readLine;
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(
                    subscribeConnection.getInputStream(), "utf-8"));
            while ((readLine = responseReader.readLine()) != null) {
                responseBuffer.append(readLine);
            }
            System.out.println("Http Response:" + responseBuffer);
            subscribeConnection.disconnect();

            PrintWriter out = response.getWriter();
            out.write(responseBuffer.toString());
        }else return;

        // 通知订阅成功之后，向80端口发起长轮询请求
        String longPollingURL = "http://localhost:8080/longPolling";
        int i = 0;
        while (true) {
            System.out.println("第" + (++i) + "次 longpolling");
            HttpURLConnection longPollingConnection = null;
            try {
                URL getLongPollingUrl = new URL(longPollingURL);
                longPollingConnection = (HttpURLConnection) getLongPollingUrl.openConnection();
                longPollingConnection.setReadTimeout(15000);//这就是等待时间，设置为50s
                longPollingConnection.setConnectTimeout(3000);
                longPollingConnection.setRequestMethod("GET");
                longPollingConnection.setRequestProperty("Accept-Charset", "utf-8");
                longPollingConnection.setRequestProperty("Content-Type", "application/json");
                longPollingConnection.setRequestProperty("Charset", "UTF-8");

                if (200 == longPollingConnection.getResponseCode()) {
                    BufferedReader reader = null;
                    try {
                        reader = new BufferedReader(new InputStreamReader(longPollingConnection.getInputStream(), "UTF-8"));
                        StringBuilder result = new StringBuilder(256);
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        System.out.println("结果 " + result);
                    } finally {
                        if (reader != null) {
                            reader.close();
                        }
                    }
                }
            } catch (IOException e) {

            }  finally {
                if (longPollingConnection != null) {
                    longPollingConnection.disconnect();
                }
            }
        }

    }



}
