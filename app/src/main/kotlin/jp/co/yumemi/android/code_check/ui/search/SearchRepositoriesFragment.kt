package jp.co.yumemi.android.code_check.ui.search

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.viewmodel.SearchRepositoriesViewModel
import jp.co.yumemi.android.code_check.model.RepositoryItem
import jp.co.yumemi.android.code_check.databinding.SearchRepositoriesFragmentBinding
import jp.co.yumemi.android.code_check.ui.adapters.CustomAdapter
import jp.co.yumemi.android.code_check.ui.base.BaseFragment
import jp.co.yumemi.android.code_check.ui.navigation.RepositoryNavigator
import jp.co.yumemi.android.code_check.viewmodel.RepositoryState
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * SearchRepositoriesFragmentは、リポジトリの一覧を表示するFragmentです。
 * RecyclerViewを使用してリポジトリの一覧を表示し、検索機能を提供します。
 */
@AndroidEntryPoint
class SearchRepositoriesFragment : BaseFragment() {
    private lateinit var navigator: RepositoryNavigator
    // ViewBindingのインスタンスを保持するプライベート変数です。Viewが破棄された際にはnullに設定されます。
    private var _binding: SearchRepositoriesFragmentBinding? = null
    //    安全にBindingインスタンスにアクセスするためのプロパティです。_bindingがnullの場合、IllegalStateExceptionを投げます。
    //    これにより、Viewのライフサイクル外でのアクセスを防ぐことができます。
    private val binding get() = _binding ?: throw IllegalStateException("BindingはonCreateViewとonDestroyViewの間でのみアクセス可能です")
    // Move ViewModel declaration to a property of the fragment for broader scope
    private val viewModel: SearchRepositoriesViewModel by viewModels()

    private val backgroundImages = listOf(
        R.drawable.image_1,
        R.drawable.image_2,
        R.drawable.image_3,
        R.drawable.image_4,
        R.drawable.image_5,
    )
    private var currentBackgroundIndex = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SearchRepositoriesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
    private fun onItemClicked(item: RepositoryItem) {
        navigator.navigateToDetail(item)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigator = RepositoryNavigator(findNavController())
        setupRecyclerView()
        observeViewModel()
        setupSwipeGestureListener()

        val lottieAnimationView: LottieAnimationView = binding.lottieAnimationView
        lottieAnimationView.setOnClickListener {
            it.visibility = View.GONE
            showSearchInput()
        }

        binding.btnCancelSearch.setOnClickListener {
            showSearchInput()
            binding.carouselRecyclerview.visibility = View.GONE
            binding.btnCancelSearch.visibility = View.GONE
        }
    }

    private fun showSearchInput() {

        // Inside onViewCreated or another method where you have access to the binding
        val searchInputView = LayoutInflater.from(requireContext()).inflate(R.layout.search_input_layout, binding.root as ViewGroup, false)

        // Create a BottomSheetDialog to host the search input
        val searchBottomSheetDialog = BottomSheetDialog(requireContext())
        searchBottomSheetDialog.setContentView(searchInputView)
        searchBottomSheetDialog.setCanceledOnTouchOutside(false)
        searchBottomSheetDialog.show()

        searchBottomSheetDialog.setOnDismissListener {
            // Check if the loading overlay is visible
            if (binding.loadingOverlay.visibility != View.VISIBLE) {
                // If the loading overlay is not visible, make the Lottie animation visible again
                binding.lottieAnimationView.visibility = View.VISIBLE
            }
        }

        // Get the TextInputEditText from the inflated view
        val searchInputText = searchInputView.findViewById<TextInputEditText>(R.id.searchInputText)

        // Set the OnEditorActionListener to listen for the "actionSearch" event
        searchInputText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(v.text.toString())
                searchBottomSheetDialog.dismiss()
                true
            } else {
                false
            }
        }

        // Request focus and show the keyboard for the TextInputEditText
        searchInputText.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(searchInputText, InputMethodManager.SHOW_IMPLICIT)
    }


    private fun performSearch(query: String) {
        if (query.startsWith("#")) {
            showDialogMessage(getString(R.string.invalid_input_message))
        } else if (query.isNotEmpty()) {
            try {
                viewModel.searchRepositories(query)
            } catch (e: Exception) {
                showDialogMessage(getString(R.string.error_message) + ": ${e.localizedMessage}")
            }
        } else {
            showDialogMessage(getString(R.string.enter_search_query))
        }
    }



    @SuppressLint("ClickableViewAccessibility")
    private fun setupSwipeGestureListener() {
        val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
            private val SWIPE_THRESHOLD = 100
            private val SWIPE_VELOCITY_THRESHOLD = 100

            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                val diffX = e2.x - e1.x
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    currentBackgroundIndex = if (diffX > 0) {
                        // Swipe right
                        (currentBackgroundIndex - 1 + backgroundImages.size) % backgroundImages.size
                    } else {
                        // Swipe left
                        (currentBackgroundIndex + 1) % backgroundImages.size
                    }
                    updateBackgroundImage()
                }
                return true
            }
        }

        val gestureDetector = GestureDetector(context, gestureListener)
        binding.backgroundImageView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    private fun updateBackgroundImage() {
        // Update the background image of the ImageView
        binding.backgroundImageView.setImageResource(backgroundImages[currentBackgroundIndex])
    }

    private fun setupRecyclerView() {
        val adapter = CustomAdapter().apply {
            itemClickListener = { item ->
                onItemClicked(item)
            }
        }
        // Use CarouselRecyclerview instead of the regular RecyclerView
        val carouselRecyclerView = binding.carouselRecyclerview
        carouselRecyclerView.adapter = adapter

        // Set properties for CarouselRecyclerview
        carouselRecyclerView.set3DItem(true) // Enable 3D effect
        carouselRecyclerView.setInfinite(true) // Enable infinite scrolling
        carouselRecyclerView.setAlpha(true) // Enable alpha changes
        carouselRecyclerView.setIntervalRatio(0.6f)

    }

    private fun observeViewModel() {
        viewModel.repositoryState.observe(viewLifecycleOwner) { state ->
            updateUIState(state)
        }
    }

    private fun showSuccess(items: List<RepositoryItem>) {
        showLoading(false) // Turn off loading indicator
        if (items.isEmpty()) {
            showEmptyState(true)
        } else {
            binding.carouselRecyclerview.visibility = View.VISIBLE
            binding.lottieAnimationView.visibility = View.GONE
            binding.btnCancelSearch.visibility = View.VISIBLE
            (binding.carouselRecyclerview.adapter as CustomAdapter).submitList(items)
            showEmptyState(false)
        }
    }

    // Consolidate the visibility toggles for UI elements
    override fun showLoading(show: Boolean) {
        super.showLoading(show)
        if (show) {
            binding.loadingOverlay.visibility = View.VISIBLE
            // Remove the line related to binding.searchBar as it's no longer in the layout
            binding.carouselRecyclerview.visibility = View.GONE
            binding.lottieAnimationView.visibility = View.GONE // Hide the floating action button when loading
            binding.btnCancelSearch.visibility = View.GONE
        } else {
            binding.loadingOverlay.visibility = View.GONE
            binding.carouselRecyclerview.visibility = View.VISIBLE
            binding.lottieAnimationView.visibility = View.GONE // Show the floating action button when not loading
        }
    }

    override fun showError(message: String) {
        super.showError(message)
        val errorMessage = getString(R.string.error_message) + "\n" + message
        showDialogMessage(errorMessage) // Pass both title and message
        binding.carouselRecyclerview.visibility = View.GONE
        binding.btnCancelSearch.visibility = View.GONE
    }

    override fun showEmptyState(show: Boolean) {
        super.showEmptyState(show)
        if (show) {
            showDialogMessage(getString(R.string.no_items_found_message))
            binding.carouselRecyclerview.visibility = View.GONE
            binding.btnCancelSearch.visibility = View.GONE
        } else {
            binding.carouselRecyclerview.visibility = View.VISIBLE
        }
    }


    private fun updateUIState(state: RepositoryState<List<RepositoryItem>>) {
        when (state) {
            is RepositoryState.Loading -> showLoading(true)
            is RepositoryState.Success -> showSuccess(state.data)
            is RepositoryState.Error -> showError(state.exception.localizedMessage ?: "An unknown error occurred")
            is RepositoryState.Empty -> showEmptyState(true)
            else -> Log.e("SearchRepositoriesFragment", "Unhandled state: $state")
        }
    }

    private fun showDialogMessage(message: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_custom, null)
        val messageTextView = dialogView.findViewById<TextView>(R.id.dialogMessage)
        messageTextView.text = message

        val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.CustomDialogStyle)
        dialogBuilder.setView(dialogView)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.show()

        val closeButton = dialogView.findViewById<ImageButton>(R.id.closeButton)
        closeButton.setOnClickListener {
            alertDialog.dismiss()

            if (binding.loadingOverlay.visibility != View.VISIBLE) {
                // If the loading overlay is not visible, make the Lottie animation visible again
                binding.lottieAnimationView.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up any observers to prevent memory leaks
        viewModel.repositoryState.removeObservers(viewLifecycleOwner)
        // Viewが破棄される際に、Bindingの参照をクリアします。これによりメモリリークを防ぎます。
        _binding = null
    }
}