package com.example.quickexpensestracker

import android.content.Intent // Used to start new activities.
import androidx.appcompat.app.AppCompatActivity // Base class for activities that use the modern Android features.
import android.os.Bundle // Used to pass data between activities.
import android.view.Menu // Used to create options menu in the activity.
import androidx.activity.viewModels // Kotlin extension to get ViewModel instance.
import androidx.appcompat.widget.SearchView // Search widget used in the app bar.
import androidx.lifecycle.asLiveData // Extension function to convert Flow to LiveData.
import androidx.lifecycle.lifecycleScope // Provides a CoroutineScope for managing coroutines tied to the lifecycle of an Activity or Fragment.
import androidx.recyclerview.widget.LinearLayoutManager // LayoutManager for RecyclerView to arrange items in a linear list.
import com.example.quickexpensestracker.adapters.TransactionAdapter // Custom adapter for displaying transaction items in a RecyclerView.
import com.example.quickexpensestracker.data.SettingsDataStore // Class for managing application settings using DataStore.
import com.example.quickexpensestracker.model.Transaction // Data model class for transactions.
import com.example.quickexpensestracker.ui.AddTransactionActivity // Activity for adding new transactions.
import com.example.quickexpensestracker.ui.DetailedActivity // Activity for displaying detailed information about a transaction.
import com.example.quickexpensestracker.viewmodels.TransactionViewModel // ViewModel class for managing UI-related data in a lifecycle-conscious way.
import com.example.quickexpensestracker.viewmodels.TransactionViewModelFactory // Factory class for creating instances of TransactionViewModel.
import com.example.quickexpensetracker.R // Resource class for accessing application resources (e.g., strings, layouts, etc.).
import com.example.quickexpensetracker.databinding.ActivityMainBinding // Binding class for activity_main layout, allows direct access to UI components.
import kotlinx.coroutines.launch // Function to launch a new coroutine.

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener { // MainActivity class which is a subclass of AppCompatActivity and implements SearchView.OnQueryTextListener interface.

    private lateinit var transactionAdapter: TransactionAdapter // Adapter for RecyclerView to handle transaction items.
    private lateinit var settingsDataStore: SettingsDataStore // DataStore for managing app settings.
    private var isAsc: Boolean = false // Boolean to determine the sort order.
    private var searchView: SearchView? = null // SearchView instance for handling search functionality.

    private val vm: TransactionViewModel by viewModels { // Lazy initialization of TransactionViewModel using a factory.
        TransactionViewModelFactory(application)
    }

    private lateinit var binding: ActivityMainBinding // Binding object for activity_main layout.

    override fun onCreate(savedInstanceState: Bundle?) { // Called when the activity is starting.
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) // Inflate the layout for this activity using view binding.
        setContentView(binding.root) // Set the activity content to the root view of the binding.

        setupViews() // Set up the RecyclerView and adapter.
        observeSettings() // Observe the settings changes from DataStore.
        observeTransactions() // Observe the transaction data from the ViewModel.
        setupListeners() // Set up click listeners for buttons.
    }

    private fun setupViews() { // Method to set up views, particularly the RecyclerView.
        binding.recyclerview.layoutManager = LinearLayoutManager(this) // Set the layout manager for RecyclerView.
        transactionAdapter = TransactionAdapter { // Initialize the adapter with a lambda function for item click.
            val intent = Intent(this, DetailedActivity::class.java) // Create an intent to start DetailedActivity.
            intent.putExtra("transactionId", it.id) // Pass the transaction ID to the DetailedActivity.
            startActivity(intent) // Start the DetailedActivity.
        }
        binding.recyclerview.adapter = transactionAdapter // Set the adapter to the RecyclerView.
    }

    private fun observeSettings() { // Method to observe changes in settings.
        settingsDataStore = SettingsDataStore(this) // Initialize SettingsDataStore.
        settingsDataStore.preferenceFlow.asLiveData().observe(this) { isAsc -> // Observe the preference flow from DataStore.
            this.isAsc = isAsc // Update the sort order.
            refreshTransactionList() // Refresh the transaction list based on new sort order.
        }
    }

    private fun observeTransactions() { // Method to observe transaction data from the ViewModel.
        vm.getAllTransactions(isAsc).observe(this) { transactions -> // Observe the list of transactions.
            vm.updateDashboard(transactions) // Update the dashboard in the ViewModel.
            updateUI(transactions) // Update the UI with the new list of transactions.
        }
    }

    private fun setupListeners() { // Method to set up click listeners for buttons.
        binding.addBtn.setOnClickListener { // Set a click listener for the add button.
            startActivity(Intent(this, AddTransactionActivity::class.java)) // Start the AddTransactionActivity.
        }

        binding.sortButton.setOnClickListener { // Set a click listener for the sort button.
            lifecycleScope.launch { // Launch a coroutine in the lifecycle scope.
                settingsDataStore.saveLayoutToPreferencesStore(!isAsc, this@MainActivity) // Save the new sort order in the DataStore.
            }
        }
    }

    private fun updateUI(transactions: List<Transaction>) { // Method to update the UI with the transaction data.
        transactionAdapter.submitList(transactions) // Submit the new list of transactions to the adapter.
        binding.balance.text = vm.formattedAmount(vm.totalAmount) // Update the balance text view with the formatted total amount.
        binding.budget.text = vm.formattedAmount(vm.budgetAmount) // Update the budget text view with the formatted budget amount.
        binding.expense.text = vm.formattedAmount(vm.expenseAmount) // Update the expense text view with the formatted expense amount.
    }

    private fun refreshTransactionList() { // Method to refresh the transaction list based on the current query and sort order.
        val query = searchView?.query?.toString() ?: "" // Get the current search query from the SearchView.
        searchDatabase(query) // Search the database with the current query.
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean { // Method to create options menu.
        menuInflater.inflate(R.menu.menu_item, menu) // Inflate the menu layout.
        val search = menu.findItem(R.id.menu_search) // Get the search menu item.
        searchView = search?.actionView as? SearchView // Get the SearchView from the menu item.
        searchView?.isSubmitButtonEnabled = true // Enable the submit button on the SearchView.
        searchView?.setOnQueryTextListener(this) // Set this activity as the query text listener for the SearchView.
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean { // Method called when the search query is submitted.
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean { // Method called when the search query text is changed.
        if (query != null) {
            searchDatabase(query) // Search the database with the new query.
        }
        return true
    }

    private fun searchDatabase(query: String) { // Method to search the database with a query.
        val searchQuery = "%$query%" // Format the query for SQL LIKE search.
        vm.searchDatabase(searchQuery, isAsc).observe(this) { list -> // Observe the search results from the ViewModel.
            list.let {
                updateUI(it) // Update the UI with the search results.
            }
        }
    }
}

