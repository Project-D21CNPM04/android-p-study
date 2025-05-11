package com.example.pstudy.data.repository

import com.example.pstudy.data.local.source.LocalDataSource
import com.example.pstudy.data.model.Content
import com.example.pstudy.data.model.FlashCard
import com.example.pstudy.data.model.MindMap
import com.example.pstudy.data.model.Quiz
import com.example.pstudy.data.model.StudyMaterials
import com.example.pstudy.data.model.Summary
import com.example.pstudy.data.remote.dto.FlashCardDto
import com.example.pstudy.data.remote.dto.MindMapDto
import com.example.pstudy.data.remote.dto.NoteDto
import com.example.pstudy.data.remote.dto.QuizDto
import com.example.pstudy.data.remote.dto.SummaryDto
import com.example.pstudy.data.remote.source.RemoteDataSource
import com.example.pstudy.data.remote.utils.NetworkResult
import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import okio.BufferedSink

@Singleton
class StudyRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val context: Context
) : StudyRepository {

    // Study materials operations
    override suspend fun getStudyMaterials(): Flow<List<StudyMaterials>> {
        return localDataSource.getAllStudyMaterials()
    }

    override suspend fun getStudyMaterial(id: String): StudyMaterials? {
        return localDataSource.getStudyMaterialById(id)
    }

    override suspend fun insertStudyMaterial(studyMaterial: StudyMaterials): String {
        return localDataSource.insertStudyMaterial(studyMaterial)
    }

    override suspend fun updateStudyMaterial(studyMaterial: StudyMaterials) {
        localDataSource.updateStudyMaterial(studyMaterial)
    }

    override suspend fun deleteStudyMaterial(id: String) {
        localDataSource.deleteStudyMaterial(id)
    }

    // Remote note operations
    override suspend fun getRemoteNoteList(): NetworkResult<List<NoteDto>> {
        return remoteDataSource.getNoteList()
    }

    override suspend fun getRemoteNoteDetail(noteId: String): NetworkResult<NoteDto> {
        return remoteDataSource.getNoteDetail(noteId)
    }

    // FlashCard operations
    override suspend fun getFlashCards(noteId: String): List<FlashCard> {
        val localFlashCards = localDataSource.getFlashCardsByStudyMaterialId(noteId)
        if (localFlashCards.isNotEmpty()) {
            return localFlashCards
        }

        return when (val remoteResponse = remoteDataSource.getFlashCards(noteId)) {
            is NetworkResult.Success -> {
                val flashCardDtoList = remoteResponse.data
                val flashCards = flashCardDtoList.map { flashCardDto ->
                    FlashCard(
                        id = flashCardDto.id,
                        title = flashCardDto.title,
                        content = Content(
                            front = flashCardDto.content.front,
                            back = flashCardDto.content.back
                        )
                    )
                }
//                localDataSource.insertFlashCards(flashCards, noteId)
                flashCards
            }

            is NetworkResult.Error -> emptyList()
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun createFlashCards(flashCards: List<FlashCard>, studyMaterialId: String) {
        localDataSource.insertFlashCards(flashCards, studyMaterialId)
    }

    override suspend fun updateFlashCard(flashCard: FlashCard, studyMaterialId: String) {
        localDataSource.updateFlashCard(flashCard, studyMaterialId)
    }

    override suspend fun deleteFlashCard(id: Int) {
        localDataSource.deleteFlashCard(id)
    }

    override suspend fun generateFlashCards(
        noteId: String,
        numFlashCards: Int,
        difficulty: Int,
    ): NetworkResult<List<FlashCardDto>> {
        return remoteDataSource.createFlashCards(noteId, numFlashCards, difficulty)
    }

    // Quiz operations
    override suspend fun getQuizzes(noteId: String): List<Quiz> {
        val localQuizzes = localDataSource.getQuizzesByStudyMaterialId(noteId)
        if (localQuizzes.isNotEmpty()) {
            return localQuizzes
        }

        return when (val remoteResponse = remoteDataSource.getQuizzes(noteId)) {
            is NetworkResult.Success -> {
                val quizDtoList = remoteResponse.data
                val quizzes = quizDtoList.map { quizDto ->
                    Quiz(
                        id = quizDto.id,
                        questions = quizDto.question,
                        choices = quizDto.choices,
                        answer = quizDto.answer
                    )
                }
                localDataSource.insertQuizzes(quizzes, noteId)
                quizzes
            }

            is NetworkResult.Error -> emptyList()
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun createQuizzes(quizzes: List<Quiz>, studyMaterialId: String) {
        localDataSource.insertQuizzes(quizzes, studyMaterialId)
    }

    override suspend fun updateQuiz(quiz: Quiz, studyMaterialId: String) {
        localDataSource.updateQuiz(quiz, studyMaterialId)
    }

    override suspend fun deleteQuiz(id: Int) {
        localDataSource.deleteQuiz(id)
    }

    override suspend fun generateQuiz(
        noteId: String,
        numQuizzes: Int,
        difficulty: Int,
    ): NetworkResult<List<QuizDto>> {
        return remoteDataSource.createQuizzes(noteId, numQuizzes, difficulty)
    }

    // MindMap operations
    override suspend fun getMindMap(noteId: String): MindMap? {
        val localMindMap = localDataSource.getMindMapByStudyMaterialId(noteId)
        if (localMindMap != null) {
            return localMindMap
        }

        return when (val remoteResponse = remoteDataSource.getMindMap(noteId)) {
            is NetworkResult.Success -> {
                val mindMapDto = remoteResponse.data
                val mindMap = MindMap(
                    id = mindMapDto.id,
                    content = mindMapDto.content,
                    summary = mindMapDto.summary
                )
//                localDataSource.insertMindMap(mindMap, noteId)
                mindMap
            }

            is NetworkResult.Error -> null
            is NetworkResult.Loading -> null
        }
    }

    override suspend fun createMindMap(mindMap: MindMap, studyMaterialId: String): String {
        return localDataSource.insertMindMap(mindMap, studyMaterialId)
    }

    override suspend fun updateMindMap(mindMap: MindMap, studyMaterialId: String) {
        localDataSource.updateMindMap(mindMap, studyMaterialId)
    }

    override suspend fun deleteMindMap(id: String) {
        localDataSource.deleteMindMap(id)
    }

    override suspend fun generateMindMap(
        noteId: String,
        numNodes: Int,
        difficulty: Int
    ): NetworkResult<MindMapDto> {
        return remoteDataSource.createMindMap(noteId, numNodes, difficulty)
    }

    // Summary operations
    override suspend fun getSummary(noteId: String): Summary? {
        val localSummary = localDataSource.getSummaryByStudyMaterialId(noteId)
        if (localSummary != null) {
            return localSummary
        }

        return when (val remoteResponse = remoteDataSource.getSummary(noteId)) {
            is NetworkResult.Success -> {
                val summaryDto = remoteResponse.data
                val summary = Summary(
                    id = summaryDto.id,
                    content = summaryDto.content,
                    noteId = noteId
                )
//                localDataSource.insertSummary(summary)
                summary
            }

            is NetworkResult.Error -> null
            is NetworkResult.Loading -> null
        }
    }

    override suspend fun insertSummary(summary: Summary): String {
        return localDataSource.insertSummary(summary)
    }

    override suspend fun updateSummary(summary: Summary) {
        localDataSource.updateSummary(summary)
    }

    override suspend fun deleteSummary(id: String) {
        localDataSource.deleteSummary(id)
    }

    override suspend fun createTextNoteSummary(text: String): NetworkResult<SummaryDto> {
        return remoteDataSource.createTextNote(text)
    }

    override suspend fun createLinkNoteSummary(link: String): NetworkResult<SummaryDto> {
        return remoteDataSource.createLinkNote(link)
    }

    override suspend fun createFileNoteSummary(fileUri: String): NetworkResult<SummaryDto> {
        try {
            val uri = Uri.parse(fileUri)

            // Get file name from the URI
            var fileName = "file.pdf"
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    fileName = cursor.getString(nameIndex)
                }
            }

            // Create RequestBody from URI content
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                Log.e("StudyRepositoryImpl", "Could not open input stream for URI: $uri")
                return NetworkResult.Error("Could not open file")
            }

            val bytes = inputStream.readBytes()
            inputStream.close()

            val mediaType = MediaType.parse("application/pdf")
            val requestBody = object : RequestBody() {
                override fun contentType(): MediaType? = mediaType

                override fun contentLength(): Long = bytes.size.toLong()

                override fun writeTo(sink: BufferedSink) {
                    sink.write(bytes)
                }
            }

            // Create MultipartBody.Part
            val part = MultipartBody.Part.createFormData("file", fileName, requestBody)

            return remoteDataSource.createFileNote(part)
        } catch (e: Exception) {
            Log.e("StudyRepositoryImpl", "Error processing file", e)
            return NetworkResult.Error("Error processing file: ${e.message}")
        }
    }

    override suspend fun createAudioNoteSummary(audioPath: String): NetworkResult<SummaryDto> {
        try {
            // Create a File from the path
            val audioFile = File(audioPath)
            if (!audioFile.exists()) {
                return NetworkResult.Error("Audio file not found")
            }

            // Get file name
            val fileName = audioFile.name

            // Create RequestBody from File
            val mediaType = MediaType.parse("audio/mpeg")
            val requestBody = RequestBody.create(mediaType, audioFile)

            // Create MultipartBody.Part
            val part = MultipartBody.Part.createFormData("file", fileName, requestBody)

            // Call the remote data source
            // For now, we'll use the text note API as a placeholder
            // This should be replaced with a proper audio API endpoint when available
            return remoteDataSource.createAudioNote(part)
        } catch (e: Exception) {
            Log.e("StudyRepositoryImpl", "Error processing audio", e)
            return NetworkResult.Error("Error processing audio: ${e.message}")
        }
    }

    override suspend fun createImageNoteSummary(imageUri: String): NetworkResult<SummaryDto> {
        try {
            val uri = Uri.parse(imageUri)

            // Get file name from the URI
            var fileName = "image.jpg"
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    fileName = cursor.getString(nameIndex)
                }
            }

            // Create RequestBody from URI content
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                Log.e("StudyRepositoryImpl", "Could not open input stream for URI: $uri")
                return NetworkResult.Error("Could not open image file")
            }

            val bytes = inputStream.readBytes()
            inputStream.close()

            val mediaType = MediaType.parse("image/jpeg")
            val requestBody = object : RequestBody() {
                override fun contentType(): MediaType? = mediaType
                override fun contentLength(): Long = bytes.size.toLong()
                override fun writeTo(sink: BufferedSink) {
                    sink.write(bytes)
                }
            }

            // Create MultipartBody.Part
            val part = MultipartBody.Part.createFormData("file", fileName, requestBody)

            // Call the remote data source
            // For now, we'll use the text note API as a placeholder
            // This should be replaced with a proper image API endpoint when available
            return remoteDataSource.createImageNote(part)
        } catch (e: Exception) {
            Log.e("StudyRepositoryImpl", "Error processing image", e)
            return NetworkResult.Error("Error processing image: ${e.message}")
        }
    }
}