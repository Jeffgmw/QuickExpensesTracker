package com.example.quickexpensestracker.ui

import android.app.DatePickerDialog // Dialog for selecting a date.
import android.content.Context // Provides access to application-specific resources and classes.
import android.content.Intent // Used to start new activities.
import android.icu.text.SimpleDateFormat // Class for formatting and parsing dates in a locale-sensitive manner.
import android.os.Bundle // Used to pass data between activities.
import android.view.inputmethod.InputMethodManager // Provides methods to control the input method.
import android.widget.ArrayAdapter // Provides access to array data in the context of an AdapterView.
import android.widget.Toast // Class for displaying transient messages to the user.
import androidx.activity.viewModels // Kotlin extension to get ViewModel instance.
import androidx.appcompat.app.AppCompatActivity // Base class for activities that use the modern Android features.
import androidx.core.widget.addTextChangedListener // Extension function to add a text change listener to an EditText.
import com.example.quickexpensestracker.model.Transaction // Data model class for transactions.
import com.example.quickexpensestracker.viewmodels.TransactionViewModel // ViewModel class for managing UI-related data in a lifecycle-conscious way.
import com.example.quickexpensestracker.viewmodels.TransactionViewModelFactory // Factory class for creating instances of TransactionViewModel.
import com.example.quickexpensetracker.R // Resource class for accessing application resources (e.g., strings, layouts, etc.).
import com.example.quickexpensetracker.databinding.ActivityDetailedBinding // Binding class for activity_detailed layout, allows direct access to UI components.
import com.google.android.material.dialog.MaterialAlertDialogBuilder // Class for building Material Design alert dialogs.
import com.google.android.material.textfield.TextInputLayout
import java.util.* // Package for date and time classes.
import kotlin.math.abs // Kotlin standard library function for getting the absolute value.

class DetailedActivity : AppCompatActivity() {

    private val vm: TransactionViewModel by viewModels {
        TransactionViewModelFactory(application)
    }
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var date: Date
    private lateinit var binding: ActivityDetailedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val transactionId = intent.getIntExtra("transactionId", -1)
        title = "Edit Expense"

        observeTransaction(transactionId)
        setupListeners()
    }


    private fun setupListeners() {
        with(binding) {
            expenseEdit.setOnClickListener { updateLabelAdapter(R.array.labelExpense) }
            incomeEdit.setOnClickListener { updateLabelAdapter(R.array.labelIncome) }

            detailRootView.setOnClickListener { hideKeyboard() }
            calendarDateEdit.setOnClickListener { showDatePicker() }

            labelInputEdit.addTextChangedListener { clearError(binding.labelLayoutEdit) }
            amountInputEdit.addTextChangedListener { clearError(binding.amountLayoutEdit) }

            updateBtnEdit.setOnClickListener { handleUpdate() }
            deleteBtnEdit.setOnClickListener { confirmDelete() }
            closeBtnEdit.setOnClickListener { finish() }
        }
    }


    private fun observeTransaction(transactionId: Int) {
        vm.getTransactionById(transactionId).observe(this) { transaction ->
            transaction?.let {
                populateUI(transaction)
            }
        }
    }


    private fun populateUI(transaction: Transaction) {
        with(binding) {
            if (transaction.amount > 0.0) incomeEdit.isChecked = true

            date = transaction.date
            val dateToDisplay = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.US).format(date)
            labelInputEdit.setText(transaction.label)
            amountInputEdit.setText(abs(transaction.amount).toString())
            descriptionInputEdit.setText(transaction.description)
            calendarDateEdit.setText(dateToDisplay)

            setupAdapter()
        }
    }


    private fun setupAdapter() {
        val labels = if (binding.incomeEdit.isChecked) {
            resources.getStringArray(R.array.labelIncome)
        } else {
            resources.getStringArray(R.array.labelExpense)
        }
        arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labels)
        binding.labelInputEdit.setAdapter(arrayAdapter)
    }


    private fun updateLabelAdapter(arrayResId: Int) {
        val labels = resources.getStringArray(arrayResId)
        if (binding.labelInputEdit.text.toString() !in labels) {
            binding.labelInputEdit.setText("")
            arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labels)
            binding.labelInputEdit.setAdapter(arrayAdapter)
        }
    }


    private fun hideKeyboard() {
        window.decorView.clearFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.detailRootView.windowToken, 0)
    }


    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                cal.set(year, month, day)
                val formattedDate = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.US).format(cal.time)
                binding.calendarDateEdit.setText(formattedDate)
                date = cal.time
            },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }


    private fun clearError(layout: TextInputLayout) {
        layout.error = null
    }


    private fun handleUpdate() {
        val label = binding.labelInputEdit.text.toString()
        val description = binding.descriptionInputEdit.text.toString()
        val amount = binding.amountInputEdit.text.toString().toDoubleOrNull()

        when {
            label.isEmpty() -> binding.labelLayoutEdit.error = "Please enter a valid label"
            amount == null -> binding.amountLayoutEdit.error = "Please enter a valid amount"
            else -> {
                val finalAmount = if (binding.expenseEdit.isChecked) -amount else amount
                val transaction = Transaction(intent.getIntExtra("transactionId", -1), label, finalAmount, description, date)
                vm.updateTransaction(transaction)
                navigateToMainActivity("Transaction Updated")
            }
        }
    }


    private fun confirmDelete() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this item?")
            .setCancelable(false)
            .setNegativeButton("No", null)
            .setPositiveButton("Yes") { _, _ ->
                vm.deleteTransaction(intent.getIntExtra("transactionId", -1))
                navigateToMainActivity("Transaction Deleted")
            }
            .show()
    }


    private fun navigateToMainActivity(message: String) {
        startActivity(Intent(this, MainActivity::class.java))
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}


