package com.example.navid.androidproject.Other;

public class PersianDetection {
    public boolean textIsPersian(String s) {
        for (int i = 0; i < Character.codePointCount(s, 0, s.length()); i++) {
            char ch = s.charAt(i);
            if (ch == '‌' || ch == '.' || ch == '_' || ch == '-' || ch == '/' || ch == '\\') ;
            else {
                int c = s.codePointAt(i);
                if ((c >= 0x0600 && c <= 0x06FF) || (c >= 0xFB50 && c <= 0xFDFF) || (c >= 0xFE70 && c <= 0xFEFF) ||(c >= 0x0600 && c <= 0x06E0) || c == 0x0020) ;
                else
                    return false;
            }
        }
        return true;
    }
}
