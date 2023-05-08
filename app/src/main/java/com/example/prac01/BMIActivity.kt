package com.example.prac01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.util.Log
import com.example.prac01.databinding.LayoutViewBindingBinding

class BMIActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = LayoutViewBindingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bmiButton.setOnClickListener{
            val height = binding.tallField.text.toString().toDouble()
            val weight = binding.weightField.text.toString().toDouble()
            val bmi = weight / Math.pow(height/100.0, 2.0)
            Log.d("test", "value is \n$height \n$weight \n$bmi")
            binding.result.text = "키: ${binding.tallField.text}, 체중 ${binding.weightField.text}, BMI: ${bmi}"
        }
    }
}