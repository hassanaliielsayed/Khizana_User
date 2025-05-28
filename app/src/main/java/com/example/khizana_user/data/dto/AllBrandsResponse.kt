
package com.example.khizana_user.data.dto

import com.google.gson.annotations.SerializedName

data class AllBrandsResponse(
    @SerializedName("smart_collections")
    val allBrands: List<BrandResponse>
)

data class BrandResponse(
    val id: Long,
    val title: String,
    val image: ImageResponse?
)

data class ImageResponse(
    val src: String
)

