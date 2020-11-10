package com.chl.face.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gyh
 * @create 2020-11-06 15:02
 */
@Data
@Accessors(chain = true)
public class Analysis {
    int day;
    boolean isSunday;
    String  id;
    String name;
    Record max;
    Record min;
    boolean isLate;
    boolean isEarly;
    boolean noEarlyRecord;
    boolean noLateRecord;

    int totalDayOfLate;
}
