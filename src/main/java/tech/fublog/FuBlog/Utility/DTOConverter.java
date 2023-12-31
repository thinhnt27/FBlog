package tech.fublog.FuBlog.Utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.fublog.FuBlog.dto.BlogPostDTO;
import tech.fublog.FuBlog.dto.CategoryDTO;
import tech.fublog.FuBlog.dto.TagDTO;
import tech.fublog.FuBlog.dto.request.BlogPostReportDTO;
import tech.fublog.FuBlog.dto.request.UserReportDTO;
import tech.fublog.FuBlog.dto.response.CategoryResponseDTO;
import tech.fublog.FuBlog.dto.response.CommentResponseDTO;
import tech.fublog.FuBlog.dto.response.UserInfoResponseDTO;
import tech.fublog.FuBlog.dto.response.UserRankDTO;
import tech.fublog.FuBlog.entity.*;
import tech.fublog.FuBlog.exception.BlogPostException;
import tech.fublog.FuBlog.repository.*;
import tech.fublog.FuBlog.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DTOConverter {
    private static UserService userService;
    private static CategoryRepository categoryRepository;
    private static CommentRepository commentRepository = null;
    private static VoteRepository voteRepository = null;
    private static UserRepository userRepository = null;
    private static AwardRepository awardRepository = null;
    private static BlogPostRepository blogPostRepository = null;
    private static UserReportRepository userReportRepository = null;
    private static BlogPostReportRepository blogPostReportRepository = null;

    @Autowired
    public DTOConverter(CategoryRepository categoryRepository, CommentRepository commentRepository, VoteRepository voteRepository, UserRepository userRepository, BlogPostRepository blogPostRepository, AwardRepository awardRepository, UserReportRepository userReportRepository, UserService userService, BlogPostReportRepository blogPostReportRepository) {
        this.categoryRepository = categoryRepository;
        this.commentRepository = commentRepository;
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.blogPostRepository = blogPostRepository;
        this.awardRepository = awardRepository;
        this.userReportRepository = userReportRepository;
        this.userService = userService;
        this.blogPostReportRepository = blogPostReportRepository;
    }

    public static CategoryResponseDTO convertResponseCategoryToDTO(CategoryEntity categoryEntity) {
        CategoryResponseDTO responseCategoryDTO = new CategoryResponseDTO();
        responseCategoryDTO.setCategoryId(categoryEntity.getId());
        responseCategoryDTO.setCategoryName(categoryEntity.getName());
        if (categoryEntity.getParentCategory() != null)
            responseCategoryDTO.setParentCategoryId(categoryEntity.getParentCategory().getId());
        List<CategoryEntity> subCategory = categoryRepository.findByParentCategory(categoryEntity);
        List<CategoryResponseDTO> subcategoryDTOResponse = new ArrayList<>();
        for (CategoryEntity sub : subCategory) {
            CategoryResponseDTO subResponseCategoryDTOs = convertResponseCategoryToDTO(sub);
            subcategoryDTOResponse.add(subResponseCategoryDTOs);
        }
        responseCategoryDTO.setSubCategory(subcategoryDTOResponse);
        return responseCategoryDTO;
    }

    public static CategoryDTO convertCategoryToDTO(CategoryEntity categoryEntity) {
        CategoryDTO CategoryDTO = new CategoryDTO();
        CategoryDTO.setCategoryId(categoryEntity.getId());
        CategoryDTO.setCategoryName(categoryEntity.getName());
        if (categoryEntity.getParentCategory() != null)
            CategoryDTO.setParentCategoryId(categoryEntity.getParentCategory().getId());
        return CategoryDTO;
    }

    public static BlogPostDTO convertPostToDTO(Long postId) {

        BlogPostEntity blogPostEntity = blogPostRepository.findById(postId).orElse(null);

        if (blogPostEntity != null) {
            UserEntity userEntity = userRepository.findById(blogPostEntity.getAuthors().getId()).orElse(null);

            Set<RoleEntity> roleEntities = userEntity.getRoles();
            Set<PostTagEntity> postTagEntity = blogPostEntity.getPostTags();
            Set<TagDTO> tagDTOs = postTagEntity.stream()
                    .map(tagEntity -> {
                        TagDTO tagDTO = new TagDTO();
                        tagDTO.setTagId(tagEntity.getId());
                        tagDTO.setTagName(tagEntity.getTag().getTagName());
                        return tagDTO;
                    })
                    .collect(Collectors.toSet());
            BlogPostReportEntity blogRejectedId = blogPostReportRepository.findBlogRejectedById(blogPostEntity.getId());
            UserInfoResponseDTO userDTO = convertUserDTO(userEntity);
            BlogPostDTO blogPostDTO = new BlogPostDTO(blogPostEntity.getId(),
                    blogPostEntity.getTypePost(),
                    blogPostEntity.getTitle(),
                    blogPostEntity.getContent(),
                    blogPostEntity.getPicture(),
                    blogPostEntity.getCategory().getName(),
                    blogPostEntity.getCategory().getParentCategory(),
                    tagDTOs,
                    userDTO,
                    blogPostEntity.getView(),
                    blogPostEntity.getCreatedDate(),
                    voteRepository.countByPostVote(blogPostEntity),
                    commentRepository.countByPostComment(blogPostEntity),
                    (long )blogPostEntity.getUserMarks().size(),
                    blogPostEntity.getIsApproved() ? "Approved" : "Rejected",
                    blogPostEntity.getIsApproved() ? null
                            : (blogPostReportRepository.findBlogRejectedById(blogPostEntity.getId()) != null
                    ? blogPostReportRepository.findBlogRejectedById(blogPostEntity.getId()).getReason() : null)
            );
            return blogPostDTO;
        } else
            throw new BlogPostException("not found blogpost with " + postId);

    }

    public static List<BlogPostDTO> convertPostListToDTO(List<BlogPostEntity> list) {
        List<BlogPostDTO> blogPostDTOList = new ArrayList<>();
        if (list != null) {
            for (BlogPostEntity entity : list) {
                blogPostDTOList.add(DTOConverter.convertPostToDTO(entity.getId()));
            }
        }
        return blogPostDTOList;
    }

    public static UserInfoResponseDTO convertUserDTO(UserEntity userEntity) {
        if (userEntity != null) {

            Set<RoleEntity> roleEntities = userEntity.getRoles();
            List<String> roleNames = roleEntities.stream()
                    .map(RoleEntity::getName)
                    .collect(Collectors.toList());

            UserInfoResponseDTO userDTO = new UserInfoResponseDTO(
                    userEntity.getId(),
                    userEntity.getFullName(),
                    userEntity.getPicture(),
                    userEntity.getEmail(),
                    roleNames.get(roleNames.size() - 1),
                    roleNames,
                    userEntity.getPoint(),
                    userEntity.getUsername()
            );
            return userDTO;
        } else
            throw new BlogPostException("not found user with " + userEntity.getId());

    }

    public static UserRankDTO convertUserRankDTO(UserEntity userEntity) {
        if (userEntity != null) {

            Set<RoleEntity> roleEntities = userEntity.getRoles();
            List<String> roleNames = roleEntities.stream()
                    .map(RoleEntity::getName)
                    .collect(Collectors.toList());
            Optional<AwardEntity> award = awardRepository.findById(userEntity.getId());
            String awardName = award.isPresent() ? award.get().getName() : null;

            UserRankDTO userDTO = new UserRankDTO(
                    userEntity.getId(),
                    userEntity.getFullName(),
                    userEntity.getPicture(),
                    userEntity.getEmail(),
                    roleNames.get(roleNames.size() - 1),
                    roleNames,
                    userEntity.getPoint(),
                    awardName,
                    userService.countViewOfBlog(userEntity.getId(), true),
                    userService.countVoteOfBlog(userEntity.getId(), true)
            );
            return userDTO;
        } else
            throw new BlogPostException("not found user with " + userEntity.getId());

    }

    public static CommentResponseDTO convertResponseDTO(CommentEntity commentEntity) {

        if (commentEntity.getStatus()) {
            CommentResponseDTO commentResponseDTO = new CommentResponseDTO();
            commentResponseDTO.setCommentId(commentEntity.getId());
            if (commentEntity.getParentComment() != null)
                commentResponseDTO.setParentCommentId(commentEntity.getParentComment().getId());
            commentResponseDTO.setContent(commentEntity.getContent());
            commentResponseDTO.setPostId(commentEntity.getPostComment().getId());
            commentResponseDTO.setUserId(commentEntity.getUserComment().getId());
            commentResponseDTO.setStatus(commentEntity.getStatus());
            List<CommentEntity> subComment = commentRepository.findByParentComment(commentEntity);
            List<CommentResponseDTO> dtoList = new ArrayList<>();
            for (CommentEntity sub : subComment) {
                CommentResponseDTO subCommentResponseDTOs = convertResponseDTO(sub);
                dtoList.add(subCommentResponseDTOs);
            }
            commentResponseDTO.setSubComment(dtoList);
            return commentResponseDTO;
        } else return null;
    }

    public static UserReportDTO convertUserReportDTO(UserReportEntity userReportEntity) {
        return new UserReportDTO(userReportEntity.getReason(), userReportEntity.getReporterId().getId(), userReportEntity.getReportedUserId().getId(), userReportEntity.getCreatedDate());
    }

    public static BlogPostReportDTO convertBlogReportDTO(BlogPostReportEntity blogPostReportEntity) {
        return new BlogPostReportDTO(blogPostReportEntity.getReason(), blogPostReportEntity.getUser().getId(), blogPostReportEntity.getBlog().getId(), blogPostReportEntity.getCreatedDate());
    }
}
