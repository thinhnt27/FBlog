package tech.fublog.FuBlog.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostMarkDTO {
    private Long postId;
    private Long userId;
}
