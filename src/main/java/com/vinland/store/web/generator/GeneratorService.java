package com.vinland.store.web.generator;

import com.vinland.store.web.blog.dao.BlogDAO;
import com.vinland.store.web.blog.model.Blog;
import com.vinland.store.web.user.dao.UserDAO;
import com.vinland.store.web.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeneratorService {
    private final BlogDAO blogDAO;
    private final UserDAO userDAO;

    public void generateBlogs(int amount) {
        List<Blog> blogs = new ArrayList<>();
        Faker faker = new Faker();
//        User user = userDAO.getReferenceById(1L);
        User user = userDAO.findByUsername("econ")
                .orElseThrow(() -> new RuntimeException("User with such username does not exist"));
        long start = System.currentTimeMillis();
        for (int i = 0; i < amount; i++) {
            var blog = new Blog();
            blog.setTitle(faker.book().title());
            blog.setBody(faker.witcher().quote());
            blog.setSlug(faker.book().title());
            blog.setTags(Set.of(faker.book().genre()));
            blog.setPublished(false);
            blog.setViewCount((long) faker.number().numberBetween(0, 1000));
            blog.setCreatedAt(LocalDateTime.now());
            blog.setUpdatedAt(LocalDateTime.now());
            blog.setAuthor(user);
            blogs.add(blog);
        }
//        user.setBlogs(blogs);
//        userDAO.save(user);
        blogDAO.saveAll(blogs);
        blogs.clear();
        long end = System.currentTimeMillis();
        log.info("Generated {} blogs in {} ms", amount, end - start);
    }
}
