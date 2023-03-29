package com.github.sdp.tarjetakuna.database

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.sdp.tarjetakuna.MainActivity
import com.github.sdp.tarjetakuna.R
import com.github.sdp.tarjetakuna.databinding.FragmentUserCardsBinding

/**
 * Fragment to manage cards in the user's collection.
 */
class UserCardsFragment : Fragment() {

    private lateinit var viewModel: UserCardsViewModel

    private var _binding: FragmentUserCardsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[UserCardsViewModel::class.java]
        _binding = FragmentUserCardsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val setMessage: TextView = binding.textSetCard
        setMessage.text = ""
        val getMessage: TextView = binding.textGetCard
        getMessage.text = ""

        val backButton = binding.buttonBackHome2
        backButton.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.changeFragment(R.id.nav_home)
        }

        // Adding a card to the user's collection (database)
        val setButton = binding.setCardButton
        setButton.setOnClickListener {
            //TODO replace this with a function call
            //this is for now hardcoded but will be replaced by a function call
            viewModel.addCardToCollection(viewModel.card2)
        }

        viewModel.setMessage.observe(viewLifecycleOwner) {
            if (it != null) {
                putSetMessage(it)
            }
        }

        // observe the message from the view model and display it in the UI when it changes
        viewModel.getMessage.observe(viewLifecycleOwner) {
            if (it != null) {
                putGetMessage(it)
            }
        }
        val getButton = binding.getCardButton
        getButton.setOnClickListener {
            viewModel.onCardButtonClick()
        }

        return root
    }

    /**
     * put the message in the "getMessage" textview.
     * @param msg the message to be displayed
     */
    private fun putGetMessage(msg: String) {
        val getMessage: TextView = binding.textGetCard
        getMessage.text = msg
    }

    /**
     * put the message in the "setMessage" textview.
     * @param msg the message to be displayed
     */
    private fun putSetMessage(msg: String) {
        val setMessage: TextView = binding.textSetCard
        setMessage.text = msg
    }

}