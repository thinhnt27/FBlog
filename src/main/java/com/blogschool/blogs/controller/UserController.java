package com.blogschool.blogs.controller;

import com.blogschool.blogs.dto.PostMarkDTO;
import com.blogschool.blogs.entity.ResponseObject;
import com.blogschool.blogs.exception.UserException;
import com.blogschool.blogs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/blogPosts/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/mark")
    public ResponseEntity<ResponseObject> markBook(@RequestBody PostMarkDTO postMarkDTO) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("ok", "found", userService.markPost(postMarkDTO.getUserId(), postMarkDTO.getPostId())));
        } catch (UserException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("failed", ex.getMessage(), ""));
        }

    }
}