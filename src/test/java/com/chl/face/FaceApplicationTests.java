package com.chl.face;

import com.chl.face.entity.TestTime;
import com.chl.face.mapper.TestTimeMapper;
import com.chl.face.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class FaceApplicationTests {

    @Resource
    TestTimeMapper testTimeMapper;

    @Test
    void contextLoads() throws ParseException {




        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.of(2020,11,1), LocalTime.of(0,0));
        LocalDateTime plus = localDateTime.plus(1, ChronoUnit.MONTHS);
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);

        System.err.println(plus);


    }

    @Test
    void test1(){
//        TestTime testTime = new TestTime();
//        testTime.setDate(LocalDateTime.now());
//        testTimeMapper.insert(testTime);


        List<TestTime> testTimes = testTimeMapper.selectAll(4);
        LocalDateTime date = testTimes.get(0).getDate();
        date.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        System.out.println(date);
    }

    @Test
    void test(){
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);

        Instant instant = now.toInstant(ZoneOffset.ofHours(8));
        System.err.println(instant.toEpochMilli());

        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(1604647982536L), ZoneId.of("Asia/Shanghai"));
        Date date = new Date(1604647982536L);
        System.err.println(localDateTime+"**************88"+date);

        System.out.println(Instant.ofEpochMilli(1604647982536L).atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime());
        System.out.println(Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8)));

    }

}
