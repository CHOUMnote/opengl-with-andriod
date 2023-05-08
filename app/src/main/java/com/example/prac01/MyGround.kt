package com.example.prac01

import android.opengl.GLES30
import android.util.Log
import com.example.prac01x.COORDS_PER_VERTEX
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MyGround {
    private val vertexCoords = floatArrayOf(
        -10f, -1f, -10f,
        -10f, -1f, 10f,
        10f, -1f, 10f,
        -10f, -1f, -10f,
        10f, -1f, 10f,
        10f, -1f, -10f,
    )

    private val color = floatArrayOf(0.8f, 0.8f, 0.8f, 1f)

    private val lineCoords = FloatArray(252).apply{
        var index = 0
        for(x in -10..10){
            this[index++] = x.toFloat()
            this[index++] = -1f
            this[index++] = -10f
            this[index++] = x.toFloat()
            this[index++] = -1f
            this[index++] = 10f
        }

        for(z in -10..10){
            this[index++] = -10f
            this[index++] = -1f
            this[index++] = z.toFloat()
            this[index++] = 10f
            this[index++] = -1f
            this[index++] = z.toFloat()
        }
    }
    private val vertexBuffer =
        ByteBuffer.allocateDirect((vertexCoords.size + lineCoords.size) * 4).run(){
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertexCoords)
                put(lineCoords)
                position(0)
            }
        }

    private val vertexShaderCode = """
        #version 300 es
        uniform mat4 uMVPMatrix;
        layout(location=8) in vec4 vPosition;
        void main(){
            gl_Position = uMVPMatrix * vPosition;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        #version 300 es
        precision mediump float;
        uniform vec4 fColor;
        out vec4 fragColor;
        void main(){
            fragColor = fColor;
        }
    """.trimIndent()

    private var mProgram = -1

    private var mvpMatrixHandle = -1

    private var mColorHandle = -1

    private val lineCount = lineCoords.size / COORDS_PER_VERTEX
    private val vertexCount:Int = vertexCoords.size / COORDS_PER_VERTEX
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

        GLES30.glEnableVertexAttribArray(8)
        GLES30.glVertexAttribPointer(
            8,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )

        mColorHandle = GLES30.glGetUniformLocation(mProgram, "fColor").also{
            GLES30.glUniform4fv(it, 1, color, 0)
        }

        mvpMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")
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

        GLES30.glUniform4fv(mColorHandle, 1, color, 0)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount)

        GLES30.glLineWidth(5.0f)
        GLES30.glUniform4f(mColorHandle, 0f,0f,0f,1f)
        GLES30.glDrawArrays(GLES30.GL_LINES, vertexCount, lineCount)
    }
}