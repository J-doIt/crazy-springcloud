package com.crazymaker.springcloud.common.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageOut<T>
{
    private static final long serialVersionUID = -275582211117389L;

    private int curPage;

    private int pageSize;

    private int pages;

    /**
     * 总数
     */
    private long count;

    /**
     * 是否成功：0 成功、1 失败
     */
    private int code;
    /**
     * 当前页结果集
     */
    private List<T> data;

}
