package face.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gyh
 * @create 2020-11-02 13:54
 */
@Data
@Accessors(chain = true)
public class LocalUser {
    String id;
    String username;
    String password;

}
