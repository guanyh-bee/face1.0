package face.controller;

import com.alibaba.fastjson.JSON;
import face.entity.LocalUser;
import face.entity.RegistryUser;
import okhttp3.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

/**
 * @author gyh
 * @create 2020-11-02 13:38
 */
@Controller

public class UserController {
    @GetMapping("/registry")
    public String registry(String id, String username,String password) {
        MediaType mediaType = MediaType.get("application/x-www-form-urlencoded; charset=utf-8");

        OkHttpClient client = new OkHttpClient();
        LocalUser localUser = new LocalUser().setId(id).setUsername(username).setPassword(password);
        RegistryUser registryUser = new RegistryUser().setUser(localUser);
        String json = JSON.toJSONString(registryUser);
        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url("http://192.168.0.118:8090/person/create")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            System.out.println(string);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return "";
    }
}
