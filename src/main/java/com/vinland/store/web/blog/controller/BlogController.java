package com.vinland.store.web.blog.controller;

import com.vinland.store.utils.PathConstants;
import com.vinland.store.web.blog.dto.BlogDTO;
import com.vinland.store.web.blog.dto.BlogModelAssembler;
import com.vinland.store.web.blog.service.BlogMapperService;
import com.vinland.store.web.blog.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(PathConstants.BLOG)
@CrossOrigin
@RequiredArgsConstructor
public class BlogController {
    private final BlogMapperService blogMapperService;
    private final BlogModelAssembler blogModelAssembler;
    private final BlogService blogService;
    private final PagedResourcesAssembler<BlogDTO> pagedResourcesAssembler;

    // TODO: implement updateBlog, deleteBlog, createBlog

    @GetMapping()
    public ResponseEntity<PagedModel<BlogDTO>> getBlogs(Pageable pageable) {
        Page<BlogDTO> blogs = blogService.getBlogs(pageable)
                .map(blogModelAssembler::toModel);
        PagedModel<BlogDTO> pagedModel = pagedResourcesAssembler.toModel(blogs, blogModelAssembler);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<BlogDTO>> getBlogById(@PathVariable Long id
            /*@AuthenticationPrincipal User user*/) {
        return blogService.getBlogById(id)
                .map(blog -> blogMapperService.blogToBlogDTO(blog, null))
                .map(blogModelAssembler::toEntityModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
