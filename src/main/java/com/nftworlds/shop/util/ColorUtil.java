package com.nftworlds.shop.util;

import com.iridium.iridiumcolorapi.IridiumColorAPI;

public class ColorUtil {

    public static String rgb(String s) {
        return IridiumColorAPI.process(s);
    }

}
