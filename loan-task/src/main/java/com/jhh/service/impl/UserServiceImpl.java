package com.jhh.service.impl;

import com.jhh.dao.MsgMapper;
import com.jhh.dao.MsgTemplateMapper;
import com.jhh.model.CodeReturn;
import com.jhh.model.Msg;
import com.jhh.model.MsgTemplate;
import com.jhh.service.UserService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
@Service("userService")
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private MsgMapper msgMapper;

    @Autowired
    private MsgTemplateMapper msgTemplateMapper;


    public String setMessage(String userId, String templateId, String params) {
        JSONObject obj = new JSONObject();
        try {
            String[] cc = params.split(",");
            // 定义最后的消息内容
            String dd = "";
            // 获取消息模版
            MsgTemplate msgTemplate = msgTemplateMapper
                    .selectByPrimaryKey(Integer.parseInt(templateId));
            if (null != msgTemplate) {

                if ("1".equals(msgTemplate.getStatus())) {
                    // 获取模版的内容
                    String ll = msgTemplate.getContent();
                    // 获取模版的标题
                    String title = msgTemplate.getTitle();
                    // 分割模版内容
                    String[] aa = ll.split("\\{");
                    // 将模版内容和参数拼接成最后的消息内容
                    for (int i = 0; i < aa.length; i++) {
                        String[] bb = aa[i].split("}");
                        if (bb.length > 1) {
                            dd += cc[i - 1] + bb[1];
                        } else {
                            dd += aa[i];
                        }
                    }
                    Msg msg = new Msg();
                    msg.setContent(dd);
                    msg.setTitle(title);
                    msg.setPerId(Integer.parseInt(userId));
                    msg.setStatus("n");
                    msg.setType(1);
                    msg.setCreateTime(new Date());
                    msgMapper.insertSelective(msg);

                    obj.put("code", CodeReturn.success);
                    obj.put("info", "消息发送成功");
                    obj.put("data", dd);
                } else {
                    obj.put("code", CodeReturn.fail);
                    obj.put("info", "模版已失效");
                    obj.put("data", "");
                }
            } else {
                obj.put("code", CodeReturn.fail);
                obj.put("info", "模版不存在");
                obj.put("data", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("code", CodeReturn.fail);
            obj.put("info", "消息发送失败,请检查参数和模版是否匹配！");
            obj.put("data", "");
        }
        return obj.toString();
    }
}