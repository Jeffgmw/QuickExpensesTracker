package com.example.quickexpensestracker.ui

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

/*
Explanation of source of methods and their functionality:

1. onCreate(savedInstanceState: Bundle?): This method is called when the activity is first created.
It is where you should do all of your normal static set up to create views, bind data to lists, etc.
This method also provides you with a Bundle containing the activity's previously frozen state, if there was one.

2. setupViews(): This is a custom method defined in MainActivity to set up the RecyclerView and its adapter.

3. observeSettings(): This custom method sets up an observer on the SettingsDataStore to monitor changes in user preferences, specifically the sorting order.

4. observeTransactions(): This custom method observes the list of transactions from the TransactionViewModel and updates the dashboard and UI accordingly.

5. setupListeners(): This custom method sets up click listeners for the buttons in the activity, specifically for adding a new transaction and changing the sort order.

6. updateUI(transactions: List<Transaction>): This custom method updates the UI components (RecyclerView, balance, budget, and expense text views) with the provided list of transactions.

7. refreshTransactionList(): This custom method refreshes the transaction list based on the current search query and sort order.

8. onCreateOptionsMenu(menu: Menu): This method is called to initialize the contents of the Activity's standard options menu.
You should place your menu items in the menu passed into this method.

9. onQueryTextSubmit(query: String?): This method is called when the user submits the query. In this case, it simply returns true.

10. onQueryTextChange(query: String?): This method is called when the query text is changed by the user.
 It triggers a new search in the database with the updated query.

11. searchDatabase(query: String): This custom method formats the query for SQL LIKE search and observes the search results from the TransactionViewModel, updating the UI with the search results.
*/
