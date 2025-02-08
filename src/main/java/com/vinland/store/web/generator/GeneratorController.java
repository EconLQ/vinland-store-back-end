package com.vinland.store.web.generator;

import com.vinland.store.utils.MessageResponse;
import com.vinland.store.utils.PathConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping(PathConstants.GENERATOR)
@PreAuthorize("hasAuthority('ADMIN')")
public class GeneratorController {
    private final GeneratorService generatorService;

    @PostMapping("/blogs")
    public ResponseEntity<MessageResponse> generateBlogs(
            @RequestParam(required = false, defaultValue = "50") int amount
    ) {
        generatorService.generateBlogs(amount);
        return ResponseEntity.ok(new MessageResponse("Generated " + amount + " blogs"));
    }
}

