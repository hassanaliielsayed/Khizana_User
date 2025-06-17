package com.example.khizana_user.data.repository.mapper

import com.example.khizana_user.data.dto.draftorderDto.ProductImageDto
import com.example.khizana_user.domain.model.ProductImage

fun ProductImageDto.toProductImageDomain() = ProductImage(
        id = id,
        src = src,
    )
