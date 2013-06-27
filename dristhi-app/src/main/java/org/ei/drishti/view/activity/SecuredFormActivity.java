package org.ei.drishti.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.webkit.WebSettings;
import org.apache.commons.io.IOUtils;
import org.ei.drishti.Context;

import java.io.IOException;
import java.text.MessageFormat;

import static java.util.UUID.randomUUID;
import static org.ei.drishti.AllConstants.*;
import static org.ei.drishti.R.string.*;
import static org.ei.drishti.util.Log.logError;

public abstract class SecuredFormActivity extends SecuredWebActivity {
    private String model;
    private String form;
    private String formName;
    private String entityId;

    @Override
    protected void onInitialization() {
        try {
            getIntentData();
        } catch (IOException e) {
            logError(e.toString());
            finish();
        }
        webViewInitialization();
    }

    private void getIntentData() throws IOException {
        Intent intent = getIntent();
        formName = intent.getStringExtra(FORM_NAME_PARAM);
        entityId = intent.getStringExtra(ENTITY_ID_PARAM);
        model = IOUtils.toString(getAssets().open("www/form/" + formName + "/model.xml"));
        form = IOUtils.toString(getAssets().open("www/form/" + formName + "/form.xml"));
    }

    private void webViewInitialization() {
        WebSettings webViewSettings = webView.getSettings();
        webViewSettings.setJavaScriptEnabled(true);
        webViewSettings.setDatabaseEnabled(true);
        webViewSettings.setDomStorageEnabled(true);
        webView.addJavascriptInterface(new FormWebInterface(model, form, this), "androidContext");
        webView.addJavascriptInterface(Context.getInstance().formDataRepository(), REPOSITORY);
        webView.addJavascriptInterface(Context.getInstance().ziggyFileLoader(), ZIGGY_FILE_LOADER);
        webView.addJavascriptInterface(Context.getInstance().formSubmissionRouter(), FORM_SUBMISSION_ROUTER);
        webView.loadUrl(MessageFormat.format("file:///android_asset/www/form/template.html?{0}={1}&{2}={3}&{4}={5}&touch=true",
                FORM_NAME_PARAM, formName, ENTITY_ID_PARAM, entityId, INSTANCE_ID_PARAM, randomUUID()));
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(form_back_confirm_dialog_message)
                .setTitle(form_back_confirm_dialog_title)
                .setCancelable(false)
                .setPositiveButton(yes_button_label,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                goBack();
                            }
                        })
                .setNegativeButton(no_button_label,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                            }
                        })
                .show();
    }

    private void goBack() {
        super.onBackPressed();
    }
}
