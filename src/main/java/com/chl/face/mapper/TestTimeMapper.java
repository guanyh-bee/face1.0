package com.chl.face.mapper;

import com.chl.face.entity.TestTime;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author gyh
 * @create 2020-11-07 09:19
 */
@Mapper
public interface TestTimeMapper {
    Integer insert(TestTime testTime);
    List<TestTime> selectAll(Integer id);
}
