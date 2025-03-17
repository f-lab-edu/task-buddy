package com.taskbuddy.api.presentation.secure;

@FunctionalInterface
public interface SecureDataDecryptor {

    /**
     * 암호화된 데이터를 복호화하고 지정된 타입으로 변환한다.
     *
     * @param encryptedData 클라이언트에서 암호화하여 전송한 (json 형식을 직렬화한) 문자열 데이터
     * @param returnClass 복호화된 데이터를 변환할 객체 타입
     * @param <R> 변환할 객체의 타입
     * @return 복호화된 데이터가 변환된 객체
     * @throws IllegalArgumentException 복호화에 실패하거나 데이터를 변환할 수 없는 경우 발생
     */
    <R> R decrypt(String encryptedData, Class<R> returnClass);
}
