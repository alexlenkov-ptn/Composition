package presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.composition.R
import com.example.composition.databinding.FragmentChooseLevelBinding
import domain.entity.Level
import presentation.gameFragment.GameFragment

class ChooseLevelFragment : Fragment() {

    private var _binding: FragmentChooseLevelBinding? = null

    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for ChooseLevelFragment must not be null")

    companion object {
        const val FRAGMENT_NAME = "ChooseLevelFragment"
        fun newInstance(): ChooseLevelFragment = ChooseLevelFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseLevelBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener(initButtonMap())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setOnClickListener(buttonMap: Map<Button, Level>) {
        for ((button, level) in buttonMap) {
            button.setOnClickListener {
                launchGameFragment(level)
            }
        }
    }

    private fun launchGameFragment(level: Level) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, GameFragment.newInstance(level))
            .addToBackStack(GameFragment.NAME)
            .commit()
    }

    private fun initButtonMap(): Map<Button, Level> {
        return with(binding) {
            mapOf(
                buttonLevelTest to Level.TEST,
                buttonLevelEasy to Level.EASY,
                buttonLevelNormal to Level.NORMAL,
                buttonLevelHard to Level.HARD
            )
        }
    }
}