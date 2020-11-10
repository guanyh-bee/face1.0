package com.chl.face.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gyh
 * @create 2020-11-03 15:05
 */
@Data
@Accessors(chain = true)
public class Record {
    Integer id;
    String personId;
    Long time;
    Integer identifyType;
}
