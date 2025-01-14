package presentation.gameFragment

import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.composition.R
import com.example.composition.databinding.FragmentGameBinding
import domain.entity.GameResult
import domain.entity.GameSettings
import domain.entity.Level
import presentation.GameFinishedFragment

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private lateinit var level: Level

    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for GameFragment must not be null")

    private val gameResultTest = GameResult(
        true, 10, 5,
        GameSettings(
            10, 5, 6, 7
        )
    )

    companion object {
        const val FRAGMENT_NAME = "game_fragment"
        const val KEY_LEVEL = "level"

        fun newInstance(level: Level): GameFragment {
            return GameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_LEVEL, level)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
        Log.d("GameFragment", "level: $level")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvSum.setOnClickListener {
            launchGameResult(gameResultTest)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun launchGameResult(gameResult: GameResult) {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(
                R.id.main_container,
                GameFinishedFragment.newInstance(gameResult)
            ).commit()
    }

    private fun parseArgs() {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable<Level>(KEY_LEVEL, Level::class.java)
                ?.let { level ->
                    this.level = level
                }
        } else {
            requireArguments().getParcelable<Level>(KEY_LEVEL)?.let { level ->
                this.level = level
            }
        }
    }

}