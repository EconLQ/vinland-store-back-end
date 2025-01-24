package com.vinland.store.web.blog.service;

import com.vinland.store.web.blog.dao.BlogDAO;
import com.vinland.store.web.blog.dto.BlogDTO;
import com.vinland.store.web.blog.model.Blog;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogService {
    private final BlogMapperService blogMapperService;
    private final BlogDAO blogDAO;

    public Optional<Blog> getBlogById(Long id) {
        return blogDAO.findByIdCustom(id);
    }

    public Page<BlogDTO> getBlogs(Pageable pageable) {
        return blogDAO.findAll(pageable)
                .map(blog -> blogMapperService.blogToBlogDTO(blog, null));
    }
}
