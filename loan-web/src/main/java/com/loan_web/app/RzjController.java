package com.loan_web.app;

import com.loan_entity.rzj.RzjRegisterDo;
import com.loan_entity.rzj.RzjRegisterVo;
import com.loan_utils.util.EmaySmsUtil;
import com.loan_web.common.constant.ErrorCode;
import com.loan_web.common.exception.CommonException;
import com.loan_web.common.util.ValidMac;
import com.loan_web.service.RzjService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 *  融之家第三方对接
 */
@Controller
@RequestMapping("/rzj")
public class RzjController {

    private static final Logger logger = LoggerFactory.getLogger(RzjController.class);

    @Autowired
    private ValidMac validMac;

    @Autowired
    private RzjService rzjService;
    /**
     *  融之家 注册推送 自动注册
     * @return
     */
    @RequestMapping("/register")
    @ResponseBody
    public RzjRegisterDo register(HttpServletRequest request,RzjRegisterVo vo) throws Exception{
        logger.info("\n\n#################融之家第三方注册start#####################");
        logger.info("融之家第三方注册请求参数:" + vo);
        // 参数校验
        Map<String, String[]> map = request.getParameterMap();
        String[] keys = { "apply_info","apply_no", "channel_no", "timestamp", "user_attribute", "sign" };
        validMac.checkMac(keys, map);
        RzjRegisterDo registerDo = rzjService.rzjRegister(vo);
        return registerDo;
    }


    @ExceptionHandler
    @ResponseBody
    public RzjRegisterDo exception(Exception ex) {
        if (ex instanceof CommonException) {
            logger.error("融之家自定义异常抛出 "+ex);
            CommonException comm = (CommonException) ex;
            return new RzjRegisterDo(comm.getResultCode(),
                    comm.getMessage());
        }
        // 打印
        logger.error(ex.getMessage(), ex);
        return new RzjRegisterDo(ErrorCode.ERROR_CODE,ErrorCode.SYSTEM_ERROR);
    }

}
