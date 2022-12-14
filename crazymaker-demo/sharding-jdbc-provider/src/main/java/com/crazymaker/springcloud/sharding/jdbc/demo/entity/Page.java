package com.crazymaker.springcloud.sharding.jdbc.demo.entity;

import java.util.List;

/**
 * 定义一个分页对象
 */
public class Page {

    private int page = 1;//当前页码, 从1开始
    private int pageTotal;//总页码
    private int rowsTotal;//总条数
    private int rows = 10;//每页显示条数
    private String hql;//分页语句
    private List<?> list;//返回的数据集合


    public Page() {
        super();
    }

    public String getHql() {
        return hql;
    }


    public void setHql(String hql) {
        this.hql = hql;
    }


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(int pageTotal) {
        this.pageTotal = pageTotal;
    }

    public int getRowsTotal() {
        return rowsTotal;
    }

    public void setRowsTotal(int rowsTotal) {
        this.rowsTotal = rowsTotal;
        pageTotal = rowsTotal % rows == 0 ? rowsTotal / rows : rowsTotal / rows + 1;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }


    @Override
    public String toString() {
        return "Pager [list=" + list + ", page=" + page + ", pageTotal="
                + pageTotal + ", rows=" + rows + ", rowsTotal=" + rowsTotal
                + "]";
    }


    public int getFirstResult() {
        return (this.getPage() - 1) * this.getRows();

    }
}