package com.changgou.system.service.impl;

import com.changgou.system.dao.AdminMapper;
import com.changgou.system.service.AdminService;
import com.changgou.system.pojo.Admin;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    /**
     * 查询全部列表
     * @return
     */
    @Override
    public List<Admin> findAll() {
        return adminMapper.selectAll();
    }

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    @Override
    public Admin findById(Integer id){
        return  adminMapper.selectByPrimaryKey(id);
    }


    /**
     * 增加
     * @param admin
     */
    @Override
    public void add(Admin admin){
        String textPwd = admin.getPassword();
        String textEncrypt = BCrypt.hashpw(textPwd, BCrypt.gensalt());
        admin.setPassword(textEncrypt);
        adminMapper.insertSelective(admin);
    }


    /**
     * 修改
     * @param admin
     */
    @Override
    public void update(Admin admin){
        String textPwd = admin.getPassword();
        String textEncrypt = BCrypt.hashpw(textPwd, BCrypt.gensalt());
        admin.setPassword(textEncrypt);
        adminMapper.updateByPrimaryKeySelective(admin);
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(Integer id){
        adminMapper.deleteByPrimaryKey(id);
    }


    /**
     * 条件查询
     * @param searchMap
     * @return
     */
    @Override
    public List<Admin> findList(Map<String, Object> searchMap){
        Example example = createExample(searchMap);
        return adminMapper.selectByExample(example);
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Admin> findPage(int page, int size){
        PageHelper.startPage(page,size);
        return (Page<Admin>)adminMapper.selectAll();
    }

    /**
     * 条件+分页查询
     * @param searchMap 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public Page<Admin> findPage(Map<String,Object> searchMap, int page, int size){
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Admin>)adminMapper.selectByExample(example);
    }

    /**
     * 构建查询对象
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Admin.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 用户名
            if(searchMap.get("loginName")!=null && !"".equals(searchMap.get("loginName"))){
                criteria.andLike("loginName","%"+searchMap.get("loginName")+"%");
           	}
            // 密码
            if(searchMap.get("password")!=null && !"".equals(searchMap.get("password"))){
                criteria.andLike("password","%"+searchMap.get("password")+"%");
           	}
            // 状态
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andEqualTo("status",searchMap.get("status"));
           	}

            // id
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }

        }
        return example;
    }

    @Override
    public Boolean login(Admin admin) {
        //根据用户名从DB查找用户信息
        Admin cond = new Admin();
        cond.setLoginName(admin.getLoginName());
        Admin adminDB = adminMapper.selectOne(cond);
        //如果用户信息不存在,就false
        if (adminDB==null){
            return false;
        }
        //获取DB用户的密文密码,跟登陆用户输入的明文密码进行校验,如果成功就返回true
        String pwdText = admin.getPassword();
        String pwdEncrpt = adminDB.getPassword();
        boolean checkpw = BCrypt.checkpw(pwdText, pwdEncrpt);
        return checkpw;
    }
}
