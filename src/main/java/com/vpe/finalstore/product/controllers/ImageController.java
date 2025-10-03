package com.vpe.finalstore.product.controllers;

import com.vpe.finalstore.product.repositories.ProductImageRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/")
class ImageController {
    private final ProductImageRepository imageRepository;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{imageId}")
    public void deleteImage(@PathVariable Integer imageId) {
        imageRepository.deleteById(imageId);
    }
}
