package com.fearefull.dotaanalyser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by A.Hosseini on 2016-08-03.
 */
public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "dotaManager",
    TABLE_DOTA = "dota",
    KEY_ID = "id",
    KEY_STEAMID = "steamID",
    KEY_PERSONNAME = "personName",
    KEY_REALNAME = "realName",
    KEY_IMAGEPATH = "imageUri",
    KEY_ISLOGOUT = "isLogout";

    public DBHandler (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_DOTA + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_STEAMID + " TEXT," + KEY_PERSONNAME + " TEXT," + KEY_REALNAME + " TEXT," + KEY_IMAGEPATH + " TEXT," + KEY_ISLOGOUT + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOTA);

        onCreate(db);
    }

    public void createUser(User user) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_STEAMID, user.getSteamID());
        values.put(KEY_PERSONNAME, user.getPersonName());
        values.put(KEY_REALNAME, user.getRealName());
        values.put(KEY_IMAGEPATH, user.getImagePath());
        values.put(KEY_ISLOGOUT, user.getIsLogout());

        db.insert(TABLE_DOTA, null, values);
        db.close();
    }

    public User getUser (int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_DOTA, new String[] { KEY_ID, KEY_STEAMID, KEY_PERSONNAME, KEY_REALNAME, KEY_IMAGEPATH, KEY_ISLOGOUT}, KEY_ID + "=?", new String[] {String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        db.close();
        assert cursor != null;
        User rerurnUser = new User(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
        cursor.close();

        return rerurnUser;
    }

    public void deleteContact(User user) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_DOTA, KEY_ID + "=?", new String[] {String.valueOf(user.getId())});
        db.close();
    }

    public int updateUser(User user) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_STEAMID, user.getSteamID());
        values.put(KEY_PERSONNAME, user.getPersonName());
        values.put(KEY_REALNAME, user.getRealName());
        values.put(KEY_IMAGEPATH, user.getImagePath());
        values.put(KEY_ISLOGOUT, user.getIsLogout());

        int returnInt = db.update(TABLE_DOTA, values, KEY_ID + " =?", new String[] { String.valueOf(user.getId()) });
        db.close();

        return returnInt;
    }

    public int getCountUser() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DOTA, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count;
    }

}
