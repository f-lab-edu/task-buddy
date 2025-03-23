package com.taskbuddy.api.business;

public interface EmailSender {

    /**
     * 주어진 이메일로 메일을 발송한다. (비동기 발송)
     *
     * @param receiver 수신자 이메일
     * @param title 메일 제목
     * @param content 메일 내용
     */
    void sendAsync(String receiver, String title, String content);
}
