package com.example.quickexpensestracker.ui

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.quickexpensestracker.MainActivity
import com.example.quickexpensestracker.model.Transaction
import com.example.quickexpensestracker.viewmodels.TransactionViewModel
import com.example.quickexpensestracker.viewmodels.TransactionViewModelFactory
import com.example.quickexpensetracker.R
import com.example.quickexpensetracker.databinding.ActivityAddTransactionBinding
import java.text.SimpleDateFormat
import java.util.*

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
        binding.addRootView.setOnClickListener {
            hideKeyboard(it)
        }
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

        binding.expense.setOnClickListener {
            updateDropdown(labelExpense)
        }

        binding.income.setOnClickListener {
            updateDropdown(labelIncome)
        }

        binding.labelInput.addTextChangedListener {
            if (it!!.isNotEmpty())
                binding.labelLayout.error = null
        }

        binding.amountInput.addTextChangedListener {
            if (it!!.isNotEmpty())
                binding.amountLayout.error = null
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
        binding.calendarDate.setText(SimpleDateFormat("EEEE, dd MMM yyyy", Locale.US).format(System.currentTimeMillis()))
        var date = Date()

        val cal = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val sdf = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.US)
            binding.calendarDate.setText(sdf.format(cal.time))
            date = cal.time
        }

        binding.calendarDate.setOnClickListener {
            DatePickerDialog(
                this, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupButtons() {
        binding.addTransactionBtn.setOnClickListener {
            validateAndAddTransaction()
        }

        binding.closeBtn.setOnClickListener {
            finish()
        }
    }

    private fun validateAndAddTransaction() {
        val label = binding.labelInput.text.toString()
        val description = binding.descriptionInput.text.toString()
        var amount = binding.amountInput.text.toString().toDoubleOrNull()

        if (label.isEmpty()) {
            binding.labelLayout.error = "Please enter a valid label"
        } else if (amount == null) {
            binding.amountLayout.error = "Please enter a valid amount"
        } else {
            if (binding.expense.isChecked) amount = -amount
            val transaction = Transaction(0, label, amount, description, Date())
            insert(transaction)
        }
    }

    private fun insert(transaction: Transaction) {
        vm.insertTransaction(transaction)
        val intentMain = Intent(this, MainActivity::class.java)
        startActivity(intentMain)
        finish()
    }
}
