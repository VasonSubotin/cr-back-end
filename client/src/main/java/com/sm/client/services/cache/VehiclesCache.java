package com.sm.client.services.cache;

import com.sm.dao.CommonDao;
import com.sm.model.VehicleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VehiclesCache {

    private Map<String, List<VehicleModel>> cacheData = new HashMap<>();

    @Autowired
    private CommonDao commonDao;

    @PostConstruct
    public void init() {
        commonDao.getVehicles().forEach(a -> {
            String key = getKey(a.getMaker(), a.getModel());
            List<VehicleModel> lst = cacheData.get(key);
            if (lst == null) {
                lst = new ArrayList<>();
                cacheData.put(key, lst);
            }
            lst.add(a);
        });
    }

    public List<VehicleModel> getModels(String maker, String model) {
        return cacheData.get(getKey(maker, model));
    }

    private String getKey(String maker, String model) {
        return (maker.trim() + "|" + model.trim()).replaceAll("\\s", "").toUpperCase();
    }
}
