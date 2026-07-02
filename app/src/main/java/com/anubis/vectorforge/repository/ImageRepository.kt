package com.anubis.vectorforge.repository

import com.anubis.vectorforge.api.ImageGenerationApi
import com.anubis.vectorforge.model.GenerateRequest
import com.anubis.vectorforge.model.GenerateResponse

class ImageRepository(private val api: ImageGenerationApi) {
    suspend fun generate(request: GenerateRequest): Result<GenerateResponse> {
        return try {
            val response = api.generateImage(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("API Error: empty body"))
                }
            } else {
                Result.failure(Exception("API Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
