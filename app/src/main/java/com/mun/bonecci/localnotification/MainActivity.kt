package com.mun.bonecci.localnotification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.mun.bonecci.localnotification.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var selectedItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(initViewBinding(layoutInflater))

        val items =
            listOf("Select an option (default: 1 minute)", "1 minute", "2 minutes", "3 minutes")
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

        binding.launchButton.setOnClickListener {
            val customTitle = binding.titleEditText.text.toString()
            val customMessage = binding.messageEditText.text.toString()
            val delayMinutes = if (selectedItem == 0) 1L else selectedItem.toLong()

            scheduleNotification(delayMinutes, customTitle, customMessage)
        }
    }

    private fun initViewBinding(inflater: LayoutInflater): View {
        binding = ActivityMainBinding.inflate(inflater)
        return binding.root
    }


    private fun scheduleNotification(delayMinutes: Long, title: String, message: String) {
        val inputData = Data.Builder()
            .putString("title", title)
            .putString("message", message)
            .build()

        val workRequest = PeriodicWorkRequest.Builder(
            NotificationWorker::class.java,
            delayMinutes, TimeUnit.MINUTES
        ).setInputData(inputData)
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                NOTIFICATION_WORK,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
    }

    companion object {
        const val NOTIFICATION_WORK = "notification_work"
    }
}