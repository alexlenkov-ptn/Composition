package presentation

import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.composition.databinding.FragmentGameFinishedBinding
import domain.entity.GameResult

class GameFinishedFragment : Fragment() {

    private var _binding: FragmentGameFinishedBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for GameFinishedFragment must not be null")

    private lateinit var gameResult: GameResult

    companion object {
        const val KEY_GAME_RESULT = "game_result"

        fun newInstance(gameResult: GameResult): GameFinishedFragment {
            return GameFinishedFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_GAME_RESULT, gameResult)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
        Log.d("GameFinishedFragment", "gameResult: $gameResult")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameFinishedBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    retryGame()
                }
            })

        binding.buttonRetry.setOnClickListener {
            retryGame()
        }
    }

    private fun parseArgs() {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable<GameResult>(KEY_GAME_RESULT, GameResult::class.java)
                ?.let { gameResult ->
                    this.gameResult = gameResult
                }
        } else {
            requireArguments().getParcelable<GameResult>(KEY_GAME_RESULT)?.let { gameResult ->
                this.gameResult = gameResult
            }
        }
    }

    private fun retryGame() {
        requireActivity().supportFragmentManager
            .popBackStack(GameFragment.FRAGMENT_NAME, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}