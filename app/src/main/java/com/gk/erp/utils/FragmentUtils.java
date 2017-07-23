package com.gk.erp.utils;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by ke.gao on 2017/7/4.
 */

public class FragmentUtils {

    private static Handler mHandler = new Handler();

    public static void commitFragment(final FragmentManager fragmentManager,final Fragment fragment, final int resId){
        mHandler.post(new CommitFragmentRunnable(fragmentManager,fragment,resId));
    }

    private static class CommitFragmentRunnable implements Runnable {

        private FragmentManager fragmentManager;
        private Fragment fragment;
        private int resId;

        public CommitFragmentRunnable(FragmentManager fragmentManager, Fragment fragment, int resId) {
            this.fragmentManager = fragmentManager;
            this.fragment = fragment;
            this.resId = resId;

        }

        @Override
        public void run() {
            fragmentManager.beginTransaction()
                    .replace(resId, fragment).commit();

        }
    }
}
