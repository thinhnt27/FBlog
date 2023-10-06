package tech.fublog.FuBlog.controller;

import tech.fublog.FuBlog.dto.request.RequestFollowDTO;
import tech.fublog.FuBlog.dto.response.ResponseFollowDTO;
import tech.fublog.FuBlog.exception.FollowException;
import tech.fublog.FuBlog.model.ResponseObject;
import tech.fublog.FuBlog.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth/user")
@CrossOrigin(origins = {"http://localhost:5173", "https://fublog.tech"})
//@CrossOrigin(origins = "*")
public class FollowController {
    private final FollowService followService;

    @Autowired
    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @GetMapping("/follower/view/{userId}")
    public ResponseEntity<ResponseObject> viewFollower(@PathVariable Long userId) {
        try {
            List<ResponseFollowDTO> dtoList = followService.viewFollower(userId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("ok", "found", dtoList));
        } catch (FollowException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("failed", ex.getMessage(), ""));
        }
    }

    @GetMapping("/following/view/{userId}")
    public ResponseEntity<ResponseObject> viewFollowing(@PathVariable Long userId) {
        try {
            List<ResponseFollowDTO> dtoList = followService.viewFollowing(userId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("ok", "found", dtoList));
        } catch (FollowException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("failed", ex.getMessage(), ""));
        }
    }

    @GetMapping("/follower/count/{userId}")
    public ResponseEntity<ResponseObject> countFollower(@PathVariable Long userId) {
        try {
            Long count = followService.countFollower(userId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("ok", "found", count));
        } catch (FollowException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("failed", ex.getMessage(), ""));
        }
    }

    @GetMapping("/following/count/{userId}")
    public ResponseEntity<ResponseObject> countFollowing(@PathVariable Long userId) {
        try {
            Long count = followService.countFollowing(userId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("ok", "found", count));
        } catch (FollowException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("failed", ex.getMessage(), ""));
        }
    }

    @PostMapping("/followAction/{action}")
    public ResponseEntity<ResponseObject> insertFollow(@PathVariable String action, @RequestBody RequestFollowDTO requestFollowDTO) {
        try {
            if (action.equals("follow"))
                followService.insertFollow(requestFollowDTO);
            else if (action.equals("unfollow"))
                followService.unFollow(requestFollowDTO);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("ok", "successfully", ""));
        } catch (FollowException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("failed", ex.getMessage(), ""));
        }
    }
}
