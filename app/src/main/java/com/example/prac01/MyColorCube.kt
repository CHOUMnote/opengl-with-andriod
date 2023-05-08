package com.example.prac01

import android.opengl.GLES30
import android.util.Log
import com.example.prac01x.COORDS_PER_VERTEX
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MyColorCube {
    private val vertexCoords = floatArrayOf(
        -0.5f, 0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, 0.5f, -0.5f,
        -0.5f, 0.5f, 0.5f,
        -0.5f, -0.5f, 0.5f,
        0.5f, -0.5f, 0.5f,
        0.5f, 0.5f, 0.5f,
    )

    private val vertexColors = floatArrayOf(
        0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 0.0f,
        1.0f, 0.0f, 0.0f,
        1.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        1.0f, 0.0f, 1.0f,
        1.0f, 1.0f, 1.0f,
    )

    private val drawOrder = shortArrayOf(
        0,3,2,0,2,1,
        2,3,7,2,7,6,
        1,2,6,1,6,5,
        4,0,1,4,1,5,
        3,0,4,3,4,7,
        5,6,7,5,7,4
    )

    private val vertexBuffer =
        ByteBuffer.allocateDirect(vertexCoords.size * 4).run(){
            order(ByteOrder.nativeOrder())

            asFloatBuffer().apply {
                put(vertexCoords)
                position(0)
            }
        }

    private val colorBuffer =
        ByteBuffer.allocateDirect(vertexColors.size * 4).run{
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply{
                put(vertexColors)
                position(0)
            }
        }

    private val indexBuffer =
        ByteBuffer.allocateDirect(drawOrder.size * 2).run(){
            order(ByteOrder.nativeOrder())

            asShortBuffer().apply {
                put(drawOrder)
                position(0)
            }
        }

    private val vertexShaderCode = """
        #version 300 es
        uniform mat4 uMVMatrix;
        layout(location=4) in vec4 vPosition;
        layout(location=5) in vec4 vColor;
        out vec4 fColor;
        void main(){
            gl_Position = uMVMatrix * vPosition;
            fColor = vColor;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        #version 300 es
        precision mediump float;
        in vec4 fColor;
        out vec4 fragColor;
        void main(){
            fragColor = fColor;
        }
    """.trimIndent()

    private var mProgram = -1
    private var mvpMatrixHandle = -1

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

        GLES30.glEnableVertexAttribArray(4)

        GLES30.glVertexAttribPointer(
            4,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )

        GLES30.glEnableVertexAttribArray(5)

        GLES30.glVertexAttribPointer(
            5,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            colorBuffer
        )

        mvpMatrixHandle = GLES30.glGetUniformLocation(mProgram,"uMVMatrix")
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

    fun draw(mvpMatrix:FloatArray){
        GLES30.glUseProgram(mProgram)

        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, drawOrder.size, GLES30.GL_UNSIGNED_SHORT, indexBuffer)
    }
}