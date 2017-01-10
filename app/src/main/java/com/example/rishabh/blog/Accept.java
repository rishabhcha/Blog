package com.example.rishabh.blog;


public class Accept {

    String challenger_pic;
    String challenger_name;

    public Accept(){

    }

    public Accept(String acceptor_pic, String challenger_pic, String challenger_name) {
        this.challenger_pic = challenger_pic;
        this.challenger_name = challenger_name;
    }

    public String getChallenger_pic() {
        return challenger_pic;
    }

    public void setChallenger_pic(String challenger_pic) {
        this.challenger_pic = challenger_pic;
    }

    public String getChallenger_name() {
        return challenger_name;
    }

    public void setChallenger_name(String challenger_name) {
        this.challenger_name = challenger_name;
    }
}
