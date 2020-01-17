package com.elder.zcommonmodule.Service.Error;
public class ApiException extends Exception {
    public int code;
    public String msg;

    public ApiException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;

    }


    public static class ServerException extends RuntimeException {
        public int code;
        public String message;

        public ServerException(int code, String msg) {
            this.code = code;
            this.message = msg;
        }
    }
}