package com.example.navid.androidproject.Other;

public class NumberDetection {
    public boolean isNumber(String number){
        for(int i = 0 ; i < number.length() ; i++)
            if((int)number.charAt(i) > 57 || (int)number.charAt(i) < 48)
                return false;
        return true;
    }
}
