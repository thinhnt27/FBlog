package tech.fublog.FuBlog.repository;

import tech.fublog.FuBlog.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    CategoryEntity findByCategoryName(String categoryName);

    List<CategoryEntity> findByParentCategory(CategoryEntity categoryEntity);

//    @Query("SELECT c FROM CategoryEntity c WHERE c.id = c.parentCategory")
    List<CategoryEntity> findByParentCategoryIsNull();
}
