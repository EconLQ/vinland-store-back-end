package com.vinland.store.web.blog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "blogs")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlogDTO extends RepresentationModel<BlogDTO> implements Serializable {
    private Long id;
    private String title;
    private String body;
    private String slug;
    private Set<String> tags;
    private Boolean published;
    private Long viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String author;
}
