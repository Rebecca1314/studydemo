package com.center.omd.callinterface.service.impl;


import com.center.omd.callinterface.service.ProviderResourceDataService;
import com.center.omd.callinterface.vo.ElectricityInfoVO;
import com.owner.backstage.core.bsc.dao.sjdbc.DAOSpringJdbc;
import com.owner.backstage.core.bsc.impl.BasePersistentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.owner.backstage.core.bsc.dao.IDAO;
import java.util.List;

/**
 * @author: lixueyan
 * @Description: TODO(对外提供的资源能耗信息数据)
 * @date: Created in 2021/2/20 10:49
 */
@Service
public class ProviderResourceDataServiceImpl extends BasePersistentServiceImpl implements ProviderResourceDataService {
    @Autowired(required=true)
    private DAOSpringJdbc daoSpringJdbc;
    /**
     * @author lixueyan
     * @Description //TODO (获取用电信息)
     * @date 2021/2/20 10:57
     * @Param
     * @return
     */
    @Override
    public List<ElectricityInfoVO> getElectricityInfoData(String startTime, String endTime) {
        StringBuilder sql = new StringBuilder();
        Object[] params = new Object[2];
        sql.append(" SELECT ID,CJSJ,NYBS,DBBH,DBZM,DWMC,SBLX FROM ").append(getTableName(ElectricityInfoVO.class));
        sql.append(" WHERE CJSJ BETWEEN TO_DATE(?,'yyyy/mm/dd hh24:mi:ss') AND TO_DATE(?,'yyyy/mm/dd hh24:mi:ss')");
        params[0] = startTime;
        params[1] = endTime;
        return findList(ElectricityInfoVO.class,sql.toString(),params);
    }
}
