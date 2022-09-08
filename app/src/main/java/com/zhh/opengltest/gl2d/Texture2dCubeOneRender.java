package com.zhh.opengltest.gl2d;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.blankj.utilcode.util.Utils;
import com.zhh.opengltest.R;
import com.zhh.opengltest.ResReadUtils;

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
public class Texture2dCubeOneRender implements GLSurfaceView.Renderer {

    private static final String TAG = "Texture2dRender";

    float[] VERTEX_POINT_FRONT = {
            -0.5f, 0.5f, 0.5f,//前左上0
            -0.5f, -0.5f, 0.5f,//前左下1
            0.5f, -0.5f, 0.5f,//前右下2
            0.5f, 0.5f, 0.5f,//前右上3
    };

    float[] VERTEX_POINT_BACK = {
            -0.5f, 0.5f, -0.5f,//后左上4
            -0.5f, -0.5f, -0.5f,//后左下5
            0.5f, -0.5f, -0.5f,//后右下6
            0.5f, 0.5f, -0.5f,//后右上7
    };

    float[] VERTEX_POINT_LEFT = {
            -0.5f, 0.5f, 0.5f,//前左上0
            -0.5f, -0.5f, 0.5f,//前左下1
            -0.5f, -0.5f, -0.5f,//后左下5
            -0.5f, 0.5f, -0.5f,//后左上4
    };

    float[] VERTEX_POINT_RIGHT = {
            0.5f, 0.5f, 0.5f,//前右上3
            0.5f, -0.5f, 0.5f,//前右下2
            0.5f, -0.5f, -0.5f,//后右下6
            0.5f, 0.5f, -0.5f,//后右上7
    };

    float[] VERTEX_POINT_TOP = {
            -0.5f, 0.5f, -0.5f,//后左上4
            -0.5f, 0.5f, 0.5f,//前左上0
            0.5f, 0.5f, 0.5f,//前右上3
            0.5f, 0.5f, -0.5f,//后右上7
    };

    float[] VERTEX_POINT_BOTTOM = {
            -0.5f, -0.5f, -0.5f,//后左下5
            -0.5f, -0.5f, 0.5f,//前左下1
            0.5f, -0.5f, 0.5f,//前右下2
            0.5f, -0.5f, -0.5f,//后右下6
    };

    private float[] VERTEX_TEX = {
            0,0,
            0,1f,
            1,1,
            1,0,
    };

    FloatBuffer vertexPointerBufferFront,vertexPointerBufferBack,vertexPointerBufferLeft,vertexPointerBufferRight,vertexPointerBufferTop,vertexPointerBufferBottom,vertexTexBuffer;
    //顶点索引缓存
    private ShortBuffer indicesBuffer;

    int[] textureId = new int[3];

    int mProgram;
    int mMatrixHandle;
    int mPositionHandle;
    int mPositionTexHandle;

    float[] mProjectMatrix = new float[16];
    float[] mViewMatrix = new float[16];
    float[] mMVPMatrix = new float[16];

    Bitmap mBitmap;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //将背景设置为灰色
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        //将顶占坐标数组转换为ByteBuffer
        ByteBuffer f = ByteBuffer.allocateDirect(VERTEX_POINT_FRONT.length * 4);
        f.order(ByteOrder.nativeOrder());
        vertexPointerBufferFront = f.asFloatBuffer();
        vertexPointerBufferFront.put(VERTEX_POINT_FRONT);
        vertexPointerBufferFront.position(0);

        ByteBuffer back = ByteBuffer.allocateDirect(VERTEX_POINT_BACK.length * 4);
        back.order(ByteOrder.nativeOrder());
        vertexPointerBufferBack = back.asFloatBuffer();
        vertexPointerBufferBack.put(VERTEX_POINT_BACK);
        vertexPointerBufferBack.position(0);

        ByteBuffer left = ByteBuffer.allocateDirect(VERTEX_POINT_LEFT.length * 4);
        left.order(ByteOrder.nativeOrder());
        vertexPointerBufferLeft = left.asFloatBuffer();
        vertexPointerBufferLeft.put(VERTEX_POINT_LEFT);
        vertexPointerBufferLeft.position(0);

        ByteBuffer right = ByteBuffer.allocateDirect(VERTEX_POINT_RIGHT.length * 4);
        right.order(ByteOrder.nativeOrder());
        vertexPointerBufferRight = right.asFloatBuffer();
        vertexPointerBufferRight.put(VERTEX_POINT_RIGHT);
        vertexPointerBufferRight.position(0);

        ByteBuffer top = ByteBuffer.allocateDirect(VERTEX_POINT_TOP.length * 4);
        top.order(ByteOrder.nativeOrder());
        vertexPointerBufferTop = top.asFloatBuffer();
        vertexPointerBufferTop.put(VERTEX_POINT_TOP);
        vertexPointerBufferTop.position(0);

        ByteBuffer bottom = ByteBuffer.allocateDirect(VERTEX_POINT_BOTTOM.length * 4);
        bottom.order(ByteOrder.nativeOrder());
        vertexPointerBufferBottom = bottom.asFloatBuffer();
        vertexPointerBufferBottom.put(VERTEX_POINT_BOTTOM);
        vertexPointerBufferBottom.position(0);




        ByteBuffer dd = ByteBuffer.allocateDirect(VERTEX_TEX.length * 4);
        dd.order(ByteOrder.nativeOrder());
        vertexTexBuffer  = dd.asFloatBuffer();
        vertexTexBuffer.put(VERTEX_TEX);
        vertexTexBuffer.position(0);

        //加载着色器
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, ResReadUtils.readResource(R.raw.vertex_texture_2d_shade));
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, ResReadUtils.readResource(R.raw.fragment_texture_2d_shade));

        //创建opengles程序，并转接着色器
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        //这里需要加载原图未经缩放的数据
        options.inScaled = false;
        mBitmap = BitmapFactory.decodeResource(Utils.getApp().getResources(),R.drawable.img_texture_2d,options);
        textureId[0] = createTexture2D(mBitmap);

        Bitmap t2 = BitmapFactory.decodeResource(Utils.getApp().getResources(),R.drawable.img_texture_2d_2,options);
        textureId[1] = createTexture2D(t2);

        Bitmap t3 = BitmapFactory.decodeResource(Utils.getApp().getResources(),R.drawable.squirtle,options);
        textureId[2] = createTexture2D(t3);

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

        mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_Matrix");
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mPositionTexHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        //3个顶点，4（点的维数）*3
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexPointerBufferFront);
        GLES20.glEnableVertexAttribArray(mPositionTexHandle);
        GLES20.glVertexAttribPointer(mPositionTexHandle,2,GLES20.GL_FLOAT,false,0,vertexTexBuffer);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId[0]);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0,VERTEX_POINT_FRONT.length/3);

        //3个顶点，4（点的维数）*3
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexPointerBufferBack);
        GLES20.glEnableVertexAttribArray(mPositionTexHandle);
        GLES20.glVertexAttribPointer(mPositionTexHandle,2,GLES20.GL_FLOAT,false,0,vertexTexBuffer);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId[0]);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0,VERTEX_POINT_BACK.length/3);

        //3个顶点，4（点的维数）*3
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexPointerBufferTop);
        GLES20.glEnableVertexAttribArray(mPositionTexHandle);
        GLES20.glVertexAttribPointer(mPositionTexHandle,2,GLES20.GL_FLOAT,false,0,vertexTexBuffer);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId[1]);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0,VERTEX_POINT_TOP.length/3);

        //3个顶点，4（点的维数）*3
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexPointerBufferBottom);
        GLES20.glEnableVertexAttribArray(mPositionTexHandle);
        GLES20.glVertexAttribPointer(mPositionTexHandle,2,GLES20.GL_FLOAT,false,0,vertexTexBuffer);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId[1]);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0,VERTEX_POINT_BOTTOM.length/3);

        //3个顶点，4（点的维数）*3
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexPointerBufferLeft);
        GLES20.glEnableVertexAttribArray(mPositionTexHandle);
        GLES20.glVertexAttribPointer(mPositionTexHandle,2,GLES20.GL_FLOAT,false,0,vertexTexBuffer);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId[2]);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0,VERTEX_POINT_LEFT.length/3);

        //3个顶点，4（点的维数）*3
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexPointerBufferRight);
        GLES20.glEnableVertexAttribArray(mPositionTexHandle);
        GLES20.glVertexAttribPointer(mPositionTexHandle,2,GLES20.GL_FLOAT,false,0,vertexTexBuffer);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId[2]);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0,VERTEX_POINT_RIGHT.length/3);


//        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexPointerBuffer2);
//        GLES20.glEnableVertexAttribArray(mPositionTexHandle);
//        GLES20.glVertexAttribPointer(mPositionTexHandle,2,GLES20.GL_FLOAT,false,0,vertexTexBuffer);
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId[1]);
//        GLES20.glDrawElements(GLES20.GL_TRIANGLES,VERTEX_POINT.length,GLES20.GL_UNSIGNED_SHORT,indicesBuffer);

//        GLES20.glUniform4fv(mColorHandle, 1, color, 0);



        GLES20.glDisableVertexAttribArray(mPositionTexHandle);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    private int createTexture2D(Bitmap bitmap){
        if (bitmap != null && !bitmap.isRecycled()){
            int[] ids = new int[1];
            GLES20.glGenTextures(1,ids,0);
            if (ids[0] == 0) {
                Log.e(TAG, "Could not generate a new OpenGL textureId object.");
                return 0;
            }
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,ids[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_REPEAT);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,bitmap,0);
            return ids[0];
        }
        return 0;
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
