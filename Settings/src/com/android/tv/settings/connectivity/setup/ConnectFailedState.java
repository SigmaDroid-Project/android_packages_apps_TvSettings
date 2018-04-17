/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.tv.settings.connectivity.setup;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.leanback.widget.GuidanceStylist;
import androidx.leanback.widget.GuidedAction;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.android.tv.settings.R;
import com.android.tv.settings.connectivity.util.State;
import com.android.tv.settings.connectivity.util.StateMachine;

import java.util.List;

/**
 * State responsible for handling the failed connection.
 */
public class ConnectFailedState implements State {
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public ConnectFailedState(FragmentActivity activity) {
        mActivity = activity;
    }

    @Override
    public void processForward() {
        mFragment = new ConnectFailedFragment();
        FragmentChangeListener listener = (FragmentChangeListener) mActivity;
        if (listener != null) {
            listener.onFragmentChange(mFragment, true);
        }
    }

    @Override
    public void processBackward() {
        StateMachine stateMachine = ViewModelProviders.of(mActivity).get(StateMachine.class);
        stateMachine.back();
    }

    @Override
    public Fragment getFragment() {
        return mFragment;
    }

    /**
     * Fragment that notifies the user the connection to network is failed.
     */
    public static class ConnectFailedFragment extends WifiConnectivityGuidedStepFragment {
        private static final int ACTION_ID_TRY_AGAIN = 100001;
        private static final int ACTION_ID_VIEW_AVAILABLE_NETWORK = 100002;
        private StateMachine mStateMachine;
        private UserChoiceInfo mUserChoiceInfo;

        @Override
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            String title = getString(
                    R.string.title_wifi_could_not_connect,
                    mUserChoiceInfo.getWifiConfiguration().getPrintableSsid());
            return new GuidanceStylist.Guidance(title, null, null, null);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            mStateMachine = ViewModelProviders
                    .of(getActivity())
                    .get(StateMachine.class);
            mUserChoiceInfo = ViewModelProviders
                    .of(getActivity())
                    .get(UserChoiceInfo.class);
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onCreateActions(List<GuidedAction> actions, Bundle savedInstanceState) {
            Context context = getActivity();
            actions.add(new GuidedAction.Builder(context)
                    .title(R.string.wifi_action_try_again)
                    .id(ACTION_ID_TRY_AGAIN)
                    .build());
            actions.add(new GuidedAction.Builder(context)
                    .title(R.string.wifi_action_view_available_networks)
                    .id(ACTION_ID_VIEW_AVAILABLE_NETWORK)
                    .build());
        }

        @Override
        public void onGuidedActionClicked(GuidedAction action) {
            if (action.getId() == ACTION_ID_TRY_AGAIN) {
                mStateMachine.getListener()
                        .onComplete(StateMachine.TRY_AGAIN);
            } else if (action.getId() == ACTION_ID_VIEW_AVAILABLE_NETWORK) {
                mStateMachine.getListener()
                        .onComplete(StateMachine.SELECT_WIFI);
            }
        }
    }
}
