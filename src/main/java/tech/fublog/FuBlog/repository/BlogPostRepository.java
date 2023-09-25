package tech.fublog.FuBlog.repository;

import tech.fublog.FuBlog.entity.BlogPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPostEntity, Long> {
    List<BlogPostEntity> findByIsApproved(Boolean isApproved);

    List<BlogPostEntity> findByTitleContaining(String title);
}
