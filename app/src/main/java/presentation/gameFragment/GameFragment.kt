package presentation.gameFragment

import android.content.res.ColorStateList
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.composition.R
import com.example.composition.databinding.FragmentGameBinding
import domain.entity.GameResult
import domain.entity.GameSettings
import domain.entity.Level
import presentation.GameFinishedFragment
import ru.sumin.composition.presentation.GameViewModel

class GameFragment : Fragment() {

    private val viewModel: GameViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[GameViewModel::class.java]
    }

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

    private val tvOptions by lazy {
        mutableListOf<TextView>().apply {
            with(binding) {
                add(tvOption1)
                add(tvOption2)
                add(tvOption3)
                add(tvOption4)
                add(tvOption5)
                add(tvOption6)
            }
        }
    }

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
        observeViewModel()
        setClickListenersToOptions()
        viewModel.startGame(level)
    }

    private fun setClickListenersToOptions() {
        tvOptions.forEach { tvOption ->
            tvOption.setOnClickListener {
                viewModel.chooseAnswer(tvOption.text.toString().toInt())
            }

        }
    }

    private fun observeViewModel() {
        viewModel.question.observe(viewLifecycleOwner) { state ->
            with(binding) {
                tvSum.text = state.sum.toString()
                tvLeftNumber.text = state.visibleNumber.toString()

                for (i in 0 until tvOptions.size) {
                    tvOptions[i].text = state.options[i].toString()
                }
            }
        }

        viewModel.percentOfRightAnswers.observe(viewLifecycleOwner) { state ->
            binding.progressBar.setProgress(state, true)
        }

        viewModel.enoughCount.observe(viewLifecycleOwner) {
            binding.tvAnswersProgress.setTextColor(getColorByState(it))
        }

        viewModel.enoughPercent.observe(viewLifecycleOwner) { state ->
            val color = getColorByState(state)
            binding.progressBar.progressTintList = ColorStateList.valueOf(color)
        }

        viewModel.formattedTime.observe(viewLifecycleOwner) { state ->
            binding.tvTimer.text = state
        }

        viewModel.minPercent.observe(viewLifecycleOwner) { state ->
            binding.progressBar.secondaryProgress = state
        }

        viewModel.gameResult.observe(viewLifecycleOwner) { state ->
            launchGameResult(state)
        }
    }

    private fun getColorByState(state: Boolean): Int {
        val colorId = if (state) {
            android.R.color.holo_green_light
        } else {
            android.R.color.holo_red_light
        }
        return ContextCompat.getColor(requireContext(), colorId)
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