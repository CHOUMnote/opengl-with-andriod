package com.example.prac01x

import android.opengl.GLES30
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder

const val COORDS_PER_VERTEX = 3

class MyTriangle{
    private val triangleCoords = floatArrayOf(
        0.0f, 0.0f, 0.0f,
        0.5f, 0.5f, 0.0f,
        -0.5f, 0.5f, 0.0f,
        0.0f, 0.0f, 0.0f,
        0.5f, -0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f
    )

    private val triangleColors = floatArrayOf(
        1.0f, 1.0f, 1.0f,
        0.0f, 1.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 0.0f,
        1.0f, 0.0f, 0.0f,
    )
    private val color = floatArrayOf(1.0f, 1.0f, 0.0f, 1.0f)

    private val vertexBuffer =
        ByteBuffer.allocateDirect(triangleCoords.size * 4).run(){
            order(ByteOrder.nativeOrder())

            asFloatBuffer().apply {
                put(triangleCoords)
                position(0)
            }
        }

    private val colorBuffer =
        ByteBuffer.allocateDirect(triangleColors.size * 4).run{
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply{
                put(triangleColors)
                position(0)
            }
        }

    private val vertexShaderCode = """
        #version 300 es
        layout(location=0) in vec4 vPosition;
        //layout(location=1) in vec4 vColor;
        //out vec4 fColor;
        void main(){
            gl_PointSize = 10.0;
            gl_Position = vPosition;
            //fColor = vColor;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        #version 300 es
        precision mediump float;
        uniform vec4 vColor;
        //in vec4 fColor;
        out vec4 fragColor;
        void main(){
            fragColor = vColor;
        }
    """.trimIndent()

    private var mProgram = -1

    //private var mPositionHandle = -1
    private var mColorHandle = -1

    private val vertexCount:Int = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride:Int = COORDS_PER_VERTEX * 4

    init{
        val vertexShader:Int = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader:Int = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mProgram = GLES30.glCreateProgram().also{
            GLES30.glAttachShader(it, vertexShader)
            GLES30.glAttachShader(it, fragmentShader)
            GLES30.glLinkProgram(it)
        }

        GLES30.glUseProgram(mProgram)

        //mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition").also{
            GLES30.glEnableVertexAttribArray(0)
            GLES30.glVertexAttribPointer(
                0,
                COORDS_PER_VERTEX,
                GLES30.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )
        //}

        mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor").also{
            GLES30.glUniform4fv(it, 1, color, 0)
        }

//        GLES30.glEnableVertexAttribArray(1)
//        GLES30.glVertexAttribPointer(
//            1,
//            COORDS_PER_VERTEX,
//            GLES30.GL_FLOAT,
//            false,
//            vertexStride,
//            colorBuffer
//        )
    }

    private fun loadShader(type:Int, shaderCode:String):Int{
        var temp = GLES30.glCreateShader(type).also{
            shader ->
            GLES30.glShaderSource(shader, shaderCode)
            GLES30.glCompileShader(shader)

            val compiled = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer()
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled)
            if(compiled.get(0) == 0){
                GLES30.glGetShaderiv(shader, GLES30.GL_INFO_LOG_LENGTH, compiled)
                if(compiled.get(0)>1){
                    Log.e("Shader", "$type shader compile error")
                }
                GLES30.glDeleteShader(shader)
                Log.e("Shader", "$type shader compile error")
            }
        }

        return temp
    }

    fun draw(){
        GLES30.glUseProgram(mProgram)
        GLES30.glLineWidth(10f)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount)
    }
}