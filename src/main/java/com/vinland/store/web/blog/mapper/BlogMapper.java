package com.vinland.store.web.blog.mapper;

import com.vinland.store.web.blog.dto.BlogDTO;
import com.vinland.store.web.blog.model.Blog;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BlogMapper {
    BlogMapper INSTANCE = Mappers.getMapper(BlogMapper.class);


    BlogDTO blogToBlogDTO(Blog blog);

    List<BlogDTO> blogsToBlogDTO(List<Blog> blogs);

}
