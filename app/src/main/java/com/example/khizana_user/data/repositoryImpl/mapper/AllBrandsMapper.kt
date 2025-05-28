package com.example.khizana_user.data.repositoryImpl.mapper

import com.example.khizana_user.data.dto.BrandResponse
import com.example.khizana_user.domain.model.Brand


fun BrandResponse.toDomain(): Brand {
    return Brand(
        id = this.id,
        title = this.title,
        imageUrl = this.image?.src
    )
}
