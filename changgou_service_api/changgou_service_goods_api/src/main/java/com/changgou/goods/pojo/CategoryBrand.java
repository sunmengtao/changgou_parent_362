package com.changgou.goods.pojo;

import javax.persistence.Table;

@Table(name = "tb_category_brand")
public class CategoryBrand {

    private Integer brandId;

    private Integer categoryId;

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
}
