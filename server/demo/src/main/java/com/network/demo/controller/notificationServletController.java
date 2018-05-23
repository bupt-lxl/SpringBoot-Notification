package com.network.demo.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
public class notificationServletController {

    @RequestMapping("/notifications")
    public void subscribeNotification(@RequestBody Map request, HttpServletResponse response)
            throws ServletException, IOException, JSONException {

        System.out.println("Enter localhost:8083/notifications. " );

        JSONObject jsonObject = new JSONObject(request);
        System.out.println("jsonObject:" + jsonObject);
        String subscriberUri = (String) jsonObject.get("subscriberUri");
        String notificationType = (String) jsonObject.get("notificationType");
        String filteringCriteria = (String) jsonObject.get("filteringCriteria");
        String managerId = (String) jsonObject.get("managerId");
        System.out.println("subscriberUri=" + subscriberUri + ", notificationType=" + notificationType + ", filteringCriteria=" + filteringCriteria + ", managerId=" + managerId );

        //  some code...   省略了存数据库的操作

        response.setHeader("Location", "http://localhost:8083/notifications/0101");
        PrintWriter out = response.getWriter();
        String result = "Success to Subscribe a notification! ";
        out.write(result);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

         Random random = new Random();

        int sleepSecends = random.nextInt(30);  //随机获取等待时间，来通过sleep模拟服务端是否准备好数据

        System.out.println("wait " + sleepSecends + " second");

        try {
            TimeUnit.SECONDS.sleep(sleepSecends);  //sleep
        } catch (InterruptedException e) {

        }

        PrintWriter out = response.getWriter();
        String info = "A new notification";
        out.write(info);
    }

    @RequestMapping("/sendNotification")
    public void sendNotification(@RequestBody Map request, HttpServletResponse response)
            throws ServletException, IOException, JSONException {

        System.out.println("request:" + request);

        JSONObject jsonObject = new JSONObject(request);
        System.out.println("jsonObject:" + jsonObject);
        String subscriptionId = (String) jsonObject.get("subscriptionId");
        String notificationType = (String) jsonObject.get("notificationType");
        String producerId = (String) jsonObject.get("producerId");
        String alarmType = (String) jsonObject.getJSONObject("message").get("alarmType");
        System.out.println("subscriptionId=" + subscriptionId + ", notificationType=" + notificationType + ", producerId=" + producerId + ", alarmType=" + alarmType );

        //  some code...  查询数据库（省略）

        // 模拟数据库查询结果
        String getNotificationType = "";
        String getAlarmType = "";
        String getsubscriberUri = "";
        if(subscriptionId.equals("http://localhost:8081/notifications/0101")){
            getNotificationType = "alarm";
            getAlarmType = "01";
            getsubscriberUri = "http://localhost:8081/notifications/001";
        }
        if(subscriptionId.equals("http://localhost:8081/notifications/0102")){
            getNotificationType = "alarm";
            getAlarmType = "02";
            getsubscriberUri = "http://localhost:8082/notifications/001";
        }

        // 判断该通知是否符合订阅条件
        String subscribeURL = "";
        if(notificationType.equals(getNotificationType) && alarmType.equals(getAlarmType)){
            subscribeURL = getsubscriberUri;
        } else return;

        // 建立连接，将通知发送给订阅者
        HttpURLConnection subscribeConnection = null;
        StringBuffer responseBuffer = new StringBuffer();
        try{
            URL getsubscribeURL = new URL(subscribeURL);
            subscribeConnection = (HttpURLConnection) getsubscribeURL.openConnection();  // 建立连接
            subscribeConnection.setDoOutput(true);
            subscribeConnection.setDoInput(true);
            subscribeConnection.setRequestMethod("POST");
            subscribeConnection.setRequestProperty("Accept-Charset", "utf-8");
            subscribeConnection.setRequestProperty("Content-Type", "application/json");
            subscribeConnection.setRequestProperty("Charset", "UTF-8");
            byte[] data = (jsonObject.toString()).getBytes();
            subscribeConnection.setRequestProperty("Content-Length", String.valueOf(data.length));

            // 开始连接请求
            subscribeConnection.connect();
            OutputStream out = subscribeConnection.getOutputStream();
            // 写入请求的字符串
            out.write((jsonObject.toString()).getBytes());
            out.flush();
            out.close();
        }catch (IOException e) {
        }
        if (subscribeConnection.getResponseCode() == 200) {    // 若响应码为200，则通知订阅成功
            System.out.println("Success to send the notification." );
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
    }


}
