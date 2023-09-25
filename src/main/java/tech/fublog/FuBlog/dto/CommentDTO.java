package tech.fublog.FuBlog.dto;

import lombok.*;
import org.springframework.web.bind.annotation.RequestParam;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long commentId;
    private Long parentCommentId;
    private String content;
    private Long postId;
    private Long userId;
}