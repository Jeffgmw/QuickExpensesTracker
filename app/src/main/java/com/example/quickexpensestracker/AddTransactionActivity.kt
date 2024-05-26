package com.example.quickexpensestracker

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.example.expensetracker.database.Transaction.Transaction
import kotlinx.android.synthetic.main.activity_add_transaction.*
import kotlinx.android.synthetic.main.activity_add_transaction.amountInput
import kotlinx.android.synthetic.main.activity_add_transaction.amountLayout
import kotlinx.android.synthetic.main.activity_add_transaction.closeBtn
import kotlinx.android.synthetic.main.activity_add_transaction.descriptionInput
import kotlinx.android.synthetic.main.activity_add_transaction.labelInput
import kotlinx.android.synthetic.main.activity_add_transaction.labelLayout
import java.util.*


class AddTransactionActivity : AppCompatActivity() {

    private val vm: TransactionViewModel by viewModels{
        TransactionViewModel.TransactionViewModelFactory(application)
    }
    lateinit var arrayAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)
        title = "Add Expense"

        addRootView.setOnClickListener {
            this.window.decorView.clearFocus()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        val labelExpense = resources.getStringArray(R.array.labelExpense)
        val labelIncome = resources.getStringArray(R.array.labelIncome)
        arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labelExpense)
        labelInput.setAdapter(arrayAdapter)



        expense.setOnClickListener {
            if (labelInput.text.toString() !in labelExpense.toList()) {
                labelInput.setText("")
                arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labelExpense)
                labelInput.setAdapter(arrayAdapter)
            }
        }

        income.setOnClickListener {
            if (labelInput.text.toString() !in labelIncome.toList()) {
                labelInput.setText("")
                arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labelIncome)
                labelInput.setAdapter(arrayAdapter)
            }
        }

        labelInput.addTextChangedListener {
            if (it!!.isNotEmpty())
                labelLayout.error = null
        }

        amountInput.addTextChangedListener {
            if (it!!.isNotEmpty())
                amountLayout.error = null
        }


        calendarDate.setText(SimpleDateFormat("EEEE, dd MMM yyyy").format(System.currentTimeMillis()))
        var date = Date()



        var cal = Calendar.getInstance()

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "EEEE, dd MMM yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                calendarDate.setText(sdf.format(cal.time))
                date = cal.time

            }

        calendarDate.setOnClickListener {
            DatePickerDialog(
                this, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }



        addTransactionBtn.setOnClickListener {
            val label = labelInput.text.toString()
            val description = descriptionInput.text.toString()
            var amount = amountInput.text.toString().toDoubleOrNull()
//            val date = calendarDate.text.toString()



            if (label.isEmpty())
                labelLayout.error = "Please enter a valid label"
            else if (amount == null)
                amountLayout.error = "Please enter a valid amount"
            else {
                if (expense.isChecked) amount = -amount
                val transaction = Transaction(0, label, amount, description, date)
                insert(transaction)
            }
        }

        closeBtn.setOnClickListener {
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

