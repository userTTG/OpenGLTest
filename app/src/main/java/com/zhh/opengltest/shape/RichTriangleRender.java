package com.zhh.opengltest.shape;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @ClassName TriangleRender
 * @Description TODO
 * @Author zhangh-be
 * @Date 2022/8/26 11:18
 * @Version 1.0
 */
public class RichTriangleRender implements GLSurfaceView.Renderer {

    private static final String TAG = "TriangleRender";

    private String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;" +
                    "varying vec4 vColor;" +
                    "attribute vec4 aColor;" +
                    "void main(){" +
                    "gl_Position = vMatrix*vPosition;" +
                    "vColor = aColor;" +
                    "}";

    private String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 vColor;" +
                    "void main(){" +
                    "gl_FragColor = vColor;" +
                    "}";

    float triagnleCoords[] = {
            0.5f, 0.5f, 0f,
            -0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f
    };

    float color[] = {
            0.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
    };

    FloatBuffer vertexBuffer,colorBuffer;

    int mProgram;
    int mMatrixHandle;
    int mPositionHandle;
    int mColorHandle;

    float[] mProjectMatrix = new float[16];
    float[] mViewMatrix = new float[16];
    float[] mMVPMatrix = new float[16];

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //将背景设置为灰色
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        //将顶占坐标数组转换为ByteBuffer
        ByteBuffer bb = ByteBuffer.allocateDirect(triagnleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(triagnleCoords);
        vertexBuffer.position(0);

        ByteBuffer dd = ByteBuffer.allocateDirect(color.length * 4);
        dd.order(ByteOrder.nativeOrder());
        colorBuffer  = dd.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);

        //加载着色器
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        //创建opengles程序，并转接着色器
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        //正交投影方式
//        final float aspectRatio = width > height ?
//                (float) width / (float) height :
//                (float) height / (float) width;
//        if (width > height) {
//            //横屏
//            Matrix.orthoM(mMVPMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
//        } else {
//            //竖屏
//            Matrix.orthoM(mMVPMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
//        }

        //透视投影方式
        float ratio = (float)width/height;
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7, 0, 0, 0, 0, 1, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //清除颜色缓冲和深度缓冲
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT| GLES20.GL_DEPTH_BUFFER_BIT);
        //使用opengl程序
        GLES20.glUseProgram(mProgram);

        mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //3个顶点，4（点的维数）*3
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 4 * 3, vertexBuffer);

        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");

        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle,4,GLES20.GL_FLOAT,false,0,colorBuffer);

//        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
//        GLES20.glDisableVertexAttribArray(mColorHandle);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    public int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        checkGlError("glCreateShader type = " + shaderType);
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e(TAG, "Could not compile shader " + shaderType + ":");
            Log.e(TAG, " " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }
        return shader;
    }

    public void checkGlError(String info) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            String errorMsg = info + ": glError" + error;
            Log.e(TAG, errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }
}
