package com.example.quickexpensestracker.ui

import android.app.DatePickerDialog // Dialog for selecting a date.
import android.content.Context // Provides access to application-specific resources and classes.
import android.content.Intent // Used to start new activities.
import android.os.Bundle // Used to pass data between activities.
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
import java.text.SimpleDateFormat // Class for formatting and parsing dates in a locale-sensitive manner.
import java.util.* // Package for date and time classes.

class AddTransactionActivity : AppCompatActivity() { // AddTransactionActivity class which is a subclass of AppCompatActivity.

    private val vm: TransactionViewModel by viewModels { // Lazy initialization of TransactionViewModel using a factory.
        TransactionViewModelFactory(application)
    }
    private lateinit var arrayAdapter: ArrayAdapter<String> // Adapter for handling label input.
    private lateinit var binding: ActivityAddTransactionBinding // Binding object for activity_add_transaction layout.

    override fun onCreate(savedInstanceState: Bundle?) { // Called when the activity is starting.
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater) // Inflate the layout for this activity using view binding.
        setContentView(binding.root) // Set the activity content to the root view of the binding.
        title = "Add Expense" // Set the title of the activity.

        setupUI() // Call method to set up the user interface.
    }

    private fun setupUI() { // Method to set up the user interface.
        setupRootView() // Call method to set up the root view.
        setupDropdown() // Call method to set up the dropdown.
        setupDatePicker() // Call method to set up the date picker.
        setupButtons() // Call method to set up the buttons.
    }

    private fun setupRootView() { // Method to set up the root view.
        binding.addRootView.setOnClickListener { // Set a click listener to hide the keyboard when the root view is clicked.
            hideKeyboard(it)
        }
    }

    private fun hideKeyboard(view: android.view.View) { // Method to hide the keyboard.
        this.window.decorView.clearFocus() // Clear focus from the current view.
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager // Get the InputMethodManager service.
        imm.hideSoftInputFromWindow(view.windowToken, 0) // Hide the keyboard.
    }

    private fun setupDropdown() { // Method to set up the dropdown for selecting labels.
        val labelExpense = resources.getStringArray(R.array.labelExpense) // Get the expense labels from resources.
        val labelIncome = resources.getStringArray(R.array.labelIncome) // Get the income labels from resources.
        arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labelExpense) // Create an ArrayAdapter with the expense labels.
        binding.labelInput.setAdapter(arrayAdapter) // Set the adapter for the label input field.

        binding.expense.setOnClickListener { // Set a click listener for the expense radio button.
            updateDropdown(labelExpense)
        }

        binding.income.setOnClickListener { // Set a click listener for the income radio button.
            updateDropdown(labelIncome)
        }

        binding.labelInput.addTextChangedListener { // Add a text changed listener to the label input field.
            if (it!!.isNotEmpty())
                binding.labelLayout.error = null // Clear error message when text is not empty.
        }

        binding.amountInput.addTextChangedListener { // Add a text changed listener to the amount input field.
            if (it!!.isNotEmpty())
                binding.amountLayout.error = null // Clear error message when text is not empty.
        }
    }

    private fun updateDropdown(labels: Array<String>) { // Method to update the dropdown based on the selected transaction type.
        if (binding.labelInput.text.toString() !in labels.toList()) { // Clear the label input field if the current label is not in the list.
            binding.labelInput.setText("")
            arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labels) // Create a new ArrayAdapter with the selected labels.
            binding.labelInput.setAdapter(arrayAdapter) // Set the adapter for the label input field.
        }
    }

    private fun setupDatePicker() { // Method to set up the date picker.
        binding.calendarDate.setText(SimpleDateFormat("EEEE, dd MMM yyyy", Locale.US).format(System.currentTimeMillis())) // Set the initial date to the current date.
        var date = Date() // Initialize a date variable.

        val cal = Calendar.getInstance() // Get a Calendar instance.
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth -> // Date picker dialog listener to update the date field.
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val sdf = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.US) // Date format.
            binding.calendarDate.setText(sdf.format(cal.time)) // Update the date input field.
            date = cal.time // Update the date variable.
        }

        binding.calendarDate.setOnClickListener { // Set a click listener to show the date picker dialog.
            DatePickerDialog(
                this, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupButtons() { // Method to set up the buttons.
        binding.addTransactionBtn.setOnClickListener { // Set a click listener for the add transaction button.
            validateAndAddTransaction() // Call method to validate and add the transaction.
        }

        binding.closeBtn.setOnClickListener { // Set a click listener for the close button.
            finish() // Close the current activity.
        }
    }

    private fun validateAndAddTransaction() { // Method to validate the input fields and add the transaction.
        val label = binding.labelInput.text.toString() // Get the label from the input field.
        val description = binding.descriptionInput.text.toString() // Get the description from the input field.
        var amount = binding.amountInput.text.toString().toDoubleOrNull() // Get the amount from the input field.

        if (label.isEmpty()) { // Check if the label is empty.
            binding.labelLayout.error = "Please enter a valid label" // Show error message.
        } else if (amount == null) { // Check if the amount is null or invalid.
            binding.amountLayout.error = "Please enter a valid amount" // Show error message.
        } else { // If inputs are valid.
            if (binding.expense.isChecked) amount = -amount // Negate the amount if it is an expense.
            val transaction = Transaction(0, label, amount, description, Date()) // Create a new Transaction object.
            insert(transaction) // Call method to insert the transaction.
        }
    }

    private fun insert(transaction: Transaction) { // Method to insert the transaction.
        vm.insertTransaction(transaction) // Insert the transaction into the ViewModel.
        val intentMain = Intent(this, MainActivity::class.java) // Create an intent to start MainActivity.
        startActivity(intentMain) // Start the MainActivity.
        finish() // Close the current activity.
    }
}

/*
Explanation of source of methods and their functionality:

1. onCreate(savedInstanceState: Bundle?): This method is called when the activity is first created. It is where you should do all of your normal static set up to create views, bind data to lists, etc. This method also provides you with a Bundle containing the activity's previously frozen state, if there was one.

2. setupUI(): This custom method sets up the user interface by calling other setup methods for different parts of the UI.

3. setupRootView(): This custom method sets up the root view of the activity. It sets a click listener to hide the keyboard when the root view is clicked.

4. hideKeyboard(view: android.view.View): This custom method hides the keyboard by clearing the focus from the current view and hiding the soft input from the window.

5. setupDropdown(): This custom method sets up the dropdown menu for selecting transaction labels. It sets the initial labels and adds click listeners to switch between expense and income labels.

6. updateDropdown(labels: Array<String>): This custom method updates the dropdown menu based on the selected transaction type. It clears the label input field if the current label is not in the list and sets a new adapter with the selected labels.

7. setupDatePicker(): This custom method sets up the date picker dialog. It initializes the date input field with the current date and adds a click listener to show the date picker dialog when the date input field is clicked.

8. setupButtons(): This custom method sets up the buttons in the activity. It adds click listeners for the add transaction button and the close button.

9. validateAndAddTransaction(): This custom method validates the input fields for the transaction. It checks if the label and amount fields are not empty and creates a new transaction if the inputs are valid. It then calls the insert method to insert the transaction.

10. insert(transaction: Transaction): This custom method inserts the transaction into the ViewModel. It then starts the MainActivity and finishes the current activity.
*/
