package link.tomorinao.xuecheng.base.exception;

/**
 * @author Mr.M
 * @version 1.0
 * @description 本项目自定义异常类型
 * @date 2023/2/12 16:56
 */
public class XueChengException extends RuntimeException {

    private String errMessage;

    public XueChengException() {
    }

    public XueChengException(String message) {
        super(message);
        this.errMessage = message;

    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public static void cast(String message){
        throw new XueChengException(message);
    }
    public static void cast(CommonError error){
        throw new XueChengException(error.getErrMessage());
    }

}
