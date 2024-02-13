package com.mun.bonecci.localnotification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.mun.bonecci.localnotification.Constants.NOTIFICATION_MESSAGE_PARAM
import com.mun.bonecci.localnotification.Constants.NOTIFICATION_TITLE_PARAM
import com.mun.bonecci.localnotification.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var selectedItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(initViewBinding(layoutInflater))

        setupSpinner()
        setupLaunchButton()
    }

    /**
     * Initializes the view binding for the activity.
     *
     * @param inflater The layout inflater used to inflate the binding.
     * @return The root view of the inflated binding.
     */
    private fun initViewBinding(inflater: LayoutInflater): View {
        binding = ActivityMainBinding.inflate(inflater)
        return binding.root
    }

    /**
     * Sets up the spinner with its adapter and item selection listener.
     * The spinner items are populated with predefined options.
     */
    private fun setupSpinner() {
        val items = listOf(
            getString(R.string.select_an_option),
            getString(R.string.one_minute),
            getString(R.string.two_minutes),
            getString(R.string.three_minutes)
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedItem = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case when nothing is selected
            }
        }
    }

    /**
     * Sets up the click listener for the launch button.
     * When clicked, it retrieves the custom title, message, and selected delay from the UI elements
     * and schedules a notification based on the provided parameters.
     */
    private fun setupLaunchButton() {
        binding.launchButton.setOnClickListener {
            val customTitle = binding.titleEditText.text.toString()
            val customMessage = binding.messageEditText.text.toString()
            val delayMinutes = if (selectedItem == 0) 1L else selectedItem.toLong()

            scheduleNotification(delayMinutes, customTitle, customMessage)
        }
    }

    /**
     * Schedules a notification to be displayed after a specified delay.
     *
     * @param delayMinutes The delay in minutes before the notification is triggered.
     * @param title The title of the notification.
     * @param message The message content of the notification.
     */
    private fun scheduleNotification(delayMinutes: Long, title: String, message: String) {
        // Prepare the data to be passed to the notification worker
        val inputData = Data.Builder()
            .putString(NOTIFICATION_TITLE_PARAM, title)
            .putString(NOTIFICATION_MESSAGE_PARAM, message)
            .build()

        // Build a one-time work request for the notification worker
        val workRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .setInputData(inputData)
            .build()

        // Enqueue the work request with WorkManager to schedule the notification
        WorkManager.getInstance(this).enqueue(workRequest)
    }


}