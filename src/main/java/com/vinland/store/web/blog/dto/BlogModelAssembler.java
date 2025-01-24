package com.vinland.store.web.blog.dto;

import com.vinland.store.web.blog.controller.BlogController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BlogModelAssembler extends RepresentationModelAssemblerSupport<BlogDTO, BlogDTO> {
    public BlogModelAssembler() {
        super(BlogController.class, BlogDTO.class);
    }

    @Override
    public BlogDTO toModel(BlogDTO entity) {
        return entity.add(linkTo(methodOn(BlogController.class).getBlogById(entity.getId())).withSelfRel());
    }

    public EntityModel<BlogDTO> toEntityModel(BlogDTO entity) {
        return EntityModel.of(entity, linkTo(methodOn(BlogController.class).getBlogById(entity.getId())).withRel("blogs"));
    }
}
