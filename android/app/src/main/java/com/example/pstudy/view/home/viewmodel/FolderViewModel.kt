package com.example.pstudy.view.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pstudy.data.local.dao.FolderDao
import com.example.pstudy.data.local.entity.FolderEntity
import com.example.pstudy.data.mapper.FolderMapper
import com.example.pstudy.data.model.Folder
import com.example.pstudy.data.model.Note
import com.example.pstudy.data.model.StudyMaterials
import com.example.pstudy.data.repository.StudyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class FolderUiState(
    val folders: List<Folder> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val folderDao: FolderDao,
    private val folderMapper: FolderMapper,
    private val studyRepository: StudyRepository
) : ViewModel() {

    private val _folderUiState = MutableStateFlow(FolderUiState())
    val folderUiState: StateFlow<FolderUiState> = _folderUiState.asStateFlow()

    private val currentUserId = "current_user_id"

    init {
        loadFolders()
    }

    fun loadFolders() {
        viewModelScope.launch {
            _folderUiState.update { it.copy(isLoading = true) }
            Log.d("FolderViewModel", "Loading folders")
            try {
                folderDao.getFoldersByUserId(currentUserId).collectLatest { folderEntities ->
                    val folders = folderEntities.map { entity ->
                        folderMapper.mapEntityToModel(entity)
                    }
                    _folderUiState.update {
                        it.copy(folders = folders, isLoading = false, error = null)
                    }
                    Log.d("FolderViewModel", "Folders loaded successfully")
                }
            } catch (e: Exception) {
                _folderUiState.update {
                    it.copy(isLoading = false, error = "Failed to load folders: ${e.message}")
                }
                Log.e("FolderViewModel", "Failed to load folders: ${e.message}")
            }
        }
    }

    suspend fun refreshStudyMaterials() {
        Log.d("FolderViewModel", "Refreshing study materials")
        try {
            studyRepository.getStudyMaterials().collect {
                Log.d("FolderViewModel", "Study materials refreshed, count: ${it.size}")
            }
        } catch (e: Exception) {
            Log.e("FolderViewModel", "Failed to refresh study materials: ${e.message}")
            _folderUiState.update {
                it.copy(error = "Failed to refresh data: ${e.message}")
            }
        }
    }

    fun createFolder(folderName: String) {
        if (folderName.isBlank()) {
            _folderUiState.update {
                it.copy(
                    error = "Folder name cannot be empty",
                    successMessage = null
                )
            }
            Log.w("FolderViewModel", "Folder name cannot be empty")
            return
        }

        viewModelScope.launch {
            _folderUiState.update { it.copy(isLoading = true) }
            Log.d("FolderViewModel", "Creating folder $folderName")
            try {
                val folderEntity = FolderEntity(
                    id = UUID.randomUUID().toString(),
                    name = folderName,
                    userId = currentUserId
                )
                folderDao.insertFolder(folderEntity)
                loadFolders()
                _folderUiState.update {
                    it.copy(
                        successMessage = "Folder created successfully",
                        error = null,
                        isLoading = false
                    )
                }
                Log.d("FolderViewModel", "Folder created successfully")
            } catch (e: Exception) {
                _folderUiState.update {
                    it.copy(
                        error = "Failed to create folder: ${e.message}",
                        successMessage = null,
                        isLoading = false
                    )
                }
                Log.e("FolderViewModel", "Failed to create folder: ${e.message}")
            }
        }
    }

    fun updateFolder(folderId: String, newName: String) {
        if (newName.isBlank()) {
            _folderUiState.update { it.copy(error = "Folder name cannot be empty") }
            Log.w("FolderViewModel", "Folder name cannot be empty")
            return
        }

        viewModelScope.launch {
            Log.d("FolderViewModel", "Updating folder $folderId")
            try {
                val folder = folderDao.getFolderById(folderId)
                if (folder != null) {
                    val updatedFolder = folder.copy(name = newName)
                    folderDao.updateFolder(updatedFolder)
                    _folderUiState.update { it.copy(error = null) }
                    Log.d("FolderViewModel", "Folder updated successfully")
                } else {
                    _folderUiState.update { it.copy(error = "Folder not found") }
                    Log.w("FolderViewModel", "Folder not found")
                }
            } catch (e: Exception) {
                _folderUiState.update {
                    it.copy(error = "Failed to update folder: ${e.message}")
                }
                Log.e("FolderViewModel", "Failed to update folder: ${e.message}")
            }
        }
    }

    fun deleteFolder(folderId: String) {
        viewModelScope.launch {
            Log.d("FolderViewModel", "Deleting folder $folderId")
            try {
                val folder = folderDao.getFolderById(folderId)
                if (folder != null) {
                    folderDao.deleteFolder(folder)
                    _folderUiState.update { it.copy(error = null) }
                    Log.d("FolderViewModel", "Folder deleted successfully")
                } else {
                    _folderUiState.update { it.copy(error = "Folder not found") }
                    Log.w("FolderViewModel", "Folder not found")
                }
            } catch (e: Exception) {
                _folderUiState.update {
                    it.copy(error = "Failed to delete folder: ${e.message}")
                }
                Log.e("FolderViewModel", "Failed to delete folder: ${e.message}")
            }
        }
    }

    fun getNotesByFolderId(folderId: String): Flow<List<Note>> {
        return callbackFlow {
            Log.d("FolderViewModel", "Getting notes for folder $folderId")
            studyRepository.getStudyMaterials().collect { materials ->
                try {
                    val notes = materials
                        .filter { it.folderId == folderId }
                        .map { material ->
                            Note(
                                id = material.id,
                                input = material.input,
                                type = material.type.name,
                                userId = material.userId,
                                timestamp = material.timeStamp,
                                title = material.title,
                                folderId = folderId
                            )
                        }
                    send(notes)
                    Log.d("FolderViewModel", "Notes loaded successfully for folder $folderId")
                } catch (e: Exception) {
                    _folderUiState.update {
                        it.copy(error = "Failed to load notes: ${e.message}")
                    }
                    Log.e("FolderViewModel", "Failed to load notes: ${e.message}")
                    send(emptyList())
                }
            }
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            Log.d("FolderViewModel", "Deleting note $noteId")
            try {
                studyRepository.deleteStudyMaterial(noteId)
                Log.d("FolderViewModel", "Note deleted successfully")
            } catch (e: Exception) {
                _folderUiState.update {
                    it.copy(error = "Failed to delete note: ${e.message}")
                }
                Log.e("FolderViewModel", "Failed to delete note: ${e.message}")
            }
        }
    }

    fun clearSuccessMessage() {
        _folderUiState.update { it.copy(successMessage = null) }
        Log.d("FolderViewModel", "Success message cleared")
    }

    fun addNotesToFolder(folderId: String, studyMaterials: List<StudyMaterials>) {
        viewModelScope.launch {
            _folderUiState.update { it.copy(isLoading = true) }
            Log.d("FolderViewModel", "Adding notes to folder $folderId")
            try {
                for (material in studyMaterials) {
                    // Create a copy with the new folderId
                    val updatedMaterial = material.copy(folderId = folderId)
                    studyRepository.updateStudyMaterial(updatedMaterial)
                }

                // Get the folder name for the success message
                val folder = folderDao.getFolderById(folderId)
                val folderName = folder?.name ?: "folder"

                _folderUiState.update {
                    it.copy(
                        successMessage = "Notes added to $folderName successfully",
                        error = null,
                        isLoading = false
                    )
                }
                Log.d("FolderViewModel", "Notes added to folder $folderId successfully")
                refreshStudyMaterials()
                loadFolders()
            } catch (e: Exception) {
                _folderUiState.update {
                    it.copy(
                        error = "Failed to add notes to folder: ${e.message}",
                        successMessage = null,
                        isLoading = false
                    )
                }
                Log.e("FolderViewModel", "Failed to add notes to folder: ${e.message}")
            }
        }
    }

    fun getAllAvailableNotes(): Flow<List<StudyMaterials>> {
        val notesFlow = MutableStateFlow<List<StudyMaterials>>(emptyList())

        viewModelScope.launch {
            Log.d("FolderViewModel", "Getting all available notes")
            try {
                studyRepository.getStudyMaterials().collect { materials ->
                    notesFlow.value = materials
                    Log.d("FolderViewModel", "All available notes loaded successfully")
                }
            } catch (e: Exception) {
                _folderUiState.update {
                    it.copy(error = "Failed to load notes: ${e.message}")
                }
                Log.e("FolderViewModel", "Failed to load notes: ${e.message}")
            }
        }

        return notesFlow
    }

    fun getFolderForStudyMaterial(studyMaterialId: String): Flow<Folder?> {
        val folderFlow = MutableStateFlow<Folder?>(null)

        viewModelScope.launch {
            Log.d("FolderViewModel", "Getting folder for study material $studyMaterialId")
            try {
                studyRepository.getStudyMaterials().collect { materials ->
                    val material = materials.find { it.id == studyMaterialId }

                    if (material?.folderId != null) {
                        val folderEntity = folderDao.getFolderById(material.folderId)
                        if (folderEntity != null) {
                            folderFlow.value = folderMapper.mapEntityToModel(folderEntity)
                            Log.d(
                                "FolderViewModel",
                                "Folder loaded successfully for study material $studyMaterialId"
                            )
                        }
                    } else {
                        folderFlow.value = null
                        Log.d(
                            "FolderViewModel",
                            "No folder found for study material $studyMaterialId"
                        )
                    }
                }
            } catch (e: Exception) {
                _folderUiState.update {
                    it.copy(error = "Failed to get folder for study material: ${e.message}")
                }
                Log.e("FolderViewModel", "Failed to get folder for study material: ${e.message}")
            }
        }

        return folderFlow
    }
}