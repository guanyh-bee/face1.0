package com.chl.face.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gyh
 * @create 2020-11-02 13:59
 */
@Data
@Accessors(chain = true)
public class RegistryUser {
    private String pass = "123456";
    private LocalUser person;
}
