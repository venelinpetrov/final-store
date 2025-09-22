package com.vpe.finalstore.product.services;

import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.product.repositories.ProductVariantRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ProductVariantService {
    private final ProductVariantRepository variantRepository;

    public void archiveVariant(Integer variantId) {
        var variant = variantRepository.findById(variantId)
            .orElseThrow(() -> new NotFoundException("Variant not found"));

        variant.setIsArchived(true);

        variantRepository.save(variant);
    }

    public void unarchiveVariant(Integer variantId) {
        var variant = variantRepository.findByVariantIdAndIsArchivedIsTrue(variantId)
            .orElseThrow(() -> new NotFoundException("Variant not found"));

        variant.setIsArchived(false);

        variantRepository.save(variant);
    }
}
