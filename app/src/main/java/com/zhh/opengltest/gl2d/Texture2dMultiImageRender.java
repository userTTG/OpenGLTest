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
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @ClassName TriangleRender
 * @Description TODO
 * @Author zhangh-be
 * @Date 2022/8/26 11:18
 * @Version 1.0
 */
public class Texture2dMultiImageRender implements GLSurfaceView.Renderer {

    private static final String TAG = "Texture2dRender";

    private float[] VERTEX_POINT = {
        0,0,0,
        1,1,0,
        -1,1,0,
        -1,-1,0,
        1,-1,0
    };


    private float[] VERTEX_POINT_2 = {
            0,0,0,
            0.5f,0.5f,0,
            -0.5f,0.5f,0,
            -0.5f,-0.5f,0,
            0.5f,-0.5f,0
    };

    private float[] VERTEX_TEX = {
        0.5f,0.5f,
        1f,0f,
        0f,0f,
        0f,1f,
        1f,1f,
    };



    private float[] VERTEX_TEX_2 = {
            0.5f,0.5f,
            1.5f,-0.5f,
            -0.5f,-0.5f,
            -0.5f,1.5f,
            1.5f,1.5f
    };

    /**
     * 顶点索引
     */
    private short[] indices = {
            0, 1, 2,
            0, 2, 3,
            0, 3, 4,
            0, 4, 1,
    };

    FloatBuffer vertexPointerBuffer,vertexPointerBuffer2,vertexTexBuffer,vertexTexBuffer2;
    //顶点索引缓存
    private ShortBuffer indicesBuffer;

    int[] textureId = new int[2];

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
        ByteBuffer bb = ByteBuffer.allocateDirect(VERTEX_POINT.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexPointerBuffer = bb.asFloatBuffer();
        vertexPointerBuffer.put(VERTEX_POINT);
        vertexPointerBuffer.position(0);

        //将顶占坐标数组转换为ByteBuffer
        ByteBuffer cc = ByteBuffer.allocateDirect(VERTEX_POINT_2.length * 4);
        cc.order(ByteOrder.nativeOrder());
        vertexPointerBuffer2 = cc.asFloatBuffer();
        vertexPointerBuffer2.put(VERTEX_POINT_2);
        vertexPointerBuffer2.position(0);

        ByteBuffer dd = ByteBuffer.allocateDirect(VERTEX_TEX.length * 4);
        dd.order(ByteOrder.nativeOrder());
        vertexTexBuffer  = dd.asFloatBuffer();
        vertexTexBuffer.put(VERTEX_TEX);
        vertexTexBuffer.position(0);

        ByteBuffer ss = ByteBuffer.allocateDirect(VERTEX_TEX_2.length * 4);
        ss.order(ByteOrder.nativeOrder());
        vertexTexBuffer2  = ss.asFloatBuffer();
        vertexTexBuffer2.put(VERTEX_TEX_2);
        vertexTexBuffer2.position(0);

        //顶点索引相关
        indicesBuffer = ByteBuffer.allocateDirect(indices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        indicesBuffer.put(indices);
        indicesBuffer.position(0);

        //加载着色器
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, ResReadUtils.readResource(R.raw.vertex_texture_2d_shade));
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, ResReadUtils.readResource(R.raw.fragment_texture_2d_shade));

        //创建opengles程序，并转接着色器
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        //这里需要加载原图未经缩放的数据
        options.inScaled = false;
        mBitmap = BitmapFactory.decodeResource(Utils.getApp().getResources(),R.drawable.img_texture_2d,options);
        textureId[0] = createTexture2D(mBitmap);

        Bitmap t2 = BitmapFactory.decodeResource(Utils.getApp().getResources(),R.drawable.img_texture_2d_2,options);
        textureId[1] = createTexture2D(t2);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        int w=mBitmap.getWidth();
        int h=mBitmap.getHeight();
        float sWH=w/(float)h;
        float sWidthHeight=width/(float)height;
        if(width>height){
            if(sWH>sWidthHeight){
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight*sWH,sWidthHeight*sWH, -1,1, 3, 7);
            }else{
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight/sWH,sWidthHeight/sWH, -1,1, 3, 7);
            }
        }else{
            if(sWH>sWidthHeight){
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -1/sWidthHeight*sWH, 1/sWidthHeight*sWH,3, 7);
            }else{
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -sWH/sWidthHeight, sWH/sWidthHeight,3, 7);
            }
        }
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
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
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexPointerBuffer);
        GLES20.glEnableVertexAttribArray(mPositionTexHandle);
        GLES20.glVertexAttribPointer(mPositionTexHandle,2,GLES20.GL_FLOAT,false,0,vertexTexBuffer);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId[0]);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,VERTEX_POINT.length,GLES20.GL_UNSIGNED_SHORT,indicesBuffer);

        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexPointerBuffer2);
        GLES20.glEnableVertexAttribArray(mPositionTexHandle);
        GLES20.glVertexAttribPointer(mPositionTexHandle,2,GLES20.GL_FLOAT,false,0,vertexTexBuffer);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId[1]);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,VERTEX_POINT.length,GLES20.GL_UNSIGNED_SHORT,indicesBuffer);

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
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);

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

    private float[] createCirclePosition(int n){
        List<Float> list = new ArrayList<>();
        float radius = 0.5f;
        list.add(0f);
        list.add(0f);
        list.add(0f);
        float angleStripe = 360/n;
        for (float index = 0;index<360+angleStripe;index+=angleStripe){
            list.add((float)Math.cos(Math.PI*index/180) * radius);
            list.add((float)Math.sin(Math.PI*index/180) * radius);
            list.add(0f);
        }
        float[] result = new float[list.size()];

        for (int i=0;i<list.size();i++){
            result[i] = list.get(i);
        }
        return result;
    }

    private float[] createCircleColor(int n){
        float[] colors = new float[4*(n+2)];
        for (int i=0;i<n+2;i++){
            colors[i*4] = 1f;
            colors[i*4+1] = 0f;
            colors[i*4+2] = 0f;
            colors[i*4+3] = 1f;
        }
        return colors;
    }
}
