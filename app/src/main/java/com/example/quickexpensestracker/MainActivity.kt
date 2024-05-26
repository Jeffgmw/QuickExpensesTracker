package com.example.quickexpensestracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quickexpensestracker.data.SettingsDataStore
import com.example.quickexpensestracker.database.Transaction.Transaction
import com.example.quickexpensetracker.R
import com.example.quickexpensetracker.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var settingsDataStore: SettingsDataStore
    private var isAsc: Boolean = false
    private var searchView: SearchView? = null

    private val vm: TransactionViewModel by viewModels {
        TransactionViewModelFactory(application)
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeSettings()
        observeTransactions()
        setupListeners()
    }

    private fun setupViews() {
        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        transactionAdapter = TransactionAdapter {
            val intent = Intent(this, DetailedActivity::class.java)
            intent.putExtra("transactionId", it.id)
            startActivity(intent)
        }
        binding.recyclerview.adapter = transactionAdapter
    }

    private fun observeSettings() {
        settingsDataStore = SettingsDataStore(this)
        settingsDataStore.preferenceFlow.asLiveData().observe(this) { isAsc ->
            this.isAsc = isAsc
            refreshTransactionList()
        }
    }

    private fun observeTransactions() {
        vm.getAllTransactions(isAsc).observe(this) { transactions ->
            vm.updateDashboard(transactions)
            updateUI(transactions)
        }
    }

    private fun setupListeners() {
        binding.addBtn.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

        binding.sortButton.setOnClickListener {
            lifecycleScope.launch {
                settingsDataStore.saveLayoutToPreferencesStore(!isAsc, this@MainActivity)
            }
        }
    }

    private fun updateUI(transactions: List<Transaction>) {
        transactionAdapter.submitList(transactions)
        binding.balance.text = vm.formattedAmount(vm.totalAmount)
        binding.budget.text = vm.formattedAmount(vm.budgetAmount)
        binding.expense.text = vm.formattedAmount(vm.expenseAmount)
    }

    private fun refreshTransactionList() {
        val query = searchView?.query?.toString() ?: ""
        searchDatabase(query)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)

        val search = menu.findItem(R.id.menu_search)
        searchView = search?.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) {
            searchDatabase(query)
        }
        return true
    }

    private fun searchDatabase(query: String) {
        val searchQuery = "%$query%"
        vm.searchDatabase(searchQuery, isAsc).observe(this) { list ->
            list.let {
                updateUI(it)
            }
        }
    }
}
