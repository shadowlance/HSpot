package com.zhupiter.hspot.cilent.data;

import android.text.TextUtils;

import java.lang.reflect.Field;

/**
 * Created by zhupiter on 17-2-16.
 */

public class WebPage {

    private String url, title;

    public WebPage(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public void replace(WebPage webPage) {
        if (webPage == null)   return;
        Field[] fields = Rule.class.getDeclaredFields();
        for (Field fd : fields) {
            try {
                fd.setAccessible(true);
                if (fd.getType() == String.class) {
                    String value = (String) fd.get(webPage);
                    if (!TextUtils.isEmpty(value)) {
                        fd.set(this, value);
                    }
                } else if (fd.getType() == Integer.class) {
                    int value = (int) fd.get(webPage);
                    if (value != 0) {
                        fd.set(this, value);
                    }
                } else {
                    fd.set(this, fd.get(webPage));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
