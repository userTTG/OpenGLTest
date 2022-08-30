package com.zhh.opengltest;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @ClassName SimpleShapeRender
 * @Description TODO
 * @Author zhangh-be
 * @Date 2022/8/30 16:51
 * @Version 1.0
 */
public class SimpleShapeRender implements GLSurfaceView.Renderer {
   //一个Float占用4Byte
   private static final int BYTES_PER_FLOAT = 4;
   //三个顶点
   private static final int POSITION_COMPONENT_COUNT = 3;
   //顶点位置缓存
   private final FloatBuffer vertexBuffer;
   //顶点颜色缓存
   private final FloatBuffer colorBuffer;
   //渲染程序
   private int mProgram;

   //相机矩阵
   private final float[] mViewMatrix = new float[16];
   //投影矩阵
   private final float[] mProjectMatrix = new float[16];
   //最终变换矩阵
   private final float[] mMVPMatrix = new float[16];

   //返回属性变量的位置
   //变换矩阵
   private int uMatrixLocation;
   //位置
   private int aPositionLocation;
   //颜色
   private int aColorLocation;

   //三个顶点的位置参数
   private float triangleCoords[] = {
           0.5f, 0.5f, 0.0f, // top
           -0.5f, -0.5f, 0.0f, // bottom left
           0.5f, -0.5f, 0.0f // bottom right
   };

   //三个顶点的颜色参数
   private float color[] = {
           1.0f, 0.0f, 0.0f, 1.0f,// top
           0.0f, 1.0f, 0.0f, 1.0f,// bottom left
           0.0f, 0.0f, 1.0f, 1.0f// bottom right
   };

   public SimpleShapeRender() {
      //顶点位置相关
      //分配本地内存空间,每个浮点型占4字节空间；将坐标数据转换为FloatBuffer，用以传入给OpenGL ES程序
      vertexBuffer = ByteBuffer.allocateDirect(triangleCoords.length * BYTES_PER_FLOAT)
              .order(ByteOrder.nativeOrder())
              .asFloatBuffer();
      vertexBuffer.put(triangleCoords);
      vertexBuffer.position(0);

      //顶点颜色相关
      colorBuffer = ByteBuffer.allocateDirect(color.length * BYTES_PER_FLOAT)
              .order(ByteOrder.nativeOrder())
              .asFloatBuffer();
      colorBuffer.put(color);
      colorBuffer.position(0);
   }

   @Override
   public void onSurfaceCreated(GL10 gl, EGLConfig config) {
      //将背景设置为白色
      GLES20.glClearColor(1.0f,1.0f,1.0f,1.0f);

      //编译顶点着色程序
      String vertexShaderStr = ResReadUtils.readResource(R.raw.vertex_simple_shade);
      int vertexShaderId = ShaderUtils.compileVertexShader(vertexShaderStr);
      //编译片段着色程序
      String fragmentShaderStr = ResReadUtils.readResource(R.raw.fragment_simple_shade);
      int fragmentShaderId = ShaderUtils.compileFragmentShader(fragmentShaderStr);
      //连接程序
      mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId);
      //在OpenGLES环境中使用程序
      GLES30.glUseProgram(mProgram);


      uMatrixLocation = GLES30.glGetUniformLocation(mProgram, "u_Matrix");
      aPositionLocation = GLES30.glGetAttribLocation(mProgram, "vPosition");
      aColorLocation = GLES30.glGetAttribLocation(mProgram, "aColor");
   }

   @Override
   public void onSurfaceChanged(GL10 gl, int width, int height) {
      //设置绘制窗口
      GLES30.glViewport(0, 0, width, height);


      //计算宽高比
      float ratio=(float)width/height;
      //设置透视投影
      Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
      //设置相机位置
      Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
      //计算变换矩阵
      Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);
   }

   @Override
   public void onDrawFrame(GL10 gl) {
      //把颜色缓冲区设置为我们预设的颜色
      GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

      //将变换矩阵传入顶点渲染器
      GLES20.glUniformMatrix4fv(uMatrixLocation,1,false,mMVPMatrix,0);
      //准备坐标数据
      GLES30.glVertexAttribPointer(aPositionLocation, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer);
      //启用顶点位置句柄
      GLES30.glEnableVertexAttribArray(aPositionLocation);

      //准备颜色数据
      GLES30.glVertexAttribPointer(aColorLocation, 4, GLES30.GL_FLOAT, false, 0, colorBuffer);
      //启用顶点颜色句柄
      GLES30.glEnableVertexAttribArray(aColorLocation);

      //绘制三个点
      //GLES30.glDrawArrays(GLES30.GL_POINTS, 0, POSITION_COMPONENT_COUNT);

      //绘制三条线
      //GLES30.glLineWidth(3);//设置线宽
      //GLES30.glDrawArrays(GLES30.GL_LINE_LOOP, 0, POSITION_COMPONENT_COUNT);

      //绘制三角形
      GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, POSITION_COMPONENT_COUNT);

      //禁止顶点数组的句柄
      GLES30.glDisableVertexAttribArray(aPositionLocation);
      GLES30.glDisableVertexAttribArray(aColorLocation);
   }
}

