package com.center.omd.callinterface.controller;


import com.center.omd.callinterface.service.ProviderResourceDataService;
import com.center.omd.callinterface.vo.ElectricityInfoVO;
import com.center.omd.util.DateUtils;
import com.center.omd.util.HttpClientUtil;
import com.center.omd.util.JSONHelper;
import com.center.omd.util.httpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: lixueyan
 * @Description: TODO(测试接口)
 * @date: Created in 2021/2/20 10:51
 */

@Controller
@RequestMapping("/testController")
public class TestController {
    @Autowired
    private ProviderResourceDataService providerResourceDataService;

    public static void main(String[] args) throws IOException {
        //获取数据信息
        String dataurl = "http://localhost:8088/testController/getElecInfoData";
        String data  = HttpClientUtil.sendGet(dataurl,"");
        System.out.println("data="+data);
        //调用接口提供数据给他们
         String appId = "ELM";
         String keys = "data,flag";
         String valuesS = data+",ydnh";
         StringBuffer values = new StringBuffer();
         values.append(data).append(",ydnh");
         Map<String, String> requestMap = new HashMap<String, String>();
         requestMap.put("appId",appId);
         requestMap.put("keys", keys);
        // requestMap.put("values", values.toString());
         requestMap.put("values", "a,a");
         System.out.println(requestMap);
          //标识，能耗用水用电，其他能耗标识
          /*  requestMap.put("flag", "ydnh");*/
          String jsonRequest = JSONHelper.map2json(requestMap);
          System.out.println(jsonRequest);
       /*  String urlPost = "http://111.33.90.43:8082/ecp/api/in_esb_post";
         String urlGet = "http://111.33.90.43:8082/ecp/api/in_esb";
         String params ="appId=ELM&keys=a&values=a";
         String msg = HttpClientUtil.sendGet(urlGet,params);
         System.out.println("msgGet="+msg);
         String ResponseMsg  = HttpClientUtil.httpRequest(urlPost,"POST",jsonRequest);
         String ResponseMsg1 =   HttpClientUtil.doPost(urlPost,requestMap);
         String remsgPost2 = httpUtils.JsonHttpRequestPost(urlPost,"POST",jsonRequest);
         System.out.println("msgPost="+ResponseMsg);
         System.out.println("msgPost1="+ResponseMsg1);
         System.out.println("remsgPost2="+remsgPost2);*/

    }
    


    /** 
     *  
     * @author lixueyan
     * @Description //TODO (获取用电数据)
     * @date 2021/2/20 14:11
     * @Param 
     * @return 
     */
    @RequestMapping(value = "/getElecInfoData")
    @ResponseBody
    public List<ElectricityInfoVO> getElecInfoData1(){
        Date currentDate = DateUtils.getDate();
        String  startTime = "2021/02/20 00:00:00";
        String endTime =  DateUtils.formatDate(currentDate,"yyyy/MM/dd HH:mm:ss");
        List<ElectricityInfoVO> listData = providerResourceDataService.getElectricityInfoData(startTime,endTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (ElectricityInfoVO listDatum : listData) {
             Date cjsj = DateUtils.str2Date(listDatum.getCjsj(),sdf);
            //SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
            String cjsj1 = DateUtils.formatDate(cjsj,"yyyyMMddHHmmss");
            listDatum.setCjsj(cjsj1);
        }
        return listData;
    }

    /**
     *
     * @author lixueyan
     * @Description //TODO （测试异常拦截）
     * @date 2021/2/26 9:57
     * @Param
     * @return
     */
    @RequestMapping(value = "/test")
    @ResponseBody
    public String test(){
        int a = 0;
        int b = 2;
        int c = 2/0;
        return "异常";
    }


}
