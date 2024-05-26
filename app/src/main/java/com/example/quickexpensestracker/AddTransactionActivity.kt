package com.example.quickexpensestracker

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.quickexpensestracker.database.Transaction.Transaction
import com.example.quickexpensetracker.R
import com.example.quickexpensetracker.databinding.ActivityAddTransactionBinding
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private val vm: TransactionViewModel by viewModels {
        TransactionViewModel.TransactionViewModelFactory(application)
    }
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var binding: ActivityAddTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Add Expense"

        binding.addRootView.setOnClickListener {
            this.window.decorView.clearFocus()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        val labelExpense = resources.getStringArray(R.array.labelExpense)
        val labelIncome = resources.getStringArray(R.array.labelIncome)
        arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labelExpense)
        binding.labelInput.setAdapter(arrayAdapter)

        binding.expense.setOnClickListener {
            if (binding.labelInput.text.toString() !in labelExpense.toList()) {
                binding.labelInput.setText("")
                arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labelExpense)
                binding.labelInput.setAdapter(arrayAdapter)
            }
        }

        binding.income.setOnClickListener {
            if (binding.labelInput.text.toString() !in labelIncome.toList()) {
                binding.labelInput.setText("")
                arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labelIncome)
                binding.labelInput.setAdapter(arrayAdapter)
            }
        }

        binding.labelInput.addTextChangedListener {
            if (it!!.isNotEmpty())
                binding.labelLayout.error = null
        }

        binding.amountInput.addTextChangedListener {
            if (it!!.isNotEmpty())
                binding.amountLayout.error = null
        }

        binding.calendarDate.setText(SimpleDateFormat("EEEE, dd MMM yyyy").format(System.currentTimeMillis()))
        var date = Date()

        val cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val myFormat = "EEEE, dd MMM yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
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

        binding.addTransactionBtn.setOnClickListener {
            val label = binding.labelInput.text.toString()
            val description = binding.descriptionInput.text.toString()
            var amount = binding.amountInput.text.toString().toDoubleOrNull()

            if (label.isEmpty())
                binding.labelLayout.error = "Please enter a valid label"
            else if (amount == null)
                binding.amountLayout.error = "Please enter a valid amount"
            else {
                if (binding.expense.isChecked) amount = -amount
                val transaction = Transaction(0, label, amount, description, date)
                insert(transaction)
            }
        }

        binding.closeBtn.setOnClickListener {
            finish()
        }
    }

    private fun insert(transaction: Transaction) {
        vm.insertTransaction(transaction)
        val intentMain = Intent(this, MainActivity::class.java)
        startActivity(intentMain)
        finish()
    }
}
