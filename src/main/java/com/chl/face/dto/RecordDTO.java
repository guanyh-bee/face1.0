package com.chl.face.dto;

import com.chl.face.entity.LocalUser;
import com.chl.face.entity.Record;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gyh
 * @create 2020-11-03 16:34
 */
@Data
@Accessors(chain = true)
public class RecordDTO {
    Record record;
    String name;
}
