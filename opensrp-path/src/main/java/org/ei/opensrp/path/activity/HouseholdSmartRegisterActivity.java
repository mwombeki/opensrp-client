package org.ei.opensrp.path.activity;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.adapter.SmartRegisterPaginatedAdapter;
import org.ei.opensrp.domain.FetchStatus;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.event.Event;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.adapter.PathRegisterActivityPagerAdapter;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.Stock;
import org.ei.opensrp.path.fragment.AdvancedSearchFragment;
import org.ei.opensrp.path.fragment.BaseSmartRegisterFragment;
import org.ei.opensrp.path.fragment.ChildSmartRegisterFragment;
import org.ei.opensrp.path.fragment.HouseholdMemberAddFragment;
import org.ei.opensrp.path.fragment.HouseholdSmartRegisterFragment;
import org.ei.opensrp.path.receiver.ServiceReceiver;
import org.ei.opensrp.path.repository.PathRepository;
import org.ei.opensrp.path.repository.StockRepository;
import org.ei.opensrp.path.view.LocationPickerView;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.repository.DetailsRepository;
import org.ei.opensrp.service.FormSubmissionService;
import org.ei.opensrp.service.ZiggyService;
import org.ei.opensrp.util.FormUtils;
import org.ei.opensrp.view.dialog.DialogOptionModel;
import org.ei.opensrp.view.viewpager.OpenSRPViewPager;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import util.JsonFormUtils;
import util.barcode.Barcode;
import util.barcode.BarcodeIntentIntegrator;
import util.barcode.BarcodeIntentResult;

import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;
import static util.Utils.getValue;

/**
 * Created by Ahmed on 13-Oct-15.
 */
public class HouseholdSmartRegisterActivity extends BaseRegisterActivity {
    private static String TAG = HouseholdSmartRegisterActivity.class.getCanonicalName();

    @Bind(R.id.view_pager)
    OpenSRPViewPager mPager;
    private FragmentPagerAdapter mPagerAdapter;
    private static final int REQUEST_CODE_GET_JSON = 3432;
    private static final int REQUEST_CODE_RECORD_OUT_OF_CATCHMENT = 1131;
    private int currentPage;
    public static final int ADVANCED_SEARCH_POSITION = 1;

    private Fragment mBaseFragment = null;
    private AdvancedSearchFragment advancedSearchFragment;

    private ServiceReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mBaseFragment = new HouseholdSmartRegisterFragment();
        advancedSearchFragment = new AdvancedSearchFragment();
        Fragment[] otherFragments = {new AdvancedSearchFragment()};

        // Instantiate a ViewPager and a PagerAdapter.
        mPagerAdapter = new PathRegisterActivityPagerAdapter(getSupportFragmentManager(), mBaseFragment, otherFragments);
        mPager.setOffscreenPageLimit(otherFragments.length);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }
        });

        Event.ON_DATA_FETCHED.addListener(onDataFetchedListener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Event.ON_DATA_FETCHED.removeListener(onDataFetchedListener);
    }

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedAdapter(clientsProvider());
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return null;
    }

    @Override
    protected void setupViews() {
    }

    @Override
    protected void onResumption() {
    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {
        return null;
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return null;
    }

    @Override
    protected void onInitialization() {
    }

    @Override
    public void startRegistration() {
    }

    @Override
    public void showFragmentDialog(DialogOptionModel dialogOptionModel, Object tag) {
        try {
            LoginActivity.setLanguage();
        } catch (Exception e) {

        }
        super.showFragmentDialog(dialogOptionModel, tag);
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        Log.d("-------------",formName);
        try {
            if (mBaseFragment instanceof HouseholdSmartRegisterFragment) {
                LocationPickerView locationPickerView = ((HouseholdSmartRegisterFragment) mBaseFragment).getLocationPickerView();
                String locationId = JsonFormUtils.getOpenMrsLocationId(context(), locationPickerView.getSelectedItem());
                JsonFormUtils.startForm(this, context(), REQUEST_CODE_GET_JSON, formName, entityId,
                        metaData, locationId);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

    }

    public void startWomanFormActivity(String formName, String entityId, String metaData) {

        Intent intent = new Intent(getApplicationContext(), PathJsonFormActivity.class);

//        intent.putExtra("json", metaData);
        startActivityForResult(intent, REQUEST_CODE_GET_JSON);


    }

    public void startAdvancedSearch() {
        try {
            mPager.setCurrentItem(ADVANCED_SEARCH_POSITION, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateAdvancedSearchFilterCount(int count) {
        AdvancedSearchFragment advancedSearchFragment = (AdvancedSearchFragment) findFragmentByPosition(ADVANCED_SEARCH_POSITION);
        if(advancedSearchFragment != null){
            advancedSearchFragment.updateFilterCount(count);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_GET_JSON) {
            if (resultCode == RESULT_OK) {

                String jsonString = data.getStringExtra("json");
                Log.d("JSONResult", jsonString);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);

                JsonFormUtils.saveForm(this, context(), jsonString, allSharedPreferences.fetchRegisteredANM());

                JSONObject form = null;
                try {
                    form = new JSONObject(jsonString);
                    if (form.getString("encounter_type").equals("Household Registration")) {
                        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                        android.app.Fragment prev = this.getFragmentManager().findFragmentByTag(HouseholdMemberAddFragment.DIALOG_TAG);
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);
                        PathRepository repo = (PathRepository) VaccinatorApplication.getInstance().getRepository();
                        net.sqlcipher.database.SQLiteDatabase db = repo.getReadableDatabase();
                        Cursor c = db.rawQuery("SELECT base_entity_id FROM ec_household WHERE last_interacted_with=(SELECT MAX(last_interacted_with) FROM ec_household)", null);
                        String householdid = "";
                        if(c.getCount()>0) {
                            c.moveToFirst();
                            if(c.getString(0)!=null && !StringUtils.isBlank(c.getString(0)))
                                householdid = c.getString(0);
                            c.close();
                        }else{
                            c.close();
                        }
                        String locationid = "";
                        DetailsRepository detailsRepository;
                        detailsRepository = org.ei.opensrp.Context.getInstance().detailsRepository();
                        Map<String, String> details = detailsRepository.getAllDetailsForClient(householdid);
                        locationid = JsonFormUtils.getOpenMrsLocationId(context(),getValue(details, "address4", false) );




                        LocationPickerView locationPickerView = ((HouseholdSmartRegisterFragment) mBaseFragment).getLocationPickerView();

                        String locationId = JsonFormUtils.getOpenMrsLocationId(context(), locationPickerView.getSelectedItem());
                        if(!StringUtils.isBlank(locationid) || locationid.equalsIgnoreCase("")){
                            locationId = locationid;
                        }
                        HouseholdMemberAddFragment addmemberFragment = HouseholdMemberAddFragment.newInstance(this,locationId,householdid,context());
                        addmemberFragment.show(ft, HouseholdMemberAddFragment.DIALOG_TAG);
//                       startFormActivity("woman_member_registration", null, null);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } else if (requestCode == BarcodeIntentIntegrator.REQUEST_CODE && resultCode == RESULT_OK) {
            BarcodeIntentResult res = BarcodeIntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (StringUtils.isNotBlank(res.getContents())) {
                onQRCodeSucessfullyScanned(res.getContents());
            } else Log.i("", "NO RESULT FOR QR CODE");
        }
    }

    @Override
    public void saveFormSubmission(String formSubmission, String id, String formName, JSONObject fieldOverrides) {
        // save the form
        try {
            FormUtils formUtils = FormUtils.getInstance(getApplicationContext());
            FormSubmission submission = formUtils.generateFormSubmisionFromXMLString(id, formSubmission, formName, fieldOverrides);

            Context context = context();
            ZiggyService ziggyService = context.ziggyService();
            ziggyService.saveForm(getParams(submission), submission.instance());

            FormSubmissionService formSubmissionService = context.formSubmissionService();
            formSubmissionService.updateFTSsearch(submission);

            Log.v("we are here", "hhregister");
            //switch to forms list fragmentstregi
            switchToBaseFragment(formSubmission); // Unnecessary!! passing on data

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public void switchToBaseFragment(final String data) {
        Log.v("we are here", "switchtobasegragment");
        final int prevPageIndex = currentPage;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPager.setCurrentItem(0, false);
                refreshList(data);
            }
        });
    }

    @Override
    public void onBackPressed() {
        BaseSmartRegisterFragment registerFragment = (BaseSmartRegisterFragment) findFragmentByPosition(currentPage);
        if (registerFragment.onBackPressed()) {
            return;
        }
        if (currentPage != 0) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.form_back_confirm_dialog_message)
                    .setTitle(R.string.form_back_confirm_dialog_title)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes_button_label,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    switchToBaseFragment(null);
                                }
                            })
                    .setNegativeButton(R.string.no_button_label,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            })
                    .show();
        } else if (currentPage == 0) {
            super.onBackPressed(); // allow back key only if we are
        }
    }

    public Fragment findFragmentByPosition(int position) {
        FragmentPagerAdapter fragmentPagerAdapter = mPagerAdapter;
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + mPager.getId() + ":" + fragmentPagerAdapter.getItemId(position));
    }

    private boolean currentActivityIsShowingForm() {
        return currentPage != 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void startQrCodeScanner() {
        BarcodeIntentIntegrator integ = new BarcodeIntentIntegrator(this);
        integ.addExtra(Barcode.SCAN_MODE, Barcode.QR_MODE);
        integ.initiateScan();
    }

    public void filterSelection() {
        if(currentPage != 0){
            switchToBaseFragment(null);
            BaseSmartRegisterFragment registerFragment = (BaseSmartRegisterFragment) findFragmentByPosition(0);
            if (registerFragment != null && registerFragment instanceof ChildSmartRegisterFragment) {
                ((ChildSmartRegisterFragment)registerFragment).triggerFilterSelection();
            }
        }
    }

    private void onQRCodeSucessfullyScanned(String qrCode) {
        Log.i(getClass().getName(), "QR code: " + qrCode);
        if (StringUtils.isNotBlank(qrCode)) {
            filterList(qrCode);
        }
    }

    private Listener<FetchStatus> onDataFetchedListener = new Listener<FetchStatus>() {
        @Override
        public void onEvent(FetchStatus fetchStatus) {
            refreshList(fetchStatus);
        }
    };

    private void refreshList(final FetchStatus fetchStatus) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            BaseSmartRegisterFragment registerFragment = (BaseSmartRegisterFragment) findFragmentByPosition(0);
            if (registerFragment != null && fetchStatus.equals(FetchStatus.fetched)) {
                registerFragment.refreshListView();
            }
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    BaseSmartRegisterFragment registerFragment = (BaseSmartRegisterFragment) findFragmentByPosition(0);
                    if (registerFragment != null && fetchStatus.equals(FetchStatus.fetched)) {
                        registerFragment.refreshListView();
                    }
                }
            });
        }

    }

    private void refreshList(String data) {
        BaseSmartRegisterFragment registerFragment = (BaseSmartRegisterFragment) findFragmentByPosition(0);
        if (registerFragment != null && data != null) {
            registerFragment.refreshListView();
        }

    }

    private void filterList(String filterString) {
        BaseSmartRegisterFragment registerFragment = (BaseSmartRegisterFragment) findFragmentByPosition(0);
        if (registerFragment != null) {
            registerFragment.openVaccineCard(filterString);
        }
    }

    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), HIDE_NOT_ALWAYS);
    }


}
