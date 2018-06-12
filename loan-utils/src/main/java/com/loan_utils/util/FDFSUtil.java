package com.loan_utils.util;

import java.util.ArrayList;
import java.util.List;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;


/**
 * @project risk-common
 * @author yy
 * @date 2017年7月11日 上午10:32:31
 * @description TODO fdfs文件存储 工具类 
 * @tag 
 * @company 上海金互行金融信息服务有限公司
 */
public class FDFSUtil {
	private static TrackerClient trackerClient = null;
    private static TrackerServer trackerServer = null;
    private static StorageServer storageServer = null;
    //使用StorageClient1进行上传
    private static StorageClient1 storageClient1 = null;

//    static {
//    		new FDFSUtil().init("fdfs_client.conf");
//		}
    public void init(String config)
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

    		 return storageClient1.upload_file1(file_buff, file_ext_name, null);
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
    		String group_name=remote_filename.substring(0, remote_filename.indexOf("/"));
        	remote_filename = remote_filename.substring(remote_filename.indexOf("/")+1);
            return new String(storageClient1.download_file(group_name, remote_filename));
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
     * @title uploadFile 
     * @description 存储字符串
     * @author yy
     * @date 2017年7月11日 上午10:48:30
     * @param stringFile
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
    	//运行main时，注释掉 static代码块
    	new FDFSUtil().init("E:/workspace/risk/risk-root/risk-api/deployEnv/dev/fdfs_client.conf");
    	String str=FDFSUtil.uploadString("{\"a\":\"av\",\"b\":\"bv\",\"c\":\"cv\"}");
    	System.out.println("str>"+str);
    	String filePath =FDFSUtil.uploadFile("C:\\Users\\yangyong\\Desktop\\newtxt.txt", "txt");
    	System.out.println("filePath>"+filePath);
		List<String> list=FDFSUtil.uploadStrings("{\"a\":\"av\",\"b\":\"bv\",\"c\":\"cv\"}","{\"a\":\"av\",\"b\":\"bv\",\"c\":\"cv\"}");
		System.out.println("list>"+list.toArray().toString());
		String reStr=FDFSUtil.downloadString(str);
        System.out.println("reStr>"+reStr);
        byte[] bs=FDFSUtil.downloadFile(str);
        System.out.println("bs>"+bs);
        System.out.println("delete str>"+FDFSUtil.deleteFile(str));
        System.out.println("delete filePath>"+FDFSUtil.deleteFile(filePath));
        for (String s : list) {
        	System.out.println("delete list.s>"+FDFSUtil.deleteFile(s));
		}
        
        
	}
}
