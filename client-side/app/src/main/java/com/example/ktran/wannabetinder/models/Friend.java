package com.example.ktran.wannabetinder.models;

import java.util.Date;

/**
 * Created by ktran on 11/30/17.
 */

public class Friend {

    Date added;
    String status;
    String _id;
    User friend;

    public Friend(Date added, String status, String _id, User friend) {
        this.added = added;
        this.status = status;
        this._id = _id;
        this.friend = friend;
    }

    @Override
    public String toString() {
        if(this.status.equals("accepted"))
            return friend.getName().toUpperCase() + " | Phone Number(slide in those DMs): " +  friend.getPhone();
        else
            return friend.getName().toUpperCase() + " | Current status: " + this.status;
    }

    public Date getAdded() {
        return added;
    }

    public void setAdded(Date added) {
        this.added = added;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }

}
