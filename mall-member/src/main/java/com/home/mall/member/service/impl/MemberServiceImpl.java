package com.home.mall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.home.common.utils.HttpUtils;
import com.home.mall.member.dao.MemberLevelDao;
import com.home.mall.member.exception.PhoneExistException;
import com.home.mall.member.exception.UserNameExistException;
import com.home.mall.member.vo.MemberLoginVo;
import com.home.mall.member.vo.MemberRegistVo;
import com.home.mall.member.vo.SocialUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.home.common.utils.PageUtils;
import com.home.common.utils.Query;

import com.home.mall.member.dao.MemberDao;
import com.home.mall.member.entity.MemberEntity;
import com.home.mall.member.service.MemberService;


@Service("memberService")
@Slf4j
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
    @Autowired
    private MemberLevelDao levelDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 实现真正的用户注册功能
     * @param vo
     */
    @Override
    public void regist(MemberRegistVo vo) {
        MemberEntity memberEntity = new MemberEntity();
        long levelId=levelDao.selectDefaultLevel();//获得默认的会员登记id
        memberEntity.setLevelId(levelId); //设置默认的会员登记
        //在设置用户名和手机号的时候需要校验是不是已经存在，防止出现重复，保证唯一性
        //使用异常机制来处理
        checkUserName(vo.getUserName());
        checkPhone(vo.getPhone());

        memberEntity.setUsername(vo.getUserName());
        memberEntity.setMobile(vo.getPhone());

        //密码不能用明文存储，需要进行加密，加密之后才能存储
        //加密
        //spring家的这个BCryptPasswordEncoder就已经默认实现了盐值加密，而且你是不知道盐值是什么的
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = passwordEncoder.encode(vo.getPassword());
        memberEntity.setPassword(password);
        memberEntity.setNickname(UUID.randomUUID().toString().substring(0,10));
        //存到数据库
        this.baseMapper.insert(memberEntity);

    }

    @Override
    public void checkUserName(String userName) throws UserNameExistException {
        Integer count = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if(count>0){
            throw new UserNameExistException();
        }

    }

    @Override
    public void checkPhone(String phone) throws PhoneExistException {
        Integer count = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if(count>0){
            throw new PhoneExistException();
        }

    }

    /**
     * 校验登录功能
     * @param vo
     * @return
     */
    @Override
    public MemberEntity login(MemberLoginVo vo) {
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", vo.getLoginacct()).or().eq("mobile", vo.getLoginacct()));
        if(memberEntity==null){
            return null;
        }else{
            String password = memberEntity.getPassword();
            String voPassword = vo.getPassword();
            //比较密码
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean matches = passwordEncoder.matches(voPassword, password);
            if(matches){
                return memberEntity;
            }else{
                return null;
            }
        }
    }

    /**
     * 第三方登录功能
     * @param vo
     * @return
     */
    @Override
    public MemberEntity oauthLogin(SocialUserVo vo) {
        //如果这个第三方登录不是第一次登录就直接登录,但是要更新token和过期时间
        MemberEntity entity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid",vo.getUid()));
        if(entity!=null){
            //如果不是第一次登录了
            //更新token和过期时间
            MemberEntity memberEntity = new MemberEntity();
            memberEntity.setId(entity.getId());
            memberEntity.setAccessToken(vo.getAccess_token());
            memberEntity.setExpiresIn(vo.getExpires_in());
            this.baseMapper.updateById(memberEntity);
            entity.setAccessToken(vo.getAccess_token());
            entity.setExpiresIn(vo.getExpires_in());
            return entity;
        }else{
            //如果是第一次登录需要注册
            MemberEntity memberEntity = new MemberEntity();
            try{
                Map<String,String> query=new HashMap<>();
                query.put("access_token",vo.getAccess_token());
                query.put("uid",vo.getUid());
                HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<String, String>(), query);
                if(response.getStatusLine().getStatusCode()==200){
                    String json = EntityUtils.toString(response.getEntity());
                    log.info("获取用户信息的结果：",json);
                    JSONObject object = JSON.parseObject(json);
                    String nickName = object.getString("name");
                    String gender = object.getString("gender");
                    long level = levelDao.selectDefaultLevel();
                    memberEntity.setNickname(nickName);
                    memberEntity.setLevelId(level);
                    memberEntity.setGender("m".equals(gender)?1:0);
                }
            }catch (Exception e){

            }
            memberEntity.setAccessToken(vo.getAccess_token());
            memberEntity.setExpiresIn(vo.getExpires_in());
            memberEntity.setSocialUid(vo.getUid());
            this.baseMapper.insert(memberEntity);
            return memberEntity;
        }
    }
}