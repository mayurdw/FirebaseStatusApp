package com.example.firebasestatusapp.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.firebasestatusapp.R
import com.example.firebasestatusapp.viewmodel.ViewStatusesViewModel

class ViewStatusesFragment : Fragment() {

    companion object {
        fun newInstance() = ViewStatusesFragment()
    }

    private lateinit var viewModel: ViewStatusesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.view_statuses_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ViewStatusesViewModel::class.java)
        // TODO: Use the ViewModel
    }

}