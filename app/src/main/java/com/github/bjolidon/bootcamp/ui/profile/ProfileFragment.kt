package com.github.bjolidon.bootcamp.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.bjolidon.bootcamp.MainActivity
import com.github.bjolidon.bootcamp.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

private var _binding: FragmentProfileBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

    _binding = FragmentProfileBinding.inflate(inflater, container, false)
    val root: View = binding.root

    val textView: TextView = binding.textProfile
    profileViewModel.text.observe(viewLifecycleOwner) {
      textView.text = it
    }

    val button: Button = binding.saveNameButton
    button.setOnClickListener {
      val name = binding.saveNameText.text.toString()
      val mainActivity = requireActivity() as MainActivity
      mainActivity.setNavHeaderName(name)
    }

    return root
  }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}