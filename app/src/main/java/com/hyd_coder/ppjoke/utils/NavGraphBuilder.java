package com.hyd_coder.ppjoke.utils;

import android.content.ComponentName;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.FragmentNavigator;

import com.hyd_coder.ppjoke.FixedFragmentNavigator;
import com.hyd_coder.ppjoke.model.Destination;

import java.util.HashMap;

/**
 * Cerated by huangyingde
 * Create date : 2020/6/2 12:58
 * description : NavGraph创造器，将json文件里的destination节点转换成对应的navigator并添加到NavGraph中
 *               再将NavGraph和navController关联
 */
public class NavGraphBuilder {

    public static void build(NavController controller, FragmentActivity activity, int containerId) {
        NavigatorProvider navigatorProvider = controller.getNavigatorProvider();

//        FragmentNavigator fragmentNavigator = navigatorProvider.getNavigator(FragmentNavigator.class);
        FixedFragmentNavigator fragmentNavigator = new FixedFragmentNavigator(activity, activity.getSupportFragmentManager(), containerId);
        navigatorProvider.addNavigator(fragmentNavigator);

        ActivityNavigator activityNavigator = navigatorProvider.getNavigator(ActivityNavigator.class);

        HashMap<String, Destination> desConfig = AppConfig.getDesConfig();

        NavGraph navGraph = new NavGraph(new NavGraphNavigator(navigatorProvider));

        for (Destination value : desConfig.values()) {
            if (value.isFragment()) {
                FragmentNavigator.Destination destination = fragmentNavigator.createDestination();
                destination.setClassName(value.getClassName());
                destination.setId(value.getId());
                destination.addDeepLink(value.getPageUrl());
                navGraph.addDestination(destination);
            } else {
                ActivityNavigator.Destination destination = activityNavigator.createDestination();
                destination.setId(value.getId());
                destination.addDeepLink(value.getPageUrl());
                destination.setComponentName(new ComponentName(AppGlobals.getAppication().getPackageName(), value.getClassName()));
                navGraph.addDestination(destination);
            }

            if (value.isAsStarter()) {
                navGraph.setStartDestination(value.getId());
            }
        }

        controller.setGraph(navGraph);
    }
}
