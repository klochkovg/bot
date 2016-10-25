package com.klochkov.app.upload;

import java.io.File;
import java.io.IOException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.klochkov.app.config.Config;

/**
 * Created by georgyklochkov on 17/10/16.
 */
public class Uploader {

    private static String bucketName     = Config.getBucketName();
    private static String keyName        = "tmp.jpg";
    private static String uploadFileName = "tmp.jpg";
    private static AmazonS3 s3client = null;

    public static void initialize(){
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(Config.getAws_access_key_id(), Config.getAws_secret_access_key());
        s3client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds)).withRegion(Regions.EU_CENTRAL_1)
                .build();
    }

    public static boolean uploadTest(String fileName, String destName){
        keyName = destName;
        uploadFileName = fileName;
        System.out.println("Attempt to send data");
        try {
            File file = new File(uploadFileName);
            s3client.putObject(new PutObjectRequest(
                    bucketName, keyName, file));

        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
                    "means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
            return false;
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
                    "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
            return false;
        }
        return true;
    }
}
