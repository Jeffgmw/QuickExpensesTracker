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
import java.util.* // Package for date and time classes.
import kotlin.math.abs // Kotlin standard library function for getting the absolute value.

class DetailedActivity : AppCompatActivity() { // DetailedActivity class which is a subclass of AppCompatActivity.

    private val vm: TransactionViewModel by viewModels { // Lazy initialization of TransactionViewModel using a factory.
        TransactionViewModelFactory(application)
    }
    lateinit var arrayAdapter: ArrayAdapter<String> // Adapter for handling label input.
    lateinit var date: Date // Variable to store the transaction date.

    private lateinit var binding: ActivityDetailedBinding // Binding object for activity_detailed layout.

    override fun onCreate(savedInstanceState: Bundle?) { // Called when the activity is starting.
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedBinding.inflate(layoutInflater) // Inflate the layout for this activity using view binding.
        setContentView(binding.root) // Set the activity content to the root view of the binding.
        val transactionId = intent.getIntExtra("transactionId", -1) // Retrieve the transaction ID from the intent.
        title = "Edit Expense" // Set the title of the activity.

        vm.getTransactionById(transactionId).observe(this) { // Observe the transaction data from the ViewModel.
            it?.let { transaction ->
                val label = transaction.label
                date = transaction.date
                val description = transaction.description
                val amount = transaction.amount
                detailedTransaction(transactionId, label, date, description, amount) // Populate the UI with the transaction data.
            }
        }
    }

    private fun detailedTransaction( // Method to populate the UI with transaction details.
        transactionId: Int,
        label: String,
        transactionDate: Date,
        description: String,
        amount: Double
    ) {
        var date = transactionDate
        if (amount > 0.0) binding.incomeEdit.isChecked = true // Set the income radio button if the amount is positive.

        val dateToDisplay = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.US).format(date) // Format the date for display.

        binding.labelInputEdit.setText(label) // Set the label input field.
        binding.amountInputEdit.setText(abs(amount).toString()) // Set the amount input field.
        binding.descriptionInputEdit.setText(description) // Set the description input field.
        binding.calendarDateEdit.setText(dateToDisplay) // Set the date input field.

        val labelExpense = resources.getStringArray(R.array.labelExpense) // Get the expense labels from resources.
        val labelIncome = resources.getStringArray(R.array.labelIncome) // Get the income labels from resources.
        var labels = labelExpense
        if (binding.incomeEdit.isChecked) labels = labelIncome // Set the labels based on the type of transaction.
        arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labels) // Create an ArrayAdapter with the labels.
        binding.labelInputEdit.setAdapter(arrayAdapter) // Set the adapter for the label input field.

        binding.expenseEdit.setOnClickListener { // Set a click listener for the expense radio button.
            if (binding.labelInputEdit.text.toString() !in labelExpense.toList()) {
                binding.labelInputEdit.setText("")
                arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labelExpense)
                binding.labelInputEdit.setAdapter(arrayAdapter)
            }
        }

        binding.incomeEdit.setOnClickListener { // Set a click listener for the income radio button.
            if (binding.labelInputEdit.text.toString() !in labelIncome.toList()) {
                binding.labelInputEdit.setText("")
                arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labelIncome)
                binding.labelInputEdit.setAdapter(arrayAdapter)
            }
        }

        binding.detailRootView.setOnClickListener { // Set a click listener for the root view to hide the keyboard.
            this.window.decorView.clearFocus()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        val cal = Calendar.getInstance() // Get a Calendar instance.

        val dateSetListener = // Date picker dialog listener to update the date field.
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "EEEE, dd MMM yyyy" // Date format.
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.calendarDateEdit.setText(sdf.format(cal.time))
                date = cal.time
            }

        binding.calendarDateEdit.setOnClickListener { // Set a click listener to show the date picker dialog.
            DatePickerDialog(
                this, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.labelInputEdit.addTextChangedListener { // Add a text changed listener to the label input field.
            if (it!!.isNotEmpty())
                binding.labelLayoutEdit.error = null // Clear error message when text is not empty.
        }

        binding.amountInputEdit.addTextChangedListener { // Add a text changed listener to the amount input field.
            if (it!!.isNotEmpty())
                binding.amountLayoutEdit.error = null // Clear error message when text is not empty.
        }

        binding.updateBtnEdit.setOnClickListener { // Set a click listener for the update button.
            val label = binding.labelInputEdit.text.toString() // Get the label from the input field.
            val description = binding.descriptionInputEdit.text.toString() // Get the description from the input field.
            var amount = binding.amountInputEdit.text.toString().toDoubleOrNull() // Get the amount from the input field.
            if (label.isEmpty())
                binding.labelLayoutEdit.error = "Please enter a valid label" // Show error if label is empty.
            else if (amount == null)
                binding.amountLayoutEdit.error = "Please enter a valid amount" // Show error if amount is invalid.
            else {
                if (binding.expenseEdit.isChecked) {
                    amount = -amount // Negate the amount if it is an expense.
                }
                val transaction = Transaction(transactionId, label, amount, description, date) // Create a new Transaction object.
                vm.updateTransaction(transaction) // Update the transaction in the ViewModel.
                startActivity(Intent(this, MainActivity::class.java)) // Start the MainActivity.
                Toast.makeText(this, "Transaction Updated", Toast.LENGTH_SHORT).show() // Show a toast message.
            }
        }

        binding.deleteBtnEdit.setOnClickListener { // Set a click listener for the delete button.
            MaterialAlertDialogBuilder(this) // Show a confirmation dialog before deleting the transaction.
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this item?")
                .setCancelable(false)
                .setNegativeButton("No") { _, _ -> }
                .setPositiveButton("Yes") { _, _ ->
                    vm.deleteTransaction(transactionId) // Delete the transaction in the ViewModel.
                    finish() // Close the current activity.
                    startActivity(Intent(this, MainActivity::class.java)) // Start the MainActivity.
                    Toast.makeText(this, "Transaction Deleted", Toast.LENGTH_SHORT).show() // Show a toast message.
                }
                .show()
        }

        binding.closeBtnEdit.setOnClickListener { // Set a click listener for the close button.
            finish() // Close the current activity.
        }
    }
}

/*
Explanation of source of methods and their functionality:

1. onCreate(savedInstanceState: Bundle?): This method is called when the activity is first created. It is where you should do all of your normal static set up to create views, bind data to lists, etc. This method also provides you with a Bundle containing the activity's previously frozen state, if there was one.

2. detailedTransaction(transactionId: Int, label: String, transactionDate: Date, description: String, amount: Double): This custom method populates the UI components with the details of the transaction. It sets the appropriate values for label, amount, description, and date, and configures the click listeners for various UI components.

3. binding.expenseEdit.setOnClickListener: This method sets a click listener for the expense radio button. It updates the label input field and adapter if the current label is not in the list of expense labels.

4. binding.incomeEdit.setOnClickListener: This method sets a click listener for the income radio button. It updates the label input field and adapter if the current label is not in the list of income labels.

5. binding.detailRootView.setOnClickListener: This method sets a click listener for the root view. It hides the keyboard and clears the focus from any input fields.

6. binding.calendarDateEdit.setOnClickListener: This method sets a click listener for the date input field. It shows a date picker dialog to allow the user to select a date.

7. binding.labelInputEdit.addTextChangedListener: This method adds a text changed listener to the label input field. It clears any error messages when the text is not empty.

8. binding.amountInputEdit.addTextChangedListener: This method adds a text changed listener to the amount input field. It clears any error messages when the text is not empty.

9. binding.updateBtnEdit.setOnClickListener: This method sets a click listener for the update button. It validates the input fields and updates the transaction if the inputs are valid. It then starts the MainActivity and shows a toast message.

10. binding.deleteBtnEdit.setOnClickListener: This method sets a click listener for the delete button. It shows a confirmation dialog before deleting the transaction. If confirmed, it deletes the transaction, starts the MainActivity, and shows a toast message.

11. binding.closeBtnEdit.setOnClickListener: This method sets a click listener for the close button. It finishes the current activity.
*/
