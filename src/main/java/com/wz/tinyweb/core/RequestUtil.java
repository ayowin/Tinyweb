package com.wz.tinyweb.core;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class RequestUtil {

    public static String getRequestContent(HttpServletRequest request) throws IOException {
        String requestContent = null;

        InputStream inputStream = request.getInputStream();
        ArrayList<Byte> byteArrayList = new ArrayList<>();
        byte[] buffer = new byte[2048];
        int length = inputStream.read(buffer);
        int count = 0;
        while (length > 0) {
            for(byte b : buffer){
                byteArrayList.add(b);
            }
            count += length;
            length = inputStream.read(buffer);
        }
        inputStream.close();
        buffer = new byte[count];
        for(int i=0;i<count;i++){
            buffer[i] = byteArrayList.get(i);
        }
        requestContent = new String(buffer, 0, count, "UTF-8");

        return requestContent;
    }

}
