package com.chl.face.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author gyh
 * @create 2020-11-07 09:18
 */
@Data
@NoArgsConstructor
public class TestTime {
    Integer id;
    LocalDateTime date;
}
