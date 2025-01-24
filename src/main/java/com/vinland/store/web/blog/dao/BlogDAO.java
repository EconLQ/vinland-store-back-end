package com.vinland.store.web.blog.dao;

import com.vinland.store.web.blog.model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
//@RepositoryRestResource(collectionResourceRel = "blog", path = "blogs")
public interface BlogDAO extends JpaRepository<Blog, Long> {
    @Query(value = "SELECT b FROM Blog b WHERE b.id = :id")
    Optional<Blog> findByIdCustom(Long id);
}
