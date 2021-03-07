package com.center.omd.callinterface.vo;

import com.owner.backstage.core.VO;
import com.owner.backstage.core.bsc.dao.MyTable;

/**
 * @ClassName ElectricityInfo
 * @Description:TODO 京张推送的用电信息
 * @Author lixueyan
 * @Date 2020/11/16
 * @Version V1.0
 **/
@MyTable(name = "JN_PUSH_ELECTRICITY_INFO")
public class ElectricityInfoVO extends VO {
    private String id;
    private String cjsj;
    private String nybs;
    private String dbbh;
    /*private Date scsj;*/
    private Double dbzm;
    /*private String flag;*/
    //添加单位id编码
    private String dwmc;
    //设备类型
    private String sblx;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

   /* public Date getCjsj() {
        return cjsj;
    }

    public void setCjsj(Date cjsj) {
        this.cjsj = cjsj;
    }*/

    public String getCjsj() {
        return cjsj;
    }

    public void setCjsj(String cjsj) {
        this.cjsj = cjsj;
    }

    public String getNybs() {
        return nybs;
    }

    public void setNybs(String nybs) {
        this.nybs = nybs;
    }

    public String getDbbh() {
        return dbbh;
    }

    public void setDbbh(String dbbh) {
        this.dbbh = dbbh;
    }

  /*  public Date getScsj() {
        return scsj;
    }

    public void setScsj(Date scsj) {
        this.scsj = scsj;
    }*/

    public Double getDbzm() {
        return dbzm;
    }

    public void setDbzm(Double dbzm) {
        this.dbzm = dbzm;
    }

    /*public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }*/

    public String getDwmc() {
        return dwmc;
    }

    public void setDwmc(String dwmc) {
        this.dwmc = dwmc;
    }

    public String getSblx() {
        return sblx;
    }

    public void setSblx(String sblx) {
        this.sblx = sblx;
    }

}
