package com.fearefull.dotaanalyser;

/**
 * Created by A.Hosseini on 2016-08-03.
 */
public class User {

    private int _id;
    private String _steamID, _personName, _realName, _imagePath, _isLogout;

    public User(int id, String steamID, String personName, String realName, String imagePath, String isLogout) {
        _id = id;
        _steamID = steamID;
        _personName = personName;
        _realName = realName;
        _imagePath = imagePath;
        _isLogout = isLogout;
    }

    public int getId() { return _id; }

    public String getImagePath() { return _imagePath; }

    public  String getSteamID() { return  _steamID; }

    public String getPersonName() { return _personName; }

    public String getRealName() { return _realName; }

    public String getIsLogout() { return _isLogout; }
}
