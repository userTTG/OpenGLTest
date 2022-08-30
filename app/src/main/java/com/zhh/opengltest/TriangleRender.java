package com.zhh.opengltest;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
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
public class TriangleRender implements GLSurfaceView.Renderer {

   private static final String TAG = "TriangleRender";

   private String vertexShaderCode =
           "attribute vec4 vPosition;" +
           "void main(){" +
           "gl_Position = vPosition;" +
           "}";

   private String fragmentShaderCode =
           "precision mediump float;" +
           "uniform vec4 vColor;" +
           "void main(){" +
           "gl_FragColor = vColor;" +
           "}";

   float triagnleCoords[] ={
     0.5f,0.5f,0f,
     -0.5f,-0.5f,0f,
     0.5f,-0.5f,0f,
   };

   float color[] = {1.0f,1.0f,1.0f,1.0f};

   FloatBuffer vertexBuffer;

   int mProgram;
   int mPositionHandle;
   int mColorHandle ;

   @Override
   public void onSurfaceCreated(GL10 gl, EGLConfig config) {
      //将背景设置为灰色
      GLES20.glClearColor(0.5f,0.5f,0.5f,1.0f);

      //将顶占坐标数组转换为ByteBuffer
      ByteBuffer bb = ByteBuffer.allocateDirect(triagnleCoords.length * 4);
      bb.order(ByteOrder.nativeOrder());
      vertexBuffer = bb.asFloatBuffer();
      vertexBuffer.put(triagnleCoords);
      vertexBuffer.position(0);

      //加载着色器
      int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,vertexShaderCode);
      int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentShaderCode);

      //创建opengles程序，并转接着色器
      mProgram = GLES20.glCreateProgram();
      GLES20.glAttachShader(mProgram,vertexShader);
      GLES20.glAttachShader(mProgram,fragmentShader);
      GLES20.glLinkProgram(mProgram);
   }

   @Override
   public void onSurfaceChanged(GL10 gl, int width, int height) {
      GLES20.glViewport(0,0,width,height);
   }

   @Override
   public void onDrawFrame(GL10 gl) {
      //清除颜色缓冲和深度缓冲
      GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
      //使用opengl程序
      GLES20.glUseProgram(mProgram);

      mPositionHandle = GLES20.glGetAttribLocation(mProgram,"vPosition");
      GLES20.glEnableVertexAttribArray(mPositionHandle);
      //3个顶点，4（点的维数）*3
      GLES20.glVertexAttribPointer(mPositionHandle,3,GLES20.GL_FLOAT,false,4*3,vertexBuffer);

      mColorHandle = GLES20.glGetUniformLocation(mProgram,"vColor");
      GLES20.glUniform4fv(mColorHandle,1,color,0);
      GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,3);
      GLES20.glDisableVertexAttribArray(mPositionHandle);
   }

   public int loadShader(int shaderType,String source){
      int shader = GLES20.glCreateShader(shaderType);
      checkGlError("glCreateShader type = "+ shaderType);
      GLES20.glShaderSource(shader,source);
      GLES20.glCompileShader(shader);
      int[] compiled = new int[1];
      GLES20.glGetShaderiv(shader,GLES20.GL_COMPILE_STATUS,compiled,0);
      if (compiled[0] == 0){
         Log.e(TAG, "Could not compile shader "+shaderType+":" );
         Log.e(TAG, " " + GLES20.glGetShaderInfoLog(shader) );
         GLES20.glDeleteShader(shader);
         shader = 0;
      }
      return shader;
   }

   public void checkGlError(String info){
      int error;
      while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR){
         String errorMsg = info+": glError"+error;
         Log.e(TAG, errorMsg );
         throw new RuntimeException(errorMsg);
      }
   }
}
