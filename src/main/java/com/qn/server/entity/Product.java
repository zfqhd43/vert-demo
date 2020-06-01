package com.qn.server.entity;


import com.qn.server.dto.ProductResult;
import lombok.Data;
import lombok.val;
import org.springframework.beans.BeanUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * @author nick
 */
@Data
@Entity
@Table(name = "product")
public class Product {

    @Id
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "description")
    private String description;

    /**
     * 将数据对象转成返给前端对象
     * @return
     */
    public ProductResult getProductResult(){
        val productDTO = new ProductResult();
        BeanUtils.copyProperties(this, productDTO);
        return productDTO;
    }

}