package org.ei.drishti.view;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ProgressBar;
import org.ei.drishti.R;
import org.ei.drishti.controller.AlertController;
import org.ei.drishti.domain.FetchStatus;
import org.ei.drishti.service.ActionService;
import org.ei.drishti.service.DrishtiService;
import org.ei.drishti.util.FakeDrishtiService;
import org.ei.drishti.view.activity.AlertsActivity;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.ei.drishti.domain.FetchStatus.fetched;
import static org.ei.drishti.util.Wait.waitForProgressBarToGoAway;

public class UpdateAlertsTaskIntegrationTest extends ActivityInstrumentationTestCase2<AlertsActivity> {
    private CountDownLatch signal;
    private final FakeDrishtiService drishtiService;

    public UpdateAlertsTaskIntegrationTest() {
        super(AlertsActivity.class);
        drishtiService = new FakeDrishtiService("Default");
        drishtiService.setSuffix(String.valueOf(new Date().getTime()));
        signal = new CountDownLatch(1);
    }

    public void testShouldNotUpdateWhileAnotherUpdateIsInProgress() throws Throwable {
        Context.setInstance(new Context() {
            @Override
            protected DrishtiService drishtiService() {
                return drishtiService;
            }
        }).updateApplicationContext(getActivity().getApplicationContext());

        ActionServiceWithSimulatedLongRunningTask service = new ActionServiceWithSimulatedLongRunningTask();
        final UpdateActionsTask updateAlertsTask = new UpdateActionsTask(null, service, (ProgressBar) getActivity().findViewById(R.id.progressBar));

        waitForProgressBarToGoAway(getActivity(), 4000);

        runTestOnUiThread(new Runnable() {
            public void run() {
                updateFromServer();
                updateFromServer();
                updateFromServer();
            }

            private void updateFromServer() {
                updateAlertsTask.updateFromServer(new AfterFetchListener() {
                    public void afterChangeHappened(FetchStatus status) {
                    }
                });
            }
        });

        signal.await(6, TimeUnit.SECONDS);
        assertEquals(1, service.numberOfTimesFetchWasCalled());
        assertEquals(AlertsActivity.class, getActivity().getClass());
    }

    private class ActionServiceWithSimulatedLongRunningTask extends ActionService {

        private int counter = 0;
        public ActionServiceWithSimulatedLongRunningTask() {
            super(null, null, null, null);
        }

        @Override
        public FetchStatus fetchNewActions() {
            counter++;
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            signal.countDown();
            return fetched;
        }

        public int numberOfTimesFetchWasCalled() {
            return counter;
        }

    }
    private AlertController fakeController() {
        return new AlertController(null, null) {
            @Override
            public void refreshAlertsFromDB() {
            }
        };
    }
}
