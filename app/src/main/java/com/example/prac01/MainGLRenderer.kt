package com.example.prac01

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import com.example.prac01x.MyTriangle
import com.example.prac01x.MySquare
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.Matrix
import android.os.SystemClock
import android.util.Log
import androidx.core.graphics.scaleMatrix
import kotlin.math.cos
import kotlin.math.sin

class MainGLRenderer(val context: Context) : GLSurfaceView.Renderer{
    private lateinit var mTriangle: MyTriangle
    private lateinit var mSquare: MySquare
    private lateinit var mCube: MyColorCube
    private lateinit var mHex: MyHexapyramid
    private lateinit var mGround: MyGround
    private lateinit var mTexGround: MyTexGround

    private var projectionMatrix = FloatArray(16)
    private var mvpMatrix = FloatArray(16)
    private var viewMatrix = FloatArray(16)
    private var vpMatrix = FloatArray(16);
    private var modelMatrix = floatArrayOf(
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f,
    )

    private var startTime = SystemClock.uptimeMillis()
    private var rotAngles = floatArrayOf(0f, 0f, 0f)
    private var aspectRatio = 1.0f

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GLES30.glClearColor(0.2f,0.2f,0.2f,1f)

        GLES30.glEnable(GLES30.GL_DEPTH_TEST)   //z-buffer
        GLES30.glEnable(GLES30.GL_POLYGON_OFFSET_FILL)
        GLES30.glPolygonOffset(1f,1f)

        Matrix.setIdentityM(mvpMatrix,0)
        Matrix.setIdentityM(viewMatrix,0)
        Matrix.setIdentityM(projectionMatrix,0)
        Matrix.setIdentityM(vpMatrix,0)

        when(drawMode){
            1 -> mTriangle = MyTriangle()
            2 -> {
                mSquare = MySquare()
//                mTriangle = MyTriangle()
            }
            3,5 -> mCube = MyColorCube()
            4 -> mHex = MyHexapyramid()
            6 -> {
                mHex = MyHexapyramid()
                mCube = MyColorCube()
            }
            7 -> {
                mCube = MyColorCube()
                mGround = MyGround()
            }
            8 -> {
                mTexGround = MyTexGround(context)
            }
        }
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)

        aspectRatio = width.toFloat() / height.toFloat()

        when(drawMode){
            3,4,7,8 -> {
                val ratio = width.toFloat() / height.toFloat()
                Matrix.perspectiveM(projectionMatrix, 0, 90f, ratio, 0.5f, 100f)
            }
            5,6-> {
                if(width > height){
                    val ratio = width.toFloat() / height.toFloat()
                    Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 0f, 1000f,)
                }else{
                    val ratio = height.toFloat() / width.toFloat()
                    Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -ratio, ratio, 0f, 1000f,)
                }
            }
        }

        when (drawMode){
            3,5,8 ->{
                Matrix.setLookAtM(viewMatrix,0, 1f,1f,1f, 0f,0f,0f, 0f, 1f, 0f)
            }
            4,7 ->{
                Matrix.setLookAtM(viewMatrix,0, 2f,2f,2f, 0f,0f,0f, 0f, 1f, 0f)
            }
            6 ->{
                Matrix.setLookAtM(viewMatrix,0, 0f,0f,2f, 0f,0f,0f, 0f, 1f, 0f)
            }
        }

        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }

    override fun onDrawFrame(p0: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

        when(drawMode){
            5 ->{   //애니메이션 뷰
                Matrix.setIdentityM(modelMatrix,0 )

                //R
                val endTime = SystemClock.uptimeMillis()
                val angle = 0.001f * (endTime - startTime).toFloat()    //deltatime
                startTime = endTime
                rotAngles[rotateAxis] += angle;

                //x 회전 행렬
                var sinAngle = sin(rotAngles[0])
                var cosAngle = cos(rotAngles[0])
                val rotXMatrix = floatArrayOf(
                    1f, 0f, 0f, 0f,
                    0f, cosAngle, sinAngle, 0f,
                    0f, -sinAngle, cosAngle, 0f,
                    0f, 0f, 0f, 1f,
                )

                sinAngle = sin(rotAngles[1])
                cosAngle = cos(rotAngles[1])
                val rotYMatrix = floatArrayOf(
                    cosAngle, 0f, -sinAngle, 0f,
                    0f, 1f, 0f, 0f,
                    sinAngle, 0f, cosAngle, 0f,
                    0f, 0f, 0f, 1f,
                )

                sinAngle = sin(rotAngles[2])
                cosAngle = cos(rotAngles[2])
                val rotZMatrix = floatArrayOf(
                    cosAngle, sinAngle, 0f, 0f,
                    -sinAngle, cosAngle, 0f, 0f,
                    0f, 0f, 1f, 0f,
                    0f, 0f, 0f, 1f,
                )
                Matrix.multiplyMM(modelMatrix, 0, rotYMatrix, 0, rotXMatrix, 0)
                Matrix.multiplyMM(modelMatrix, 0, rotZMatrix, 0, modelMatrix, 0)

                //S
                var scaleMatrix = floatArrayOf(
                    scaleFactor, 0f, 0f, 0f,
                    0f, scaleFactor, 0f, 0f,
                    0f, 0f, scaleFactor, 0f,
                    0f, 0f, 0f, 1f,
                )
                Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, scaleMatrix, 0)

                //T
                val translateMatrix = floatArrayOf(
                    1f, 0f, 0f, 0f,
                    0f, 1f, 0f, 0f,
                    0f, 0f, 1f, 0f,
                    displce[0], displce[1], displce[2], 1f,
                )

                Matrix.multiplyMM(modelMatrix,0,translateMatrix,0,modelMatrix,0)
            }
            6 ->{
                Matrix.setIdentityM(modelMatrix,0)

                val endTime = SystemClock.uptimeMillis()
                val angle = 0.1 * (endTime-startTime).toInt()
                startTime = endTime
                if(isRotating)
                    rotAngles[rotateAxis] += angle.toFloat();

                //R
                Matrix.setRotateM(modelMatrix, 0, rotAngles[0], 1f,0f,0f)   //x
                val tempMatrix = floatArrayOf(1f,0f,0f,0f,0f,1f,0f,0f,0f,0f,1f,0f,0f,0f,0f,1f)
                Matrix.setRotateM(tempMatrix,0, rotAngles[1],0f,1f, 1f) //y
                Matrix.multiplyMM(modelMatrix,0, tempMatrix, 0, modelMatrix, 0) //xy
                Matrix.setRotateM(tempMatrix,0, rotAngles[2],0f,0f,1f) //z
                Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0)    //xyz

                //S
                Matrix.scaleM(tempMatrix,0, 0.5f,0.5f,0.5f);
                Matrix.multiplyMM(modelMatrix,0, modelMatrix,0, tempMatrix,0)

                //T
//                Matrix.setIdentityM(tempMatrix,0)
//                Matrix.translateM(tempMatrix, 0, 0f, -0.5f, 0f)
//                Matrix.multiplyMM(modelMatrix,0, tempMatrix,0, modelMatrix,0)
            }
        }

        Matrix.multiplyMM(mvpMatrix,0,vpMatrix,0,modelMatrix,0) //RS

        //T
        when(drawMode){
            1 -> mTriangle.draw()
            2 -> mSquare.draw(vpMatrix)
            3 -> mCube.draw(vpMatrix)
            4 -> mHex.draw(vpMatrix)
            5 -> mCube.draw(mvpMatrix)
            6 -> {
                val rotMatrix = modelMatrix.copyOf(16)
                val tempMatrix = floatArrayOf(1f,0f,0f,0f,0f,1f,0f,0f,0f,0f,1f,0f,0f,0f,0f,1f)

                Matrix.translateM(tempMatrix, 0, -0.5f, -0.5f, 0f)
                Matrix.multiplyMM(modelMatrix,0, tempMatrix,0, modelMatrix,0)
                Matrix.multiplyMM(mvpMatrix,0, vpMatrix,0, modelMatrix,0)
                mCube.draw(mvpMatrix)


                Matrix.translateM(tempMatrix, 0, 1.5f, 1.5f, 0f)
                Matrix.multiplyMM(modelMatrix,0, tempMatrix,0, modelMatrix,0)
                Matrix.multiplyMM(mvpMatrix,0, vpMatrix,0, modelMatrix,0)
                mHex.draw(mvpMatrix)


                Matrix.transposeM(modelMatrix, 0, rotMatrix, 0)
                Matrix.setIdentityM(tempMatrix,0)

                Matrix.translateM(tempMatrix, 0, -0.5f, 0.5f, 0f)
                Matrix.multiplyMM(modelMatrix,0, tempMatrix,0, modelMatrix,0)
                Matrix.multiplyMM(mvpMatrix,0, vpMatrix,0, modelMatrix,0)
                mHex.draw(mvpMatrix)

                Matrix.translateM(tempMatrix, 0, 1.5f, -1.5f, 0f)
                Matrix.multiplyMM(modelMatrix,0, tempMatrix,0, modelMatrix,0)
                Matrix.multiplyMM(mvpMatrix,0, vpMatrix,0, modelMatrix,0)
                mCube.draw(mvpMatrix)

                mCube.draw(mvpMatrix)
            }
            7 ->{
                if(viewMode == 0){
                    Matrix.perspectiveM(projectionMatrix,0, 90f, aspectRatio, 0.001f, 100f)
                }else{
                    if(aspectRatio >= 1f){
                        Matrix.orthoM(projectionMatrix,0, -aspectRatio, aspectRatio, -1f, 1f, 0f, 100f)
                    }else{
                        val ratio = 1f/aspectRatio
                        Matrix.orthoM(projectionMatrix,0, -1f, 1f, -ratio, ratio, 0f, 100f)
                    }
                }

                eyeAt[0] = eyePos[0] + cameraVec[0]
                eyeAt[1] = eyePos[1] + cameraVec[1]
                eyeAt[2] = eyePos[2] + cameraVec[2]
                Matrix.setLookAtM(viewMatrix,0, eyePos[0], eyePos[1], eyePos[2], eyeAt[0],eyeAt[1],eyeAt[2],0f,1f,0f)
                Matrix.multiplyMM(vpMatrix,0,projectionMatrix,0,viewMatrix,0)

                mCube.draw(vpMatrix)
                mGround.draw(vpMatrix)
            }
            8 -> {
                mTexGround.draw(vpMatrix)
            }
        }
    }
}