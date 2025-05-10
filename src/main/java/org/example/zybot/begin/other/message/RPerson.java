package org.example.zybot.begin.other.message;

//RPerson类  作为传输数据的类储存各模块返回的内容
public class RPerson {
    public int mode=1;//模式 1：纯文本 2：图片 3：图片+文本
    private String text="0";//返回的文本 如果文本不为-1则代表内容被处理
    private String imageUrl="0";//返回的图片URL

    //get与set方法
    public int getMode() {
        return mode;
    }
    public void setMode(int mode) {
        this.mode = mode;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}

