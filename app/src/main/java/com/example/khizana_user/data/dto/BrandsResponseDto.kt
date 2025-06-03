
package com.example.khizana_user.data.dto

import com.google.gson.annotations.SerializedName

data class BrandsResponseDto(
    @SerializedName("smart_collections")
    val allBrands: List<BrandResponseDto>
)

data class BrandResponseDto(
    val id: Long,
    val title: String,
    val image: ImageResponseDto?
)

data class ImageResponseDto(
    val src: String
)

