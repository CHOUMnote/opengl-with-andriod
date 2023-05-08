package com.example.prac01

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLUtils
import android.util.Log
import com.example.prac01x.COORDS_PER_VERTEX
import java.io.BufferedInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MyTexGround(val myContext : Context) {
    private val vertexCoords = floatArrayOf(
        -1f, -1f, -1f,
        -1f, -1f, 1f,
        1f, -1f, 1f,
        -1f, -1f, -1f,
        1f, -1f, 1f,
        1f, -1f, -1f,
    )

    private val vertexUVs = floatArrayOf(
        0f, 0f,
        0f, 2f,
        2f, 2f,
        0f, 0f,
        2f, 2f,
        2f, 0f
    )

    private val vertexBuffer =
        ByteBuffer.allocateDirect(vertexCoords.size * 4).run(){
            order(ByteOrder.nativeOrder())

            asFloatBuffer().apply {
                put(vertexCoords)
                position(0)
            }
        }

    private val uvBuffer =
        ByteBuffer.allocateDirect(vertexUVs.size * 4).run(){
            order(ByteOrder.nativeOrder())

            asFloatBuffer().apply {
                put(vertexUVs)
                position(0)
            }
        }

    private val vertexShaderCode = """
        #version 300 es
        uniform mat4 uMVMatrix;
        layout(location=11) in vec4 vPosition;
        layout(location=12) in vec2 vTexCoord;
        out vec2 fTexCoord;
        void main(){
            gl_Position = uMVMatrix * vPosition;
            fTexCoord = vTexCoord;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        #version 300 es
        precision mediump float;
        uniform sampler2D sTexture;
        in vec2 fTexCoord;
        out vec4 fragColor;
        void main(){
            fragColor = texture(sTexture, fTexCoord);
        }
    """.trimIndent()

    private var mProgram = -1

    private var mvpMatrixHandle = -1
    private var textureID = IntArray(1)

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

        GLES30.glEnableVertexAttribArray(11)
        GLES30.glVertexAttribPointer(
            11,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )

        GLES30.glEnableVertexAttribArray(12)
        GLES30.glVertexAttribPointer(
            12,
            2,
            GLES30.GL_FLOAT,
            false,
            0,
            uvBuffer
        )

        mvpMatrixHandle = GLES30.glGetUniformLocation(mProgram,"uMVMatrix")

        GLES30.glGenTextures(1, textureID, 0)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,textureID[0])
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT)
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, loadBitmap("logo.bmp"), 0)
    }

    private fun loadBitmap(filename:String):Bitmap{
        val manager = myContext.assets
        val inputStream = BufferedInputStream(manager.open(filename))
        val bitmap : Bitmap? = BitmapFactory.decodeStream(inputStream)
        return bitmap!!
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
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureID[0])

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount)
    }
}