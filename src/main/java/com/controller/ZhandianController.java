package com.controller;


import java.text.SimpleDateFormat;
import com.alibaba.fastjson.JSONObject;
import java.util.*;
import org.springframework.beans.BeanUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.ContextLoader;
import javax.servlet.ServletContext;
import com.service.TokenService;
import com.utils.StringUtil;
import java.lang.reflect.InvocationTargetException;

import com.service.DictionaryService;
import org.apache.commons.lang3.StringUtils;
import com.annotation.IgnoreAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;

import com.entity.ZhandianEntity;

import com.service.ZhandianService;
import com.entity.view.ZhandianView;
import com.utils.PageUtils;
import com.utils.R;

/**
 * 快递站点
 * 后端接口
 * @author
 * @email
 * @date 2021-03-11
*/
@RestController
@Controller
@RequestMapping("/zhandian")
public class ZhandianController {
    private static final Logger logger = LoggerFactory.getLogger(ZhandianController.class);

    @Autowired
    private ZhandianService zhandianService;


    @Autowired
    private TokenService tokenService;
    @Autowired
    private DictionaryService dictionaryService;


    //级联表service


    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("page方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(StringUtil.isNotEmpty(role) && "用户".equals(role)){
            params.put("yonghuId",request.getSession().getAttribute("userId"));
        }
        PageUtils page = zhandianService.queryPage(params);

        //字典表数据转换
        List<ZhandianView> list =(List<ZhandianView>)page.getList();
        for(ZhandianView c:list){
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(c);
        }
        return R.ok().put("data", page);
    }
    /**
    * 后端详情
    */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        logger.debug("info方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        ZhandianEntity zhandian = zhandianService.selectById(id);
        if(zhandian !=null){
            //entity转view
            ZhandianView view = new ZhandianView();
            BeanUtils.copyProperties( zhandian , view );//把实体数据重构到view中

            //修改对应字典表字段
            dictionaryService.dictionaryConvert(view);
            return R.ok().put("data", view);
        }else {
            return R.error(511,"查不到数据");
        }

    }

    /**
    * 后端保存
    */
    @RequestMapping("/save")
    public R save(@RequestBody ZhandianEntity zhandian, HttpServletRequest request){
        logger.debug("save方法:,,Controller:{},,zhandian:{}",this.getClass().getName(),zhandian.toString());
        Wrapper<ZhandianEntity> queryWrapper = new EntityWrapper<ZhandianEntity>()
            .eq("zdname", zhandian.getZdname())
            .eq("address", zhandian.getAddress())
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        ZhandianEntity zhandianEntity = zhandianService.selectOne(queryWrapper);
        if(zhandianEntity==null){
        //  String role = String.valueOf(request.getSession().getAttribute("role"));
        //  if("".equals(role)){
        //      zhandian.set
        //  }
            zhandianService.insert(zhandian);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    /**
    * 修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody ZhandianEntity zhandian, HttpServletRequest request){
        logger.debug("update方法:,,Controller:{},,zhandian:{}",this.getClass().getName(),zhandian.toString());
        //根据字段查询是否有相同数据
        Wrapper<ZhandianEntity> queryWrapper = new EntityWrapper<ZhandianEntity>()
            .notIn("id",zhandian.getId())
            .eq("zdname", zhandian.getZdname())
            .eq("address", zhandian.getAddress())
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        ZhandianEntity zhandianEntity = zhandianService.selectOne(queryWrapper);
        if(zhandianEntity==null){
            //  String role = String.valueOf(request.getSession().getAttribute("role"));
            //  if("".equals(role)){
            //      zhandian.set
            //  }
            zhandianService.updateById(zhandian);//根据id更新
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }


    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids){
        logger.debug("delete:,,Controller:{},,ids:{}",this.getClass().getName(),ids.toString());
        zhandianService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }


}

