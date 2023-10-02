package com.blogschool.blogs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private String email;

    @Column
    private String fullName;

    @Column
    private Boolean status;

    @Column
    private Boolean isVerify;


    @OneToMany(mappedBy = "authors")
    private Set<BlogPostEntity> blogAuthors = new HashSet<>();
//    @OneToMany(mappedBy = "authorsModified")
//    private Set<BlogPostEntity> blogAuthorsModified = new HashSet<>();

    @OneToMany(mappedBy = "request")
    private Set<ApprovalRequestEntity> requested = new HashSet<>();

    @OneToMany(mappedBy = "review")
    private Set<ApprovalRequestEntity> reviewed = new HashSet<>();

    @OneToMany(mappedBy = "userComment")
    private Set<CommentEntity> comments = new HashSet<>();

    @OneToMany(mappedBy = "userVote")
    private Set<VoteEntity> votes = new HashSet<>();

    @OneToMany(mappedBy = "notification")
    private Set<NotificationEntity> notificationList = new HashSet<>();

    @OneToMany(mappedBy = "following")
    private Set<FollowEntity> followingList = new HashSet<>();

    @OneToMany(mappedBy = "follower")
    private Set<FollowEntity> followersList = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<UserAwardEntity> userAwards = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "Users_Id"),
            inverseJoinColumns = @JoinColumn(name = "Roles_Id"))
    private Set<RoleEntity> roles = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "user_mark_post",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id"))
    private Set<BlogPostEntity> markPosts = new HashSet<>();

    public UserEntity(String username, String password, String email, String fullName, Boolean status) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.status = status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id, username);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserEntity user = (UserEntity) obj;
        return Objects.equals(Id, user.getId()) &&
                Objects.equals(username, user.getUsername());
    }
}