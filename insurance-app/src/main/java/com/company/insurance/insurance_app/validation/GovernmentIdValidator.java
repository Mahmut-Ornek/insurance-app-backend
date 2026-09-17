package com.company.insurance.insurance_app.validation;

public class GovernmentIdValidator {
    public static boolean isValid(String tckn){
        if (tckn == null || tckn.length() != 11 || tckn.charAt(0) == '0'){return false;}

        for (int i = 0; i < tckn.length(); i++){
            if (!Character.isDigit(tckn.charAt(i))){
                return false;
            }
        }
        int x = 0, y = 0;
        for (int i = 0; i < 9; i++){
            if (i % 2 == 0){ x += Character.getNumericValue(tckn.charAt(i));}
            else {y += Character.getNumericValue(tckn.charAt(i));}
        }

        int result = ((x * 7) - y);
        if (Math.floorMod(result, 10) != Character.getNumericValue(tckn.charAt(9))){return false;}

        int result2 = x + y + Character.getNumericValue(tckn.charAt(9));
        if (Math.floorMod(result2, 10) != Character.getNumericValue(tckn.charAt(10))){return false;}

        return true;
    }
}
