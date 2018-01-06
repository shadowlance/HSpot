package com.zhupiter.hspot.cilent.data;

import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by zhupiter on 17-1-30.
 */

public class Site {

    private int SId;
    private String SiteName, SiteCover, SiteURL, searchURL;
    private List<WebPage> categories;
    protected Rule siteRule, chapterRule, comicRule, imageRule;

    public Site() {

    }


    public int getSId() {
        return SId;
    }

    public String getSiteName() {
        return SiteName;
    }

    public String getSiteCover() {
        return SiteCover;
    }

    public String getSiteURL() {
        return SiteURL;
    }

    public String getSearchURL() {
        return searchURL;
    }

    public Rule getSiteRule() {
        return siteRule;
    }

    public Rule getChapterRule() {
        return chapterRule;
    }

    public Rule getComicRule() {
        return comicRule;
    }

    public Rule getImageRule() {
        return imageRule;
    }

    public List<WebPage> getCategories() {
        return categories;
    }

    public void setSId(int SId) {
        this.SId = SId;
    }

    public void setSiteCover(String cover) {
        this.SiteCover = cover;
    }

    public void setSiteURL(String siteURL) {
        SiteURL = siteURL;
    }

    public void setSearchURL(String searchURL) {
        this.searchURL = searchURL;
    }

    public void setSiteName(String siteName) {
        SiteName = siteName;
    }

    public void setCategories(List<WebPage> categories) {
        this.categories = categories;
    }

    public void setSiteRule(Rule siteRule) {
        this.siteRule = siteRule;
    }

    public void setChapterRule(Rule chapterRule) {
        this.chapterRule = chapterRule;
    }

    public void setComicRule(Rule comicRule) {
        this.comicRule = comicRule;
    }

    public void setImageRule(Rule imageRule) {
        this.imageRule = imageRule;
    }

    public void replace(Site site) {
        if (site == null)
            return;
        Field[] fs = Site.class.getDeclaredFields();
        try {
            for (Field f : fs) {
                if (!("SId".equals(f.getName()))) {
                    f.setAccessible(true);
                    if (f.getType() == String.class) {
                        String value = (String) f.get(site);
                        if (!TextUtils.isEmpty(value)) {
                            f.set(this,value);
                        }
                    } else if (f.getType() == Integer.class) {
                        int value = (int) f.get(site);
                        if (value != 0) {
                            f.set(this, value);
                        }
                    } else if ("categories".equals(f.getName())) {
                        List<WebPage> categories = (List<WebPage>) f.get(site);
                        if (this.categories != null && categories == null) {
                            categories = new ArrayList<>();
                        }
                        f.set(this, categories);
                    } else if (f.getType() == Rule.class) {
                        Rule newRule = (Rule) f.get(site);
                        Rule oldRule = (Rule) f.get(this);
                        if (oldRule == null) {
                            oldRule = newRule;
                        } else {
                            oldRule.replace(newRule);
                        }
                        f.set(this, oldRule);
                    } else {
                        f.set(this, f.get(site));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
