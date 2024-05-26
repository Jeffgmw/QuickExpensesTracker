package com.example.quickexpensestracker


import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.example.expensetracker.database.Transaction.Transaction
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_detailed.*
import java.util.*
import kotlin.math.abs

class DetailedActivity : AppCompatActivity() {

    private val vm: TransactionViewModel by viewModels {
        TransactionViewModel.TransactionViewModelFactory(application)
    }
    lateinit var arrayAdapter: ArrayAdapter<String>
    lateinit var date: Date


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)
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
        if (amount > 0.0) incomeEdit.isChecked = true

        val dateToDisplay = SimpleDateFormat("EEEE, dd MMM yyyy").format(date)

        labelInputEdit.setText(label)
        amountInputEdit.setText(amount.let { abs(it).toString() })
        descriptionInputEdit.setText(description)
        calendarDateEdit.setText(dateToDisplay)

        val labelExpense = resources.getStringArray(R.array.labelExpense)
        val labelIncome = resources.getStringArray(R.array.labelIncome)
        var labels = labelExpense
        if (incomeEdit.isChecked) labels = labelIncome
        arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labels)
        labelInputEdit.setAdapter(arrayAdapter)

        expenseEdit.setOnClickListener {
            if (labelInputEdit.text.toString() !in labelExpense.toList()) {
                labelInputEdit.setText("")
                arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labelExpense)
                labelInputEdit.setAdapter(arrayAdapter)
            }
        }

        incomeEdit.setOnClickListener {
            if (labelInputEdit.text.toString() !in labelIncome.toList()) {
                labelInputEdit.setText("")
                arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labelIncome)
                labelInputEdit.setAdapter(arrayAdapter)
            }
        }


        detailRootView.setOnClickListener {
            this.window.decorView.clearFocus()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        val cal = Calendar.getInstance()

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "EEEE, dd MMM yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                calendarDateEdit.setText(sdf.format(cal.time))
                date = cal.time

            }

        calendarDateEdit.setOnClickListener {
            DatePickerDialog(
                this, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


        labelInputEdit.addTextChangedListener {
            if (it!!.isNotEmpty())
                labelLayoutEdit.error = null
        }

        amountInputEdit.addTextChangedListener {
            if (it!!.isNotEmpty())
                amountLayoutEdit.error = null
        }

        updateBtnEdit.setOnClickListener {
            val label = labelInputEdit.text.toString()
            val description = descriptionInputEdit.text.toString()
            var amount = amountInputEdit.text.toString().toDoubleOrNull()
            if (label.isEmpty())
                labelLayoutEdit.error = "Please enter a valid label"
            else if (amount == null)
                amountLayoutEdit.error = "Please enter a valid amount"
            else {
                if (expenseEdit.isChecked) {
                    amount = -amount
                }
                val transaction = Transaction(transactionId, label, amount, description, date)
                vm.updateTransactions(transaction)
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "Transaction Updated", Toast.LENGTH_SHORT).show()
            }
        }

        deleteBtnEdit.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this item?")
                .setCancelable(false)
                .setNegativeButton("No") { _, _ -> }
                .setPositiveButton("yes") { _, _ ->
                    vm.deleteTransactions(transactionId)
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                    Toast.makeText(this, "Transaction Deleted", Toast.LENGTH_SHORT).show()
                }
                .show()
        }

        closeBtnEdit.setOnClickListener {
            finish()
        }
    }

}
