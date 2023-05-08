package com.example.prac01x

import android.opengl.GLES30
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MySquare {
    private val squareCoords = floatArrayOf(
        -0.75f, 0.75f, 0.0f,
        -0.75f, -0.75f, 0.0f,
        0.75f, -0.75f, 0.0f,
        0.75f, 0.75f, 0.0f,
    )

    private val color = floatArrayOf(
        1f,0f,0f,
        1f,1f,0f,
        0f,1f,0f,
        0f,0f,1f,
    )

    private val drawOrder = shortArrayOf(0,1,2,0,2,3)

    private val vertexBuffer =
        ByteBuffer.allocateDirect(squareCoords.size * 4).run(){
            order(ByteOrder.nativeOrder())

            asFloatBuffer().apply {
                put(squareCoords)
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

    private val colorBuffer =
        ByteBuffer.allocateDirect(color.size * 4).run(){
            order(ByteOrder.nativeOrder())

            asFloatBuffer().apply {
                put(color)
                position(0)
            }
        }

    private val vertexShaderCode = """
        #version 300 es
        uniform mat4 uMVMatrix;
        layout(location=2) in vec4 vPosition;
        layout(location=3) in vec4 vColor;
        out vec4 fColor;
        void main(){
            gl_PointSize = 10.0;
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
    //private var mPositionHandle = -1
    //private var mColorHandle = -1

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
            GLES30.glEnableVertexAttribArray(2)

            GLES30.glVertexAttribPointer(
                2,
                COORDS_PER_VERTEX,
                GLES30.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )
        //}

//        mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor").also{
//            GLES30.glUniform4fv(it, 1, color, 0)
//        }

        GLES30.glEnableVertexAttribArray(3)

        GLES30.glVertexAttribPointer(
            3,
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