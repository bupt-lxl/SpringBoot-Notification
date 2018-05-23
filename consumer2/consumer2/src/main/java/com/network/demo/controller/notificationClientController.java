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

}
