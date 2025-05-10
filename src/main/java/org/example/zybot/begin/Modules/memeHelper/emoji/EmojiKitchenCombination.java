package org.example.zybot.begin.Modules.memeHelper.emoji;

public class EmojiKitchenCombination {
    private String gStaticUrl;
    private String alt;
    private String leftEmoji;
    private String leftEmojiCodepoint;
    private String rightEmoji;
    private String rightEmojiCodepoint;
    private String date;
    private Boolean isLatest;
    private int gBoardOrder;

    public EmojiKitchenCombination() {}


    // 构造函数
    public EmojiKitchenCombination(String gStaticUrl, String alt, String leftEmoji, String leftEmojiCodepoint,
                                   String rightEmoji, String rightEmojiCodepoint, String date, Boolean isLatest,
                                   int gBoardOrder) {
        this.gStaticUrl = gStaticUrl;
        this.alt = alt;
        this.leftEmoji = leftEmoji;
        this.leftEmojiCodepoint = leftEmojiCodepoint;
        this.rightEmoji = rightEmoji;
        this.rightEmojiCodepoint = rightEmojiCodepoint;
        this.date = date;
        this.isLatest = isLatest;
        this.gBoardOrder = gBoardOrder;
    }

    // Getter 和 Setter 方法
    public String getgStaticUrl() {
        return gStaticUrl;
    }

    public void setgStaticUrl(String gStaticUrl) {
        this.gStaticUrl = gStaticUrl;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getLeftEmoji() {
        return leftEmoji;
    }

    public void setLeftEmoji(String leftEmoji) {
        this.leftEmoji = leftEmoji;
    }

    public String getLeftEmojiCodepoint() {
        return leftEmojiCodepoint;
    }

    public void setLeftEmojiCodepoint(String leftEmojiCodepoint) {
        this.leftEmojiCodepoint = leftEmojiCodepoint;
    }

    public String getRightEmoji() {
        return rightEmoji;
    }

    public void setRightEmoji(String rightEmoji) {
        this.rightEmoji = rightEmoji;
    }

    public String getRightEmojiCodepoint() {
        return rightEmojiCodepoint;
    }

    public void setRightEmojiCodepoint(String rightEmojiCodepoint) {
        this.rightEmojiCodepoint = rightEmojiCodepoint;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getIsLatest() {
        return isLatest;
    }

    public void setIsLatest(Boolean isLatest) {
        this.isLatest = isLatest;
    }

    public int getgBoardOrder() {
        return gBoardOrder;
    }

    public void setgBoardOrder(int gBoardOrder) {
        this.gBoardOrder = gBoardOrder;
    }

    // Kotlin 中的 val filename: String get() = gStaticUrl.substringAfterLast('/') 转换为 Java
    public String getFilename() {
        return gStaticUrl.substring(gStaticUrl.lastIndexOf('/') + 1);
    }
}