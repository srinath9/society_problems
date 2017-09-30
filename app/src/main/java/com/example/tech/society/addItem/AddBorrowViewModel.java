package com.example.tech.society.addItem;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.tech.society.MainActivity;
import com.example.tech.society.models.AppDatabase;
import com.example.tech.society.models.DataBaseRetrieve;
import com.example.tech.society.models.ProblemModel;

import java.util.List;

/**
 * Created by tech on 9/30/17.
 */


public class AddBorrowViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public AddBorrowViewModel(Application application) {
        super(application);

        appDatabase = AppDatabase.getDatabase(this.getApplication());

    }

    public void addBorrow(final ProblemModel borrowModel) {
        new addAsyncTask(appDatabase).execute(borrowModel);
    }

    public void getData(DataBaseRetrieve data) {
        new getAsyncTask(appDatabase).execute(data);
//        return appDatabase.itemAndPersonModel().getAllBorrowedItems();
    }

    private static class addAsyncTask extends AsyncTask<ProblemModel, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final ProblemModel... params) {
            db.itemAndPersonModel().addBorrow(params[0]);

            Log.i(getClass().getSimpleName(), "doInBackground: "+db.itemAndPersonModel().getAllBorrowedItems());
            return null;
        }

    }
    private static class getAsyncTask extends AsyncTask<DataBaseRetrieve, Void, List<ProblemModel>> {

        private AppDatabase db;

        getAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected List<ProblemModel> doInBackground(final DataBaseRetrieve... params) {
            List<ProblemModel> val = db.itemAndPersonModel().getAllBorrowedItems();
            Log.i(getClass().getSimpleName(), "doInBackground: "+val);
            params[0].onReceive(val);
            return val;
        }

    }
}

