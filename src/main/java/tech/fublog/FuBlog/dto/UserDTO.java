package tech.fublog.FuBlog.dto;

import lombok.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {
    private String fullname;
//    private String password;
    private String email;
    private Long id;
    private String picture;
    private List<String> role;

}