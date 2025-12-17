package com.ziro.fit.model

import com.google.gson.annotations.SerializedName

data class GetTrainersResponse(
    val trainers: List<TrainerSummary>
)

data class TrainerSummary(
    val id: String,
    val name: String,
    val username: String?,
    val profile: TrainerProfileSummary?
)

data class TrainerProfileSummary(
    val profilePhotoPath: String?,
    val certifications: String?, // Comma separated string in JSON
    val averageRating: Double?,
    val locations: List<TrainerLocationSummary>?,
    val services: List<TrainerServiceSummary>?
)

data class TrainerLocationSummary(
    val id: String,
    val address: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class TrainerServiceSummary(
    val price: String?,
    val currency: String?,
    val duration: Int?
)

data class PublicTrainerProfileResponse(
    val id: String,
    val name: String,
    val username: String?,
    val role: String,
    val profile: PublicTrainerProfileDetails
)

data class PublicTrainerProfileDetails(
    val bio: TrainerBio,
    val images: TrainerImages,
    val professional: TrainerProfessionalInfo,
    val locations: List<TrainerLocation>,
    val services: List<TrainerService>,
    val testimonials: List<TrainerTestimonial>,
    val transformations: List<TrainerTransformation>,
    val socials: List<TrainerSocial>,
    val benefits: List<TrainerBenefit>
)

data class TrainerBio(
    val aboutMe: String?,
    val philosophy: String?,
    val methodology: String?,
    val branding: String?
)

data class TrainerImages(
    val profilePhoto: String?,
    val bannerImage: String?
)

data class TrainerProfessionalInfo(
    val specialties: List<String>,
    val trainingTypes: List<String>,
    val certifications: String?,
    val averageRating: Double?,
    val minServicePrice: Double?
)

data class TrainerLocation(
    val id: String,
    val address: String,
    val latitude: Double?,
    val longitude: Double?
)

data class TrainerService(
    val id: String,
    val title: String,
    val description: String,
    val price: String?,
    val currency: String?,
    val duration: Double?
)

data class TrainerTestimonial(
    val id: String,
    val clientName: String,
    val text: String,
    val rating: Double?
)

data class TrainerTransformation(
    val id: String,
    val imagePath: String,
    val caption: String?,
    val clientName: String?
)

data class TrainerSocial(
    val platform: String,
    val username: String,
    val url: String
)

data class TrainerBenefit(
    val title: String,
    val description: String?,
    val iconName: String?
)
