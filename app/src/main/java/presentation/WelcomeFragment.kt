package presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.composition.R
import com.example.composition.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null

    private val binding
        get() = _binding
            ?: throw RuntimeException("Binding for WelcomeFragment must not be null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonUnderstand.setOnClickListener {
            launchChooseLevelFragment()
        }
    }

    private fun launchChooseLevelFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, ChooseLevelFragment.newInstance())
            .addToBackStack(ChooseLevelFragment.FRAGMENT_NAME)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}