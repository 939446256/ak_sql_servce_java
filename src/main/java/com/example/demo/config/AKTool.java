package com.example.demo.config;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.example.demo.admin.AKInterface;
import com.example.demo.ak.akSQL.AKSQLMapper;
import com.example.demo.ak.akSQL.dto.AKPageParams;
import com.example.demo.dto.AKFeildPageParams;
import com.example.demo.dto.AKFeildParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class AKTool {

    @Autowired
    private AKSQLMapper sqlMapper;

    @Value("${nodejs.url}")
    private String nodejsUrl;


    public AKFeildParams getIOParams(String url, Map body) throws IOException {
        AKInterface dto = new AKInterface();
        dto.setUrl(url);
        AKInterface detail = sqlMapper.getDetail(dto);

        if(Objects.isNull(detail)){
            throw new RuntimeException("没有找到对应的URL,请创建");
        }

        // 通过nodeJs转换请求参数
        Object obj = request(
                "POST",
                nodejsUrl + "/translateRequestParams",
                "{\"data\":" + detail.getTableDetail() + "}"
        );
        AKFeildParams feildParams = BeanUtil.copyProperties(obj, AKFeildParams.class);
        // 参数转为对象
        feildParams.setRequestParams(body);
        return feildParams;
    }


    public AKFeildParams getIOParamsById(String interfaceId, Map body) throws IOException {
        AKInterface dto = new AKInterface();
        dto.setId(interfaceId);
        AKInterface detail = sqlMapper.getDetail(dto);

        if(Objects.isNull(detail)){
            throw new RuntimeException("没有找到对应的URL,请创建");
        }

        // 通过nodeJs转换请求参数
        Object obj = request(
                "POST",
                nodejsUrl + "/translateRequestParams",
                "{\"data\":" + detail.getTableDetail() + "}"
        );
        AKFeildParams feildParams = BeanUtil.copyProperties(obj, AKFeildParams.class);
        // 参数转为对象
        feildParams.setRequestParams(body);
        return feildParams;
    }

    public AKFeildPageParams getIOPageParams(String url, AKPageParams<Map> body) throws IOException {
        AKInterface dto = new AKInterface();
        dto.setUrl(url);
        AKInterface detail = sqlMapper.getDetail(dto);

        if(Objects.isNull(detail)){
            throw new RuntimeException("没有找到对应的URL,请创建");
        }
        Map data = new HashMap<>();
        data.put("data", detail.getTableDetail());
        data.put("classAnntations", detail.getClassAnntations());

        // 通过nodeJs转换请求参数
        Object obj = request(
                "POST",
                nodejsUrl + "/translateRequestParams",
                JSON.toJSONString(data)
        );
        AKFeildPageParams feildPageParams = BeanUtil.copyProperties(obj, AKFeildPageParams.class);
        // 参数转为对象
        feildPageParams.setRequestParams(body);
        return feildPageParams  ;
    }

    public Object request(String requestMethod, String path,String postContent) throws IOException {
//        R clSendResult = R.success();
        URL url = new URL(path);
        HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
        httpUrlConnection.setRequestMethod(requestMethod);
        httpUrlConnection.setConnectTimeout(10000);
        httpUrlConnection.setReadTimeout(10000);
        httpUrlConnection.setDoOutput(true);
        httpUrlConnection.setDoInput(true);
        httpUrlConnection.setRequestProperty("Charset", "UTF-8");
        httpUrlConnection.setRequestProperty("Content-Type", "application/json");
        httpUrlConnection.connect();
        if(requestMethod.equals("POST")){
            OutputStream os = httpUrlConnection.getOutputStream();
            os.write(postContent.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }
        StringBuilder sb = new StringBuilder();
        int httpRspCode = httpUrlConnection.getResponseCode();
        if (httpRspCode == HttpURLConnection.HTTP_OK || httpRspCode == HttpURLConnection.HTTP_CREATED) {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(httpUrlConnection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            Object parse = JSON.parse(sb.toString());
            return parse;
        }
        return null;
    }
}
