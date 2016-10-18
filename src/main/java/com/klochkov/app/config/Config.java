package com.klochkov.app.config;



import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by georgyklochkov on 17/10/16.
 */
public class Config {

    private static String bucketName = "klochkovgbotimages";
    private static String sourceDirectoryName;
    private static String resizeDirectoryName = "tmpDir";
    private static String aws_access_key_id = null;
    private static String aws_secret_access_key = null;
    private static String queueHost = "localhost";

    public static String getAws_secret_access_key() {
        return aws_secret_access_key;
    }

    public static String getAws_access_key_id() {
        return aws_access_key_id;
    }

    public static String getSourceDirectoryName() {
        return sourceDirectoryName;
    }

    public static String getBucketName() {
        return bucketName;
    }

    public static String getFileName() {
        return fileName;
    }


    public static String getResizeDirectoryName(){
        return resizeDirectoryName;
    }

    public static String getQueueHost(){
        return queueHost;
    }

    private static String fileName;


    public static void parse(String fName){
        fileName = fName;

        JSONParser parser = new JSONParser();
        try{
            JSONObject obj = (JSONObject)parser.parse(new FileReader(fileName));
            String tmp = (String)obj.get("bucketName");
            if(tmp!=null){
                bucketName = tmp;
            }
            tmp = (String)obj.get("sourceDirectoryName");
            if(tmp!=null){
                sourceDirectoryName = tmp;
            }
            tmp = (String)obj.get("tmpDirectoryName");
            if(tmp!=null){
                resizeDirectoryName = tmp;
            }
            tmp = (String)obj.get("aws_access_key_id");
            if(tmp!=null){
                aws_access_key_id = tmp;
            }
            tmp = (String)obj.get("aws_secret_access_key");
            if(tmp!=null){
                aws_secret_access_key = tmp;
            }
            tmp = (String)obj.get("queue_host");
            if(tmp!=null){
                queueHost = tmp;
            }
            //Add other parameters here


        }catch(FileNotFoundException ex){
            ex.printStackTrace();
        } catch (ParseException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
