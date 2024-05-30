package com.example.quickexpensestracker.ui

import android.app.DatePickerDialog
import android.content.Context // Provides access to application-specific resources and classes.
import android.content.Intent // Used to start new activities.
import android.os.Bundle // Used to pass data between activities.
import android.view.View
import android.view.inputmethod.InputMethodManager // Provides methods to control the input method.
import android.widget.ArrayAdapter // Provides access to array data in the context of an AdapterView.
import androidx.activity.viewModels // Kotlin extension to get ViewModel instance.
import androidx.appcompat.app.AppCompatActivity // Base class for activities that use the modern Android features.
import androidx.core.widget.addTextChangedListener // Extension function to add a text change listener to an EditText.
import com.example.quickexpensestracker.model.Transaction // Data model class for transactions.
import com.example.quickexpensestracker.viewmodels.TransactionViewModel // ViewModel class for managing UI-related data in a lifecycle-conscious way.
import com.example.quickexpensestracker.viewmodels.TransactionViewModelFactory // Factory class for creating instances of TransactionViewModel.
import com.example.quickexpensetracker.R // Resource class for accessing application resources (e.g., strings, layouts, etc.).
import com.example.quickexpensetracker.databinding.ActivityAddTransactionBinding // Binding class for activity_add_transaction layout, allows direct access to UI components.
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat // Class for formatting and parsing dates in a locale-sensitive manner.
import java.util.* // Package for date and time classes.

class AddTransactionActivity : AppCompatActivity() {

    private val vm: TransactionViewModel by viewModels {
        TransactionViewModelFactory(application)
    }

    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var binding: ActivityAddTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Add Expense"

        setupUI()
    }


    private fun setupUI() {
        setupRootView()
        setupDropdown()
        setupDatePicker()
        setupButtons()
    }


    private fun setupRootView() {
        binding.addRootView.setOnClickListener { hideKeyboard(it) }
    }


    private fun hideKeyboard(view: android.view.View) {
        this.window.decorView.clearFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    private fun setupDropdown() {
        val labelExpense = resources.getStringArray(R.array.labelExpense)
        val labelIncome = resources.getStringArray(R.array.labelIncome)
        arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labelExpense)
        binding.labelInput.setAdapter(arrayAdapter)

        binding.expense.setOnClickListener { updateDropdown(labelExpense) }
        binding.income.setOnClickListener { updateDropdown(labelIncome) }

        binding.labelInput.addTextChangedListener {
            if (it!!.isNotEmpty()) binding.labelLayout.error = null
        }

        binding.amountInput.addTextChangedListener {
            if (it!!.isNotEmpty()) binding.amountLayout.error = null
        }
    }


    private fun updateDropdown(labels: Array<String>) {
        if (binding.labelInput.text.toString() !in labels.toList()) {
            binding.labelInput.setText("")
            arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labels)
            binding.labelInput.setAdapter(arrayAdapter)
        }
    }


    private fun setupDatePicker() {
        val initialDate = Date()
        binding.calendarDate.setText(formatDate(initialDate))

        val cal = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            binding.calendarDate.setText(formatDate(cal.time))
        }

        binding.calendarDate.setOnClickListener {
            DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }


    private fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.US)
        return sdf.format(date)
    }


    private fun setupButtons() {
        binding.addTransactionBtn.setOnClickListener { validateAndAddTransaction() }
        binding.closeBtn.setOnClickListener { finish() }
    }


    private fun validateAndAddTransaction() {
        val label = binding.labelInput.text.toString()
        val description = binding.descriptionInput.text.toString()
        val amount = binding.amountInput.text.toString().toDoubleOrNull()
        val date = Date()

        when {
            label.isEmpty() -> binding.labelLayout.error = "Please enter a valid label"
            amount == null -> binding.amountLayout.error = "Please enter a valid amount"
            else -> {
                val finalAmount = if (binding.expense.isChecked) -amount else amount
                val transaction = Transaction(0, label, finalAmount, description, date)
                insertTransaction(transaction)
            }
        }
    }


    private fun insertTransaction(transaction: Transaction) {
        vm.insertTransaction(transaction)
        showSnackbar("Transaction Created")
        binding.root.postDelayed({
            navigateToMainActivity()
        }, 2000) //
    }


    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showSnackbar(message: String) {
        val rootView = findViewById<View>(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }
}


