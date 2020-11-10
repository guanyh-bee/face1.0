package com.chl.face.mapper;

import com.chl.face.entity.LocalUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author gyh
 * @create 2020-11-02 14:51
 */
@Mapper
public interface UserMapper {
    Integer create(LocalUser user);
    List<LocalUser> findById(String id);
    Integer update(LocalUser user);
    Integer updateFace(String id);
}
