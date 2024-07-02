package java13.exchange_rates_bot_2.exception;

public class ServiceException extends Exception{
    public ServiceException(String message,Throwable cause){
        super(message, cause);
    }
}
