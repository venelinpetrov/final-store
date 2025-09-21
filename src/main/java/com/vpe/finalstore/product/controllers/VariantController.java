package com.vpe.finalstore.product.controllers;

import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.product.dtos.ProductVariantDto;
import com.vpe.finalstore.product.mappers.ProductVariantMapper;
import com.vpe.finalstore.product.repositories.ProductVariantRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/api/variants")
class VariantController {
    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantMapper productVariantMapper;


}
