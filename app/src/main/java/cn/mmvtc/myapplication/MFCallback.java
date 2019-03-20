package cn.mmvtc.myapplication;

interface back {
    String back(String s);
}

public  class MFCallback implements back {
    @Override
    public String back(String s) {
        return s;
    }
}
