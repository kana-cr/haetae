/*
 * betahouse.us
 * CopyRight (c) 2012 - 2019
 */
package us.betahouse.haetae.asset.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.betahouse.haetae.asset.builder.AssetBOBuilder;
import us.betahouse.haetae.asset.dal.service.AssetRepoService;
import us.betahouse.haetae.asset.manager.AssetManager;
import us.betahouse.haetae.asset.model.basic.AssetBO;
import us.betahouse.haetae.asset.request.AssetRequest;

/**
 * @author guofan.cp
 * @version : AssetManagerInpl.java 2019/01/24 9:33 guofan.cp
 */
@Service
public class AssetManagerImpl implements AssetManager {
    @Autowired
    private AssetRepoService assetRepoService;

    /**
     * 创建物资
     *
     * @param request
     * @return
     */
    @Override
    public AssetBO create(AssetRequest request) {
        AssetBOBuilder assetBOBuilder = AssetBOBuilder.getInstance()
                .withAssetName(request.getAssetName())
                .withAssetType(request.getAssetType())
                .withAssetMount(request.getAssetAmount())
                .withAssetOrginnaztionId(request.getAssetOrganizationId())
                .withAssetOrginnaztionName(request.getAssetOrganizationName());
        return assetRepoService.createAsset(assetBOBuilder.builder());
    }

    /**
     * @param userId
     * @return
     */
    @Override
    public AssetBO findAssetByAssetID(String userId) {
        return null;
    }
}
