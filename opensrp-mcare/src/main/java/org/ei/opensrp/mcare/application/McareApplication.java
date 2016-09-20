package org.ei.opensrp.mcare.application;

import android.content.Intent;
import android.content.res.Configuration;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonFtsObject;
import org.ei.opensrp.mcare.LoginActivity;
import org.ei.opensrp.sync.DrishtiSyncScheduler;
import org.ei.opensrp.util.StringUtil;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.ei.opensrp.view.receiver.SyncBroadcastReceiver;

import static org.ei.opensrp.util.Log.logInfo;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by koros on 1/22/16.
 */
@ReportsCrashes(
        formKey = "",
        formUri = "https://drishtiapp.cloudant.com/acra-drishtiapp/_design/acra-storage/_update/report",
        reportType = org.acra.sender.HttpSender.Type.JSON,
        httpMethod = org.acra.sender.HttpSender.Method.POST,
        formUriBasicAuthLogin = "sompleakereepeavoldiftle",
        formUriBasicAuthPassword = "ecUMrMeTKf1X1ODxHqo3b43W",
        mode = ReportingInteractionMode.SILENT
)
public class McareApplication extends DrishtiApplication {

    @Override
    public void onCreate() {
        DrishtiSyncScheduler.setReceiverClass(SyncBroadcastReceiver.class);
        super.onCreate();
//        ACRA.init(this);

        DrishtiSyncScheduler.setReceiverClass(SyncBroadcastReceiver.class);

        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());
        context.updateCustomHumanReadableConceptResponse(getHumanReadableConceptResponse());
        applyUserLanguagePreference();
        cleanUpSyncState();
    }

    @Override
    public void logoutCurrentUser() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
        context.userService().logoutSession();
    }

    private void cleanUpSyncState() {
        DrishtiSyncScheduler.stop(getApplicationContext());
        context.allSharedPreferences().saveIsSyncInProgress(false);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        logInfo("Application is terminating. Stopping Dristhi Sync scheduler and resetting isSyncInProgress setting.");
        cleanUpSyncState();
    }

    private void applyUserLanguagePreference() {
        Configuration config = getBaseContext().getResources().getConfiguration();

        String lang = context.allSharedPreferences().fetchLanguagePreference();
        if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
            locale = new Locale(lang);
            updateConfiguration(config);
        }
    }

    private void updateConfiguration(Configuration config) {
        config.locale = locale;
        Locale.setDefault(locale);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    public static String convertToEnglishDigits(String value) {
//        ০১২৩৪৫৬৭৮৯
        String newValue = value.replace("১", "1").replace("২", "2").replace("৩", "3").replace("৪", "4").replace("৫", "5")
                .replace("৬", "6").replace("৭", "7").replace("৮", "8").replace("৯", "9").replace("০", "0");

        return newValue;
    }

    public static String convertToBengaliDigits(String value) {
//        ০১২৩৪৫৬৭৮৯
        String newValue = value.replace("1", "১").replace("2", "২").replace("3", "৩").replace("4", "৪").replace("5", "৫")
                .replace("6", "৬").replace("7", "৭").replace("8", "৮").replace("9", "৯").replace("0", "০");

        return newValue;
    }

    private String[] getFtsSearchFields(String tableName) {
        if (tableName.equals("ec_household")) {
            String[] ftsSearchFields = {"FWHOHFNAME", "FWGOBHHID", "FWJIVHHID"};
            return ftsSearchFields;
        } else if (tableName.equals("ec_elco")) {
            String[] ftsSearchFields = {"FWWOMFNAME", "GOBHHID", "JiVitAHHID"};
            return ftsSearchFields;
        } else if (tableName.equals("ec_mcaremother")) {
            String[] ftsSearchFields = {"FWWOMFNAME", "GOBHHID", "JiVitAHHID"};
            return ftsSearchFields;
        } else if (tableName.equals("ec_pnc")) {
            String[] ftsSearchFields = {"FWWOMFNAME", "GOBHHID", "JiVitAHHID"};
            return ftsSearchFields;
        } else if (tableName.equals("ec_mcarechild")) {
            String[] ftsSearchFields = {"FWWOMFNAME", "GOBHHID", "JiVitAHHID"};
            return ftsSearchFields;
        }
        return null;
    }

    private String[] getFtsSortFields(String tableName) {
        if (tableName.equals("ec_household")) {
            String[] sortFields = {"FWHOHFNAME", "FWGOBHHID", "FWJIVHHID"};
            return sortFields;
        } else if (tableName.equals("ec_elco")) {
            String[] sortFields = {"FWWOMFNAME", "GOBHHID", "JiVitAHHID"};
            return sortFields;
        } else if (tableName.equals("ec_mcaremother")) {
            String[] sortFields = {"FWWOMFNAME", "GOBHHID", "JiVitAHHID", "FWPSRLMP"};
            return sortFields;
        } else if (tableName.equals("ec_pnc")) {
            String[] sortFields = {"FWWOMFNAME", "GOBHHID", "JiVitAHHID", "FWBNFDTOO"};
            return sortFields;
        } else if (tableName.equals("ec_mcarechild")) {
            String[] sortFields = {"FWWOMFNAME", "GOBHHID", "JiVitAHHID"};
            return sortFields;
        }
        return null;
    }

    private String[] getFtsMainConditions(String tableName) {
        if (tableName.equals("ec_household")) {
            String[] mainConditions = {"FWHOHFNAME"};
            return mainConditions;
        } else if (tableName.equals("ec_elco")) {
            String[] mainConditions = {"FWWOMFNAME", "is_closed"};
            return mainConditions;
        } else if (tableName.equals("ec_mcaremother")) {
            String[] mainConditions = {"FWWOMFNAME", "is_closed"};
            return mainConditions;
        } else if (tableName.equals("ec_pnc")) {
            String[] mainConditions = {"is_closed"};
            return mainConditions;
        } else if (tableName.equals("ec_mcarechild")) {
            String[] mainConditions = {"FWBNFGEN"};
            return mainConditions;
        }
        return null;
    }

    private String[] getFtsTables() {
        String[] ftsTables = {"ec_household", "ec_elco", "ec_mcaremother", "ec_pnc", "ec_mcarechild"};
        return ftsTables;
    }

    private CommonFtsObject createCommonFtsObject() {
        CommonFtsObject commonFtsObject = new CommonFtsObject(getFtsTables());
        for (String ftsTable : commonFtsObject.getTables()) {
            commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields(ftsTable));
            commonFtsObject.updateSortFields(ftsTable, getFtsSortFields(ftsTable));
            commonFtsObject.updateMainConditions(ftsTable, getFtsMainConditions(ftsTable));
        }
        return commonFtsObject;
    }

    /**
     * Map linking generated Concepts with human readable values
     * @return
     */
    private Map<String, String> getHumanReadableConceptResponse(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "1");
        map.put("1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "0");
        return map;
    }

}
