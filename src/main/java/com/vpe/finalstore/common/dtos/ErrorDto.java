package com.vpe.finalstore.common.dtos;

import java.util.List;

public record ErrorDto (
    String message,
    List<String> errors
) {}
