package com.vinland.store.web.blog.service;

import com.vinland.store.web.blog.dto.BlogDTO;
import com.vinland.store.web.blog.model.Blog;
import com.vinland.store.web.user.model.User;
import org.springframework.stereotype.Service;

@Service
public class BlogMapperService {
    public BlogDTO blogToBlogDTO(Blog blog, User user) {
        BlogDTO blogDTO = new BlogDTO();
        blogDTO.setId(blog.getId());
        blogDTO.setTitle(blog.getTitle());
        blogDTO.setBody(blog.getBody());
        blogDTO.setTags(blog.getTags());
        blogDTO.setPublished(blog.getPublished());
        blogDTO.setViewCount(blog.getViewCount());
        blogDTO.setCreatedAt(blog.getCreatedAt());
        blogDTO.setAuthor(user == null ? null : user.getUsername());
        return blogDTO;
    }
}
