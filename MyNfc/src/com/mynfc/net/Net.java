package com.mynfc.net;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;

import com.mynfc.data.BaseBean;
import com.mynfc.tool.Config;
import com.mynfc.tool.DebugLog;
import com.mynfc.tool.Tools;
import com.mynfc.xmlparser.BaseHandler;
import com.mynfc.xmlparser.BaseParser;


public class Net extends Thread {

    public final static int NET_SETERROR = 998;// 网络设置问题
    public final static int NET_ERROR = 999;// 网络异常

    private int NET_OK;// 获取网络数据成功，用于跟UI交互的标通

    private String netURL;// 请求网络地址
    private BaseHandler beanHandler;// 解析XML
    private BaseBean bean;// 数据实例
    private Handler hdler;// 传通Message
    private int imgIndex = -1;//图片索引

    private String postCon;//Post方式下，参数通

    //1:Get|2:Post|3:File方式，即区分是提交客户端数据接口(POST)，还是其他URL接口(GET),下载APK(File)
    private int channeltype = 0;
    private final int TYPE_GET = 1;
    private final int TYPE_POST = 2;
    private final int TYPE_FILE = 3;
    private final int TYPE_IMG = 4;

    private String filePath;//下载文件存放路径
    private final int BUFFER = 1024;

    private Context context;
    private int netType = 0;// 0:不可用网络通1:wifi通：cmwap
    private HttpURLConnection httpconnection;
    private final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
    private ByteArrayInputStream resultStream = null;

    //网络类型通:Wifi、cnnet;2:CMWap;0:无网通
    private final int WIFIAndCMNET = 1;
    private final int CMWAP = 2;
    private final int NONET = 0;

    private String content = "";// 获取到的原始数据
    private boolean reGetData = false;// 再次循环
    private int reConnectTimes = 3;// 通��重复3次连通

    private int connnetTime = 5000;//超时时间

    /** CM_SIM - 移动通UN_SIM - 联通通 CT_SIM - 电信通*/
    private final int CM_SIM = 11, UN_SIM = 12, CT_SIM = 13;
    /** 打印标识 */
    private final String TAG = "Net";

    //Post方式时构造函数_HttpURLConnection方式联网,真机上采用此方法
    public Net(Context ctt, String url, String postCon, BaseHandler beanHD, Handler handler, int result) {
        this.context = ctt;
        this.netURL = url;
        this.postCon = postCon;
        this.beanHandler = beanHD;
        this.hdler = handler;
        this.NET_OK = result;
        this.channeltype = TYPE_POST;
    }

    //Get方式时构造函通
    public Net(Context ctt, String url, BaseHandler beanHD, Handler handler, int result) {
        this.context = ctt;
        this.netURL = url;
        this.beanHandler = beanHD;
        this.hdler = handler;
        this.NET_OK = result;
        this.channeltype = TYPE_GET;
    }

    //下载APK
    public Net(Context ctt, String url, String filePath, Handler handler, int result) {
        this.context = ctt;
        this.netURL = url;
        this.hdler = handler;
        this.NET_OK = result;
        this.filePath = filePath;
        this.channeltype = TYPE_FILE;
    }

    //下载图片
    public Net(Context ctt, String url, Handler handler, int result) {
        this.context = ctt;
        this.netURL = url;
        this.hdler = handler;
        this.NET_OK = result;
        this.channeltype = TYPE_IMG;
    }

    //下载图片--图片索引
    public Net(Context ctt, String url, Handler handler, int result, int imgIndex) {
        this.context = ctt;
        this.netURL = url;
        this.hdler = handler;
        this.NET_OK = result;
        this.channeltype = TYPE_IMG;
        this.imgIndex = imgIndex;
    }

    //Get方式 & 缓存数据时构造函通
    public Net(Context ctt, String url, BaseHandler beanHD, Handler handler, int result, boolean cachesFlag) {
        this.context = ctt;
        this.netURL = url;
        this.beanHandler = beanHD;
        this.hdler = handler;
        this.NET_OK = result;
        this.channeltype = TYPE_GET;
    }

    public void run() {
        //        if(true == cachesDataType){//缓存类型的接通
        //            String cachesValue = CachesBean.getCachesBean().getCachesData(this.netURL);
        //            if(cachesValue != null){
        //                //获取缓存数据进行解析、通知界通
        //                content = CachesBean.getCachesBean().getLegalCachesData(cachesValue);
        //                resultStream = new ByteArrayInputStream(content.getBytes());
        //                ParserData parserData = new ParserData();
        //                bean = parserData.parser(resultStream, beanHandler);
        //                try {
        //                    resultStream.close();
        //                } catch (IOException e) {
        //                    e.printStackTrace();
        //                }
        //                
        //                notifyUI(NET_OK);
        //            }else{
        //                getDataByNet();
        //            }
        //        }else{            
        getDataByNet();
        //        }
    }

    /**
     * 通过网络获取数据
     * 
     * @create_time 2012-3-28 上午11:19:13
     */
    private void getDataByNet() {
        //网络环境(WiFi、CMWap、CMNet)
        netType = getNetTyle();
        if (netType > 0) {//网络设置
            do {
                reGetData = false;// 每次通��获取网络，都将其置为false;
                reConnectTimes--;

                //获取网络连接阶段
                boolean getHttpConnectStatue = false;
                if (channeltype == TYPE_GET || channeltype == TYPE_FILE || channeltype == TYPE_IMG) {
                    getHttpConnectStatue = get();
                } else if (channeltype == TYPE_POST) {
                    getHttpConnectStatue = post();
                }
                //获取网络数据阶段
                if (getHttpConnectStatue == true) {
                    if ((channeltype == TYPE_GET) || (channeltype == TYPE_POST)) {
                        if (getNetContent()) {
                            if (content.indexOf("<rss version") >= 0) {
                                if (resultStream != null) {
                                    ParserData parserData = new ParserData();
                                    bean = parserData.parser(resultStream, beanHandler);
                                    try {
                                        resultStream.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    //                                    //缓存数据
                                    //                                    if(true == this.cachesDataType){
                                    //                                        CachesBean.getCachesBean().saveNewCachesData(this.netURL, content);
                                    //                                    }

                                    notifyUI(NET_OK);
                                } else {
                                    notifyUI(NET_ERROR);
                                }
                            } else {
                                reGetData = true;// 再次连接
                            }
                        } else {
                            notifyUI(NET_ERROR);
                        }
                    } else if (channeltype == TYPE_FILE) {
                        if (getNetFile()) {
                            notifyUI(NET_OK);
                        } else {
                            notifyUI(NET_ERROR);
                        }
                    } else if (channeltype == this.TYPE_IMG) {
                        Object imgObj = getNetImage();
                        int i = 0;
                        while (imgObj == null && i < 3) {
                            imgObj = getNetImage();
                            i++;
                        }

                        if (imgObj != null) {
                            if(this.imgIndex >= 0){   
                                notifyUI(NET_OK, imgObj, this.imgIndex);
                            }else{
                                notifyUI(NET_OK, imgObj);
                            }
                        } else {
                            notifyUI(NET_ERROR);
                        }
                    }
                } else {
                    notifyUI(NET_ERROR);
                }
            } while ((reGetData == true) && (reConnectTimes > 0));

            //若发起了规定次数的网络连通还是没获取到数据
            if (reGetData && reConnectTimes == 0) {
                notifyUI(NET_ERROR);
            }
        } else {
            notifyUI(NET_SETERROR);
        }
    }

    private BaseBean getNetBean() {
        return bean;
    }

    /**
     * 将联网结果通知UI
     */
    private void notifyUI(int resultState) {
        Message msg = new Message();
        msg.what = resultState;
        msg.obj = getNetBean();
        hdler.sendMessage(msg);
    }

    /**
     * 将下载进度通知UI
     */
    private void notifyUI(int resultState, double percent) {
        Message msg = new Message();
        msg.what = resultState;
        msg.obj = percent;
        hdler.sendMessage(msg);
    }

    /**
     * 将下载图片通知UI
     */
    private void notifyUI(int resultState, Object object) {
        Message msg = new Message();
        msg.what = resultState;
        msg.obj = object;
        hdler.sendMessage(msg);
    }

    /**
     * 将下载图片通知UI
     * @param resultState 响应ID
     * @param object 
     * @param imgIndex 图片标识
     * @create_time 2012-6-22 上午10:55:17
     */
    private void notifyUI(int resultState, Object object, int imgIndex) {
        Message msg = new Message();
        msg.what = resultState;
        msg.arg1 = imgIndex;
        msg.obj = object;
        hdler.sendMessage(msg);
        
        DebugLog.logOnFile(TAG, "%s", netURL + "|" + netType + "|" + reConnectTimes + "\r\n"
                + "---imgIndex = " + imgIndex + "--");
    }

    // 获取Mobile网络下的cmwap、cmnet
    private int getCurrentApnInUse() {
        int type = NONET;

        Cursor cursor = context.getContentResolver().query(PREFERRED_APN_URI, new String[] { "_id", "apn", "type" },
                null, null, null);
        cursor.moveToFirst();
        int counts = cursor.getCount();
        if (counts != 0) {//适配平板外挂3G模块情况
            if (!cursor.isAfterLast()) {
                String apn = cursor.getString(1);
                int simType = getMobileType();

                //#777、ctnet 都是中国电信定制机接入点名称,中国电信的接入点：Net、Wap都采用Net即非代理方式联网即可
                //internet 是模拟器上模拟接入点名称
                if (apn.equalsIgnoreCase("cmnet") || apn.equalsIgnoreCase("3gnet") || apn.equalsIgnoreCase("uninet")
                        || apn.equalsIgnoreCase("#777") || apn.equalsIgnoreCase("ctnet")
                        || apn.equalsIgnoreCase("internet")) {
                    type = WIFIAndCMNET;
                } else if (apn.equalsIgnoreCase("cmwap") || apn.equalsIgnoreCase("3gwap")
                        || apn.equalsIgnoreCase("uniwap")) {
                    type = CMWAP;
                } else if (simType == CT_SIM) {
                    //若上面的接入点都不匹配，则看是否是中国电信的机器，是则全部采用直连方式通测试通��：三星i909，apn为空
                    type = WIFIAndCMNET;
                }
            } else {
                //适配中国电信定制通如海信EG968,上面方式获取的cursor为空，所以换种方通
                Cursor c = context.getContentResolver().query(PREFERRED_APN_URI, null, null, null, null);
                c.moveToFirst();
                String user = c.getString(c.getColumnIndex("user"));
                if (user.equalsIgnoreCase("ctnet")) {
                    type = WIFIAndCMNET;
                }
                c.close();
            }
        } else {
            type = WIFIAndCMNET;//平板外挂3G,采用非代理方式上通
        }
        cursor.close();

        return type;
    }

    // 获取手机的网络类型wifi、mobile
    private int getNetTyle() {
        int Type = NONET;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null) {
            String typeName = info.getTypeName(); // wifi,mobile
            if (!typeName.equalsIgnoreCase("wifi")) {
                Type = getCurrentApnInUse();
            } else {
                Type = WIFIAndCMNET;
            }
        }

        return Type;
    }

    /**
     * 获取手机卡类通移动、联通通电信
     * 
     */
    private int getMobileType() {
        int SIMType = 0;

        TelephonyManager iPhoneManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String iNumeric = iPhoneManager.getSimOperator();
        if (iNumeric.length() > 0) {
            if (iNumeric.equals("46000") || iNumeric.equals("46002")) {
                SIMType = CM_SIM;// 中国移动
            } else if (iNumeric.equals("46001")) {
                SIMType = UN_SIM;// 中国联通
            } else if (iNumeric.equals("46003")) {
                SIMType = CT_SIM;// 中国电信
            }
        }

        return SIMType;
    }

    // 根据网络类型获用Get方式取数据放入到httpconnection
    private boolean get() {
        boolean getFlag = false;

        URL getUrl = null;
        try {
            if (netType == WIFIAndCMNET) {
                try {
                    getUrl = new URL(this.netURL);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                httpconnection = (HttpURLConnection) getUrl.openConnection();
            } else {
                try {
                    getUrl = new URL(getAgentURL(this.netURL));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                httpconnection = (HttpURLConnection) getUrl.openConnection();
                httpconnection.setRequestProperty("X-Online-Host", getDomain(this.netURL));
            }
            httpconnection.setUseCaches(false);
            httpconnection.setRequestMethod("GET");
            httpconnection.setConnectTimeout(connnetTime);
            httpconnection.connect();
            getFlag = true;
        } catch (IOException e) {
        } catch (IllegalArgumentException exp) {
            exp.printStackTrace();
        } catch (SecurityException exp) {
            exp.printStackTrace();
        } catch (Exception exp) {
            exp.printStackTrace();
        }

        return getFlag;
    }

    // 从httpconnection中获取网络数通
    private boolean getNetContent() {
        boolean getNetDataFlag = false;// 获取数据是否正常
        StringBuffer conbuffer = new StringBuffer();

        try {
            InputStream inputstream = httpconnection.getInputStream();
            DataInputStream dis = new DataInputStream(inputstream);
            int a;
            while ((a = dis.read()) != -1) {
                conbuffer.append((char) a);
            }

            dis.close();
            dis = null;
            inputstream.close();
            inputstream = null;

            content = new String(conbuffer.toString().getBytes("ISO8859-1"), "UTF-8");

            if (content.indexOf("<br/>") > 0) {
                content = content.replaceAll("<br/>", "\n");// 去掉"<br/>"
            }
            if (content.indexOf("<br />") > 0) {
                content = content.replaceAll("<br />", "\n");// 去掉"<br />"
            }
            if (content.indexOf("&nbsp") > 0) {
                content = content.replaceAll("&nbsp", "");// 去掉"&nbsp"
            }

            resultStream = new ByteArrayInputStream(content.getBytes());
            getNetDataFlag = true;
        } catch (InterruptedIOException e) {
        } catch (NullPointerException e) {
        } catch (IOException e) {
        } catch (IllegalArgumentException exp) {
        } catch (SecurityException exp) {
        } catch (Exception exp) {
        }

        DebugLog.logOnFile(TAG, "%s", netURL + "|" + netType + "|" + reConnectTimes + "\r\n" + content);

        return getNetDataFlag;
    }

    // 获取代理地址
    private String getAgentURL(String iURL) {
        int start = "http://".length() + 1;
        int end = iURL.indexOf("/", start) + 1;
        String agentURL = "http://10.0.0.172:80/" + iURL.substring(end);

        return agentURL;
    }

    // 获取域名
    private String getDomain(String iURL) {
        int start = "http://".length();
        int end = iURL.indexOf("/", start);
        String domain = iURL.substring(start, end);

        return domain;
    }

    //根据网络类型获用Post方式取数据放入到httpconnection
    private boolean post() {
        boolean postFlag = false;

        URL postUrl = null;
        try {
            if (netType == WIFIAndCMNET) {
                try {
                    postUrl = new URL(this.netURL);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                httpconnection = (HttpURLConnection) postUrl.openConnection();
            } else {
                try {
                    postUrl = new URL(getAgentURL(this.netURL));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                httpconnection = (HttpURLConnection) postUrl.openConnection();
                httpconnection.setRequestProperty("X-Online-Host", getDomain(this.netURL));
            }
            httpconnection.setRequestProperty("connection", "keep-alive");
            httpconnection.setRequestMethod("POST");
            httpconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpconnection.setRequestProperty("Content-Length", Integer.toString(postCon.getBytes().length));
            httpconnection.setDoInput(true);
            httpconnection.setDoOutput(true);
            httpconnection.setUseCaches(false);
            httpconnection.setConnectTimeout(connnetTime);
            OutputStream out = httpconnection.getOutputStream();
            DataOutputStream data = new DataOutputStream(out);
            data.write(postCon.getBytes());
            data.flush();
            data.close();
            out.close();
            httpconnection.connect();
            postFlag = true;
        } catch (Exception exp) {
        }

        return postFlag;
    }

    //下载文件
    private boolean getNetFile() {
        boolean getNetFileFlag = false;// 获取数据是否正常

        try {
            InputStream in = httpconnection.getInputStream();
            FileOutputStream out = new FileOutputStream(new File(filePath));
            int totalLen = httpconnection.getContentLength();//整个包大小

            byte[] b = new byte[BUFFER];
            int writedLen = 0;//累计下载长度
            int len = 0;//每次下载长度
            
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
                
                writedLen = writedLen + len;
                double percent = (double)writedLen / (double)totalLen;
                
                //notifyUI(ApkFile.DownLoadPercent_OK, percent * 100);
            }
            in.close();
            out.close();
            
            getNetFileFlag = true;
        }catch (Exception exp) {
        } 

        return getNetFileFlag;
    }

    //下载图片
    private Object getNetImage() {

        Bitmap bitmap = null;// 获取数据是否正常
        
//        boolean existImageFlag = false;
//        if(Config.updateImgFlag == false){
//        	if(Tools.getSDPath() != null){
//            	String path = Tools.getSDPath() + Config.SDCachesImageFolder + this.netURL.replace("http://", "").replace("/", "_");
//    			File dirFile = new File(path);
//    			if (dirFile.exists()) {
//    				bitmap = BitmapFactory.decodeFile(path);
//    				existImageFlag = true;
//    		     }
//    		}
//        }
//
//        if(!existImageFlag){
//        	try {
//                InputStream is = httpconnection.getInputStream();
//                int length = (int) httpconnection.getContentLength();
//                if (length != -1) {
//                    byte[] imgData = new byte[length];
//                    byte[] buffer = new byte[512];
//                    int readLen = 0;
//                    int destPos = 0;
//                    while ((readLen = is.read(buffer)) > 0) {
//                        System.arraycopy(buffer, 0, imgData, destPos, readLen);
//                        destPos += readLen;
//                    }
//                    bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
//                    saveFile(bitmap, this.netURL.replace("http://", "").replace("/", "_"));
//                    
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        return bitmap;
        
    }
    
//	/**
//	 * 缓存图片
//	 * 
//	 * @param bm
//	 * @param fileName
//	 * @throws IOException
//	 */
//	public void saveFile(Bitmap bm, String fileName) throws IOException {
//		if(Tools.getSDPath() != null){
//			String path = Tools.getSDPath() + Config.SDCachesImageFolder;
//			File dirFile = new File(path);
//			if (!dirFile.exists()) {
//				dirFile.mkdir();
//			}
//			File myCaptureFile = new File(path + fileName);
//			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
//			bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
//			bos.flush();
//			bos.close();
//		}
//		
//	}
}
