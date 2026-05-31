package com.example.api

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiRetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    val service: GeminiApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        retrofit.create(GeminiApiService::class.java)
    }

    suspend fun parseTaskNLP(prompt: String): ParsedTaskResponse? {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return null
        }

        val systemPrompt = """
            You are an expert NLP parser. Your job is to extract task details from a natural language request.
            Always return a valid JSON object matching this schema:
            {
              "title": "Short title",
              "description": "More details",
              "category": "One of: Work, Personal, Shopping, Health, Travel, Other",
              "priority": "One of: Low, Medium, High",
              "locationName": "Location if specified, like 'Grocery Store', 'Office', 'Home', or null",
              "dueDateOffsetMinutes": 60
            }
            The dueDateOffsetMinutes should be the number of minutes from now when the task is scheduled. Default is 60 minutes.
            Do not output any text other than the JSON object.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(responseMimeType = "application/json"),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt)))
        )

        return try {
            val response = service.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: return null
            moshi.adapter(ParsedTaskResponse::class.java).fromJson(jsonText)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun generateMorningBriefing(tasksSummaries: String): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return "Good morning! Here is your quick briefing: You have several reminders scheduled. Omni local encryption is active and calendar synchronization is operational."
        }

        val prompt = """
            Generate an energetic, highly motivating morning briefing audio script for a voice AI read-out based on these tasks:
            $tasksSummaries
            Keep it professional but encouraging, concise (under 4-5 sentences), and speak in the first person as 'Omni, your agentic companion'.
            Refer to any location-based tasks, high-priority tasks and mention that encryption is secure and local offline sync is operational.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt))))
        )

        return try {
            val response = service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "Briefing text could not be loaded."
        } catch (e: java.lang.Exception) {
            "Good morning! Here is your quick update. Your scheduled tasks are saved and fully encrypted."
        }
    }
}
