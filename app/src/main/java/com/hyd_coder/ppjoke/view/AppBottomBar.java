package com.hyd_coder.ppjoke.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.hyd_coder.ppjoke.R;
import com.hyd_coder.ppjoke.model.BottomBar;
import com.hyd_coder.ppjoke.model.Destination;
import com.hyd_coder.ppjoke.utils.AppConfig;

import java.util.HashMap;
import java.util.List;

/**
 * Cerated by huangyingde
 * Create date : 2020/6/3 15:52
 * description : 自定义的底部导航栏
 */
public class AppBottomBar extends BottomNavigationView {

    private static int[] sIcons = new int[]{R.drawable.icon_tab_home, R.drawable.icon_tab_sofa,
            R.drawable.icon_tab_publish, R.drawable.icon_tab_find, R.drawable.icon_tab_mine};

    public AppBottomBar(@NonNull Context context) {
        this(context, null);
    }

    public AppBottomBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("RestrictedApi")
    public AppBottomBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        BottomBar bottomBar = AppConfig.getBottomBar();
        List<BottomBar.Tabs> tabs = bottomBar.getTabs();

        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_selected};
        states[1] = new int[]{};
        int[] colors = new int[]{Color.parseColor(bottomBar.getActiveColor()), Color.parseColor(bottomBar.getInActiveColor())};

        ColorStateList colorStateList = new ColorStateList(states, colors);
        setItemTextColor(colorStateList);
        setItemIconTintList(colorStateList);

        //LABEL_VISIBILITY_LABELED:设置按钮的文本为一直显示模式
        //LABEL_VISIBILITY_AUTO:当按钮个数小于三个时一直显示，或者当按钮个数大于3个且小于5个时，被选中的那个按钮文本才会显示
        //LABEL_VISIBILITY_SELECTED：只有被选中的那个按钮的文本才会显示
        //LABEL_VISIBILITY_UNLABELED:所有的按钮文本都不显示
        setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        for (BottomBar.Tabs tab : tabs) {
            if (!tab.isEnable()) {
                continue;
            }
            int itemId = getItemId(tab.getPageUrl());
            if (itemId < 0) {
                continue;
            }
            MenuItem menuItem = getMenu().add(0, itemId, tab.getIndex(), tab.getTitle());
            menuItem.setIcon(sIcons[tab.getIndex()]);
        }

        // 必须添加完全部tab后，才能设置iconSize，因为getMenu().add会先remove掉已经添加的好的
        for (int i = 0; i < tabs.size(); i++) {
            BottomBar.Tabs tab = tabs.get(i);

            if (!tab.isEnable()) {
                continue;
            }
            int itemId = getItemId(tab.getPageUrl());
            if (itemId < 0) {
                continue;
            }
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) getChildAt(0);
            BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(tab.getIndex());
            itemView.setIconSize(dip2px(tab.getSize()));

            if (TextUtils.isEmpty(tab.getTitle())) {
                int tintColor = TextUtils.isEmpty(tab.getTintColor()) ? Color.parseColor("#ff678f") : Color.parseColor(tab.getTintColor());
                itemView.setIconTintList(ColorStateList.valueOf(tintColor));
                // 禁止掉点按时 上下浮动的效果
                itemView.setShifting(false);
                /*
                 * 如果想要禁止掉所有按钮的点击浮动效果。
                 * 那么还需要给选中和未选中的按钮配置一样大小的字号。
                 *
                 *  在MainActivity布局的AppBottomBar标签增加如下配置，
                 *  @style/active，@style/inActive 在style.xml中
                 *  app:itemTextAppearanceActive="@style/active"
                 *  app:itemTextAppearanceInactive="@style/inActive"
                 */
            }
        }

        // 底部导航栏默认选中项
        if (bottomBar.getSelectTab() != 0) {
            BottomBar.Tabs tab = bottomBar.getTabs().get(bottomBar.getSelectTab());
            if (tab.isEnable()) {
                int itemId = getItemId(tab.getPageUrl());
                // 这里需要延迟一下 再定位到默认选中的tab
                // 因为需要等待内容区域,也就NavGraphBuilder解析数据并初始化完成，
                // 否则会出现 底部按钮切换过去了，但内容区域还没切换过去
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setSelectedItemId(itemId);
                    }
                }, 300);
            }
        }

    }

    private int dip2px(int size) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return (int)(displayMetrics.density * size + 0.5f);
    }

    private int getItemId(String pageUrl) {
        HashMap<String, Destination> desConfig = AppConfig.getDesConfig();
        Destination destination = desConfig.get(pageUrl);
        if (destination == null) {
            return -1;
        }
        return destination.getId();
    }
}
