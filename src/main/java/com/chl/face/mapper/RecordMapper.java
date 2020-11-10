package com.chl.face.mapper;

import com.chl.face.entity.Record;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author gyh
 * @create 2020-11-03 15:07
 */
@Mapper
public interface RecordMapper {
    Integer insert(Record record);
    List<Record> findById(String id);
}
