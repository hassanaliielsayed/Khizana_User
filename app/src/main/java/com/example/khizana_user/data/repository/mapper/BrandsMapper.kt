package com.example.khizana_user.data.repository.mapper

import com.example.khizana_user.data.dto.BrandResponseDto
import com.example.khizana_user.domain.model.Brand


fun BrandResponseDto.toBrandModel(): Brand {
    return Brand(
        id = this.id,
        title = this.title,
        imageUrl = this.image?.src
    )
}
