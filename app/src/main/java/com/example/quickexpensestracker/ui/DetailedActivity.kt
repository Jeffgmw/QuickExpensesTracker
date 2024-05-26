package com.example.quickexpensestracker.ui

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.quickexpensestracker.MainActivity
import com.example.quickexpensestracker.model.Transaction
import com.example.quickexpensestracker.viewmodels.TransactionViewModel
import com.example.quickexpensestracker.viewmodels.TransactionViewModelFactory
import com.example.quickexpensetracker.R
import com.example.quickexpensetracker.databinding.ActivityDetailedBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*
import kotlin.math.abs

class DetailedActivity : AppCompatActivity() {

    private val vm: TransactionViewModel by viewModels {
        TransactionViewModelFactory(application)
    }
    lateinit var arrayAdapter: ArrayAdapter<String>
    lateinit var date: Date

    private lateinit var binding: ActivityDetailedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val transactionId = intent.getIntExtra("transactionId", -1)
        title = "Edit Expense"

        vm.getTransactionById(transactionId).observe(this) {
            it?.let {
                val label = it.label
                date = it.date
                val description = it.description
                val amount = it.amount
                detailedTransaction(transactionId, label, date, description, amount)
            }
        }
    }

    private fun detailedTransaction(
        transactionId: Int,
        label: String,
        transactionDate: Date,
        description: String,
        amount: Double
    ) {
        var date = transactionDate
        if (amount > 0.0) binding.incomeEdit.isChecked = true

        val dateToDisplay = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.US).format(date)

        binding.labelInputEdit.setText(label)
        binding.amountInputEdit.setText(amount.let { abs(it).toString() })
        binding.descriptionInputEdit.setText(description)
        binding.calendarDateEdit.setText(dateToDisplay)

        val labelExpense = resources.getStringArray(R.array.labelExpense)
        val labelIncome = resources.getStringArray(R.array.labelIncome)
        var labels = labelExpense
        if (binding.incomeEdit.isChecked) labels = labelIncome
        arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labels)
        binding.labelInputEdit.setAdapter(arrayAdapter)

        binding.expenseEdit.setOnClickListener {
            if (binding.labelInputEdit.text.toString() !in labelExpense.toList()) {
                binding.labelInputEdit.setText("")
                arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labelExpense)
                binding.labelInputEdit.setAdapter(arrayAdapter)
            }
        }

        binding.incomeEdit.setOnClickListener {
            if (binding.labelInputEdit.text.toString() !in labelIncome.toList()) {
                binding.labelInputEdit.setText("")
                arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labelIncome)
                binding.labelInputEdit.setAdapter(arrayAdapter)
            }
        }

        binding.detailRootView.setOnClickListener {
            this.window.decorView.clearFocus()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        val cal = Calendar.getInstance()

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "EEEE, dd MMM yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.calendarDateEdit.setText(sdf.format(cal.time))
                date = cal.time
            }

        binding.calendarDateEdit.setOnClickListener {
            DatePickerDialog(
                this, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.labelInputEdit.addTextChangedListener {
            if (it!!.isNotEmpty())
                binding.labelLayoutEdit.error = null
        }

        binding.amountInputEdit.addTextChangedListener {
            if (it!!.isNotEmpty())
                binding.amountLayoutEdit.error = null
        }

        binding.updateBtnEdit.setOnClickListener {
            val label = binding.labelInputEdit.text.toString()
            val description = binding.descriptionInputEdit.text.toString()
            var amount = binding.amountInputEdit.text.toString().toDoubleOrNull()
            if (label.isEmpty())
                binding.labelLayoutEdit.error = "Please enter a valid label"
            else if (amount == null)
                binding.amountLayoutEdit.error = "Please enter a valid amount"
            else {
                if (binding.expenseEdit.isChecked) {
                    amount = -amount
                }
                val transaction = Transaction(transactionId, label, amount, description, date)
                vm.updateTransaction(transaction)
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "Transaction Updated", Toast.LENGTH_SHORT).show()
            }
        }

        binding.deleteBtnEdit.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this item?")
                .setCancelable(false)
                .setNegativeButton("No") { _, _ -> }
                .setPositiveButton("Yes") { _, _ ->
                    vm.deleteTransaction(transactionId)
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                    Toast.makeText(this, "Transaction Deleted", Toast.LENGTH_SHORT).show()
                }
                .show()
        }

        binding.closeBtnEdit.setOnClickListener {
            finish()
        }
    }
}
