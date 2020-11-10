package com.chl.face.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gyh
 * @create 2020-11-02 13:54
 */
@Data
@Accessors(chain = true)
public class LocalUser {
    Integer idSql;
    String id;
    String name;
    String password;
    Integer isFaced;

}
