package com.vinland.store.web.generator;

import com.vinland.store.utils.PathConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping(PathConstants.GENERATOR)
public class GeneratorController {
    private final GeneratorService generatorService;

    @PostMapping("/blogs")
    //    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> generateBlogs(
            @RequestParam(required = false, defaultValue = "50") int amount
    ) {
        generatorService.generateBlogs(amount);
        return ResponseEntity.ok().build();
    }
}

