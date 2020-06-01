package com.qn.server.service;

import com.qn.server.dto.ProductResult;
import com.qn.server.entity.Product;
import com.qn.server.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author nick
 */
@Slf4j
@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    /**
     * 获取全部数据
     * @return
     */
    public List<ProductResult> getAllProduct() {
        val entityList = productRepository.findAll();
        if (CollectionUtils.isEmpty(entityList)){
            return Collections.EMPTY_LIST;
        }
        return entityList.stream().map(Product::getProductResult).collect(Collectors.toList());
    }

    /**
     * 查询单个数据Constants
     * @param productId
     * @return
     */
    public ProductResult findProductById(Integer productId){
        if (StringUtils.isEmpty(productId)){
            return null;
        }
        val entity = productRepository.findById(productId);
        return Objects.isNull(entity) ? null :  entity.map(Product::getProductResult).get();
    }

    /**
     * 添加
     * @param productResult
     * @return
     */
    public ProductResult addProduct(ProductResult productResult){
        Product toBeSavedEntity = new Product();
        BeanUtils.copyProperties(productResult, toBeSavedEntity);
        val savedEntity = productRepository.save(toBeSavedEntity);
        return savedEntity.getProductResult();
    }

    /**
     * 删除数据
     * @param productId
     * @return
     */
    public Integer deleteProduct(Integer productId){
        productRepository.deleteById(productId);
        return productId;
    }


    public ProductResult patchProduct(ProductResult productResult) {
        val entity = productRepository.findById(productResult.getProductId()).get();
        entity.setProductName(productResult.getProductName());
        entity.setDescription(productResult.getDescription());
        val saved = productRepository.save(entity);
        return saved.getProductResult();
    }

}