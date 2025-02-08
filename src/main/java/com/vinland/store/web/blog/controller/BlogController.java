package com.vinland.store.web.blog.controller;

import com.vinland.store.utils.PathConstants;
import com.vinland.store.web.blog.dto.BlogDTO;
import com.vinland.store.web.blog.dto.BlogModelAssembler;
import com.vinland.store.web.blog.service.BlogMapperService;
import com.vinland.store.web.blog.service.BlogService;
import com.vinland.store.web.user.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(PathConstants.BLOG)
@CrossOrigin
@RequiredArgsConstructor
public class BlogController {
    private final BlogMapperService blogMapperService;
    private final BlogModelAssembler blogModelAssembler;
    private final BlogService blogService;
    private final PagedResourcesAssembler<BlogDTO> pagedResourcesAssembler;

    @GetMapping()
    public ResponseEntity<PagedModel<BlogDTO>> getBlogs(Pageable pageable) {
        final Page<BlogDTO> blogs = blogService.getBlogs(pageable).map(blogModelAssembler::toModel);
        PagedModel<BlogDTO> pagedModel = pagedResourcesAssembler.toModel(blogs, blogModelAssembler);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<BlogDTO>> getBlogById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return blogService.getBlogById(id)
                .map(blog -> blogMapperService.blogToBlogDTO(blog, null))
                .map(blogModelAssembler::toEntityModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(path = "/{id}", consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BlogDTO> updateBlog(@PathVariable Long id, @Valid @RequestBody BlogDTO blogDTO, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(blogMapperService.blogToBlogDTO(blogService.updateBlog(id, blogDTO, null), null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long id) {
        blogService.deleteBlog(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BlogDTO> createBlog(@Valid @RequestBody BlogDTO blogDTO, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(blogMapperService.blogToBlogDTO(blogService.createBlog(blogDTO, null), null));
    }
}
