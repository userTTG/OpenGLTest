package com.zhh.opengltest.shape;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @ClassName TriangleRender
 * @Description TODO
 * @Author zhangh-be
 * @Date 2022/8/26 11:18
 * @Version 1.0
 */
public class CubeRender implements GLSurfaceView.Renderer {

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

    float cubeCoords[] = {
            -0.5f, 0.5f, 0.5f,//前左上0
            -0.5f, -0.5f, 0.5f,//前左下1
            0.5f, -0.5f, 0.5f,//前右下2
            0.5f, 0.5f, 0.5f,//前右上3
            -0.5f, 0.5f, -0.5f,//后左上4
            -0.5f, -0.5f, -0.5f,//后左下5
            0.5f, -0.5f, -0.5f,//后右下6
            0.5f, 0.5f, -0.5f,//后右上7
    };

    /**
     * 顶点索引
     */
    private short[] indices = {
            0,1,2,0,2,3,//前面
            0,4,5,0,5,1,//左面
            0,4,7,0,7,3,//上面
            3,7,6,3,6,2,//右面
            1,2,6,1,6,5,//下面
            4,5,6,4,6,7,//后面
    };



    float color[] = {
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
    };

    FloatBuffer vertexBuffer,colorBuffer;
    //顶点索引缓存
    private ShortBuffer indicesBuffer;

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
        ByteBuffer bb = ByteBuffer.allocateDirect(cubeCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(cubeCoords);
        vertexBuffer.position(0);

        ByteBuffer dd = ByteBuffer.allocateDirect(color.length * 4);
        dd.order(ByteOrder.nativeOrder());
        colorBuffer  = dd.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);

        //顶点索引相关
        indicesBuffer = ByteBuffer.allocateDirect(indices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        indicesBuffer.put(indices);
        indicesBuffer.position(0);

        //加载着色器
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        //创建opengles程序，并转接着色器
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        //计算宽高比
        float ratio=(float)width/height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 5.0f, 5.0f, 10.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);

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
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");

        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle,4,GLES20.GL_FLOAT,false,0,colorBuffer);

//        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
//        GLES20.glDisableVertexAttribArray(mColorHandle);
        GLES20.glDrawElements(GL10.GL_TRIANGLES,indices.length,GL10.GL_UNSIGNED_SHORT,indicesBuffer);
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
