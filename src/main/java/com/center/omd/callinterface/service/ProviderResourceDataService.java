package com.center.omd.callinterface.service;


import com.center.omd.callinterface.vo.ElectricityInfoVO;
import com.owner.backstage.core.bsc.IBasePersistentService;

import java.util.List;

/**
 * @author: lixueyan
 * @Description: TODO(对外提供的资源能耗信息数据)
 * @date: Created in 2021/2/20 10:44
 */
public interface ProviderResourceDataService extends IBasePersistentService {
    /**
     * @author lixueyan
     * @Description //TODO (获取用电信息)
     * @date 2021/2/20 10:57
     * @Param 
     * @return 
     */
    public List<ElectricityInfoVO> getElectricityInfoData(String startTime, String endTime);

}
