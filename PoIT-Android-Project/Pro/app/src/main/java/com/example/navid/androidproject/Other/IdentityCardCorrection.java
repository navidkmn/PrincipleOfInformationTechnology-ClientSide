package com.example.navid.androidproject.Other;

public class IdentityCardCorrection {
    public boolean isIdentityCard(String code){
        if(code.length() == 10){
            if(code.equals("0000000000") || code.equals("1111111111") || code.equals("2222222222") ||
                    code.equals("3333333333") || code.equals("4444444444") || code.equals("5555555555") ||
                    code.equals("6666666666") || code.equals("7777777777") || code.equals("8888888888") ||
                    code.equals("9999999999"))
                return false;
            else{
                int a = Integer.parseInt(code.substring(9,10));
                int b = Integer.parseInt(code.substring(0,1))*10 + Integer.parseInt(code.substring(1,2))*9 +
                        Integer.parseInt(code.substring(2,3))*8 + Integer.parseInt(code.substring(3,4))*7 +
                        Integer.parseInt(code.substring(4,5))*6 + Integer.parseInt(code.substring(5,6))*5 +
                        Integer.parseInt(code.substring(6,7))*4 + Integer.parseInt(code.substring(7,8))*3 +
                        Integer.parseInt(code.substring(8,9))*2;
                int c = b - (b/11) * 11;
                return (c == 0 && c == a) || (c == 1 && a == 1) || (c > 1 && a == 11 - c);
            }
        } else
            return false;
    }
}
