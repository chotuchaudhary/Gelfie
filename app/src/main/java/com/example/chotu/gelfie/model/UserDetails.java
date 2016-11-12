package com.example.chotu.gelfie.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by CHOTU on 11/5/2016.
 */
public class UserDetails{
    Map<String ,String> imageMap;


    public UserDetails()
    {
        imageMap=new HashMap<>();
    }

    public void putImageUrl(String img,String url)
    {

        imageMap.put(img,url);
    }

    public ArrayList<String> getImageUrl()
    {
       ArrayList<String> listOfUrl=new ArrayList<>();
        for(Map.Entry me:imageMap.entrySet())
        {
            listOfUrl.add((String)me.getValue());
        }
        return listOfUrl;
    }

}
