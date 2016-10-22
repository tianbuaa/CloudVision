package com.xunda.cloudvision.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xunda.cloudvision.Config;
import com.xunda.cloudvision.R;
import com.xunda.cloudvision.bean.AdvertiseBean;
import com.xunda.cloudvision.bean.CorporateBean;
import com.xunda.cloudvision.bean.ProductBean;
import com.xunda.cloudvision.bean.resp.QueryCorporateResp;
import com.xunda.cloudvision.bean.resp.QueryProductResp;
import com.xunda.cloudvision.http.HttpStatus;
import com.xunda.cloudvision.presenter.CorporatePresenter;
import com.xunda.cloudvision.ui.adapter.RecommendedProductPagerAdapter;
import com.xunda.cloudvision.ui.adapter.RecommendedVideoAdapter;
import com.xunda.cloudvision.ui.widget.NoScrollGridView;
import com.xunda.cloudvision.utils.DataManager;
import com.xunda.cloudvision.view.ICorporateView;

import java.util.List;

/**
 * 企业首页
 * Created by yinglovezhuzhu@gmail.com on 2016/9/18.
 */
public class CorporateActivity extends BaseActivity implements ICorporateView {

    private CorporatePresenter mCorporatePresenter;

    private ImageView mIvCorporateLogo;
    private TextView mTvCorporateName;

    private RecommendedProductPagerAdapter mRecommendedProductAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_corporate);

        mCorporatePresenter = new CorporatePresenter(this, this);

        initView();

        mCorporatePresenter.queryCorporateInfo();
        mCorporatePresenter.queryRecommendedProduct();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_corporate_culture:
                gotoCorporateIntro(CorporateIntroActivity.PAGE_CORPORATE_CULTURE);
                break;
            case R.id.btn_corporate_honor:
                gotoCorporateIntro(CorporateIntroActivity.PAGE_CORPORATE_HONOR);
                break;
            case R.id.btn_corporate_environment:
                gotoCorporateIntro(CorporateIntroActivity.PAGE_CORPORATE_ENVIRONMENT);
                break;
            case R.id.btn_corporate_image:
                gotoCorporateIntro(CorporateIntroActivity.PAGE_CORPORATE_IMAGE);
                break;
            case R.id.btn_corporate_intro:
                gotoCorporateIntro(CorporateIntroActivity.PAGE_CORPORATE_INTRO);
                break;
            case R.id.btn_corporate_all_product:
                startActivity(new Intent(this, ProductActivity.class));
                break;
            case R.id.btn_corporate_all_video:
                startActivity(new Intent(this, VideoActivity.class));
                break;
            case R.id.ibtn_corporate_back:
                finish(RESULT_CANCELED, null);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPreExecute(String key) {

    }

    @Override
    public void onCanceled(String key) {

    }

    @Override
    public void onQueryRecommendedProductResult(QueryProductResp result) {
        if(null == result) {

        } else {
            switch (result.getHttpCode()) {
                case HttpStatus.SC_OK:
                    List<ProductBean> product = result.getProduct();
                    if(null == product || product.isEmpty()) {
                        // TODO 错误
                    } else {
                        mRecommendedProductAdapter.addAll(product, true);
                    }
                    break;
                case HttpStatus.SC_CACHE_NOT_FOUND:
                    // TODO 无网络，读取缓存错误
                    break;
                default:
                    // TODO 错误
                    break;
            }
        }
    }

    @Override
    public void onQueryCorporateInfoResult(QueryCorporateResp result) {
        if(null == result) {

        } else {
            switch (result.getHttpCode()) {
                case HttpStatus.SC_OK:
                    final CorporateBean corporate = result.getEnterprise();
                    if(null == corporate) {
                        // TODO 错误
                    } else {
                        DataManager.getInstance().updateCorporateInfo(corporate);
                        mTvCorporateName.setText(corporate.getName());
                    }
                    break;
                case HttpStatus.SC_CACHE_NOT_FOUND:
                    // TODO 无网络，读取缓存错误
                    break;
                default:
                    // TODO 错误
                    break;
            }
        }
    }

    private void initView() {
        mIvCorporateLogo = (ImageView) findViewById(R.id.iv_corporate_logo);
        mTvCorporateName = (TextView) findViewById(R.id.tv_corporate_name);

        findViewById(R.id.btn_corporate_culture).setOnClickListener(this);
        findViewById(R.id.btn_corporate_honor).setOnClickListener(this);
        findViewById(R.id.btn_corporate_environment).setOnClickListener(this);
        findViewById(R.id.btn_corporate_image).setOnClickListener(this);
        findViewById(R.id.btn_corporate_intro).setOnClickListener(this);
        findViewById(R.id.btn_corporate_all_product).setOnClickListener(this);
        findViewById(R.id.ibtn_corporate_back).setOnClickListener(this);
        findViewById(R.id.btn_corporate_all_video).setOnClickListener(this);

        // 推荐产品ViewPager高度设置
        final int dmWidth = getResources().getDisplayMetrics().widthPixels;
        final View recommendedProductContent = findViewById(R.id.ll_corporate_recommended_product_content);
        ViewGroup.LayoutParams lp = recommendedProductContent.getLayoutParams();
        if(null == lp) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        lp.height = dmWidth * 4 / 5;
        recommendedProductContent.setLayoutParams(lp);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_corporate_recommended_product);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.contentPadding_level6));
        mRecommendedProductAdapter = new RecommendedProductPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mRecommendedProductAdapter);

        final int videoItemWidth = (dmWidth - getResources().getDimensionPixelSize(R.dimen.contentPadding_level4) * 3) / 2;
        final NoScrollGridView videoGrid = (NoScrollGridView) findViewById(R.id.gv_corporate_recommended_video);
        videoGrid.setAdapter(new RecommendedVideoAdapter(this, videoItemWidth));
        videoGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(CorporateActivity.this, VideoPlayerActivity.class);
                i.setData(Uri.parse("http://120.24.234.204/static/upload/video/FUKESI.mp4"));
                startActivity(i);
            }
        });

    }

    /**
     * 跳转到企业简介页面
     * @param page 页面
     * @see CorporateIntroActivity#PAGE_CORPORATE_HONOR
     * @see CorporateIntroActivity#PAGE_CORPORATE_CULTURE
     * @see CorporateIntroActivity#PAGE_CORPORATE_IMAGE
     * @see CorporateIntroActivity#PAGE_CORPORATE_INTRO
     * @see CorporateIntroActivity#PAGE_CORPORATE_ENVIRONMENT
     */
    private void gotoCorporateIntro(int page) {
        Intent intent = new Intent(this, CorporateIntroActivity.class);
        intent.putExtra(Config.EXTRA_DATA, DataManager.getInstance().getCorporateInfo());
        intent.putExtra(Config.EXTRA_POSITION, page);
        startActivity(intent);
    }
}
