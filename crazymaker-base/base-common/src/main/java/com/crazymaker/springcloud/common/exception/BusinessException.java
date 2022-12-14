package com.crazymaker.springcloud.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.StringUtils;

@Builder
@Data
@AllArgsConstructor
public class BusinessException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    /**
     * 默认的错误编码
     */
    private static final int DEFAULT_CODE = -1;


    /**
     * 默认的错误提示
     */
    private static final String DEFAULT_MSG = "业务异常";

    /**
     * 业务错误编码
     */
    @lombok.Builder.Default
    private int errCode = DEFAULT_CODE;
    /**
     * 错误的提示信息
     */
    @lombok.Builder.Default
    private String errMsg = DEFAULT_MSG;

    public BusinessException()
    {
        super(DEFAULT_MSG);
    }

    public BusinessException(String msg)
    {
        super(msg);
    }


    public BusinessException(String s, Throwable e) {
        super(s, e);
    }

    public BusinessException(Throwable e) {
        super(e);
    }
    /**
     * 带格式设置异常消息
     * @param format  格式
     * @param objects  替换的对象
     */
    public BusinessException setDetail(String format, Object... objects) {
        format = StringUtils.replace(format, "{}", "%s");
        this.errMsg = String.format(format, objects);
        return this;
    }
}
