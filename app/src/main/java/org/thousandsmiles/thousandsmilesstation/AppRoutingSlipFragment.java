/*
 * (C) Copyright Syd Logan 2017
 * (C) Copyright Thousand Smiles Foundation 2017
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.thousandsmiles.thousandsmilesstation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemDetailActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class AppRoutingSlipFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    private View m_rootView;

    /**
     * The dummy content this fragment is presenting.
     */
    private static PatientItem mItem;

    private SessionSingleton m_sess = SessionSingleton.getInstance();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AppRoutingSlipFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean wasClicked = false;

        if (getArguments().containsKey(ARG_ITEM_ID) && getArguments().containsKey("isWaiting")) {
            // isWaiting means either the active list or waiting list has been clicked

            boolean isWaiting = getArguments().getBoolean("isWaiting");

            m_sess.setListWasClicked(true);
            wasClicked = true;

            String itemId = getArguments().getString(ARG_ITEM_ID);
            if (isWaiting) {
                mItem = WaitingPatientList.ITEM_MAP.get(itemId);
                m_sess.setWaitingIsFromActiveList(false);
            } else {
                mItem = ActivePatientList.ITEM_MAP.get(itemId);
                m_sess.setWaitingIsFromActiveList(true);
            }
        }
        else if (m_sess.getListWasClicked() == false){
            // isWaiting is not present as an argument. Based on our state, get the relevant mItem

            if (m_sess.isActive()) {
                mItem = m_sess.getActivePatientItem();
            } else if (m_sess.isWaiting()) {
                mItem = m_sess.getWaitingPatientItem();
            }
        }
        try {
            if (mItem != null && mItem.pObject != null) {
                m_sess.setDisplayPatientId(Integer.parseInt(mItem.pObject.getString("id")));
            }
        } catch (JSONException e) {
        }
    }

    private void updatePatientView()
    {
        if (mItem != null) {
            try {
                ((TextView) m_rootView.findViewById(R.id.detail_row_name_id)).setText("ID:");
                ((TextView) m_rootView.findViewById(R.id.detail_row_value_id)).setText(mItem.pObject.getString("id"));
                String last = String.format("%s,\n%s",
                        mItem.pObject.getString("paternal_last"), mItem.pObject.getString("maternal_last"));
                ((TextView) m_rootView.findViewById(R.id.detail_row_name_last)).setText("Last:");
                ((TextView) m_rootView.findViewById(R.id.detail_row_value_last)).setText(last);
                ((TextView) m_rootView.findViewById(R.id.detail_row_name_first)).setText("First:");
                ((TextView) m_rootView.findViewById(R.id.detail_row_value_first)).setText(mItem.pObject.getString("first"));
                ((TextView) m_rootView.findViewById(R.id.detail_row_name_gender)).setText("Gender:");
                String gender = mItem.pObject.getString("gender");
                ((TextView) m_rootView.findViewById(R.id.detail_row_value_gender)).setText(gender);
                ImageView img = ((ImageView) m_rootView.findViewById(R.id.headshot));

                if (gender.equals("Male")) {
                    img.setImageResource(R.drawable.boyfront);
                } else {
                    img.setImageResource(R.drawable.girlfront);
                }

                ((TextView) m_rootView.findViewById(R.id.detail_row_name_dob)).setText("DOB:");
                ((TextView) m_rootView.findViewById(R.id.detail_row_value_dob)).setText(mItem.pObject.getString("dob"));
            } catch (JSONException e) {

            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        m_rootView = inflater.inflate(R.layout.item_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            updatePatientView();
        }

        return m_rootView;
    }
}
