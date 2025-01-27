package com.vinland.store.web.blog.service;

import com.vinland.store.web.blog.dto.BlogDTO;
import com.vinland.store.web.blog.model.Blog;
import com.vinland.store.web.user.model.User;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class BlogMapperService {
    public BlogDTO blogToBlogDTO(Blog blog, User user) {
        BlogDTO blogDTO = new BlogDTO();
        blogDTO.setId(blog.getId());
        blogDTO.setTitle(blog.getTitle());
        blogDTO.setSlug(blog.getSlug());
        blogDTO.setBody(blog.getBody());
        blogDTO.setTags(blog.getTags());
        blogDTO.setPublished(blog.getPublished());
        blogDTO.setViewCount(blog.getViewCount());
        blogDTO.setCreatedAt(blog.getCreatedAt());
        blogDTO.setUpdatedAt(blog.getUpdatedAt());
        blogDTO.setAuthor(user == null ? null : user.getUsername());
        return blogDTO;
    }

    public Blog blogDTOToBlog(@Valid BlogDTO blogDTO, User user) {
        Blog blog = new Blog();
        blog.setTitle(blogDTO.getTitle());
        blog.setSlug(blogDTO.getSlug());
        blog.setBody(blogDTO.getBody());
        blog.setTags(blogDTO.getTags());
        blog.setPublished(blogDTO.getPublished());
        blog.setViewCount(blogDTO.getViewCount());
        blog.setCreatedAt(blogDTO.getCreatedAt());
        blog.setUpdatedAt(blogDTO.getUpdatedAt());
        blog.setAuthor(user);
        return blog;
    }
}
