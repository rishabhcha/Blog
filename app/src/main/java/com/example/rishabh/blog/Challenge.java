package com.example.rishabh.blog;

public class Challenge {

    String acceptor_name;
    String acceptor_pic;
    String challenger_pic;
    String challenger_name;

    public Challenge(){

    }

    public Challenge(String acceptor_name, String acceptor_pic, String challenger_pic, String challenger_name) {
        this.acceptor_name = acceptor_name;
        this.acceptor_pic = acceptor_pic;
        this.challenger_pic = challenger_pic;
        this.challenger_name = challenger_name;
    }

    public String getAcceptor_name() {
        return acceptor_name;
    }

    public void setAcceptor_name(String acceptor_name) {
        this.acceptor_name = acceptor_name;
    }

    public String getAcceptor_pic() {
        return acceptor_pic;
    }

    public void setAcceptor_pic(String acceptor_pic) {
        this.acceptor_pic = acceptor_pic;
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
