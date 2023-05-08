package com.example.prac01

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.prac01.databinding.ActivityRotateCubeBinding

var rotateAxis = 0;
var scaleFactor = 1f;
var displce = floatArrayOf(0f, 0f, 0f)

class RotateCube : AppCompatActivity() {
    val binding : ActivityRotateCubeBinding by lazy{
        ActivityRotateCubeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initGLSurfaceView()
        setContentView(binding.root)

        binding.rotateX.setOnClickListener{
            rotateAxis = 0
        }
        binding.rotateY.setOnClickListener{
            rotateAxis = 1
        }
        binding.rotateZ.setOnClickListener{
            rotateAxis = 2
        }

        binding.scaleUp.setOnClickListener{
            scaleFactor*=1.1f
        }
        binding.scaledown.setOnClickListener{
            scaleFactor*=0.9f
        }

        binding.posX.setOnClickListener{
            displce[0] += 0.1f;
        }
        binding.posY.setOnClickListener{
            displce[1] += 0.1f;
        }
        binding.posZ.setOnClickListener{
            displce[2] += 0.1f;
        }
        binding.negX.setOnClickListener{
            displce[0] -= 0.1f
        }
        binding.negY.setOnClickListener{
            displce[1] -= 0.1f
        }
        binding.negZ.setOnClickListener{
            displce[2] -= 0.1f
        }
    }

    fun initGLSurfaceView(){
        binding.surfaceView.setEGLContextClientVersion(3)
        binding.surfaceView.setRenderer(MainGLRenderer(this))
        binding.surfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }
}