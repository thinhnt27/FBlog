package tech.fublog.FuBlog.service;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import tech.fublog.FuBlog.dto.BlogPostDTO;
import tech.fublog.FuBlog.dto.SortDTO;
import tech.fublog.FuBlog.entity.BlogPostEntity;
import tech.fublog.FuBlog.entity.CategoryEntity;
import tech.fublog.FuBlog.entity.UserEntity;
import tech.fublog.FuBlog.repository.BlogPostRepository;
import tech.fublog.FuBlog.repository.CategoryRepository;
import tech.fublog.FuBlog.repository.UserRepository;
import tech.fublog.FuBlog.model.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.swing.text.View;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class BlogPostService {

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;


    public ResponseEntity<ResponseObject> getAllBlogPosts() {
        List<BlogPostEntity> blogPostEntity = blogPostRepository.findAll();
        List<BlogPostDTO> blogPostList = new ArrayList<>();

        if (!blogPostEntity.isEmpty()) {
            for (int i = 0; i < blogPostEntity.size(); i++) {
                if (blogPostEntity.get(i).getStatus() != null
                        && blogPostEntity.get(i).getStatus()) {
                    BlogPostDTO blogPost = new BlogPostDTO(blogPostEntity.get(i).getTypePost(),
                            blogPostEntity.get(i).getTitle(),
                            blogPostEntity.get(i).getContent(),
                            blogPostEntity.get(i).getCategory().getId(),
                            blogPostEntity.get(i).getAuthors().getId(),
                            blogPostEntity.get(i).getCreatedDate(),
                            blogPostEntity.get(i).getViews());

                    blogPostList.add(blogPost);
                }
            }
            sort(blogPostList);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("ok", "found", blogPostList));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseObject("failed", "not found", ""));
    }


    public BlogPostEntity getBlogById(Long postId) {

        return blogPostRepository.findById(postId).orElse(null);
    }

    public ResponseEntity<ResponseObject> getBlogPostById(Long postId) {

        BlogPostEntity blogPostEntity = blogPostRepository.findById(postId).orElse(null);

        if (blogPostEntity != null) {
            blogPostEntity.setViews(blogPostEntity.getViews());

            blogPostRepository.save(blogPostEntity);


            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("found", "post found", blogPostEntity));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseObject("not found", "post not found", ""));
    }


    //xoá --> set Status = 0
    public ResponseEntity<ResponseObject> deleteBlogPost(Long postId) {
        Optional<BlogPostEntity> blogPostEntity = blogPostRepository.findById(postId);

        if (blogPostEntity.isPresent()
                && blogPostEntity.get().getStatus()) {
            BlogPostEntity blogPost = this.getBlogById(postId);

            blogPost.setStatus(false);

            blogPostRepository.save(blogPost);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("ok", "deleted successful", ""));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseObject("failed", "deleted failed", ""));
    }


    public BlogPostEntity createBlogPost(BlogPostDTO blogPostDTO) {
        Optional<CategoryEntity> categoryEntity = categoryRepository.findById(blogPostDTO.getCategory());
        Optional<UserEntity> userEntity = userRepository.findById(blogPostDTO.getAuthors());
//        Optional<UserEntity> userEntity = userRepository.findByUsername(user);

        if (categoryEntity.isPresent()
                && userEntity.isPresent()) {


            CategoryEntity category = categoryEntity.get();
            UserEntity authors = userEntity.get();

            Date createdDate = new Date();
            ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh"); // Múi giờ Việt Nam
            createdDate.toInstant().atZone(zoneId).toLocalDateTime();

            String typePost = blogPostDTO.getTypePost();
            String title = blogPostDTO.getTitle();
            String content = blogPostDTO.getContent();
            Double point = userEntity.get().getPoint();
            userEntity.get().setPoint(point + 1);

//            //tạo và save bài BlogPost mới
            BlogPostEntity newBlogPost = new BlogPostEntity(typePost, title, content, createdDate, null,
                    null, true, false, category, authors);
            return blogPostRepository.save(newBlogPost);
        }

        return null;
    }


    public ResponseEntity<ResponseObject> updateBlogPost(Long postId, BlogPostDTO blogPostDTO) {

        Optional<CategoryEntity> categoryEntity = categoryRepository.findById(blogPostDTO.getCategory());
        Optional<UserEntity> userEntity = userRepository.findById(blogPostDTO.getAuthors());
        Optional<BlogPostEntity> blogPostEntity = blogPostRepository.findById(postId);

        if (blogPostEntity.isPresent()
                && categoryEntity.isPresent()
                && userEntity.isPresent()) {

            BlogPostEntity blogPost = this.getBlogById(postId);

            CategoryEntity category = categoryEntity.get();
//            UserEntity authors = userEntity.get();
            Date modifiedDate = new Date();
            ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
            modifiedDate.toInstant().atZone(zoneId).toLocalDateTime();

            blogPost.setTypePost(blogPostDTO.getTypePost());
            blogPost.setTitle(blogPostDTO.getTitle());
            blogPost.setContent(blogPostDTO.getContent());
            blogPost.setModifiedDate(modifiedDate);
            blogPost.setCategory(category);
//            blogPost.setAuthorsModified(authors);

            blogPostRepository.save(blogPost);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("ok", "updated successful", ""));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseObject("failed", "updated failed", ""));
    }


    public ResponseEntity<ResponseObject> findBlogByCategory(String name) {

        Optional<CategoryEntity> categoryEntity = categoryRepository.findByCategoryName(name);
        List<BlogPostDTO> blogPostList = new ArrayList<>();

        if (categoryEntity != null) {

            for (int i = 0; i < categoryEntity.get().getBlogPosts().size(); i++) {
                if (categoryEntity.get().getBlogPosts().get(i).getStatus() != null
                        && categoryEntity.get().getBlogPosts().get(i).getStatus()) {
                    BlogPostDTO blogPost = new BlogPostDTO(categoryEntity.get().getBlogPosts().get(i).getTypePost(),
                            categoryEntity.get().getBlogPosts().get(i).getTitle(),
                            categoryEntity.get().getBlogPosts().get(i).getContent(),
                            categoryEntity.get().getBlogPosts().get(i).getCategory().getId(),
                            categoryEntity.get().getBlogPosts().get(i).getAuthors().getId(),
                            categoryEntity.get().getBlogPosts().get(i).getCreatedDate(),
                            categoryEntity.get().getBlogPosts().get(i).getViews());

                    blogPostList.add(blogPost);
                }
            }

            if (!blogPostList.isEmpty()) {

                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("ok", "found", blogPostList));
            } else {

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject("failed", "cannot found any blog with category", ""));
            }
        } else {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject("failed", "category not exists", ""));
        }
    }


    //sort theo thứ tự bài BlogPost mới nằm đầu tiên (giảm dần theo NGÀY)
    public void sort(List<BlogPostDTO> blogPostList) {

        Collections.sort(blogPostList, new Comparator<BlogPostDTO>() {

            @Override
            public int compare(BlogPostDTO o1, BlogPostDTO o2) {

                if (o1.getCreatedDate().compareTo(o2.getCreatedDate()) < 0) {
                    return 1;
                } else if (o1.getCreatedDate().compareTo(o2.getCreatedDate()) == 0) {
                    return 0;
                } else {
                    return -1;
                }
            }

        });
    }

    public List<BlogPostEntity> getAllBlogPost(int page, int size){
        Pageable pageable = PageRequest.of(page-1,size);
        return  blogPostRepository.findAll(pageable).get().toList();
    }

    public List<BlogPostEntity> getAllBlogPostByTitle(String title,int page, int size){
        Pageable pageable = PageRequest.of(page-1,size);
        return  blogPostRepository.getBlogPostEntitiesByTitle(title, pageable);
    }

    public Page<BlogPostEntity> getBlogPostsByCategoryId(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page-1,size);
        Optional<CategoryEntity> categoryOptional = categoryRepository.findById(categoryId);

        if (!categoryOptional.isPresent()) {
            return new PageImpl<>(Collections.emptyList());
        }

        CategoryEntity category = categoryOptional.get();
        return blogPostRepository.findByCategory(category,pageable);
    }

    public Page<SortDTO> getSortedBlogPosts(String sortBy, int page, int size) {
        Pageable pageable = PageRequest.of(page-1,size);
        Page<SortDTO> sortedBlogPosts;

        if ("newest".equalsIgnoreCase(sortBy)) {
            sortedBlogPosts = blogPostRepository.findAllByOrderByCreatedDateDesc(pageable);
        } else if ("oldest".equalsIgnoreCase(sortBy)) {
            sortedBlogPosts = blogPostRepository.findAllByOrderByCreatedDateAsc(pageable);
        } else if ("latestModified".equalsIgnoreCase(sortBy)) {
            sortedBlogPosts = blogPostRepository.findAllByOrderByModifiedDateDesc(pageable);
        } else if ("oldestModified".equalsIgnoreCase(sortBy)) {
            sortedBlogPosts = blogPostRepository.findAllByOrderByModifiedDateAsc(pageable);
        } else if ("mostViewed".equalsIgnoreCase(sortBy)) {
            return blogPostRepository.findAllByOrderByViewsDesc(pageable);
        } else {
            // Mặc định, sắp xếp theo ngày tạo mới nhất.
            sortedBlogPosts = blogPostRepository.findAllByOrderByCreatedDateDesc(pageable);
        }

        return sortedBlogPosts;
    }

}
