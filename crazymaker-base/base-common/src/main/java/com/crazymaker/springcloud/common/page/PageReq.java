package com.crazymaker.springcloud.common.page;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("分页")
@Data
public class PageReq
{

    @ApiModelProperty(value ="当前页",example = "1")
    private int curPage = 1;

    @ApiModelProperty(value = "每页条数",example = "10")
    private int pageSize = 20;

    @JsonIgnore
    public int getJpaPage()
    {
        return curPage - 1;
    }
}
