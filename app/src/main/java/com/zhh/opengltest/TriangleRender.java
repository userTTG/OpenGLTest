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


//   public static void glVertexAttribPointer(
//           int indx,//顶点属性的索引,即glGetAttribLocation获取的句柄
//           int size,//维数，一个点或颜色的维数。必须是1、2、3、4
//           int type,//属性的元素类型
//           boolean normalized,//转换的时候是否要经过规范化，true：是；false：直接转化
//           int stride,//跨距，默认是0。（若将顶点位置和颜色数据存放在一个数组内，则需要设置）
//           java.nio.Buffer ptr//数据
//   )

//   public static native void glDrawArrays(
//           int mode,//需要渲染的图元类型，包括 GL_POINTS, GL_LINE_STRIP, GL_LINE_LOOP, GL_LINES, GL_TRIANGLE_STRIP, GL_TRIANGLE_FAN ，GL_TRIANGLES。
//           int first,//从数组缓存中的哪一位开始绘制，一般为0.
//           int count//数组中顶点的数量.
//   );
//   https://blog.csdn.net/frank06504/article/details/117523329

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
