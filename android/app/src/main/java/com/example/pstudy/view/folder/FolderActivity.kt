package com.example.pstudy.view.folder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.base.ui.base.BindingActivity
import com.example.pstudy.R
import com.example.pstudy.data.model.Folder
import com.example.pstudy.data.model.Note
import com.example.pstudy.data.model.StudyMaterials
import com.example.pstudy.data.model.MaterialType
import com.example.pstudy.databinding.ActivityFolderBinding
import com.example.pstudy.databinding.DialogAddNotesToFolderBinding
import com.example.pstudy.databinding.DialogCreateFolderBinding
import com.example.pstudy.view.home.viewmodel.FolderViewModel
import com.example.pstudy.view.result.ResultActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FolderActivity : BindingActivity<ActivityFolderBinding>() {

    private val viewModel: FolderViewModel by viewModels()
    private var currentFolder: Folder? = null
    private lateinit var noteAdapter: NoteAdapter

    override fun inflateBinding(inflater: LayoutInflater): ActivityFolderBinding {
        return ActivityFolderBinding.inflate(inflater)
    }

    override fun getStatusBarColor() = R.color.black

    override fun updateUI(savedInstanceState: Bundle?) {
        // Get folder from intent
        currentFolder = intent.getParcelableExtraCompat(EXTRA_FOLDER, Folder::class.java)

        setupToolbar()
        setupViews()
        collectUiState()
        loadNotesForCurrentFolder()

        // Force refresh data with a slight delay to ensure DB operations are complete
        lifecycleScope.launch {
            delay(300)
            Log.d("FolderActivity", "Forcing data refresh after delay")
            loadNotesForCurrentFolder()
        }
    }

    private fun setupToolbar() {
        Log.d("FolderActivity", "Setting up toolbar")
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = currentFolder?.name ?: "Folder"
        }
    }

    private fun setupViews() {
        Log.d("FolderActivity", "Setting up views")
        binding.swipeRefresh.isRefreshing = false
        binding.swipeRefresh.setOnRefreshListener {
            currentFolder?.let { folder ->
                viewModel.loadFolders()
                loadNotesForCurrentFolder()
                binding.swipeRefresh.isRefreshing = false
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        noteAdapter = NoteAdapter(
            onNoteClick = { note ->
                // Handle note click
                Log.d("FolderActivity", "Note clicked: $note")
                navigateToResultActivity(note)
            },
            onNoteOptionsClick = { note, view ->
                showNoteOptionsMenu(note, view)
            }
        )
        binding.recyclerView.adapter = noteAdapter
    }

    private fun collectUiState() {
        Log.d("FolderActivity", "Collecting UI state")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                currentFolder?.let { folder ->
                    // Listen for changes in the UI state to get any errors or loading state
                    launch {
                        viewModel.folderUiState.collect { uiState ->
                            Log.d("FolderActivity", "UI state updated: $uiState")
                            binding.swipeRefresh.isRefreshing = uiState.isLoading

                            uiState.error?.let { error ->
                                Log.e("FolderActivity", "Error: $error")
                                Toast.makeText(this@FolderActivity, error, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                } ?: run {
                    // Handle case where folder is null
                    Log.d("FolderActivity", "Folder is null")
                    binding.emptyView.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                    finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("FolderActivity", "On resume")
        // Reload notes when activity resumes to ensure fresh data
        currentFolder?.let { folder ->
            viewModel.loadFolders() // Reload all folders
            // Force reload notes for current folder
            loadNotesForCurrentFolder()
        }
    }

    private fun loadNotesForCurrentFolder() {
        Log.d("FolderActivity", "Loading notes for current folder")
        currentFolder?.let { folder ->
            lifecycleScope.launch {
                // Clear previous data
                noteAdapter.submitList(emptyList())

                // Force fetch fresh data
                viewModel.getNotesByFolderId(folder.id).collectLatest { notes ->
                    Log.d("FolderActivity", "Notes loaded: $notes")
                    if (notes.isEmpty()) {
                        binding.emptyView.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    } else {
                        binding.emptyView.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                        noteAdapter.submitList(notes)
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d("FolderActivity", "Creating options menu")
        menuInflater.inflate(R.menu.menu_folder, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("FolderActivity", "Options item selected: $item")
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            R.id.action_menu -> {
                showFolderOptionsMenu(binding.toolbar)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showFolderOptionsMenu(view: View) {
        Log.d("FolderActivity", "Showing folder options menu")
        val wrapper = ContextThemeWrapper(this, R.style.PopupMenuTheme)
        val popupMenu = PopupMenu(wrapper, view, android.view.Gravity.END)
        popupMenu.menuInflater.inflate(R.menu.menu_folder_options, popupMenu.menu)

        try {
            val popupHelper = PopupMenu::class.java.getDeclaredField("mPopup")
            popupHelper.isAccessible = true
            val popupMenuHelper = popupHelper.get(popupMenu)
            popupMenuHelper?.javaClass?.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                ?.invoke(popupMenuHelper, true)
        } catch (e: Exception) {
            Log.e("FolderActivity", "Error showing folder options menu", e)
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            Log.d("FolderActivity", "Folder options item selected: $menuItem")
            when (menuItem.itemId) {
                R.id.action_rename -> {
                    currentFolder?.let { showRenameDialog(it) }
                    true
                }

                R.id.action_delete -> {
                    currentFolder?.let { showDeleteConfirmationDialog(it) }
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    private fun showRenameDialog(folder: Folder) {
        Log.d("FolderActivity", "Showing rename dialog")
        val dialogBinding = DialogCreateFolderBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this, R.style.TransparentDialog)
            .setView(dialogBinding.root)
            .create()

        // Remove window background to allow custom rounded corners
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.tvDialogTitle.text = getString(R.string.rename_folder)
        dialogBinding.etFolderName.setText(folder.name)
        dialogBinding.btnCreate.text = getString(R.string.rename)

        dialogBinding.btnCancel.setOnClickListener {
            Log.d("FolderActivity", "Rename dialog cancelled")
            dialog.dismiss()
        }

        dialogBinding.btnCreate.setOnClickListener {
            Log.d("FolderActivity", "Rename dialog confirmed")
            val newName = dialogBinding.etFolderName.text.toString()
            if (newName.isNotBlank()) {
                viewModel.updateFolder(folder.id, newName)
                supportActionBar?.title = newName
                dialog.dismiss()
            } else {
                dialogBinding.tilFolderName.error = getString(R.string.error_folder_name_empty)
            }
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(folder: Folder) {
        Log.d("FolderActivity", "Showing delete confirmation dialog")
        AlertDialog.Builder(this, R.style.TransparentDialog)
            .setTitle(getString(R.string.delete_folder))
            .setMessage(getString(R.string.delete_folder_confirmation))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                Log.d("FolderActivity", "Delete confirmed")
                viewModel.deleteFolder(folder.id)
                finish()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showNoteOptionsMenu(note: Note, view: View) {
        Log.d("FolderActivity", "Showing note options menu")
        val wrapper = ContextThemeWrapper(this, R.style.PopupMenuTheme)
        val popupMenu = PopupMenu(wrapper, view, android.view.Gravity.END)
        popupMenu.menuInflater.inflate(R.menu.menu_note_options, popupMenu.menu)

        // Remove edit option
        popupMenu.menu.removeItem(R.id.action_edit)

        try {
            val popupHelper = PopupMenu::class.java.getDeclaredField("mPopup")
            popupHelper.isAccessible = true
            val popupMenuHelper = popupHelper.get(popupMenu)
            popupMenuHelper?.javaClass?.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                ?.invoke(popupMenuHelper, true)
        } catch (e: Exception) {
            Log.e("FolderActivity", "Error showing note options menu", e)
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            Log.d("FolderActivity", "Note options item selected: $menuItem")
            when (menuItem.itemId) {
                R.id.action_delete -> {
                    showDeleteNoteConfirmationDialog(note)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun showDeleteNoteConfirmationDialog(note: Note) {
        Log.d("FolderActivity", "Showing delete note confirmation dialog")
        AlertDialog.Builder(this, R.style.TransparentDialog)
            .setTitle(getString(R.string.delete_note))
            .setMessage(getString(R.string.delete_note_confirmation))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                Log.d("FolderActivity", "Delete note confirmed")
                viewModel.deleteNote(note.id)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun navigateToResultActivity(note: Note) {
        Log.d("FolderActivity", "Navigating to ResultActivity with note: $note")
        val studyMaterials = StudyMaterials(
            id = note.id,
            input = note.input,
            type = when (note.type) {
                "text" -> MaterialType.TEXT
                "file" -> MaterialType.FILE
                "link" -> MaterialType.LINK
                "photo" -> MaterialType.PHOTO
                "audio" -> MaterialType.AUDIO
                else -> MaterialType.TEXT
            },
            userId = note.userId,
            timeStamp = note.timestamp,
            title = note.title,
            folderId = note.folderId
        )
        ResultActivity.start(this, studyMaterials)
    }

    companion object {
        private const val EXTRA_FOLDER = "extra_folder"

        fun start(context: Context, folder: Folder) {
            val intent = Intent(context, FolderActivity::class.java).apply {
                putExtra(EXTRA_FOLDER, folder)
            }
            context.startActivity(intent)
        }
    }
}

fun <T : android.os.Parcelable> Intent.getParcelableExtraCompat(
    key: String,
    clazz: Class<T>
): T? {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(key, clazz)
    } else {
        @Suppress("DEPRECATION")
        getParcelableExtra(key) as? T
    }
}