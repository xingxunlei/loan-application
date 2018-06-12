package com.loan_entity.lakala.batchTrade;


import com.loan_entity.lakala.LakalaCrossPaySuperResponse;

/**
 * 批量文件上传响应参数
 */
public class BatUploadFileResponse extends LakalaCrossPaySuperResponse {

    private static final long serialVersionUID = 6474794926023025286L;

    private String sign;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BatUploadFileResponse{");
        sb.append("sign='").append(sign).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
