package com.zhupiter.hspot.cilent.data;

import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhupiter on 17-2-16.
 */

public class Rule {
    private String regex, param;
    private List<String> elements;
    private String item;

    public Rule(){

    }

    public void replace(Rule rule) {
        if (rule == null)   return;
        Field[] fields = Rule.class.getDeclaredFields();
        for (Field fd : fields) {
            try {
                fd.setAccessible(true);
                if ("elements".equals(fd.getName())) {
                    List<String> elements = (List<String>) fd.get(rule);
                    if (this.elements != null) {
                        if (elements == null)
                            elements = new ArrayList<>();
                        for (String ele : this.elements) {
                            if (!elements.contains(ele)) {
                                elements.add(ele);
                            }
                        }
                    }
                    fd.set(this, elements);
                } else if (fd.getType() == String.class) {
                    String value = (String) fd.get(rule);
                    if (!TextUtils.isEmpty(value)) {
                        fd.set(this, value);
                    }
                } else {
                    fd.set(this, fd.get(rule));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
