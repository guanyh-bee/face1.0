package com.chl.face.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chl.face.dto.RecordDTO;
import com.chl.face.entity.*;
import com.chl.face.mapper.RecordMapper;
import com.chl.face.mapper.UserMapper;
import okhttp3.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author gyh
 * @create 2020-11-02 13:38
 */
@Controller
public class UserController {
    @Resource
    private UserMapper userMapper;
    @Resource
    private RecordMapper recordMapper;

    private boolean isSuccess = false;

    @GetMapping("/")
    public String toLogin() {
        return "login";
    }

    @GetMapping("/toRegistry")
    public String toRegistry() {
        return "registry";
    }

    @PostMapping("/login")
    public String login(String id, String username, String password, Model model, HttpSession session) {
        List<LocalUser> byId = userMapper.findById(id);
        if (id.equals("") || username.equals("") || password.equals("")) {
            model.addAttribute("msg", "id,用户名，密码不能为空");
            return "login";
        }

        if (byId.size() == 0) {
            model.addAttribute("msg", "用户不存在");
            return "login";
        }
        LocalUser localUser = byId.get(0);

        if (localUser.getName().equals(username) && localUser.getPassword().equals(password)) {
            session.setAttribute("user", localUser);
            return "index";
        } else {
            model.addAttribute("msg", "用户名或者密码错误");
            model.addAttribute("id", id);
            model.addAttribute("name", username);
            return "login";
        }
    }

    @PostMapping("/registry")
    public String registry(String id, String username, String password, Model model) {
        MediaType mediaType = MediaType.get("application/x-www-form-urlencoded; charset=utf-8");

        if (id.equals("") || username.equals("") || password.equals("")) {
            model.addAttribute("msg", "id,用户名，密码不能为空");
            return "registry";
        }

        OkHttpClient client = new OkHttpClient();
        LocalUser localUser = new LocalUser().setId(id).setName(username).setPassword(password);
        RegistryUser registryUser = new RegistryUser().setPerson(localUser);
        String json = JSON.toJSONString(registryUser);
        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url("http://192.168.0.119:8090/person/create")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            JSONObject jsonObject = JSON.parseObject(string);
            Object data = jsonObject.get("code");
            if ("LAN_SUS-0".equals(data.toString())) {
                List<LocalUser> byId = userMapper.findById(localUser.getId());
                if (byId.size() == 0) {
                    userMapper.create(localUser);
                    return "redirect:/";
                } else {
                    if (byId.get(0).getId().equals(id) || byId.get(0).getName().equals(username)) {
                        model.addAttribute("msg", "人员信息已经存在");
                        model.addAttribute("id", id);
                        model.addAttribute("name", username);
                        return "registry";
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @GetMapping("/registry/toFace")
    public String registryToFace() {
        return "faceRegistry";
    }

    @PostMapping("/registry/face")
    public String registryFace(String id, Model model, HttpSession session) {
        MediaType mediaType = MediaType.get("application/x-www-form-urlencoded; charset=utf-8");

        OkHttpClient client = new OkHttpClient();


        Face face = new Face("123456", id);
        String json = JSON.toJSONString(face);


        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url("http://192.168.0.119:8090/face/takeImg")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            JSONObject jsonObject = JSON.parseObject(string);
            Object data = jsonObject.get("code");
            if ("LAN_SUS-0".equals(data.toString())) {
                Long begin = System.currentTimeMillis();
                while (!isSuccess) {
                    long end = System.currentTimeMillis();
                    if (end - begin > 1000 * 60 * 1.5) {
                        model.addAttribute("msg", "注册失败1，请重试");
                        return "faceRegistry";
                    }

                }
                userMapper.updateFace(id);
                List<LocalUser> byId = userMapper.findById(id);
                session.setAttribute("user", byId.get(0));
                return "redirect:/index";
            } else {
                model.addAttribute("msg", "注册失败2，请重试");
                return "faceRegistry";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return "redirect:/registry/toFace";
    }

    @org.springframework.web.bind.annotation.ResponseBody
    @PostMapping("/registry/callback")
    public void regitryCallback(String time, HttpServletResponse response) throws IOException {
        if (time == null) {

        } else {
            isSuccess = true;
            AttendanceCallback aTrue = new AttendanceCallback(1, true);
            String s = JSON.toJSONString(aTrue);
            PrintWriter writer = response.getWriter();
            writer.write(s);
            writer.close();
        }
    }


    @PostMapping("/attendance/callback")
    public void callback(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String times = request.getParameter("time");
        long time = Long.parseLong(times);


        String personId = request.getParameter("personId");

        String identifyType = request.getParameter("identifyType");
        int type = Integer.parseInt(identifyType);

        Record record = new Record().setPersonId(personId).setTime(time).setIdentifyType(type);

        recordMapper.insert(record);
        AttendanceCallback aTrue = new AttendanceCallback(1, true);
        String s = JSON.toJSONString(aTrue);
        PrintWriter writer = response.getWriter();
        writer.write(s);
        writer.close();
//        Map<String, String[]> parameterMap = request.getParameterMap();
//        String[] times = parameterMap.get("time");
//        System.out.println(times.toString());
//        String[] personIds = parameterMap.get("personId");
//        System.out.println("personIds = " + personIds.toString());
//        String[] identifyTypes = parameterMap.get("identifyType");
//        System.out.println("identifyTypes = " + identifyTypes.toString());
    }

    @GetMapping("/toRecord")
    public String toRecord() {
        return "record";
    }

    @PostMapping("/record")
    public String record(Model model, String beginTime, String endTime, HttpSession session) throws ParseException {
        LocalUser user = (LocalUser) session.getAttribute("user");
        String id = user.getId();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Long begin = sdf.parse(beginTime).getTime();
        Long end = sdf.parse(endTime).getTime() + 1000 * 59;
        List<Record> byId = recordMapper.findById(id);
        List<Record> recordList = byId.stream().filter
                (record -> record.getTime() <= end && record.getTime() >= begin).collect(Collectors.toList());
        List<RecordDTO> recordDTOList = new ArrayList<>();
        for (Record record : recordList) {
            RecordDTO recordDTO = new RecordDTO().setRecord(record).setName(user.getName());
            recordDTOList.add(recordDTO);
        }


        model.addAttribute("data", recordDTOList);
        return "record";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/toAnalysis")
    public String toAnalysis() {
        return "analysis";
    }


    @PostMapping("/analysis")
    public String analysis(HttpSession session, String time,Model model) throws ParseException {
        LocalUser user = (LocalUser) session.getAttribute("user");
        String id = user.getId();
        String name = user.getName();
        List<Record> records = recordMapper.findById(id);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM");

        YearMonth begin = YearMonth.parse(time, dateTimeFormatter);
        LocalDateTime beginTime = LocalDateTime.of(begin.getYear(), begin.getMonth(), 1, 0, 0, 0);

        LocalDateTime endTime = LocalDateTime.of(begin.getYear(), begin.getMonth(), begin.lengthOfMonth(), 23, 59, 59);
        List<Record> okRecord = records.stream().filter(record -> LocalDateTime.ofInstant(Instant.ofEpochMilli(record.getTime()), ZoneId.of("Asia/Shanghai")).isAfter(beginTime) && LocalDateTime.ofInstant(Instant.ofEpochMilli(record.getTime()), ZoneId.of("Asia/Shanghai")).isBefore(endTime)).collect(Collectors.toList());
        Set<Integer> days = okRecord.stream().map(record -> Instant.ofEpochMilli(record.getTime()).atZone(ZoneId.of("Asia/Shanghai")).getDayOfMonth()).collect(Collectors.toSet());
        List<Analysis> analyses = new ArrayList<>();

        for (int i = 1; i <= begin.lengthOfMonth(); i++) {
            int finalI = i;

            LocalDate localDate = LocalDate.of(begin.getYear(), begin.getMonth(), i);
            DayOfWeek dayOfWeek = localDate.getDayOfWeek();


            List<Record> list = okRecord.stream().filter(record -> Instant.ofEpochMilli(record.getTime()).atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime().getDayOfMonth() == finalI).collect(Collectors.toList());

            if(dayOfWeek.equals(DayOfWeek.SUNDAY)){
                Analysis analysis = new Analysis().setSunday(true).setDay(i);
                analyses.add(analysis);
                continue;
            }
            if(list.size() == 0){
                Analysis analysis = new Analysis().setNoEarlyRecord(true).setNoLateRecord(true).setDay(i).setName(name).setId(id);
                analyses.add(analysis);

            }else if(list.size() == 1){
                LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(list.get(0).getTime()), ZoneId.of("Asia/Shanghai"));
                int hour = localDateTime.getHour();
                int minute = localDateTime.getMinute();
                if (hour>12){
                    Analysis analysis = new Analysis();
                    if (localDateTime.toLocalTime().isBefore(LocalTime.of(17,30))){
                        analysis.setMax(list.get(0)).setEarly(true).setNoEarlyRecord(true).setDay(i).setName(name).setId(id);
                        analyses.add(analysis);
                    }else {
                        analysis.setMax(list.get(0)).setEarly(false).setNoEarlyRecord(true).setDay(i).setName(name).setId(id);
                        analyses.add(analysis);
                    }
                }else {
                    Analysis analysis = new Analysis();
                    if (localDateTime.toLocalTime().isAfter(LocalTime.of(9,00))){
                        analysis .setMin(list.get(0)).setLate(true).setNoLateRecord(true).setDay(i).setName(name).setId(id);
                        analyses.add(analysis);
                    }else {
                        analysis .setMin(list.get(0)).setLate(false).setNoLateRecord(true).setDay(i).setName(name).setId(id);
                        analyses.add(analysis);
                    }



                }
            }else {
                Record recordMax = list.stream().max((o1, o2) -> (int) (o1.getTime() - o2.getTime())).get();
                Record recordMin = list.stream().min((o1, o2) -> (int) (o1.getTime() - o2.getTime())).get();
                LocalDateTime max = LocalDateTime.ofInstant(Instant.ofEpochMilli(recordMax.getTime()), ZoneId.of("Asia/Shanghai"));

                //当天最后打卡时刻
                LocalTime maxLocalTime = max.toLocalTime();
                LocalDateTime min = LocalDateTime.ofInstant(Instant.ofEpochMilli(recordMin.getTime()), ZoneId.of("Asia/Shanghai"));
                //当天最早打卡时刻
                LocalTime minLocalTime = min.toLocalTime();
                Analysis analysis = new Analysis();
                analysis.setMax(recordMax).setMin(recordMin).setDay(i).setName(name).setId(id);
                //上午未打卡，下午打卡未早退
                if(minLocalTime.isAfter(LocalTime.of(12,00))){
                    if(maxLocalTime.isAfter(LocalTime.of(17,30))){

                    }else {
                        analysis.setEarly(true);
                    }
                    analysis.setNoEarlyRecord(true);
                //上午打卡，下午未打卡未迟到
                }else if(maxLocalTime.isBefore(LocalTime.of(12,0))){
                    if(minLocalTime.isBefore(LocalTime.of(9,0))){
                    }else {
                        analysis.setLate(true);
                    }
                    analysis.setNoLateRecord(true);
                }else if(minLocalTime.isBefore(LocalTime.of(9,0)) && maxLocalTime.isAfter(LocalTime.of(17,30)) ){

                }else if(minLocalTime.isAfter(LocalTime.of(9,0)) && maxLocalTime.isBefore(LocalTime.of(17,30))){
                    analysis.setLate(true).setEarly(true);
                }else if(minLocalTime.isBefore(LocalTime.of(9,0)) && maxLocalTime.isBefore(LocalTime.of(17,30))){
                    analysis.setEarly(true);
                }else if(minLocalTime.isAfter(LocalTime.of(9,0)) && maxLocalTime.isAfter(LocalTime.of(17,30))){
                    analysis.setLate(true);
                }

                analyses.add(analysis);






//                if (maxLocalTime.isAfter(LocalTime.of(17,30))  && minLocalTime.isAfter(LocalTime.of(17,30))){
//
//                    analysis.setMax(recordMax).setEarly(false).setNoEarlyRecord(true);
//                }else if(maxLocalTime.isBefore(LocalTime.of(17,30)) &&  maxLocalTime.isAfter(LocalTime.of(12,0))) {
//
//                    if(minLocalTime.isBefore(LocalTime.of(9,00))){
//                        analysis.setMax(recordMax).setEarly(true);
//                    }
//
//                }
//                if (minLocalTime.isBefore(LocalTime.of(9,00))){
//                    analysis.setMax(recordMax).setLate(false);
//                }else if(minLocalTime.isAfter(LocalTime.of(9,0)) &&  maxLocalTime.isBefore(LocalTime.of(12,0))) {
//                    analysis.setMax(recordMax).setLate(true);
//                }
//                analyses.add(analysis);





            }



        }
        model.addAttribute("info",analyses);
        return "analysis";


//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
//        Long begin = sdf.parse(beginTime).getTime();
//        Long end = sdf.parse(endTime).getTime()+1000*59;
//        List<List<Record>> lists = new ArrayList<>();
//        List<Record> collect = records.stream().filter(record -> record.getTime() > begin && record.getTime() < end).collect(Collectors.toList());
//        List<Date> dates = collect.stream().map(record -> new Date(record.getTime())).collect(Collectors.toList());
//        Set<Integer> days = dates.stream().map(date -> date.getDay()).collect(Collectors.toSet());
//        for (Integer day : days) {
//            Record recordMax = collect.stream().filter(record -> new Date(record.getTime()).getDay() == day).max((o1, o2) -> (int) (o1.getTime() - o2.getTime())).get();
//            Record recordMin = collect.stream().filter(record -> new Date(record.getTime()).getDay() == day).min((o1, o2) -> (int) (o1.getTime() - o2.getTime())).get();
//            ArrayList<Record> recordArrayList = new ArrayList<>();
//            recordArrayList.add(recordMax);
//            recordArrayList.add(recordMin);
//            lists.add(recordArrayList);
//        }
//
//        Optional<Integer> max = days.stream().max((o1, o2) -> o1 - o2);


//        List<Date> list = new ArrayList<>();
//        //获取有记录的天数
//        records.stream().filter(record -> record.getTime() > begin && record.getTime() < end).map(record -> list.add(new Date(record.getTime()))).collect(Collectors.toList());
//
//        Set<Integer> collect = list.stream().map(date -> date.getDay()).collect(Collectors.toSet());
//        //日期最大天数
//        Optional<Integer> max = collect.stream().max((o1, o2) -> o1 - o2);
//
//        for (int i = 0; i < max.get(); i++) {
//
//        }


    }
}
