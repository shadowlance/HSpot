package com.zhupiter.hspot.cilent.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.google.gson.Gson;
import com.zhupiter.hspot.cilent.data.Site;

/**
 * Created by zhupiter on 17-2-3.
 */

public class SiteManager {

    private DbManager manager;

    public SiteManager(Context context) {
        this.manager = new DbManager(context);
    }

    public void addNewSite(Site site) {
        ContentValues sValues = new ContentValues();
        sValues.put("siteURL", site.getSiteURL());
        sValues.put("siteName", site.getSiteName());
        sValues.put("coverURL", site.getSiteCover());
        sValues.put("searchURL", site.getSearchURL());
        sValues.put("siteRule", new Gson().toJson(site.getSiteRule()));
        sValues.put("comicRule", new Gson().toJson(site.getComicRule()));
        sValues.put("chapterRule", new Gson().toJson(site.getChapterRule()));
        sValues.put("imageRule", new Gson().toJson(site.getImageRule()));

        manager.insert("Site",sValues);
        manager.closeDataBase();
    }

    public void deleteSite(Site site, Context context) {
        int id = site.getSId();
        manager.deleteDB("Site", "Sid = ?", new String[]{String.valueOf(id)});
        manager.closeDataBase();
    }

    public void upgradeSite(Site site, Context context, ContentValues values){
        int id = site.getSId();
        manager.update("Site", values,"Sid = ?", new String[]{String.valueOf(id)});
        manager.closeDataBase();
    }

    public Cursor findAllSite() {
        return manager.findAll("Site");
    }

    public int getSiteCount() {
        return manager.getAllCount("Site");
    }

}
