package com.zhh.opengltest;

import android.content.res.Resources;

import com.blankj.utilcode.util.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @ClassName ResReadUtils
 * @Description TODO
 * @Author zhangh-be
 * @Date 2022/8/30 16:57
 * @Version 1.0
 */
public class ResReadUtils {

   /**
    * 读取资源
    * @param resourceId
    */
   public static String readResource(int resourceId) {
      StringBuilder builder = new StringBuilder();
      try {
         InputStream inputStream = Utils.getApp().getResources().openRawResource(resourceId);
         InputStreamReader streamReader = new InputStreamReader(inputStream);

         BufferedReader bufferedReader = new BufferedReader(streamReader);
         String textLine;
         while ((textLine = bufferedReader.readLine()) != null) {
            builder.append(textLine);
            builder.append("\n");
         }
      } catch (IOException e) {
         e.printStackTrace();
      } catch (Resources.NotFoundException e) {
         e.printStackTrace();
      }
      return builder.toString();
   }

}

