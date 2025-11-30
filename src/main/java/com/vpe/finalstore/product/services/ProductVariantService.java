package com.vpe.finalstore.product.services;
import com.vpe.finalstore.product.exceptions.VariantNotFoundException;
import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.inventory.dtos.InventoryMovementCreateDto;
import com.vpe.finalstore.inventory.enums.MovementType;
import com.vpe.finalstore.inventory.services.InventoryMovementService;
import com.vpe.finalstore.product.dtos.ProductVariantCreateDto;
import com.vpe.finalstore.product.dtos.ProductVariantUpdateDto;
import com.vpe.finalstore.product.entities.*;
import com.vpe.finalstore.product.repositories.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class ProductVariantService {
    private final ProductVariantRepository variantRepository;
    private final ProductImageRepository imageRepository;
    private final ProductVariantOptionRepository optionRepository;
    private final ProductVariantOptionValueRepository optionValueRepository;
    private final ProductVariantImageAssignmentRepository imageAssignmentRepository;
    private final InventoryMovementService inventoryMovementService;

    @Transactional
    public void archiveVariant(Integer variantId) {
        var variant = variantRepository.findById(variantId)
            .orElseThrow(VariantNotFoundException::new);

        variant.setIsArchived(true);

        variantRepository.save(variant);
    }

    @Transactional
    public void unarchiveVariant(Integer variantId) {
        var variant = variantRepository.findByVariantIdAndIsArchivedIsTrue(variantId)
            .orElseThrow(VariantNotFoundException::new);

        variant.setIsArchived(false);

        variantRepository.save(variant);
    }

    @Transactional
    public void updateVariant(Integer variantId, ProductVariantUpdateDto req) {
        var variant = variantRepository.findById(variantId)
            .orElseThrow(VariantNotFoundException::new);

        variant.setUnitPrice(req.getUnitPrice());

        variantRepository.save(variant);
    }

    @Transactional
    public void deleteVariant(Integer variantId) {
        variantRepository.deleteById(variantId);
    }

    @Transactional
    public ProductVariant createVariant(Product product, ProductVariantCreateDto variantDto) {
        var variant = new ProductVariant();
        variant.setProduct(product);
        variant.setSku(variantDto.getSku());
        variant.setUnitPrice(variantDto.getUnitPrice());
        variant.setIsArchived(variantDto.getIsArchived());

        if (variantDto.getOptions() != null) {
            for (var optionReq : variantDto.getOptions()) {
                var option = optionRepository.findByNameIgnoreCase(optionReq.getName())
                    .orElseGet(() -> {
                        var o = new ProductVariantOption();
                        o.setName(optionReq.getName());

                        return optionRepository.save(o);
                    });
                var value = optionValueRepository.findByOptionAndValue(option, optionReq.getValue())
                    .orElseGet(() -> {
                        var v = new ProductVariantOptionValue();
                        v.setOption(option);
                        v.setValue(optionReq.getValue());

                        return optionValueRepository.save(v);
                    });

                var assignment = new ProductVariantOptionAssignment(variant, value);

                variant.getOptionAssignments().add(assignment);
            }
        }

        for (var reqImage : variantDto.getImages()) {
            var image = imageRepository.findByLink(reqImage.getLink())
                .orElseGet(() -> imageRepository.save(
                    new ProductImage(reqImage.getLink(), reqImage.getAltText())
                ));

            var assignment = new ProductVariantImageAssignment(variant, image, reqImage.getIsPrimary());
            variant.getImages().add(assignment);
        }

        variant = variantRepository.save(variant);

        var initMovement = new InventoryMovementCreateDto();
        initMovement.setVariantId(variant.getVariantId());
        initMovement.setMovementType(MovementType.ADJUSTMENT);
        initMovement.setQuantity(variantDto.getQuantityInStock());
        initMovement.setReason("Initialize stock for new variant");

        inventoryMovementService.createMovement(initMovement);

        return variantRepository.save(variant);
    }

    @Transactional
    public void assignImages(List<Integer> imageIds, ProductVariant variant) {
        List<Integer> distinctImageIds = imageIds.stream()
            .distinct()
            .toList();

        var images = imageRepository.findAllById(distinctImageIds);
        if (images.size() != distinctImageIds.size()) {
            throw new NotFoundException("Some images were not found");
        }

        List<ProductVariantImageAssignment> assignments = new ArrayList<>();
        for (int i = 0; i < distinctImageIds.size(); i++) {
            var imageId = distinctImageIds.get(i);
            var image = images.stream()
                .filter(img -> img.getImageId().equals(imageId))
                .findFirst()
                .orElseThrow();

            boolean isPrimary = (i == 0);
            assignments.add(new ProductVariantImageAssignment(variant, image, isPrimary));
        }

        imageAssignmentRepository.saveAll(assignments);
    }

    public void unassignImages(List<Integer> imageIds, ProductVariant variant) {
        var assignments = imageIds.stream()
            .map(imageId -> imageAssignmentRepository.findAssignment(imageId, variant.getVariantId())
                .orElseThrow(
                    () -> new NotFoundException("Image with ID %d not assigned to variant %d".formatted(imageId, variant.getVariantId()))
                )
            )
            .toList();

        imageAssignmentRepository.deleteAll(assignments);
    }
}
