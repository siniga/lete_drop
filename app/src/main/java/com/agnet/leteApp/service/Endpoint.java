package com.agnet.leteApp.service;

/**
 * Created by Peter Khamis on 5/23/2017.
 */

/**
 * Created by Peter Khamis on 5/22/2017.
 */

public class Endpoint {

//     private static String root = "http://lete.app/api/public/index.php/api/";
//     private static String storage = "http://lete.app/api/storage/app/";

     private static String root = "http://letedeve.aggreyapps.com/api/public/index.php/api/";
     private static String storage = "http://letedeve.aggreyapps.com/api/storage/app/";

//    private static String root = "http://lete.aggreyapps.com/api/public/index.php/api/";
//    private static String storage = "http://lete.aggreyapps.com/api/storage/app/";

   /* private static String root = "http://ec2-3-133-133-214.us-east-2.compute.amazonaws.com/api/public/index.php/api/auth/";
    private static String storage = "http://ec2-3-133-133-214.us-east-2.compute.amazonaws.com/api/storage/app/";*/

//   private static  String root ="https://msm.aggreyapps.com/api/public/index.php/api/auth/";
//   private static String storage ="https://msm.aggreyapps.com/api/storage/app/";

    private static String completeUrl;

    public static void setUrl(String url) {
        completeUrl = root + url;
    }

    public static String getUrl() {
        return completeUrl;
    }

    public static void setStorageUrl(String storageUrl) {
        completeUrl = storage + storageUrl;
    }

    public static String getStorageUrl() {
        return completeUrl;
    }
}

