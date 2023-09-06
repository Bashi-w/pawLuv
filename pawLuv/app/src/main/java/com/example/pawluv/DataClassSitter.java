package com.example.pawluv;

import static java.nio.charset.StandardCharsets.UTF_8;

import android.content.Context;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class DataClassSitter {
    String name;
    String age;
    String mobile;
    String intro;
    String image;

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getMobile() {
        return mobile;
    }

    public String getIntro() {
        return intro;
    }

    public String getImage() {
        return image;
    }



    public DataClassSitter(String name, String age, String mobile, String intro, String image) {
        this.name = name;
        this.age = age;
        this.mobile = mobile;
        this.intro = intro;
        this.image = image;
    }

    @NonNull
    static ArrayList<DataClassSitter> getSitters(String fileName, Context context){
        //create arraylist of Book objects
        ArrayList<DataClassSitter> sitterList=new ArrayList<>();
        try{
            InputStream inputStream=context.getAssets().open(fileName);

            byte[] buffer=new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();


            //JSONObject json=new JSONObject(new String(buffer), StandardCharsets.UTF_8));
            JSONObject json=new JSONObject(new String(buffer,UTF_8));
            JSONArray sitters=json.getJSONArray("sitters");

            for(int i=0;i<sitters.length();i++){
                sitterList.add(new DataClassSitter(
                        sitters.getJSONObject(i).getString("Name"),
                        sitters.getJSONObject(i).getString("Age"),
                        sitters.getJSONObject(i).getString("Mobile number"),
                        sitters.getJSONObject(i).getString("Introduction"),
                        sitters.getJSONObject(i).getString("Cover")
                        )

                );
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return sitterList;
    }




}
