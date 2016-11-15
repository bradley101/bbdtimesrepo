package com.tbt;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Created by bradley on 22-08-2016.
 */
public class StorageHandler {
    private String fileName;
    private String fileContent;
    private Context context;
    private String homeDirectory;
    private File file;

    public StorageHandler(Context context, String file, String content) {
        fileName = file;
        fileContent = content;
        this.context = context;
        initStorageDirectory();
    }
    public StorageHandler(Context context, String file) {
        fileName = file;
        this.context = context;
        initStorageDirectory();
    }

    void initStorageDirectory() {
        String homeDirectory = context.getFilesDir().getAbsolutePath();
        file = new File(homeDirectory, fileName);
    }

    public String readFile() {
        String fileContent = null;
        if(file.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                fileContent = "";
                while((line = reader.readLine()) != null) {
                    fileContent = fileContent + line;
                }
                reader.close();
                inputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileContent;
    }

    public boolean saveFile() {
        boolean operationSuccess = false;
        try {
            FileOutputStream outputStream = new FileOutputStream(file, false);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream));
            writer.print(fileContent);
            writer.close();
            outputStream.close();
            operationSuccess = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return operationSuccess;
    }
}
