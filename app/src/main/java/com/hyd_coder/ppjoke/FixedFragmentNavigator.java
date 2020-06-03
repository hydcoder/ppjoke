package com.hyd_coder.ppjoke;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import androidx.navigation.fragment.FragmentNavigator;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Map;

/**
 * Cerated by huangyingde
 * Create date : 2020/6/3 17:27
 * description : 定制的Fragment导航器，替换ft.replace(mContainerId, frag);为 hide()/show()
 */
@Navigator.Name("fixedFragment")
public class FixedFragmentNavigator extends FragmentNavigator {

    private static final String TAG = "FixedFragmentNavigator";
    private Context mContext;
    private FragmentManager mManager;
    private int mContainerId;

    public FixedFragmentNavigator(@NonNull Context context, @NonNull FragmentManager manager, int containerId) {
        super(context, manager, containerId);
        mContext = context;
        mManager = manager;
        mContainerId = containerId;
    }

    @Nullable
    @Override
    public NavDestination navigate(@NonNull Destination destination, @Nullable Bundle args, @Nullable NavOptions navOptions, @Nullable Navigator.Extras navigatorExtras) {
        if (mManager.isStateSaved()) {
            Log.i(TAG, "Ignoring navigate() call: FragmentManager has already"
                    + " saved its state");
            return null;
        }
        String className = destination.getClassName();
        if (className.charAt(0) == '.') {
            className = mContext.getPackageName() + className;
        }
//        final Fragment frag = instantiateFragment(mContext, mManager,
//                className, args);
//        frag.setArguments(args);
        final FragmentTransaction ft = mManager.beginTransaction();

        int enterAnim = navOptions != null ? navOptions.getEnterAnim() : -1;
        int exitAnim = navOptions != null ? navOptions.getExitAnim() : -1;
        int popEnterAnim = navOptions != null ? navOptions.getPopEnterAnim() : -1;
        int popExitAnim = navOptions != null ? navOptions.getPopExitAnim() : -1;
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = enterAnim != -1 ? enterAnim : 0;
            exitAnim = exitAnim != -1 ? exitAnim : 0;
            popEnterAnim = popEnterAnim != -1 ? popEnterAnim : 0;
            popExitAnim = popExitAnim != -1 ? popExitAnim : 0;
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim);
        }

//        ft.replace(mContainerId, frag);
//        ft.setPrimaryNavigationFragment(frag);

        Fragment navigationFragment = mManager.getPrimaryNavigationFragment();
        if (navigationFragment != null) {
            // 先隐藏当前正在显示的fragment
            ft.hide(navigationFragment);
        }

        String fragTag = String.valueOf(destination.getId());
        Fragment fragment = mManager.findFragmentByTag(fragTag);
        if (fragment != null) {
            ft.show(fragment);
        } else {
            fragment = instantiateFragment(mContext, mManager, className, args);
            fragment.setArguments(args);
            ft.add(mContainerId, fragment, fragTag);

        }
        ft.setPrimaryNavigationFragment(fragment);

        final @IdRes int destId = destination.getId();

        ArrayDeque<Integer> mBackStack = null;
        try {
            Field field = FragmentNavigator.class.getDeclaredField("mBackStack");
            field.setAccessible(true);
            mBackStack = (ArrayDeque<Integer>) field.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        final boolean initialNavigation = mBackStack.isEmpty();
        final boolean isSingleTopReplacement = navOptions != null && !initialNavigation
                && navOptions.shouldLaunchSingleTop()
                && mBackStack.peekLast() == destId;

        boolean isAdded;
        if (initialNavigation) {
            isAdded = true;
        } else if (isSingleTopReplacement) {
            // Single Top means we only want one instance on the back stack
            if (mBackStack.size() > 1) {
                // If the Fragment to be replaced is on the FragmentManager's
                // back stack, a simple replace() isn't enough so we
                // remove it from the back stack and put our replacement
                // on the back stack in its place
                mManager.popBackStack(
                        generateBackStackName(mBackStack.size(), mBackStack.peekLast()),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
                ft.addToBackStack(generateBackStackName(mBackStack.size(), destId));
            }
            isAdded = false;
        } else {
            ft.addToBackStack(generateBackStackName(mBackStack.size() + 1, destId));
            isAdded = true;
        }
        if (navigatorExtras instanceof Extras) {
            Extras extras = (Extras) navigatorExtras;
            for (Map.Entry<View, String> sharedElement : extras.getSharedElements().entrySet()) {
                ft.addSharedElement(sharedElement.getKey(), sharedElement.getValue());
            }
        }
        ft.setReorderingAllowed(true);
        ft.commit();
        // The commit succeeded, update our view of the world
        if (isAdded) {
            mBackStack.add(destId);
            return destination;
        } else {
            return null;
        }
    }

    private String generateBackStackName(int backStackIndex, int destId) {
        return backStackIndex + "-" + destId;
    }
}
