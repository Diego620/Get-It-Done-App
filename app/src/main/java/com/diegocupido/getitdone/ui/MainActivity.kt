package com.diegocupido.getitdone.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.diegocupido.getitdone.R
import com.diegocupido.getitdone.data.GetItDoneDatabase
import com.diegocupido.getitdone.data.Task
import com.diegocupido.getitdone.data.TaskDao

import com.diegocupido.getitdone.databinding.ActivityMainBinding
import com.diegocupido.getitdone.databinding.DialogAddTaskBinding
import com.diegocupido.getitdone.ui.tasks.TaskFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: GetItDoneDatabase
    private val taskDao: TaskDao by lazy {database.getTaskDao()}
    private val tasksFragment: TaskFragment = TaskFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pager.adapter = PagerAdapter(this)
        TabLayoutMediator(binding.tabs, binding.pager) { tab, position ->
            tab.text = "Tasks"
        }.attach()

        binding.fab.setOnClickListener { showAddTaskDialog() }

        database =  GetItDoneDatabase.getDatabase(this)
        
    }

    private fun showAddTaskDialog() {
        val dialogBinding = DialogAddTaskBinding.inflate(layoutInflater)

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.buttonShowDetails.setOnClickListener{
            dialogBinding.editTextTaskDetails.visibility =
                if (dialogBinding.editTextTaskDetails.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        dialogBinding.buttonSave.setOnClickListener{
            val task = Task(
                title = dialogBinding.editTextTaskTitle.text.toString(),
                description = dialogBinding.editTextTaskDetails.text.toString()
            )
            thread {
                taskDao.createTask(task)
            }
            dialog.dismiss()
            tasksFragment.fetchAllTasks()
        }
        dialog.show()
    }

    inner class PagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount() = 1

        override fun createFragment(position: Int): Fragment {
            return tasksFragment
        }


    }

}