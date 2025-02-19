package com.vinland.store.web.blog.service;

import com.vinland.store.web.blog.dao.BlogDAO;
import com.vinland.store.web.blog.dto.BlogDTO;
import com.vinland.store.web.blog.model.Blog;
import com.vinland.store.web.user.model.User;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlogService {
    private final BlogMapperService blogMapperService;
    private final BlogDAO blogDAO;

    public Optional<Blog> getBlogById(Long id) {
        return blogDAO.findByIdCustom(id);
    }

    public Page<BlogDTO> getBlogs(Pageable pageable) {
        return blogDAO.findAll(pageable)
                .map(blogMapperService::blogToBlogDTO);
    }

    @Transactional
    public Blog updateBlog(Long id, @Valid BlogDTO blogDTO, User user) {
        if (getBlogById(id).isEmpty()) {
            throw new IllegalArgumentException("Blog with id " + blogDTO.getId() + " not found");
        }
        Blog blog = blogMapperService.blogDTOToBlog(blogDTO, user);
        log.info("Updated blog with id: {}", blogDTO.getId());
        return blogDAO.save(blog);
    }

    @Transactional
    public void deleteBlog(Long id) {
        if (!blogDAO.existsById(id)) {
            throw new IllegalArgumentException("Blog not found");
        }
        log.info("Deleted blog with id: {}", id);
        blogDAO.deleteById(id);
    }

    @Transactional
    public Blog createBlog(@Valid BlogDTO blogDTO, User user) {
        if (blogDTO.getId() != null) {
            throw new IllegalArgumentException("Blog with id " + blogDTO.getId() + " already exists");
        }
        Blog blog = blogMapperService.blogDTOToBlog(blogDTO, user);
        return blogDAO.save(blog);
    }
}
