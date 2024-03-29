//package com.example.demo.config;
//
//
//import cn.hutool.core.util.StrUtil;
//import ggd.wuyang.basic.base.R;
//import ggd.wuyang.basic.exception.ArgumentException;
//import ggd.wuyang.basic.exception.BizException;
//import ggd.wuyang.basic.exception.ForbiddenException;
//import ggd.wuyang.basic.exception.UnauthorizedException;
//import ggd.wuyang.basic.exception.code.ExceptionCode;
//import org.apache.ibatis.exceptions.PersistenceException;
//import org.mybatis.spring.MyBatisSystemException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.converter.HttpMessageNotReadableException;
//import org.springframework.stereotype.Controller;
//import org.springframework.validation.BindException;
//import org.springframework.validation.FieldError;
//import org.springframework.web.HttpMediaTypeNotSupportedException;
//import org.springframework.web.HttpRequestMethodNotSupportedException;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.MissingServletRequestParameterException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.context.request.RequestAttributes;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
//import org.springframework.web.multipart.MultipartException;
//import org.springframework.web.multipart.support.MissingServletRequestPartException;
//import org.springframework.web.servlet.DispatcherServlet;
//
//import javax.servlet.Servlet;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.validation.ConstraintViolation;
//import javax.validation.ConstraintViolationException;
//import java.sql.SQLException;
//import java.util.List;
//import java.util.Objects;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Configuration
//@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
//@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
//@RestControllerAdvice(annotations = {RestController.class, Controller.class})
//public class BaseExceptionConfiguration {
//    private static final Logger log = LoggerFactory.getLogger(BaseExceptionConfiguration.class);
//    protected String profiles;
//
////    public void AbstractGlobalExceptionHandler() {
////    }
//
//    @ExceptionHandler({BizException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public R<?> bizException(BizException ex) {
//        log.warn("BizException:", ex);
//        return R.result(ex.getCode(), (Object)null, ex.getMessage()).setErrorMsg(this.getErrorMsg(ex)).setPath(this.getPath());
//    }
//
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler({ArgumentException.class})
//    public R<?> bizException(ArgumentException ex) {
//        log.warn("ArgumentException:", ex);
//        return R.result(ex.getCode(), (Object)null, ex.getMessage()).setErrorMsg(this.getErrorMsg(ex)).setPath(this.getPath());
//    }
//
//    @ExceptionHandler({ForbiddenException.class})
//    @ResponseStatus(HttpStatus.FORBIDDEN)
//    public R<?> forbiddenException(ForbiddenException ex) {
//        log.warn("BizException:", ex);
//        return R.result(ex.getCode(), (Object)null, ex.getMessage()).setErrorMsg(this.getErrorMsg(ex)).setPath(this.getPath());
//    }
//
//    @ExceptionHandler({UnauthorizedException.class})
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public R<?> unauthorizedException(UnauthorizedException ex) {
//        log.warn("BizException:", ex);
//        return R.result(ex.getCode(), (Object)null, ex.getMessage()).setErrorMsg(this.getErrorMsg(ex)).setPath(this.getPath());
//    }
//
//    @ExceptionHandler({HttpMessageNotReadableException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public R<?> httpMessageNotReadableException(HttpMessageNotReadableException ex) {
//        log.warn("HttpMessageNotReadableException:", ex);
//        String message = ex.getMessage();
//        if (StrUtil.containsAny(message, new CharSequence[]{"Could not read document:"})) {
//            String msg = String.format("无法正确的解析json类型的参数：%s", StrUtil.subBetween(message, "Could not read document:", " at "));
//            return R.result(ExceptionCode.PARAM_EX.getCode(), (Object)null, msg, ex.getMessage()).setPath(this.getPath());
//        } else {
//            return R.result(ExceptionCode.PARAM_EX.getCode(), (Object)null, ExceptionCode.PARAM_EX.getMsg()).setErrorMsg(this.getErrorMsg(ex)).setPath(this.getPath());
//        }
//    }
//
//    @ExceptionHandler({BindException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public R<?> bindException(BindException ex) {
//        log.warn("BindException:", ex);
//
//        try {
//            String msg = ((FieldError) Objects.requireNonNull(ex.getBindingResult().getFieldError())).getDefaultMessage();
//            if (StrUtil.isNotEmpty(msg)) {
//                return R.result(ExceptionCode.PARAM_EX.getCode(), (Object)null, msg, ex.getMessage()).setPath(this.getPath());
//            }
//        } catch (Exception var4) {
//            log.debug("获取异常描述失败", var4);
//        }
//
//        StringBuilder msg = new StringBuilder();
//        List<FieldError> fieldErrors = ex.getFieldErrors();
//        fieldErrors.forEach((oe) -> {
//            msg.append("参数:[").append(oe.getObjectName()).append(".").append(oe.getField()).append("]的传入值:[").append(oe.getRejectedValue()).append("]与预期的字段类型不匹配.");
//        });
//        return R.result(ExceptionCode.PARAM_EX.getCode(), (Object)null, msg.toString()).setErrorMsg(this.getErrorMsg(ex)).setPath(this.getPath());
//    }
//
//    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public R<?> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
//        log.warn("MethodArgumentTypeMismatchException:", ex);
//        String msg = "参数：[" + ex.getName() + "]的传入值：[" + ex.getValue() + "]与预期的字段类型：[" + ((Class)Objects.requireNonNull(ex.getRequiredType())).getName() + "]不匹配";
//        return R.result(ExceptionCode.PARAM_EX.getCode(), (Object)null, msg).setErrorMsg(this.getErrorMsg(ex)).setPath(this.getPath());
//    }
//
//    @ExceptionHandler({IllegalStateException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public R<?> illegalStateException(IllegalStateException ex) {
//        log.warn("IllegalStateException:", ex);
//        return R.result(ExceptionCode.ILLEGAL_ARGUMENT_EX.getCode(), (Object)null, ExceptionCode.ILLEGAL_ARGUMENT_EX.getMsg()).setErrorMsg(this.getErrorMsg(ex)).setPath(this.getPath());
//    }
//
//    @ExceptionHandler({MissingServletRequestParameterException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public R<?> missingServletRequestParameterException(MissingServletRequestParameterException ex) {
//        log.warn("MissingServletRequestParameterException:", ex);
//        return R.result(ExceptionCode.ILLEGAL_ARGUMENT_EX.getCode(), (Object)null, "缺少必须的[" + ex.getParameterType() + "]类型的参数[" + ex.getParameterName() + "]").setErrorMsg(this.getErrorMsg(ex)).setPath(this.getPath());
//    }
//
//    @ExceptionHandler({NullPointerException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public R<?> nullPointerException(NullPointerException ex) {
//        log.warn("NullPointerException:", ex);
//        return R.result(ExceptionCode.NULL_POINT_EX.getCode(), (Object)null, ExceptionCode.NULL_POINT_EX.getMsg()).setErrorMsg(this.getErrorMsg(ex)).setPath(this.getPath());
//    }
//
//    @ExceptionHandler({IllegalArgumentException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public R<?> illegalArgumentException(IllegalArgumentException ex) {
//        log.warn("IllegalArgumentException:", ex);
//        return R.result(ExceptionCode.ILLEGAL_ARGUMENT_EX.getCode(), (Object)null, ExceptionCode.ILLEGAL_ARGUMENT_EX.getMsg()).setErrorMsg(this.getErrorMsg(ex)).setPath(this.getPath());
//    }
//
//    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public R<?> httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
//        log.warn("HttpMediaTypeNotSupportedException:", ex);
//        MediaType contentType = ex.getContentType();
//        return contentType != null ? R.result(ExceptionCode.MEDIA_TYPE_EX.getCode(), (Object)null, "请求类型(Content-Type)[" + contentType + "] 与实际接口的请求类型不匹配", ex.getMessage()).setPath(this.getPath()) : R.result(ExceptionCode.MEDIA_TYPE_EX.getCode(), (Object)null, "无效的Content-Type类型").setErrorMsg(this.getErrorMsg(ex)).setPath(this.getPath());
//    }
//
//    @ExceptionHandler({MissingServletRequestPartException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public R<?> missingServletRequestPartException(MissingServletRequestPartException ex) {
//        log.warn("MissingServletRequestPartException:", ex);
//        return R.result(ExceptionCode.REQUIRED_FILE_PARAM_EX.getCode(), (Object)null, ExceptionCode.REQUIRED_FILE_PARAM_EX.getMsg()).setErrorMsg(this.getErrorMsg(ex)).setPath(this.getPath());
//    }
//
//    @ExceptionHandler({ServletException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public R<?> servletException(ServletException ex) {
//        log.warn("ServletException:", ex);
//        String msg = "UT010016: Not a multi part request";
//        return msg.equalsIgnoreCase(ex.getMessage()) ? R.result(ExceptionCode.REQUIRED_FILE_PARAM_EX.getCode(), (Object)null, ExceptionCode.REQUIRED_FILE_PARAM_EX.getMsg(), ex.getMessage()) : R.result(ExceptionCode.SYSTEM_BUSY.getCode(), (Object)null, ex.getMessage()).setErrorMsg(this.getErrorMsg(ex)).setPath(this.getPath());
//    }
//
//    @ExceptionHandler({MultipartException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public R<?> multipartException(MultipartException ex) {
//        log.warn("MultipartException:", ex);
//        return R.result(ExceptionCode.REQUIRED_FILE_PARAM_EX.getCode(), (Object)null, ExceptionCode.REQUIRED_FILE_PARAM_EX.getMsg()).setErrorMsg(this.getErrorMsg(ex)).setPath(this.getPath());
//    }
//
//    @ExceptionHandler({ConstraintViolationException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public R<?> constraintViolationException(ConstraintViolationException ex) {
//        log.warn("ConstraintViolationException:", ex);
//        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
//        String message = (String)violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(";"));
//        return R.result(ExceptionCode.BASE_VALID_PARAM.getCode(), (Object)null, message).setErrorMsg(this.getErrorMsg(ex)).setPath(this.getPath());
//    }
//
//    @ExceptionHandler({MethodArgumentNotValidException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public R<?> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
//        log.warn("MethodArgumentNotValidException:", ex);
//        return R.result(ExceptionCode.BASE_VALID_PARAM.getCode(), (Object)null, ((FieldError)Objects.requireNonNull(ex.getBindingResult().getFieldError())).getDefaultMessage()).setErrorMsg(this.getErrorMsg(ex)).setPath(this.getPath());
//    }
//
//    @ExceptionHandler({Exception.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public R<?> otherExceptionHandler(Exception ex) {
//        log.warn("Exception:", ex);
//        return ex.getCause() instanceof BizException ? this.bizException((BizException)ex.getCause()) : R.result(ExceptionCode.CODE_EXCEPTION.getCode(), (Object)null, ExceptionCode.CODE_EXCEPTION.getMsg()).setErrorMsg(this.getErrorMsg(ex)).setPath(this.getPath());
//    }
//
//    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public R<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
//        log.warn("HttpRequestMethodNotSupportedException:", ex);
//        return R.result(ExceptionCode.METHOD_NOT_ALLOWED.getCode(), (Object)null, ExceptionCode.METHOD_NOT_ALLOWED.getMsg()).setErrorMsg(this.getErrorMsg(ex)).setPath(this.getPath());
//    }
//
//    @ExceptionHandler({PersistenceException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public R<?> persistenceException(PersistenceException ex) {
//        log.warn("PersistenceException:", ex);
//        if (ex.getCause() instanceof BizException) {
//            BizException cause = (BizException)ex.getCause();
//            return R.result(cause.getCode(), (Object)null, cause.getMessage());
//        } else {
//            return R.result(ExceptionCode.CODE_EXCEPTION.getCode(), (Object)null, ExceptionCode.CODE_EXCEPTION.getMsg()).setErrorMsg(this.getErrorMsg(ex)).setPath(this.getPath());
//        }
//    }
//
//    @ExceptionHandler({MyBatisSystemException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public R<?> myBatisSystemException(MyBatisSystemException ex) {
//        log.warn("PersistenceException:", ex);
//        return ex.getCause() instanceof PersistenceException ? this.persistenceException((PersistenceException)ex.getCause()) : R.result(ExceptionCode.CODE_EXCEPTION.getCode(), (Object)null, ExceptionCode.CODE_EXCEPTION.getMsg()).setErrorMsg(this.getErrorMsg(ex)).setPath(this.getPath());
//    }
//
//    @ExceptionHandler({SQLException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public R<?> sqlException(SQLException ex) {
//        log.warn("SQLException:", ex);
//        return R.result(ExceptionCode.CODE_EXCEPTION.getCode(), (Object)null, ExceptionCode.CODE_EXCEPTION.getMsg()).setErrorMsg(this.getErrorMsg(ex)).setPath(this.getPath());
//    }
//
//    @ExceptionHandler({DataIntegrityViolationException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public R<?> dataIntegrityViolationException(DataIntegrityViolationException ex) {
//        log.warn("DataIntegrityViolationException:", ex);
//        return R.result(ExceptionCode.CODE_EXCEPTION.getCode(), (Object)null, ExceptionCode.CODE_EXCEPTION.getMsg()).setErrorMsg(this.getErrorMsg(ex)).setPath(this.getPath());
//    }
//
//    private String getPath() {
//        String path = "";
//        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//        if (requestAttributes != null) {
//            HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
//            path = request.getRequestURI();
//        }
//
//        return path;
//    }
//
//    private String getErrorMsg(Exception ex) {
//        return "prod".equals(this.profiles) ? "" : ex.getLocalizedMessage();
//    }
//
//}
