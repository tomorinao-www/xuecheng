package link.tomorinao.xuecheng.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2023/2/12 17:01
 */
@Slf4j
//@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {

    //对项目的自定义异常类型进行处理
//    @ResponseBody
    @ExceptionHandler(XueChengException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(XueChengException e) {
        log.error("系统异常: {}", e.getErrMessage(), e);
        //解析出异常信息
        String errMessage = e.getErrMessage();
        RestErrorResponse restErrorResponse = new RestErrorResponse(errMessage);
        return restErrorResponse;
    }


    //    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception e) {
        // 日志
        log.error("系统异常: {}", e.getMessage(), e);
        // 权限不足异常转换
        if ("Access Denied".equals(e.getMessage())) {
            return new RestErrorResponse("您没有权限操作此功能");
        }
        // 返回异常信息
        RestErrorResponse restErrorResponse = new RestErrorResponse(e.getMessage());
        return restErrorResponse;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("参数非法: {}", e.getMessage(), e);

        BindingResult bindingResult = e.getBindingResult();
        ArrayList<String> msgList = new ArrayList<>();
        bindingResult.getFieldErrors().forEach(item -> {
            msgList.add(item.getDefaultMessage());
        });
        String errMessage = StringUtils.join(msgList);
        //解析出异常信息
        RestErrorResponse restErrorResponse = new RestErrorResponse(errMessage);
        return restErrorResponse;
    }

}
