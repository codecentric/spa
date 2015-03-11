package de.codecentric.spa.tester;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.codecentric.spa.EntityWrapper;
import de.codecentric.spa.ctx.PersistenceContext;
import de.codecentric.spa.tester.entity.TypeCheckBean;

public class TypeCheckActivity extends Activity {

    private Button executeButton;
    private TextView executionText;

    private EntityWrapper wrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_check);

        executeButton = (Button) findViewById(R.id.executeTypeCheckBtn);
        executionText = (TextView) findViewById(R.id.executionText);

        wrapper = PersistenceContext.getInstance().getEntityWrapper();
    }

    @Override
    protected void onStart() {
        super.onStart();
        executeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logMessage("Starting new batch operations test...\n");

                List<TypeCheckBean> data = prepareData();
                wrapper.batchInsert(data, TypeCheckBean.class);
                logMessage("Batch inserted...\n");

                logMessage("Loading and checking data...\n");
                List<TypeCheckBean> dataFromDB = wrapper.listAll(TypeCheckBean.class);
                for (TypeCheckBean bean : dataFromDB) {
                    if (!checkBeanFromDB(bean)) {
                        logMessage("Bean does not look alright:\n");
                        logMessage(bean.toString());
                        throw new RuntimeException(bean.toString());
                    }
                }
                logMessage("Data checked: everything is ok.\n");

                logMessage("\nClearing database table.\n");
                wrapper.deleteAll(TypeCheckBean.class);
                if (wrapper.listAll(TypeCheckBean.class).size() == 0) {
                    logMessage("Database cleared.\n");
                }

                logMessage("\nStarting new single operation test...\n");
                data = prepareData();
                TypeCheckBean aBean = data.get(0);
                logMessage("Fresh insert...\n");
                wrapper.saveOrUpdate(aBean);
                logMessage("Loading and checking data...\n");
                wrapper.findById(1L, TypeCheckBean.class);
                if (!checkBeanFromDB(aBean)) {
                    logMessage("Bean does not look alright:\n");
                    logMessage(aBean.toString());
                    throw new RuntimeException();
                }
                logMessage("Data checked: everything is ok.\n");
                logMessage("Updating data...\n");
                wrapper.saveOrUpdate(aBean);
                logMessage("Loading and checking data...\n");
                wrapper.findById(1L, TypeCheckBean.class);
                if (!checkBeanFromDB(aBean)) {
                    logMessage("Bean does not look alright:\n");
                    logMessage(aBean.toString());
                    throw new RuntimeException();
                }
                logMessage("Data checked: everything is ok.\n");

                logMessage("\nTest finished successfully.\n");
            }
        });
    }

    private boolean checkBeanFromDB(TypeCheckBean bean) {
        return (bean.id != 0 && bean.aLong != null && bean.date != null && bean.aInteger != null
                && bean.aString != null && bean.aFloat != null && bean.aShort != null &&
                bean.aBoolean != null && bean.aDouble != null &&
                bean.timeCreated != null && bean.lastUpdated != null);
    }

    private List<TypeCheckBean> prepareData() {
        List<TypeCheckBean> data = new ArrayList<TypeCheckBean>(10);

        for (int i = 0; i < 10; i++) {
            TypeCheckBean bean = new TypeCheckBean();
            bean.aBoolean = i % 2 == 0;

            String iString = String.valueOf(i);
            bean.aDouble = Double.parseDouble(iString);
            bean.aFloat = Float.parseFloat(iString);
            bean.aInteger = Integer.parseInt(iString);
            bean.aLong = Long.parseLong(iString);
            bean.aShort = Short.parseShort(iString);
            bean.aString = iString;
            bean.date = new Date();
            data.add(bean);
        }

        return data;
    }

    private void logMessage(String message) {
        executionText.setText(executionText.getText() + message);
    }

}
