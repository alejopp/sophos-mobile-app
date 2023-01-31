package com.example.sophos_mobile_app.ui.documents

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.sophos_mobile_app.R
import com.example.sophos_mobile_app.databinding.FragmentShowDocumentImageBinding

class ShowDocumentImageDialog(private val img: Bitmap?): DialogFragment() {

    private var _binding: FragmentShowDocumentImageBinding? = null
    private val binding get() = _binding!!

    companion object{
        const val TAG_SHOW_DOCUMENT_IMAGE = "show_document_image"
    }

    override fun onCreateView (
        inflater: LayoutInflater,
        container: ViewGroup?,
        saveInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowDocumentImageBinding.inflate(inflater, container,false)
        setListeners()
        return binding.root
    }

    private fun setListeners() {
        if(img != null){
            binding.ivShowDocumentImage.setImageBitmap(img)
        }
        else{
            binding.ivShowDocumentImage.setImageResource(R.drawable.ic_baseline_broken_image_24)
        }
        binding.tvShowDocReturn.setOnClickListener{
            dismiss()
        }
    }

}