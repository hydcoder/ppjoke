package com.hyd_coder.ppjoke.utils;

import android.content.res.AssetManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.hyd_coder.ppjoke.model.BottomBar;
import com.hyd_coder.ppjoke.model.Destination;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Cerated by huangyingde
 * Create date : 2020/6/2 12:38
 * description : 解析destination.json文件
 */
public class AppConfig {

    private static HashMap<String, Destination> sDesConfig;

    private static BottomBar sBottomBar;

    public static HashMap<String, Destination> getDesConfig() {
        if (sDesConfig == null) {
            String content = parseFile("destination.json");
            sDesConfig = JSON.parseObject(content, new TypeReference<HashMap<String, Destination>>() {
            }.getType());
        }

        return sDesConfig;
    }

    public static BottomBar getBottomBar() {
        if (sBottomBar == null) {
            String content = parseFile("main_tabs_config.json");
            sBottomBar = JSON.parseObject(content, BottomBar.class);
        }
        return sBottomBar;
    }

    private static String parseFile(String fileName) {
        AssetManager assetManager = AppGlobals.getAppication().getResources().getAssets();

        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            inputStream = assetManager.open(fileName);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }
}
