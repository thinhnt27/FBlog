package tech.fublog.FuBlog.controller;

import tech.fublog.FuBlog.Utility.TokenChecker;
import tech.fublog.FuBlog.dto.request.FollowRequestDTO;
import tech.fublog.FuBlog.dto.response.FollowResponseDTO;
import tech.fublog.FuBlog.exception.FollowException;
import tech.fublog.FuBlog.model.ResponseObject;
import tech.fublog.FuBlog.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.fublog.FuBlog.service.JwtService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth/user")
@CrossOrigin(origins = {"http://localhost:5173", "https://fublog.tech"})
//@CrossOrigin(origins = "*")
public class FollowController {
    private final FollowService followService;
    private final JwtService jwtService;

    @Autowired
    public FollowController(FollowService followService, JwtService jwtService, TokenChecker tokenChecker) {
        this.followService = followService;
        this.jwtService = jwtService;
    }

    @GetMapping("/follower/view/{userId}")
    public ResponseEntity<ResponseObject> viewFollower(@RequestHeader("Authorization") String token,
                                                       @PathVariable Long userId) {
        try {
            if (TokenChecker.checkToken(token)) {
                List<FollowResponseDTO> dtoList = followService.viewFollower(userId);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("ok", "found", dtoList));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("failed", "not found", ""));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("failed", ex.getMessage(), ""));
        }
    }

    @GetMapping("/following/view/{userId}")
    public ResponseEntity<ResponseObject> viewFollowing(@RequestHeader("Authorization") String token,
                                                        @PathVariable Long userId) {
        try {
            if (TokenChecker.checkToken(token)) {
                List<FollowResponseDTO> dtoList = followService.viewFollowing(userId);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("ok", "found", dtoList));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("failed", "not found", ""));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("failed", ex.getMessage(), ""));
        }
    }

    @GetMapping("/follower/count/{userId}")
    public ResponseEntity<ResponseObject> countFollower(@RequestHeader("Authorization") String token,
                                                        @PathVariable Long userId) {
        try {
            if (TokenChecker.checkToken(token)) {
                Long count = followService.countFollower(userId);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("ok", "found", count));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("failed", "not found", ""));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("failed", ex.getMessage(), ""));
        }
    }

    @GetMapping("/following/count/{userId}")
    public ResponseEntity<ResponseObject> countFollowing(@RequestHeader("Authorization") String token,
                                                         @PathVariable Long userId) {
        try {
            if (TokenChecker.checkToken(token)) {
                Long count = followService.countFollowing(userId);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("ok", "found", count));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("failed", "not found", ""));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("failed", ex.getMessage(), ""));
        }
    }

    @PostMapping("/followAction/{action}")
    public ResponseEntity<ResponseObject> insertFollow(@RequestHeader("Authorization") String token,
                                                       @PathVariable String action, @RequestBody FollowRequestDTO followRequestDTO) {
        try {
            if (TokenChecker.checkToken(token)) {
                if (action.equals("follow"))
                    followService.insertFollow(followRequestDTO);
                else if (action.equals("unfollow"))
                    followService.unFollow(followRequestDTO);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("ok", "successfully", ""));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("failed", "not found", ""));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("failed", ex.getMessage(), ""));
        }
    }


    @PutMapping("/checkFollowAction")
    public ResponseEntity<ResponseObject> checkFollow(@RequestHeader("Authorization") String token,
                                                      @RequestBody FollowRequestDTO followRequestDTO) {
        try {
            if (TokenChecker.checkToken(token)) {
                boolean result = followService.checkFollow(followRequestDTO);
                if (result)
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ResponseObject("ok", "You already follow this user", true));
                else
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ResponseObject("ok", "You hasn't follow this user", false));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("failed", "not found", ""));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("failed", ex.getMessage(), ""));
        }
    }
}
