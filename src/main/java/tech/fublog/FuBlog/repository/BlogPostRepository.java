package tech.fublog.FuBlog.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.fublog.FuBlog.dto.BlogPostDTO;
import tech.fublog.FuBlog.dto.response.MonthlyPostCountDTO;
import tech.fublog.FuBlog.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPostEntity, Long> {
    List<BlogPostEntity> findByIsApproved(Boolean isApproved);
    List<BlogPostEntity> findByAuthorsAndStatusIsTrue(UserEntity userEntity);

    Optional<BlogPostEntity> findByIdAndStatusIsTrueAndIsApprovedIsTrue(Long postId);
    List<BlogPostEntity> findByTitleLike(String title);

    //    public List<BlogPostEntity> getBlogPostEntitiesByTitle(String title, Pageable pageable);
    public Page<BlogPostEntity> getBlogPostEntitiesByTitleLikeAndIsApprovedIsTrueAndStatusIsTrue(String title, Pageable pageable);

//    List<BlogPostEntity> findAllByCategory(Long id);

    Page<BlogPostEntity> findByCategory(CategoryEntity category, Pageable pageable);

    Integer countByCategoryAndIsApprovedTrueAndStatusTrue(CategoryEntity category);

    @Query("SELECT bp FROM BlogPostEntity bp WHERE bp.category IN:categoryEntityList AND " +
            "bp.isApproved = true AND bp.status = true ORDER BY bp.category.name asc ")
    Page<BlogPostEntity> findByCategoryInAndIsApprovedTrueAndStatusTrue(@Param("categoryEntityList") List<CategoryEntity> categoryEntityList,
                                                                        Pageable pageable);

    @Query("SELECT bp FROM BlogPostEntity bp WHERE bp.category IN:categoryEntityList AND " +
            "bp.isApproved = true AND bp.status = true ORDER BY bp.category.name asc ")
    List<BlogPostEntity> findByCategoryInAndIsApprovedTrueAndStatusTrue(@Param("categoryEntityList") List<CategoryEntity> categoryEntityList);

    @Query("SELECT bp FROM BlogPostEntity bp JOIN FollowEntity f WHERE bp.authors.id = f.following.id AND f.follower.id = :userId AND " +
            "bp.isApproved = true AND bp.status = true")
    List<BlogPostEntity> findByFollowAndIsApprovedTrueAndStatusTrue(@Param("userId") Long userId);

    //    @Query("SELECT bp FROM BlogPostEntity bp WHERE (bp.category.id = :categoryId OR bp.category.parentCategory.id = :categoryId) " +
//            "AND (bp.isApproved = true AND bp.status = true) ORDER BY bp.category.categoryName asc ")
//    Page<BlogPostEntity> findBlogPostsByCategoryIdOrParentId(
//            @Param("categoryId") Long categoryId,
//            Pageable pageable
//    );

    @Query("SELECT count(b) FROM BlogPostEntity b WHERE year(b.createdDate) = year(current_date) AND month(b.createdDate) = month(current_date)")
    Double countAllInCurrentMonth();

    @Query("SELECT count(b) FROM BlogPostReportEntity b WHERE year(b.createdDate) = year(current_date) AND month(b.createdDate) = month(current_date) - 1")
    Double countAllInPreviousMonth();

    @Query("SELECT count(b) FROM BlogPostReportEntity b WHERE year(b.createdDate)= year(current_date) - 1 AND month(b.createdDate)= month(current_date) + 11")
    Double countAllInPreviousMonthAndYear();

    Set<BlogPostEntity> findAllByAuthors(UserEntity userEntity);

    BlogPostEntity findByPinnedIsTrue();

    //    @Query("SELECT e FROM BlogPostEntity e ORDER BY e.createdDate DESC")
    Page<BlogPostEntity> findAllByStatusIsTrueAndIsApprovedIsTrue(Pageable pageable);

    Page<BlogPostEntity> findAllByStatusTrueAndIsApprovedTrueOrderByCreatedDateDesc(Pageable pageable);
    List<BlogPostEntity> findAllByStatusTrueAndIsApprovedTrueOrderByCreatedDateDesc();

    Page<BlogPostEntity> findAllByStatusTrueAndIsApprovedTrueOrderByCreatedDateAsc(Pageable pageable);

    Page<BlogPostEntity> findAllByStatusTrueAndIsApprovedTrueOrderByModifiedDateDesc(Pageable pageable);

    Page<BlogPostEntity> findAllByStatusTrueAndIsApprovedTrueOrderByModifiedDateAsc(Pageable pageable);

    //    Page<BlogPostEntity> findAllByOrderByViewDesc(Pageable pageable);
    Page<BlogPostEntity> findAllByStatusTrueAndIsApprovedTrueOrderByViewDesc(Pageable pageable);

    Page<BlogPostEntity> findByPostTagsTag(TagEntity tag, Pageable pageable);

    Page<BlogPostEntity> findByAuthorsAndStatusTrueAndIsApprovedTrueOrderByCreatedDateDesc(UserEntity userEntity, Pageable pageable);

    @Query("SELECT bp FROM BlogPostEntity bp WHERE bp.status = true AND bp.isApproved = true ORDER BY bp.view DESC LIMIT 6")
    List<BlogPostEntity> findAllByStatusTrueAndIsApprovedTrueOrderByViewDesc();

    @Query("SELECT bp FROM BlogPostEntity bp WHERE bp.status = true AND bp.isApproved = true ORDER BY SIZE(bp.votes) DESC LIMIT 6")
    List<BlogPostEntity> findAllByStatusTrueAndIsApprovedTrueOrderByVoteDesc();

    @Query("SELECT bp FROM BlogPostEntity bp WHERE bp.status = true AND bp.isApproved = true ORDER BY bp.view DESC, bp.createdDate DESC")
    Page<BlogPostEntity> findAllByTrending(Pageable pageable);

    @Query("SELECT bp FROM BlogPostEntity bp WHERE bp.status = true AND bp.isApproved = true ORDER BY SIZE(bp.votes) DESC, bp.createdDate DESC")
    Page<BlogPostEntity> findAllByVote(Pageable pageable);

    List<BlogPostEntity> findByUserMarksAndStatusTrueAndIsApprovedTrueOrderByCreatedDateDesc(UserEntity userEntity);

    @Query("SELECT bp FROM BlogPostEntity bp JOIN ApprovalRequestEntity a WHERE bp.category IN:categoryEntityList AND " +
            "bp.isApproved = false AND a.review.id = null")
    List<BlogPostEntity> findByCategoryInAndIsApprovedFalse(@Param("categoryEntityList") List<CategoryEntity> categoryEntityList);

    @Query("SELECT bp FROM BlogPostEntity bp JOIN ApprovalRequestEntity a WHERE bp.isApproved = false AND a.review.id = null")
    List<BlogPostEntity> findByBlogByRequestIsApprovedFalse();

    @Query("SELECT MONTH(b.createdDate) as month, COUNT(b.id) as postCount " +
            "FROM BlogPostEntity b " +
            "WHERE YEAR(b.createdDate) = YEAR(CURRENT_DATE) " +
            "GROUP BY MONTH(b.createdDate)")
    List<Object[]> countPostsByMonth();
}
