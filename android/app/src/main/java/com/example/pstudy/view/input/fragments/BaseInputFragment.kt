package com.example.pstudy.view.input.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewbinding.ViewBinding
import com.example.pstudy.view.input.InputViewModel
import com.google.android.material.snackbar.Snackbar

abstract class BaseInputFragment<VB : ViewBinding> : Fragment() {

    protected val viewModel: InputViewModel by activityViewModels()
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    protected abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB
    protected abstract fun setupViews()
    protected abstract fun observeViewModel()

    abstract fun validateAndSubmit()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun showSnackbar(message: Int) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    protected fun showSnackbar(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
        }
    }
}