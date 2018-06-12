package com.loan_utils.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.aspectj.util.FileUtil;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.ServerInfo;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

import com.alibaba.dubbo.common.threadpool.support.fixed.FixedThreadPool;


/**
 * @project risk-common
 * @author yy
 * @date 2017年7月11日 上午10:32:31
 * @description TODO fdfs文件存储 工具类
 * @tag
 * @company 上海金互行金融信息服务有限公司
 */
public class FDFSNewUtil {
    private static TrackerClient trackerClient = null;
    private static TrackerServer trackerServer = null;
    private static StorageServer storageServer = null;
    //使用StorageClient1进行上传
    private static StorageClient1 storageClient1 = null;

    private static FDFSNewUtil fDFSNewUtil = new FDFSNewUtil();

    private FDFSNewUtil() {
    }

    public static FDFSNewUtil getInstance() {
        try {
            if (trackerClient == null) {
                System.out.println("初始化文件服务器");
                init("fdfs_client.conf");
            }
            return fDFSNewUtil;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void init(String config)
    {
        try {
            ClientGlobal.init(config);
            trackerClient = new TrackerClient();
            trackerServer = trackerClient.getConnection();
            storageServer = trackerClient.getStoreStorage(trackerServer);
            storageClient1 = new StorageClient1(trackerServer, storageServer);
        } catch (Exception e) {
            throw new RuntimeException("初始化FDFS出错：",e);
        }
    }

    /**
     * @title uploadFile
     * @description 按字节上传文件
     * @author yy
     * @date 2017年7月11日 上午10:38:51
     * @param file_buff 字节数
     * @param file_ext_name 后缀名
     * @return
     * @throws Exception
     * @return String
     */
    public static String uploadFile(byte[] file_buff, String file_ext_name) throws Exception {
        String responseFileName = "http://"+storageServer.getInetSocketAddress().getHostName()+"/"+storageClient1.upload_file1(file_buff, file_ext_name, null);

        Thread.sleep(1500);

        String testString = downloadString(responseFileName);
        if (StringUtils.isEmpty(testString)) {
            System.out.println("test验证文件不存在"+responseFileName);
            throw new RuntimeException();
        }
        else{
            System.out.println("test验证文件存在"+testString.substring(0,testString.length()>10 ? 10 : testString.length()));
        }
        return responseFileName;

//        return "http://"+storageServer.getInetSocketAddress().getHostName()+"/"+storageClient1.upload_file1(file_buff, file_ext_name, null);
    }
    /**
     * @title deleteFile
     * @description 删除文件
     * @author yy
     * @date 2017年7月11日 上午10:54:38
     * @param remote_filename
     * @return
     * @throws Exception
     * @return boolean
     */
    private static boolean delete_file(String remote_filename) throws Exception
    {
        String group_name=remote_filename.substring(0, remote_filename.indexOf("/"));
        remote_filename = remote_filename.substring(remote_filename.indexOf("/")+1);
        int i = storageClient1.delete_file(group_name, remote_filename);
        if(i==0)
            return true;
        return false;
    }
    /**
     * @title uploadFile
     * @description  按名称上传文件
     * @author yy
     * @date 2017年7月11日 上午10:42:13
     * @param local_filename 文件名
     * @param file_ext_name 后缀名
     * @return
     * @throws Exception
     * @return String
     */
    public static String uploadFile(String local_filename, String file_ext_name) {

        try {
            return storageClient1.upload_file1(local_filename, file_ext_name, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    /**
     * @title downloadFileAsString
     * @description 下载文件 ，返回默认编码 格式的字符串
     * @author yy
     * @date 2017年7月11日 上午10:43:39
     * @param remote_filename 文件名
     * @return
     * @throws Exception
     * @return String
     */
    public static String downloadString(String remote_filename)
    {
        try {
//            String group_name=remote_filename.substring(0, remote_filename.indexOf("/"));
//            remote_filename = remote_filename.substring(remote_filename.indexOf("/")+1);
//            return new String(storageClient1.download_file(group_name, remote_filename));
            return HttpUrlPost.sendGet(remote_filename,"");
//            return HttpTools.post(remote_filename, "");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    /**
     * @title downloadFile
     * @description 返回字节流
     * @author yy
     * @date 2017年7月11日 上午11:04:44
     * @param remote_filename
     * @return
     * @return byte[]
     */
    public static byte[] downloadFile(String remote_filename)
    {
        try {
            String group_name=remote_filename.substring(0, remote_filename.indexOf("/"));
            remote_filename = remote_filename.substring(remote_filename.indexOf("/")+1);
            return storageClient1.download_file(group_name, remote_filename);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * @title uploadBytes
     * @description 按字节流上传
     * @author yy
     * @date 2017年7月12日 下午2:22:13
     * @param bytes
     * @return
     * @return String
     */
    public static String uploadBytes(byte[] bytes) {
        try {
            return uploadFile(bytes, "");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * @title uploadFile
     * @description 存储字符串
     * @author yy
     * @date 2017年7月11日 上午10:48:30
     * @return
     * @return String
     */
    public static String uploadString(String stringContent) {
        try {
            return uploadFile(stringContent.getBytes(), "");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @title uploadFile
     * @description 存储多个字符串，要么全部成功，要么全部失败
     * @author yy
     * @date 2017年7月11日 上午10:48:30
     * @param stringFile
     * @return
     * @return List<FilePath>
     */
    public static List<String> uploadStrings(String...stringContent) {
        List<String> list  = new ArrayList<String>();
        try {
            for (String string : stringContent) {
                list.add(uploadFile(string.getBytes(), ""));
            }
            return list;
        } catch (Exception e) {
            try {//删掉已经成功上传的
                for (String string : list) {
                    delete_file(string);
                }
            } catch (Exception e2) {
                // TODO: handle exception
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * @title deleteFile
     * @description 删除文件
     * @author yy
     * @date 2017年7月11日 上午11:07:05
     * @param remote_filename
     * @return
     * @return boolean
     */
    public static boolean deleteFile(String remote_filename)
    {
        try {
            String group_name=remote_filename.substring(0, remote_filename.indexOf("/"));
            remote_filename = remote_filename.substring(remote_filename.indexOf("/")+1);
            int i = storageClient1.delete_file(group_name, remote_filename);
            if(i==0)
                return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }


    public static void main(String[] args) {

//    	FixedThreadPool fp=new FixedThreadPool()
        for (int i = 0; i < 1; i++) {



            //运行main时，注释掉 static代码块
            new FDFSNewUtil().init("D:\\fdfs_client.conf");
            try {
                boolean b = FDFSNewUtil.delete_file("group1/M01/00/35/wKgBP1ma04GAHdgNAAAff5K7hls6629483");
                System.out.println(b);

            }catch (Exception e){
                e.printStackTrace();
            }
//            System.out.println(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
//            String uploads="[{\"phone\":\"17602151396\",\"name\":\"测试号\"},{\"phone\":\"13569936336\",\"name\":\"测试二号\"},{\"phone\":\"17301835064\",\"name\":\"本机号码\"},{\"phone\":\"13995972134\",\"name\":\"舒丹\"},{\"phone\":\"15871215313\",\"name\":\"大”+姨妈\"},{\"phone\":\"13627238533\",\"name\":\"郑郑306\"},{\"phone\":\"7339998\",\"name\":\"新视界\"},{\"phone\":\"13197015991\",\"name\":\"石成效\"},{\"phone\":\"13545217476\",\"name\":\"晏紫红\"},{\"phone\":\"15629158693\",\"name\":\">”+左思琦\"},{\"phone\":\"13528599974\",\"name\":\"伍楠\"},{\"phone\":\"15629707464\",\"name\":\"明大姐\"},{\"phone\":\"18202761399\",\"name\":\"王伟\"},{\"phone\":\"15623596016\",\"name\":\"柳宛成\"},{\"phone\":\"13554055299\",\"name\":\"”+王盼盼\"},{\"phone\":\"15629159110\",\"name\":\"刘曼\"},{\"phone\":\"18271879953\",\"name\":\"文工团敖小青\"},{\"phone\":\"15871143279\",\"name\":\"何君\"},{\"phone\":\"13377914931\",\"name\":\"李珣妈\"},{\"phone\":\"15207106431\",\"n”+ame\":\"张小娴\"},{\"phone\":\"18772348406\",\"name\":\"王鹏\"},{\"phone\":\"15572965896\",\"name\":\"外婆\"},{\"phone\":\"13419562337\",\"name\":\"李珣\"},{\"phone\":\"15623895153\",\"name\":\"孙海一\"},{\"phone\":\"18062688390\",\"nam”+e\":\"阮芳云\"},{\"phone\":\"18503002215\",\"name\":\"邢丹\"},{\"phone\":\"13277040640\",\"name\":\"团副助理冉晓\"},{\"phone\":\"13098846024\",\"name\":\"刘金菊\"},{\"phone\":\"15107147563\",\"name\":\"校卫队王蜀颖\"},{\"phone\":\"131”+47236299\",\"name\":\"郑瑾\"},{\"phone\":\"18202730359\",\"name\":\"钱军明\"},{\"phone\":\"15629638625\",\"name\":\"周桂红\"},{\"phone\":\"15629157379\",\"name\":\"严媛媛\"},{\"phone\":\"13597625563\",\"name\":\"张秀芳\"},{\"phone\":\"1”+8271391491\",\"name\":\"杨涛\"},{\"phone\":\"15527097901\",\"name\":\"徐岚茜\"},{\"phone\":\"18202719022\",\"name\":\"王婷\"},{\"phone\":\"15623192828\",\"name\":\"团副钟怀\"},{\"phone\":\"13476247229\",\"name\":\"程志\"},{\"phone\":\"1”+8707167761\",\"name\":\"助理张国庆\"},{\"phone\":\"13237188252\",\"name\":\"黄桂容\"},{\"phone\":\"15071435438\",\"name\":\"文工团王婧洁\"},{\"phone\":\"15927191079\",\"name\":\"川菜馆\"},{\"phone\":\"13627285955\",\"name\":\"宣传部”+部长\"},{\"phone\":\"18671437269\",\"name\":\"姨父\"},{\"phone\":\"18771963208\",\"name\":\"赵登翔\"},{\"phone\":\"13476158731\",\"name\":\"万张亮\"},{\"phone\":\"15107157499\",\"name\":\"外联部部长\"},{\"phone\":\"13277054718\",\"nam”+e\":\"青协会长朱晗\"},{\"phone\":\"15629160758\",\"name\":\"汪晴\"},{\"phone\":\"15102719505\",\"name\":\"万鑫\"},{\"phone\":\"18271859654\",\"name\":\"组织部邓晋辉\"},{\"phone\":\"15972147020\",\"name\":\"学生会秘书部张恒\"},{\"pho”+ne\":\"18271418206\",\"name\":\"曾慧\"},{\"phone\":\"18062010818\",\"name\":\"电商老师江靖\"},{\"phone\":\"15671682018\",\"name\":\"李名鹏\"},{\"phone\":\"15897770385\",\"name\":\"袁鸣\"},{\"phone\":\"520007\",\"name\":\"钱包小助手\"},”+{\"phone\":\"13627286685\",\"name\":\"邓先娥\"},{\"phone\":\"15387240197\",\"name\":\"姑父\"},{\"phone\":\"15334285403\",\"name\":\"送冰\"},{\"phone\":\"13597748323\",\"name\":\"谈荣强\"},{\"phone\":\"13635181900\",\"name\":\"赵灯\"},{\"”+phone\":\"13397231476\",\"name\":\"石清\"},{\"phone\":\"13177318268\",\"name\":\"小爸\"},{\"phone\":\"18062288458\",\"name\":\"小爸\"},{\"phone\":\"15623588229\",\"name\":\"唐继龙\"},{\"phone\":\"13476245313\",\"name\":\"唐继龙\"},{\"ph”+one\":\"13986583676\",\"name\":\"小姨\"},{\"phone\":\"13098396968\",\"name\":\"小姨\"},{\"phone\":\"18040557225\",\"name\":\"徐能飞\"},{\"phone\":\"15172042280\",\"name\":\"徐能飞\"},{\"phone\":\"13656609747\",\"name\":\"李金哥\"},{\"ph”+one\":\"15972375808\",\"name\":\"李金哥\"},{\"phone\":\"18823719180\",\"name\":\"李金哥\"},{\"phone\":\"15972526404\",\"name\":\"周洁\"},{\"phone\":\"15717181530\",\"name\":\"周洁\"},{\"phone\":\"18771656663\",\"name\":\"裴婷婷\"},{\"ph”+one\":\"13100714473\",\"name\":\"裴婷婷\"},{\"phone\":\"18571074310\",\"name\":\"徐哲\"},{\"phone\":\"18717144470\",\"name\":\"徐哲\"},{\"phone\":\"15629159396\",\"name\":\"袁文贤\"},{\"phone\":\"15802768502\",\"name\":\"袁文贤\"},{\"ph”+one\":\"18271852736\",\"name\":\"夏婉\"},{\"phone\":\"13871088339\",\"name\":\"石院长\"},{\"phone\":\"15629162755\",\"name\":\"易小梅\"},{\"phone\":\"15071333926\",\"name\":\"梁鑫东\"},{\"phone\":\"15527097978\",\"name\":\"李梦菲\"},{\"”+phone\":\"18071231609\",\"name\":\"奶奶\"},{\"phone\":\"18571554511\",\"name\":\"魏振宇\"},{\"phone\":\"13995688656\",\"name\":\"马院长\"},{\"phone\":\"13807257469\",\"name\":\"邱熊\"},{\"phone\":\"6217995200017540037\",\"name\":\"hh\"”+},{\"phone\":\"13886196748\",\"name\":\"夏教练\"},{\"phone\":\"15072321939\",\"name\":\"地大驾照\"},{\"phone\":\"13177336819\",\"name\":\"余国栋\"},{\"phone\":\"13451050880\",\"name\":\"赵宇\"},{\"phone\":\"13508343086\",\"name\":\"田>”+溪瑶\"},{\"phone\":\"13476289658\",\"name\":\"赵紫君\"},{\"phone\":\"15771183091\",\"name\":\"舒雯\"},{\"phone\":\"13476125319\",\"name\":\"刘进文\"},{\"phone\":\"15629159073\",\"name\":\"余意\"},{\"phone\":\"18121357390\",\"name\":\"张”+秋爽\"},{\"phone\":\"18616739716\",\"name\":\"朱桦\"},{\"phone\":\"13162696662\",\"name\":\"师傅\"},{\"phone\":\"13375717998\",\"name\":\"章建春\"},{\"phone\":\"13127801186\",\"name\":\"杨越\"},{\"phone\":\"13816648386\",\"name\":\"李晨”+\"},{\"phone\":\"18221549841\",\"name\":\"肖明宪\"},{\"phone\":\"18202126041\",\"name\":\"夏婉上\"},{\"phone\":\"15704948104\",\"name\":\"张亚倩\"},{\"phone\":\"13816517742\",\"name\":\"刘媛\"},{\"phone\":\"13816648082\",\"name\":\"余意”+\"},{\"phone\":\"13651736698\",\"name\":\"房东r\"},{\"phone\":\"15827060352\",\"name\":\"王振\"},{\"phone\":\"18651221032\",\"name\":\"秦闻\"},{\"phone\":\"18221481449\",\"name\":\"王芳\"},{\"phone\":\"13023239798\",\"name\":\"李伟东\"},”+{\"phone\":\"18146625330\",\"name\":\"徐建鹏\"},{\"phone\":\"18321524225\",\"name\":\"谢况\"},{\"phone\":\"18601624399\",\"name\":\"应俊\"},{\"phone\":\"15623126937\",\"name\":\"徐能飞新\"},{\"phone\":\"18668218880\",\"name\":\"陈总\"},”+{\"phone\":\"13061887327\",\"name\":\"左思琦上\"},{\"phone\":\"18120176364\",\"name\":\"李秀梅\"},{\"phone\":\"17717298699\",\"name\":\"王坤\"},{\"phone\":\"17786039486\",\"name\":\"王锐\"},{\"phone\":\"18507237773\",\"name\":\"卢涛\"},”+{\"phone\":\"13774249975\",\"name\":\"未知\"},{\"phone\":\"18911521570\",\"name\":\"曾燕\"},{\"phone\":\"13611773764\",\"name\":\"吴文勇\"},{\"phone\":\"18638907189\",\"name\":\"卢晨阳\"},{\"phone\":\"13764021315\",\"name\":\"胡圣迪\"},”+{\"phone\":\"15239739114\",\"name\":\"妈\"},{\"phone\":\"18736094229\",\"name\":\"王振胜\"},{\"phone\":\"13838902289\",\"name\":\"老爸\"},{\"phone\":\"18501628460\",\"name\":\"刘子源\"},{\"phone\":\"18149752021\",\"name\":\"李利君\"},{\"”+phone\":\"18203632459\",\"name\":\"郭向阳\"},{\"phone\":\"18236817059\",\"name\":\"黄永浩\"},{\"phone\":\"15010540239\",\"name\":\"苗子\"},{\"phone\":\"15168305983\",\"name\":\"王永宽\"},{\"phone\":\"18221062745\",\"name\":\"胡玉龙\"},”+{\"phone\":\"18221153035\",\"name\":\"王亚坤\"},{\"phone\":\"15936513176\",\"name\":\"冯文华\"},{\"phone\":\"15238357460\",\"name\":\"王剑飞\"},{\"phone\":\"18268082989\",\"name\":\"贺玉乐\"},{\"phone\":\"15221071335\",\"name\":\"徐翔>”+斌\"},{\"phone\":\"15729053873\",\"name\":\"曹前飞\"},{\"phone\":\"15239768131\",\"name\":\"曹大伟\"},{\"phone\":\"13782785970\",\"name\":\"阳阳\"},{\"phone\":\"17316380391\",\"name\":\"哥\"},{\"phone\":\"13817562051\",\"name\":\"眼镜店”+联系人\"},{\"phone\":\"18321889505\",\"name\":\"宦定一\"},{\"phone\":\"18803772992\",\"name\":\"方宏业\"},{\"phone\":\"02156701002\",\"name\":\"溪田HR\"},{\"phone\":\"13681799678\",\"name\":\"尹辰健\"},{\"phone\":\"18672012306\",\"nam”+e\":\"specialgirl\"},{\"phone\":\"18850223101\",\"name\":\"陈家松\"},{\"phone\":\"17190013039\",\"name\":\"莫星惘\"},{\"phone\":\"18607092460\",\"name\":\"林平君\"},{\"phone\":\"13903077931\",\"name\":\"小四\"},{\"phone\":\"1851679965”+1\",\"name\":\"小春\"},{\"phone\":\"15618918763\",\"name\":\"邱硕\"},{\"phone\":\"13265499830\",\"name\":\"思泽\"},{\"phone\":\"18801613166\",\"name\":\"1号店莫婷\"},{\"phone\":\"13916075520\",\"name\":\"韦亭软件人事\"},{\"phone\":\"021”+51865061\",\"name\":\"健客网\"},{\"phone\":\"01089193313\",\"name\":\"直聘招聘助手\"},{\"phone\":\"13253678873\",\"name\":\"陈腾飞\"},{\"phone\":\"03747163792\",\"name\":\"黄永浩\"},{\"phone\":\"18739966431\",\"name\":\"曹振邦\"},{\"p”+hone\":\"15203908986\",\"name\":\"二帅\"},{\"phone\":\"15514712376\",\"name\":\"二姨\"},{\"phone\":\"17707410969\",\"name\":\"楼上男邻居\"},{\"phone\":\"4006701855\",\"name\":\"苹果开发客服\"},{\"phone\":\"13381558879\",\"name\":\"赵>”+进\"},{\"phone\":\"13564007713\",\"name\":\"束晨\"},{\"phone\":\"15938127102\",\"name\":\"小姑\"},{\"phone\":\"18321302758\",\"name\":\"嫂子\"},{\"phone\":\"15720826020\",\"name\":\"杨云华\"},{\"phone\":\"13570665702\",\"name\":\"张琦\"}”+,{\"phone\":\"13341814726\",\"name\":\"薛鹏飞\"},{\"phone\":\"18616363245\",\"name\":\"王鸽\"},{\"phone\":\"18917856438\",\"name\":\"上海电信网络维修\"}]";
//            String str=FDFSNewUtil.uploadString(uploads);
//            System.out.println(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS")+"str>"+str);
//            String reStr=FDFSNewUtil.downloadString(str);
//            System.out.println("reStr:"+reStr);


//            String filePath =FDFSUtil.uploadFile("C:\\Users\\yangyong\\Desktop\\newtxt.txt", "txt");
//            System.out.println(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS")+"filePath>"+filePath);
//            List<String> list=FDFSNewUtil.uploadStrings("{\"a\":\"av\",\"b\":\"bv\",\"c\":\"cv\"}","{\"a\":\"av\",\"b\":\"bv\",\"c\":\"cv\"}");
//            System.out.println(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS")+"list>"+list.toArray().toString());
////            String reStr=FDFSNewUtil.downloadString(str);
////            System.out.println(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS")+"reStr>"+reStr);
////            byte[] bs=FDFSNewUtil.downloadFile(str);
////            System.out.println(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS")+"bs>"+bs);
//            System.out.println(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS")+"delete str>"+FDFSNewUtil.deleteFile(str));
////            System.out.println(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS")+"delete filePath>"+FDFSNewUtil.deleteFile(filePath));
//            for (String s : list) {
//                System.out.println(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS")+"delete list.s>"+FDFSNewUtil.deleteFile(s));
//            }


        }
    }
}
